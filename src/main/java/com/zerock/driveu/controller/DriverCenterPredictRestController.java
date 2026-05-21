package com.zerock.driveu.controller;

import com.zerock.driveu.dto.DriverCenterMapResponseDTO;
import com.zerock.driveu.dto.DriverLicenseExamPredictResponseDTO;
import com.zerock.driveu.dto.DriverLicenseIssuePredictResponseDTO;
import com.zerock.driveu.service.DriverCenterMapService;
import com.zerock.driveu.service.DriverLicenseExamStatsService;
import com.zerock.driveu.service.DriverLicenseIssueStatsService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
@Log4j2
public class DriverCenterPredictRestController {

    private final DriverLicenseExamStatsService examService;
    private final DriverLicenseIssueStatsService issueService;
    private final DriverCenterMapService mapService;

    @GetMapping("/centerMap")
    public ResponseEntity<List<DriverCenterMapResponseDTO>> getCenterByGroup (@RequestParam("cityGroup") String cityGroup) {

        List <DriverCenterMapResponseDTO> centers = mapService.getCentersByGroup(cityGroup);

        return ResponseEntity.ok(centers);

    }

    @GetMapping("/centerStatus")
    public ResponseEntity <?> getIssuePrediction (@RequestParam("testCenterName") String testCenterName, @RequestParam("month") int month, @RequestParam("day") int day, @RequestParam("hour") int hour) {

        DriverLicenseExamPredictResponseDTO examResult = examService.predictCurrentExamStatus(testCenterName, month, day, hour);
        DriverLicenseIssuePredictResponseDTO issueResult = issueService.predictCurrentIssueStatus(testCenterName, month, day, hour);

        return ResponseEntity.ok(Map.of(

                "exam", examResult,
                "issue", issueResult

        ));

    }

}
