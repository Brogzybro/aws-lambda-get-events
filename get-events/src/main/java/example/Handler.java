package example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import example.scrapers.Scraper;
import example.scrapers.TenTimesScraper;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler implements RequestHandler<ScheduledEvent, String> {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private final List<Scraper> scrapers =  List.of(new TenTimesScraper());

    @Override
    public String handleRequest(ScheduledEvent scheduledEvent, Context context) {

        logger.info("Searching for events...");

        var events = new ArrayList<Event>();

        for (var scraper : scrapers) {
            try {
                logger.info("Searching events for URL: {}.", scraper.getUrl());
                var scrapedEvents = scraper.getEvents();
                logger.info("Found {} events for URL: {}.", scrapedEvents.size(), scraper.getUrl());
                events.addAll(scrapedEvents);
            } catch (IOException e) {
                logger.error("Scraper failed!", e);
            }
        }

        logger.info("Finished searching for events!");
        logger.info("found in total {} events.", events.size());


        for (var event : events) {
            logger.info("Extracted: {}.", event);
        }

        return "Finished searching for events.";
    }

    private InputStream getObject(S3Client s3Client, String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    private void putObject(S3Client s3Client, ByteArrayOutputStream outputStream,
                           String bucket, String key) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Length", Integer.toString(outputStream.size()));

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .build();

        // Uploading to S3 destination bucket
        logger.info("Writing to: " + bucket + "/" + key);
        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(outputStream.toByteArray()));
        } catch (AwsServiceException e) {
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        var dummyAwsEvent = new ScheduledEvent();
        dummyAwsEvent.setId("Test");

        Handler handler = new Handler();
        handler.handleRequest(dummyAwsEvent, null);
    }
}