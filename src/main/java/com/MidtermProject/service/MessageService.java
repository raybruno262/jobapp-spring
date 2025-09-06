package com.MidtermProject.service;

import com.MidtermProject.repository.MessageRepository;
import com.MidtermProject.repository.UserRepository;
import com.MidtermProject.model.Message;
import com.MidtermProject.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final HttpSession session;


    private User getCurrentUser() {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }





    @Transactional
    public Message sendMessage(Message messageRequest, int receiverId) {
        User sender = getCurrentUser();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));
        
        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setMessageText(messageRequest.getMessageText());
        newMessage.setDate(LocalDateTime.now());
        
        return messageRepository.save(newMessage);
    }



// Add this method to your MessageService class
public List<User> getAllJobSeekers() {
    User currentUser = getCurrentUser();
    if (!currentUser.getRole().equals("EMPLOYER")) {
        throw new IllegalStateException("Only employers can view job seekers");
    }
    return userRepository.findByRole("JOBSEEKER");
}




    public Message getMessageById(int id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
    }



    public Message updateMessage(int id, Message messageDetails) {
        Message message = getMessageById(id);
        if (messageDetails.getMessageText() != null) {
            message.setMessageText(messageDetails.getMessageText());
        }
        return messageRepository.save(message);
    }




    public void deleteMessage(int id) {
        if (!messageRepository.existsById(id)) {
            throw new EntityNotFoundException("Message not found");
        }
        messageRepository.deleteById(id);
    }




    @Transactional
    public Message sendMessageToJobSeeker(int jobSeekerId, String messageText) {
        User sender = getCurrentUser();
    
        // Ensure the sender is an EMPLOYER
        if (!"EMPLOYER".equals(sender.getRole())) {
            throw new IllegalStateException("Only employers can message job seekers.");
        }
    
        // Retrieve the job seeker user from the database
        User receiver = userRepository.findById(jobSeekerId)
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found: " + jobSeekerId));
    
        // Ensure the selected user is a JOBSEEKER
        if (!"JOBSEEKER".equals(receiver.getRole())) {
            throw new IllegalStateException("User " + jobSeekerId + " is not a job seeker.");
        }
    
        // Create and save the message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessageText(messageText);
        message.setDate(LocalDateTime.now());
    
        return messageRepository.save(message);
    }
    @Transactional
    public List<Message> broadcastToAllEmployers(Message messageRequest) {
        User sender = getCurrentUser();
        if (!sender.getRole().equals("JOBSEEKER")) {
            throw new IllegalStateException("Only job seekers can broadcast to employers");
        }

        List<User> employers = userRepository.findByRole("EMPLOYER");
        return employers.stream()
                .map(receiver -> createMessage(sender, receiver, messageRequest.getMessageText()))
                .collect(Collectors.toList());
    }

    private Message createMessage(User sender, User receiver, String text) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessageText(text);
        message.setDate(LocalDateTime.now());
        return messageRepository.save(message);
    }





    // For current user
    public Page<Message> getMyMessages(Pageable pageable) {
        User user = getCurrentUser();
        return messageRepository.findBySenderOrReceiver(user, user, pageable);
    }




    // For employers (admin view)
    public Page<Message> getAllEmployerMessages(Pageable pageable) {
        User user = getCurrentUser();
        if (!user.getRole().equals("EMPLOYER")) {
            throw new IllegalStateException("EMPLOYER access required");
        }
        return messageRepository.findBySender_RoleOrReceiver_Role("EMPLOYER", "EMPLOYER", pageable);
    }





    // For job seekers
    public Page<Message> getJobSeekerMessages(Pageable pageable) {
        User user = getCurrentUser();
        if (!user.getRole().equals("JOBSEEKER")) {
            throw new IllegalStateException("Job seeker access required");
        }
        return messageRepository.findBySenderOrReceiver(user, user, pageable);
    }




    public Page<Message> getSentMessages(Pageable pageable) {
        return messageRepository.findBySender(getCurrentUser(), pageable);
    }




    public Page<Message> getReceivedMessages(Pageable pageable) {
        return messageRepository.findByReceiver(getCurrentUser(), pageable);
    }


    
    // Admin-only access
    public Page<Message> getAllMessages(Pageable pageable) {
        User user = getCurrentUser();
        if (!user.getRole().equals("EMPLOYER")) {
            throw new IllegalStateException("EMPLOYER access required");
        }
        return messageRepository.findAll(pageable);
    }
}