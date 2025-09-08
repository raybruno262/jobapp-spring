package com.MidtermProject.controller;


import com.MidtermProject.model.User;
import com.MidtermProject.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final HttpSession userSession;

    public UserController(UserService userService, HttpSession userSession) {
        this.userService = userService;
        this.userSession = userSession;
    }

    // User CRUD Operations
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createNewuser(user));
    }

    @GetMapping("/profile/current")
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfileData(userSession));
    }


    @PostMapping("/jobseeker")
    public ResponseEntity<User> createJobSeeker(@RequestBody User user) {
        return ResponseEntity.ok(userService.signupJobSeeker(user));
    }



    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.locateuserById(userId));
    }



    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.fetchAllusers());
    }




     // Get paginated job categories
    @GetMapping("/paginated")
    public ResponseEntity<Page<User>> getPaginatedUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> categories = userService.getPaginatedUsers(pageable);
        return ResponseEntity.ok(categories);
    }



    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer userId,
            @RequestBody User user) {
        return ResponseEntity.ok(userService.modifyuser(userId, user));
    }




    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.removeuser(userId);
        return ResponseEntity.noContent().build();
    }

// Add these methods to UserController.java

@PutMapping("/edit/profile")
public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> profileData) {
    Integer userId = (Integer) userSession.getAttribute("userId");
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    User updatedUser = userService.updateUserProfile(userId, profileData);
    
    // Return the updated profile data
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("userId", updatedUser.getUserId());
    response.put("fullname", updatedUser.getfullname());
    response.put("username", updatedUser.getUsername());
    response.put("email", updatedUser.getEmail());
    response.put("role", updatedUser.getRole());
    
    return ResponseEntity.ok(response);
}

@PostMapping("/password/change")
public ResponseEntity<Void> changePassword(
        @RequestParam String currentPassword,
        @RequestParam String newPassword) {
    Integer userId = (Integer) userSession.getAttribute("userId");
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    userService.changeUserPassword(userId, currentPassword, newPassword);
    return ResponseEntity.noContent().build();
}

    // Authentication Operations
    @PostMapping("/login")
    public ResponseEntity<User> login(
            @RequestParam String email,
            @RequestParam String password) {
        return ResponseEntity.ok(userService.authenticateUser(email, password));
    }




       @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        try {
            userService.terminateSession();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // OTP Operations
    // @PostMapping("/otp/login")
    // public ResponseEntity<Void> sendLoginOtp(@RequestParam String email) {
    //     userService.dispatchLoginVerificationCode(email);
    //     System.out.println("Otp sent to " + email);
    //     return ResponseEntity.noContent().build();
        
    // }




    @PostMapping("/otp/login/verify")
    public ResponseEntity<User> verifyLoginOtp(
            @RequestParam String email,
            @RequestParam String otpEmail,
            @RequestParam String verificationCode,
            @RequestParam String password) {
        return ResponseEntity.ok(userService.verifyCodeAndAuthenticate(
                email, otpEmail, verificationCode, password));
    }



    // Password Reset Operations
    // @PostMapping("/password/reset/otp")
    // public ResponseEntity<Void> sendPasswordResetOtp(
    //         @RequestParam String email,
    //         @RequestParam String otpEmail) {
    //     userService.dispatchPasswordResetCode(email, otpEmail);
    //     return ResponseEntity.noContent().build();
    // }




    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String email,
            @RequestParam String otpEmail,
            @RequestParam String verificationCode,
            @RequestParam String newPassword) {
        userService.verifyResetCodeAndUpdatePassword(
                email, otpEmail, verificationCode, newPassword);
        return ResponseEntity.noContent().build();
    }




    // Utility Operations
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.locateByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateCredentials(
            @RequestParam String email,
            @RequestParam String password) {
        return ResponseEntity.ok(userService.validateAccess(email, password));
    }
}