package com.MidtermProject.controller;

import com.MidtermProject.model.Message;
import com.MidtermProject.model.User;
import com.MidtermProject.service.MessageService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // DTO for message requests
    public static class MessageRequest {
        private String messageText;

        // Getters and setters
        public String getMessageText() {
            return messageText;
        }
        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }
    }


    @PostMapping("/send/{receiverId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable int receiverId,
            @RequestBody MessageRequest request) {
        try {
            Message message = new Message();
            message.setMessageText(request.getMessageText());
            Message sentMessage = messageService.sendMessage(message, receiverId);
            return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(
            @PathVariable int id,
            @RequestBody MessageRequest request) {
        try {
            Message messageDetails = new Message();
            messageDetails.setMessageText(request.getMessageText());
            return ResponseEntity.ok(messageService.updateMessage(id, messageDetails));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable int id) {
        try {
            messageService.deleteMessage(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/employer/send-to-jobseeker/{jobSeekerId}")
    public ResponseEntity<?> sendMessageToJobSeeker(
            @PathVariable int jobSeekerId,
            @RequestBody MessageRequest request) {
        try {
            Message sentMessage = messageService.sendMessageToJobSeeker(jobSeekerId, request.getMessageText());
            return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/jobseeker/broadcast-to-employers")
    public ResponseEntity<?> broadcastToEmployers(@RequestBody MessageRequest request) {
        try {
            Message message = new Message();
            message.setMessageText(request.getMessageText());
            List<Message> sentMessages = messageService.broadcastToAllEmployers(message);
            return ResponseEntity.status(HttpStatus.CREATED).body(sentMessages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(@PathVariable int id) {
        try {
            return ResponseEntity.ok(messageService.getMessageById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my-messages")
    public ResponseEntity<Page<Message>> getMyMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,desc") String sort) {
        try {
            String[] sortParams = sort.split(",");
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]));
            return ResponseEntity.ok(messageService.getMyMessages(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/employer/conversations")
    public ResponseEntity<Page<Message>> getEmployerConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
            return ResponseEntity.ok(messageService.getAllEmployerMessages(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Add this to your MessageController
@GetMapping("/jobseekers")
public ResponseEntity<?> getAllJobSeekers() {
    try {
        List<User> jobSeekers = messageService.getAllJobSeekers();
        return ResponseEntity.ok(jobSeekers);
    } catch (IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}



    @GetMapping("/jobseeker/conversations")
    public ResponseEntity<Page<Message>> getJobSeekerMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
            return ResponseEntity.ok(messageService.getJobSeekerMessages(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @GetMapping("/admin/all")
    public ResponseEntity<Page<Message>> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
            return ResponseEntity.ok(messageService.getAllMessages(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}