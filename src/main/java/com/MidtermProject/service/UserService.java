package com.MidtermProject.service;


import com.MidtermProject.model.User;
import com.MidtermProject.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userDataStore;
    private final ConcurrentHashMap<String, String> otpCache = new ConcurrentHashMap<>();
    private final JavaMailSender emailDispatcher;
    private final HttpSession userSession;

    // Security Utilities
    private String hashing(String plainText) {
        try {
            MessageDigest encoder = MessageDigest.getInstance("SHA-256");
            byte[] encryptedBytes = encoder.digest(plainText.getBytes());
            StringBuilder resultBuilder = new StringBuilder();
            for (byte b : encryptedBytes) {
                resultBuilder.append(String.format("%02x", b));
            }
            return resultBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Add these methods to UserService.java

public User updateUserProfile(Integer userId, Map<String, Object> profileData) {
    return userDataStore.findById(userId)
            .map(user -> {
                if (profileData.containsKey("fullname")) {
                    user.setfullname((String) profileData.get("fullname"));
                }
                if (profileData.containsKey("username")) {
                    String newUsername = (String) profileData.get("username");
                    // Check if username is already taken by another user
                    if (!user.getUsername().equals(newUsername) ){
                        if (userDataStore.findByUsername(newUsername).isPresent()) {
                            throw new IllegalArgumentException("Username is already taken");
                        }
                        user.setUsername(newUsername);
                    }
                }
                if (profileData.containsKey("email")) {
                    String newEmail = (String) profileData.get("email");
                    // Check if email is already used by another user
                    if (!user.getEmail().equals(newEmail)) {
                        if (userDataStore.findByEmail(newEmail).isPresent()) {
                            throw new IllegalArgumentException("Email is already registered");
                        }
                        user.setEmail(newEmail);
                    }
                }
                return userDataStore.save(user);
            })
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
}

public void changeUserPassword(Integer userId, String currentPassword, String newPassword) {
    User user = userDataStore.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    
    // Verify current password
    if (!user.getPassword().equals(hashing(currentPassword))) {
        throw new IllegalArgumentException("Current password is incorrect");
    }
    
    // Update to new password
    user.setPassword(hashing(newPassword));
    userDataStore.save(user);
}

    public Map<String, Object> getCurrentUserProfileData(HttpSession session) {
        // Get userId from session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("No user ID found in session");
        }
        
        User user = userDataStore.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        Map<String, Object> profileData = new LinkedHashMap<>();
        profileData.put("userId", user.getUserId());
        profileData.put("fullname", user.getfullname());
        profileData.put("username", user.getUsername());
        profileData.put("email", user.getEmail());
        profileData.put("role", user.getRole());
        
        return profileData;
    }


    //signup

    public User signupJobSeeker(User newUser) {
        if (userDataStore.findByUsername(newUser.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userDataStore.findByEmail(newUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        
        newUser.setRole("JOBSEEKER");
        return storeuser(newUser);
    }






    //  Operations
    public User createNewuser(User newuser) {
        if (userDataStore.findByUsername(newuser.getUsername()).isPresent()) {
            throw new IllegalArgumentException("This username is already taken");
        }
        return storeuser(newuser);
    }

    public User storeuser(User user) {
        user.setPassword(hashing(user.getPassword()));
        return userDataStore.save(user);
    }





    public User modifyuser(Integer userId, User user) {
        return userDataStore.findById(userId)
                .map(e -> {
                    e.setfullname(user.getfullname());
                    e.setUsername(user.getUsername());
                    e.setEmail(user.getEmail());
                    e.setRole(user.getRole());
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        e.setPassword(hashing(user.getPassword()));
                    }
                    return userDataStore.save(e);
                })
                .orElseThrow(() -> new EntityNotFoundException("user not found "));
    }




    public void removeuser(Integer userId) {
        if (userDataStore.existsById(userId)) {
            userDataStore.deleteById(userId);
        } else {
            throw new EntityNotFoundException(" user not exists ");
        }
    }

 


    // Authentication Services
    public User authenticateUser(String emailAddress, String secretPhrase) {
        Optional<User> user = userDataStore.findByEmail(emailAddress);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Authentication failed - invalid credentials");
        }
        User founduser = user.get();
        if (!founduser.getPassword().equals(hashing(secretPhrase))) {
            throw new IllegalArgumentException("Authentication failed - invalid credentials");
        }
        
        userSession.setAttribute("userId", founduser.getUserId());
        return founduser;
    }





    public void terminateSession() {
     
        userSession.removeAttribute("userId");
  
        userSession.invalidate();
    }




    public boolean validateAccess(String emailAddress, String secretPhrase) {
        Optional<User> user = userDataStore.findByEmail(emailAddress);
        return user.isPresent() && 
               user.get().getPassword().equals(hashing(secretPhrase));
    }



    // OTP Services
    public void dispatchLoginVerificationCode(String recipientEmail) {
        String verificationCode = generateRandomCode();
        otpCache.put(recipientEmail, verificationCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Your Verification Code");
        message.setText("Your login verification code is: " + verificationCode);
        emailDispatcher.send(message);
    }




    public void dispatchPasswordResetCode(String email, String otpEmail) {
        // Verify the user exists with the provided email
        if (!userDataStore.findByEmail(email).isPresent()) {
            throw new EntityNotFoundException("No user found with this email");
        }
        
        String verificationCode = generateRandomCode();
        // Store OTP with the otpEmail as key (could be different from user's email)
        otpCache.put(otpEmail + "_reset", verificationCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(otpEmail);
        message.setSubject("Your Password Reset Code");
        message.setText("Your password reset verification code is: " + verificationCode);
        emailDispatcher.send(message);
    }






    public User verifyCodeAndAuthenticate(String email, String codeEmail, String verificationCode, String secretPhrase) {
        String storedCode = otpCache.get(codeEmail);
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        
        Optional<User> user = userDataStore.findByEmail(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("No user found with this email");
        }
        
        User useruser = user.get();
        if (!useruser.getPassword().equals(hashing(secretPhrase))) {
            throw new IllegalArgumentException("Incorrect secret phrase");
        }
        
        otpCache.remove(codeEmail);
        userSession.setAttribute("userId", useruser.getUserId());
        return useruser;
    }





    public void verifyResetCodeAndUpdatePassword(String email, String otpEmail, String verificationCode, String newPassword) {
        String cacheKey = otpEmail + "_reset";
        String storedCode = otpCache.get(cacheKey);
        
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        
        Optional<User> userOptional = userDataStore.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("No user found with this email");
        }
        
        User user = userOptional.get();
        user.setPassword(hashing(newPassword));
        userDataStore.save(user);
        
        otpCache.remove(cacheKey);
    }



    // User Data Services
    public List<User> fetchAllusers() {
        return userDataStore.findAll();
    }




 public Page<User> getPaginatedUsers(Pageable pageable) {
        return userDataStore.findAll(pageable);
    }


    public Optional<User> locateByUsername(String username) {
        return userDataStore.findByUsername(username);
    }


    
    public User locateuserById(Integer userId) {
        return userDataStore.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found with ID: " + userId));
    }



    
    // Helper Methods
    private String generateRandomCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}