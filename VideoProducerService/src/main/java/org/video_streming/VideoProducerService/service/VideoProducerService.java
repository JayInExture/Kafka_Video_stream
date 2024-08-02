package org.video_streming.VideoProducerService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

@Service
public class VideoProducerService implements CommandLineRunner {

    @Value("${kafka.topic}")
    private String topic;

    @Value("${video.file.path}")
    private String videoFilePath;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final Logger logger = Logger.getLogger(VideoProducerService.class.getName());

    public VideoProducerService(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        streamVideo();
    }

    public void streamVideo() {
        File videoFile = new File(videoFilePath);
        if (!videoFile.exists()) {
            logger.severe("Video file does not exist: " + videoFilePath);
            return;
        }

        try (FileInputStream fis = new FileInputStream(videoFile)) {
            byte[] buffer = new byte[10240]; // Define buffer size
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                kafkaTemplate.send(topic, chunk);
                logger.info("Sent video chunk to Kafka. Size: " + chunk.length);
            }
        } catch (IOException e) {
            logger.severe("Error reading video file: " + e.getMessage());
        }
    }
}




//package org.video_streming.VideoProducerService.service;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import org.bytedeco.javacv.Frame;
//import org.bytedeco.javacv.OpenCVFrameGrabber;
//import org.bytedeco.javacv.FFmpegFrameRecorder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.logging.Logger;
//
//@Service
//public class VideoProducerService {
//
//    @Value("${kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${kafka.topic}")
//    private String topic;
//
//    @Autowired
//    private KafkaTemplate<String, byte[]> kafkaTemplate;
//
//    private OpenCVFrameGrabber grabber;
//    private FFmpegFrameRecorder recorder;
//    private File videoFile;
//    private Logger logger = Logger.getLogger(VideoProducerService.class.getName());
//
//    @PostConstruct
//    public void start() {
//        grabber = new OpenCVFrameGrabber(0); // Capture from default camera
//        try {
//            grabber.start();
//            videoFile = new File("video.mp4");
//            recorder = new FFmpegFrameRecorder(videoFile, grabber.getImageWidth(), grabber.getImageHeight());
//            recorder.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
//            recorder.setFormat("mp4");
//            recorder.setFrameRate(30);
//            recorder.start();
//            new Thread(this::streamVideo).start();
//        } catch (Exception e) {
//            throw new RuntimeException("Camera not available or encoding failed!", e);
//        }
//    }
//
//    @PreDestroy
//    public void stop() {
//        try {
//            if (recorder != null) {
//                recorder.stop();
//                recorder.release();
//            }
//            if (grabber != null) {
//                grabber.stop();
//                grabber.release();
//            }
//            if (videoFile != null) {
//                // Send video file to Kafka
//                try (FileInputStream fis = new FileInputStream(videoFile)) {
//                    byte[] videoBytes = fis.readAllBytes();
//                    kafkaTemplate.send(topic, videoBytes);
//                } catch (IOException e) {
//                    logger.severe("Error reading video file: " + e.getMessage());
//                }
//                videoFile.delete(); // Clean up
//            }
//        } catch (Exception e) {
//            logger.severe("Error stopping video capture or recording: " + e.getMessage());
//        }
//    }
//
//    private void streamVideo() {
//        while (true) {
//            try {
//                Frame frame = grabber.grab();
//                if (frame != null) {
//                    recorder.record(frame);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                break;
//            }
//        }
//    }
//}



//package org.video_streming.VideoProducerService.service;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import org.bytedeco.javacv.Frame;
//import org.bytedeco.javacv.OpenCVFrameGrabber;
//import org.bytedeco.javacv.Java2DFrameConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//@Service
//public class VideoProducerService {
//
//    @Value("${kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${kafka.topic}")
//    private String topic;
//
//    @Autowired
//    private KafkaTemplate<String, byte[]> kafkaTemplate;
//
//    private OpenCVFrameGrabber grabber;
//
//    @PostConstruct
//    public void start() {
//        grabber = new OpenCVFrameGrabber(0); // Capture from default camera
//        try {
//            grabber.start();
//            new Thread(this::streamVideo).start();
//        } catch (Exception e) {
//            throw new RuntimeException("Camera not available!", e);
//        }
//    }
//
//    @PreDestroy
//    public void stop() {
//        try {
//            if (grabber != null) {
//                grabber.stop();
//                grabber.release();
//            }
//        } catch (Exception e) {
//            // Handle the exception
//        }
//    }
//
//    private void streamVideo() {
//        Java2DFrameConverter converter = new Java2DFrameConverter();
//        while (true) {
//            try {
//                Frame frame = grabber.grab();
//                if (frame != null) {
//                    BufferedImage image = converter.convert(frame);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    ImageIO.write(image, "jpg", baos);
//                    byte[] imageBytes = baos.toByteArray();
//                    kafkaTemplate.send(topic, imageBytes);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                break;
//            }
//        }
//    }
//}
