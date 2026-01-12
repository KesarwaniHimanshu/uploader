//package com.uploader.demo.kafka;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaConsumer {
//
//    @KafkaListener(topics = "csv-upload-topic", groupId = "csv-group")
//    public void consume(String message) {
//        System.out.println("Kafka message received: " + message);
//    }
//}
