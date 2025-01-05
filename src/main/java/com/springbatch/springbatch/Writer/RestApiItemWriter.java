package com.springbatch.springbatch.Writer;

import com.springbatch.springbatch.dto.FailedRecordTracker;
import com.springbatch.springbatch.dto.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.rmi.ConnectException;


@Component
public class RestApiItemWriter {

    String url = "http://localhost:9999/api/insertMemberDetails";

    private final RestTemplate restTemplate;
    private final FailedRecordTracker failedRecordTracker;

    @Autowired
    public RestApiItemWriter(RestTemplate restTemplate, FailedRecordTracker failedRecordTracker) {
        this.restTemplate = restTemplate;
        this.failedRecordTracker = failedRecordTracker;
    }

    public void sendMemberRequest(MemberDto item) throws Exception {
        int retries = 3; // Number of retry attempts
        int delay = 1000; // Initial delay in milliseconds (1 second)

        System.out.println("========Print the Item details====== " + item.toString());

        while (retries > 0) {
            try {
                // Sending POST request and receiving ResponseEntity
                ResponseEntity<MemberDto> response = restTemplate.postForEntity(url, item, MemberDto.class);

                // Check if the response status is not successful (4xx or 5xx)
                if (!response.getStatusCode().is2xxSuccessful()) {
                    HttpStatus status = response.getStatusCode();
                    switch (status) {
                        case BAD_REQUEST:
                            System.err.println("400 Bad Request: " + response.getBody());
                            break;
                        case UNAUTHORIZED:
                            System.err.println("401 Unauthorized: " + response.getBody());
                            break;
                        case FORBIDDEN:
                            System.err.println("403 Forbidden: " + response.getBody());
                            break;
                        case NOT_FOUND:
                            System.err.println("404 Not Found: " + response.getBody());
                            break;
                        case INTERNAL_SERVER_ERROR:
                            System.err.println("500 Internal Server Error: " + response.getBody());
                            break;
                        case SERVICE_UNAVAILABLE:
                            failedRecordTracker.addFailedRecord(item);
                            System.err.println("503 Service Unavailable: " + response.getBody());
                            break;
                        default:
                            System.err.println("Unhandled error: " + status + ", " + response.getBody());
                    }
                } else {
                    System.out.println("Request successful, received response: " + response.getBody());
                    return; // Exit successfully if the request was successful
                }
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                // Handle client/server errors (4xx, 5xx)
                System.err.println("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            } catch (ResourceAccessException e) {
                // Handle I/O network errors

                if (e.getCause() instanceof ConnectException) {
                    System.err.println("ResourceAccessException: Connection refused: " + e.getCause().getMessage());
                    failedRecordTracker.addFailedRecord(item);
                } else {
                    System.err.println("I/O error: " + e.getMessage());
                    failedRecordTracker.addFailedRecord(item);
                }
            } catch (Exception e) {
                // Catch all for other unexpected errors
                e.printStackTrace();
                failedRecordTracker.addFailedRecord(item);
            }

            retries--; // Decrement retry count

            if (retries > 0) {
                // Exponential backoff
                delay *= 2;
                System.out.println("Retrying in " + delay + " ms...");
                Thread.sleep(delay); // Wait before retrying
            } else {
                System.err.println("Max retries reached. Giving up.");
            }
        }
    }
}
