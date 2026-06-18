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
     * It is invoked automatically whenever a message is published to the bound
     * `ChannelTopic`.
     * 
     * Implement the following logic:
     * 1. Extract the raw byte array from `message.getBody()`.
     * 2. Deserialize it to a String or specific Alert object (depending on your
     * Publisher implementation).
     * 3. Handle the live alert (e.g., log it or push it to a WebSocket).
     * 
     * @param message the received Redis message
     * @param pattern the pattern matching the channel (if pattern matching was
     *                used)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {

        // Extract raw bytes and convert to a String
        String alertMessage = new String(message.getBody());

        System.out.println("PUB/SUB: Received message, starting processing...");

        try {
            // Simulated processing delay (e.g., sending an actual email/SMS)
            // You can manually change this delay to see how it affects the receipt time!
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Processing interrupted!");
        }

        // Handle the live alert
        System.out.println("PUB/SUB RECEIVE: LIVE ALERT PROCESSED: " + alertMessage);
    }
}
