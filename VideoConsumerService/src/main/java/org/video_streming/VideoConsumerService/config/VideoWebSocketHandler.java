package org.video_streming.VideoConsumerService.config;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

@Component
public class VideoWebSocketHandler extends BinaryWebSocketHandler {

    private static final Logger logger = Logger.getLogger(VideoWebSocketHandler.class.getName());

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Autowired
    private KStream<String, byte[]> kStream;

    @PostConstruct
    public void initialize() {
        kStream.foreach((key, value) -> {
            if (value != null) {
                logger.info("Received video chunk. Size: " + value.length);
                sendVideoToClients(value);
            } else {
                logger.warning("Received null video chunk.");
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        logger.info("WebSocket connection established: " + session.getId() + " | Number of sessions: " + sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        logger.info("WebSocket connection closed: " + session.getId() + " | Status: " + status + " | Number of sessions: " + sessions.size());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        // Handle incoming binary messages if necessary
    }

    private void sendVideoToClients(byte[] videoBytes) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    logger.info("Sending video chunk to client: " + session.getId() + " | Chunk size: " + videoBytes.length);
                    session.sendMessage(new BinaryMessage(videoBytes));
                } else {
                    logger.warning("Attempted to send message to closed session: " + session.getId());
                }
            } catch (IOException e) {
                logger.severe("Error sending video bytes to client: " + session.getId() + " - " + e.getMessage());
            }
        }
    }
}
