package com.aummn.eventbridge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileService {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.sqs.url}")
    private String uploadedFileQueue;

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5000) // 5000 milliseconds = 5 seconds
    public void downloadFile() throws JsonProcessingException {
        log.info("In downloadFile()");
        // Poll the SQS queue for a new message
        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(uploadedFileQueue)
                .maxNumberOfMessages(1)
                .build());

        log.info("message size {}", receiveMessageResponse.messages().size());
        if (!receiveMessageResponse.messages().isEmpty()) {
            // Extract the file name from the SQS message
            String message = receiveMessageResponse.messages().get(0).body();
            log.info("message {}", message);
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode detailNode = rootNode.get("detail");
            JsonNode objectNode = detailNode.get("object");
            String fileName = objectNode.get("key").asText();

            log.info("fileName {}", fileName);

            // Download the file from S3
            ResponseBytes<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build(), ResponseTransformer.toBytes());

            // Delete the message from the SQS queue
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(uploadedFileQueue)
                    .receiptHandle(receiveMessageResponse.messages().get(0).receiptHandle())
                    .build());
            log.info("Deleted the msg from the SQS");

            // Process the file
            processFile(s3Object.asByteArray(), fileName);
            log.info("Downloaded file [" + fileName + "] successfully");
        }
    }

    private void processFile(byte[] fileContent, String fileName) {
        Path path = Paths.get(filePath + fileName);
        log.info("Path [" + path.toString() + "]");
        try {
            Files.write(path, fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file " + fileName, e);
        }
    }

    public Message deserializeOrderInfo(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, Message.class);
    }

}
