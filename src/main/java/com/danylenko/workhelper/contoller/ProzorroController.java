package com.danylenko.workhelper.contoller;

import com.danylenko.workhelper.dto.ObjectCountDto;
import com.danylenko.workhelper.service.ProzorroService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/prozorro")
@RequiredArgsConstructor
public class ProzorroController {
    private final ProzorroService prozorroService;

    @GetMapping("/all")
    public ResponseEntity<List<ObjectCountDto>> getProzzoroObjectCount(HttpServletRequest request) {
        log.info("getProzzoroObjectCount: ip: {}",request.getRemoteAddr());
        List<ObjectCountDto> allObjects = prozorroService.getAllObjectCountRecords();
        return new ResponseEntity<>(allObjects, HttpStatus.OK);
    }
}
