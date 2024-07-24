package com.danylenko.workhelper.service;

import com.danylenko.workhelper.dto.ObjectCountDto;

import java.util.List;

public interface ProzorroService {

    //void checkApiResponse();
    void checkApiPayload();
    // String manualCheckApiResponse();
    String manualCheckApiPayload();
    int checkObjectCountForDate(String data);
    int getTotalObjectCount(List<String> dates);
    List<ObjectCountDto> getAllObjectCountRecords();

}
