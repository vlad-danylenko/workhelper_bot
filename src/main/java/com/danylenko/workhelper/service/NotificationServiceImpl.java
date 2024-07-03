package com.danylenko.workhelper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private MessageService messageService;
    private boolean isResponseSuccess = false;

    public void checkApiResponse() {
    if (!isResponseSuccess) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = "https://procedure.prozorro.sale/api/search/large_announcement/bySystemDateModified/2024-07-01";
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                log.info("Scheduler made request to {}", url);
                String responseBody = String.valueOf(response.getCode());
                log.info("Response code is {}", responseBody);
                if (responseBody.equals("204")) {
                } else if (responseBody.equals("200")) {
                    messageService.sendMessage(85262401L, "Status 200 OK. Check the content. It's available now", false);
                    isResponseSuccess = true;
                } else {
                    log.error("Something went wrong");
                    messageService.sendMessage(85262401L, "Something went wrong", false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

    public String manualCheckApiResponse() {
    String responseBody = null;
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String url = "https://procedure.prozorro.sale/api/search/large_announcement/bySystemDateModified/2024-07-01";
                HttpGet request = new HttpGet(url);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    log.info("Successfully made request to {}", url);
                    responseBody = String.valueOf(response.getCode());
                    log.info("Response code is {}", responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return responseBody;
    }

    public String checkObjectCount() {
        String responseBody = null;
        int objectCount = 0;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = "https://procedure.prozorro.sale/api/search/bySystemDateModified/2022-01-01";
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                log.info("Successfully made request to {}", url);
                responseBody = new String(response.getEntity().getContent().readAllBytes());

                // Parse JSON response and count objects
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                objectCount = jsonNode.size();
                log.info("Object count is {}", objectCount);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return String.valueOf(objectCount);
    }
}
