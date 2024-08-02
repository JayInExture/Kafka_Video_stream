//package org.video_streming.VideoConsumerService.Controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.ByteArrayInputStream;
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Base64;
//import java.util.logging.Logger;
//
//@RestController
//public class VideoConsumerController {
//
//    @Value("${kafka.topic}")
//    private String topic;
//
//    private byte[] lastImage;
//
//    private static final Logger logger = Logger.getLogger(VideoConsumerController.class.getName());
//
//    @KafkaListener(topics = "${kafka.topic}", groupId = "video-group")
//    public void listen(byte[] imageBytes) {
//        logger.info("Received image bytes from Kafka. Size: " + imageBytes.length);
//        lastImage = imageBytes;
//    }
//
//    @GetMapping("/video-stream")
//    public String getVideoStream() {
//        if (lastImage == null) {
//            logger.warning("No video stream available.");
//            return "No video stream available.";
//        }
//        try {
//            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(lastImage));
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "jpg", baos);
//            byte[] imageBytes = baos.toByteArray();
//            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//            return "data:image/jpeg;base64," + base64Image;
//        } catch (IOException e) {
//            logger.severe("Error processing video stream: " + e.getMessage());
//            return "Error processing video stream.";
//        }
//    }
//}

//package org.video_streming.VideoConsumerService.Controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.logging.Logger;
//
//@RestController
//public class VideoConsumerController {
//
//    @Value("${kafka.topic}")
//    private String topic;
//
//    private final BlockingQueue<byte[]> videoQueue = new LinkedBlockingQueue<>();
//
//    private static final Logger logger = Logger.getLogger(VideoConsumerController.class.getName());
//
//    @KafkaListener(topics = "${kafka.topic}", groupId = "video-group")
//    public void listen(byte[] videoBytes) {
//        logger.info("Received video bytes from Kafka. Size: " + videoBytes.length);
//        videoQueue.offer(videoBytes);
//    }
//
//    @GetMapping(value = "/video-stream", produces = "video/mp4")
//    public ResponseEntity<byte[]> getVideoStream() {
//        byte[] videoBytes = videoQueue.poll();
//        if (videoBytes == null) {
//            logger.warning("No video stream available.");
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(videoBytes);
//    }
//}
//
//

// kafka video streaming........
package org.video_streming.VideoConsumerService.Controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@Controller
public class VideoConsumerController {

    @Value("${kafka.output-topic}")
    private String outputTopic;

    private final BlockingQueue<byte[]> videoQueue = new LinkedBlockingQueue<>();
    private static final Logger logger = Logger.getLogger(VideoConsumerController.class.getName());

    @Autowired
    private KStream<String, byte[]> kStream;

//    @PostConstruct
//    public void initialize() {
//        kStream.foreach((key, value) -> {
//            if (value != null) {
//                logger.info("Received video bytes from Kafka Streams. Size: " + value.length);
//                videoQueue.offer(value);
//            } else {
//                logger.warning("Received null video bytes from Kafka Streams.");
//            }
//        });
//    }

//    @GetMapping(value = "/video-stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    public void streamVideo(HttpServletResponse response) {
//        response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
//        try (OutputStream out = response.getOutputStream()) {
//            byte[] videoBytes;
//            while ((videoBytes = videoQueue.poll()) != null) {
//                out.write(videoBytes);
//                out.flush();
//                logger.info("Sending video bytes to client. Size: " + videoBytes.length);
//            }
//        } catch (Exception e) {
//            logger.severe("Error streaming video: " + e.getMessage());
//        }
//    }
}