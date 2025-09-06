package com.MidtermProject.repository;

import com.MidtermProject.model.Message;
import com.MidtermProject.model.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
   List<Message> findBySender(User sender);
    Page<Message> findBySender(User sender, Pageable pageable);
    List<Message> findByReceiver(User receiver);
    Page<Message> findByReceiver(User receiver, Pageable pageable);

 
List<Message> findTop5ByReceiverUserIdOrSenderUserIdOrderByDateDesc(int receiverId, int senderId);

Page<Message> findBySenderOrReceiver(User sender, User receiver, Pageable pageable);
    
List<Message> findBySenderOrReceiver(User sender, User receiver);

List<Message> findTop5ByOrderByDateDesc();



   // For employer to see all messages in their organization
   Page<Message> findBySender_RoleOrReceiver_Role(String senderRole, String receiverRole, Pageable pageable);
}
