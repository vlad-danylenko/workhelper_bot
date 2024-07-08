package com.danylenko.workhelper.service;

import java.util.List;

public interface ProzorroService {

    void checkApiResponse();
    String manualCheckApiResponse();
    int checkObjectCountForDate(String data);
    int getTotalObjectCount(List<String> dates);

}
