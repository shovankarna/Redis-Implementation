package com.ecommerce.notification.messaging;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationSubscriber implements MessageListener {

    /**
     * SKELETON ONLY: Pub/Sub Listener Callback.
     * 
     * Javadocs:
     * This method is the channel consumption callback framework.
     * It is invoked automatically whenever a message is published to the bound `ChannelTopic`.
     * 
     * Implement the following logic:
     * 1. Extract the raw byte array from `message.getBody()`.
     * 2. Deserialize it to a String or specific Alert object (depending on your Publisher implementation).
     * 3. Handle the live alert (e.g., log it or push it to a WebSocket).
     * 
     * @param message the received Redis message
     * @param pattern the pattern matching the channel (if pattern matching was used)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // TODO: Implement Phase 3 Pub/Sub Listener
    }
}
