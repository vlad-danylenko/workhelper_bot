package com.danylenko.workhelper.service;

import com.danylenko.workhelper.dto.ObjectCountDto;
import com.danylenko.workhelper.mapper.ObjectCountMapper;
import com.danylenko.workhelper.entity.ObjectCount;
import com.danylenko.workhelper.repository.ObjectCountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProzorroServiceImpl implements ProzorroService {

    @Autowired
    private MessageService messageService;
    private final ObjectCountRepository objectCountRepository;
    private final ObjectCountMapper objectCountMapper;

    private boolean isResponseSuccess = false;

//    public ProzorroServiceImpl(ObjectCountRepository objectCountRepository) {
//        this.objectCountRepository = objectCountRepository;
//    }

//    public void checkApiResponse() {
//        if (!isResponseSuccess) {
//            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//                String url = "https://procedure.prozorro.sale/api/search/large_announcement/bySystemDateModified/2024-07-01";
//                HttpGet request = new HttpGet(url);
//                try (CloseableHttpResponse response = httpClient.execute(request)) {
//                    log.info("Scheduler made request to {}", url);
//                    String responseBody = String.valueOf(response.getCode());
//                    log.info("Response code is {}", responseBody);
//                    if (responseBody.equals("204")) {
//                    } else if (responseBody.equals("200")) {
//                        messageService.sendMessage(85262401L, "Status 200 OK. Check the content. It's available now", false);
//                        isResponseSuccess = true;
//                    } else {
//                        log.error("Something went wrong");
//                        messageService.sendMessage(85262401L, "Something went wrong", false);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
    public void checkApiPayload() {
        if (!isResponseSuccess) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String url = "https://procedure.prozorro.sale/api/search/procedures";
                HttpPost request = new HttpPost(url);

                // JSON body
                String jsonBody = """
            {
                "page": 1,
                "limit": 10,
                "filters": [
                    {
                        "field": "sellingMethod",
                        "operator": "in",
                        "value": [
                            "largePrivatization-english"
                        ]
                    }
                ]
            }
            """;

                StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
                request.setEntity(entity);

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    log.info("Scheduler made request to {}", url);
                    String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                    log.info("Response body is {}", responseBody);
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonArray payload = jsonResponse.getAsJsonArray("payload");
                    if (payload.size() == 0) {
                        log.info("Payload empty: {}",payload);
                    } else if (payload.size() >= 1) {
                        //messageService.sendMessage(85262401L, "Status 200 OK. Check the content. It's available now", false);
                        log.info("Payload: {}",payload);
                        messageService.sendMessage(85262401L, "Check https://procedure.prozorro.sale/api/search/procedures\n" +
                                "\nPayload is present for largePrivatization-english", false);
                        isResponseSuccess = true;
                    } else {
                        log.error("Something went wrong");
                        messageService.sendMessage(137867872L, "Something went wrong", false);
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    public String manualCheckApiResponse() {
//        String responseBody = null;
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            String url = "https://procedure.prozorro.sale/api/search/large_announcement/bySystemDateModified/2024-07-01";
//            HttpGet request = new HttpGet(url);
//            try (CloseableHttpResponse response = httpClient.execute(request)) {
//                log.info("Successfully made request to {}", url);
//                responseBody = String.valueOf(response.getCode());
//                log.info("Response code is {}", responseBody);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return responseBody;
//    }
public String manualCheckApiPayload() {
        String responseBody = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = "https://procedure.prozorro.sale/api/search/procedures";
            HttpPost request = new HttpPost(url);

            // JSON body
            String jsonBody = """
            {
                "page": 1,
                "limit": 10,
                "filters": [
                    {
                        "field": "sellingMethod",
                        "operator": "in",
                        "value": [
                            "largePrivatization-english"
                        ]
                    }
                ]
            }
            """;

            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                log.info("Scheduler made request to {}", url);
                responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.info("Response body is {}", responseBody);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    return responseBody;
}

    public int checkObjectCountForDate(String date) {
        String responseBody = null;
        int objectCount = 0;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = "https://procedure.prozorro.sale/api/search/byDatePublished/" + date + "?limit=200";
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                log.info("Successfully made request to {}", url);
                responseBody = new String(response.getEntity().getContent().readAllBytes());

                // Parse JSON response and count objects
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                objectCount = jsonNode.size();
                log.info("Object count for date {} is {}", date, objectCount);
                objectCountRepository.save(ObjectCountMapper.INSTANCE.toModel(date, objectCount));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return objectCount;
    }

    public int getTotalObjectCount(List<String> dates) {
        int totalObjectCount = 0;
        Map<String, Integer> objectCountsMap = loadObjectCountsFromDatabase(dates);

        for (String date : dates) {
            if (objectCountsMap.containsKey(date)) {
                totalObjectCount += objectCountsMap.get(date);
            } else {
                totalObjectCount += checkObjectCountForDate(date);
            }
        }
        log.info("Total object count is {}", totalObjectCount);
        return totalObjectCount;
    }

    private Map<String, Integer> loadObjectCountsFromDatabase(List<String> dates) {
        List<ObjectCount> objectCountEntities = objectCountRepository.findByHdayIn(dates);

        return objectCountEntities.stream()
                .collect(Collectors.toMap(ObjectCount::getHday, ObjectCount::getCnt));
    }

    public List<ObjectCountDto> getAllObjectCountRecords() {
        var objects = IterableUtils.toList(objectCountRepository.findAll());
        return objects.stream().map(objectCountMapper.INSTANCE::toDto)
                .toList();
    }



}
