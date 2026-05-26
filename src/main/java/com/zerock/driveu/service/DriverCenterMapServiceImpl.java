package com.zerock.driveu.service;

import com.zerock.driveu.dto.DriverCenterMapResponseDTO;
import com.zerock.driveu.repository.DriverLicenseExamStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DriverCenterMapServiceImpl implements DriverCenterMapService {

    private final DriverLicenseExamStatsRepository mapRepository;

    // 행정구역 매핑
    private static final Map<String, String> CENTER_GROUP_MAP = new HashMap<>();

    static {

        // 수도권
        CENTER_GROUP_MAP.put("강남", "수도권");
        CENTER_GROUP_MAP.put("도봉", "수도권");
        CENTER_GROUP_MAP.put("강서", "수도권");
        CENTER_GROUP_MAP.put("서부", "수도권");
        CENTER_GROUP_MAP.put("인천", "수도권");
        CENTER_GROUP_MAP.put("용인", "수도권");
        CENTER_GROUP_MAP.put("안산", "수도권");
        CENTER_GROUP_MAP.put("의정부", "수도권");

        // 대전충남
        CENTER_GROUP_MAP.put("대전", "대전•충남");
        CENTER_GROUP_MAP.put("예산", "대전•충남");

        // 충북
        CENTER_GROUP_MAP.put("청주", "충북");
        CENTER_GROUP_MAP.put("충주", "충북");

        // 강원
        CENTER_GROUP_MAP.put("춘천", "강원");
        CENTER_GROUP_MAP.put("강릉", "강원");
        CENTER_GROUP_MAP.put("원주", "강원");
        CENTER_GROUP_MAP.put("태백", "강원");

        // 대구경북
        CENTER_GROUP_MAP.put("대구", "대구•경북");
        CENTER_GROUP_MAP.put("문경", "대구•경북");
        CENTER_GROUP_MAP.put("포항", "대구•경북");

        // 전북
        CENTER_GROUP_MAP.put("전북", "전북");

        // 부산울산경남
        CENTER_GROUP_MAP.put("부산북부", "부산•울산•경남");
        CENTER_GROUP_MAP.put("부산남부", "부산•울산•경남");
        CENTER_GROUP_MAP.put("울산", "부산•울산•경남");
        CENTER_GROUP_MAP.put("마산", "부산•울산•경남");

        // 광주전남
        CENTER_GROUP_MAP.put("전남", "광주•전남");
        CENTER_GROUP_MAP.put("광양", "광주•전남");

        // 제주
        CENTER_GROUP_MAP.put("제주", "제주");

    }

    @Override
    public List<DriverCenterMapResponseDTO> getCentersByGroup (String cityGroup) {

        List <String> allCenter = mapRepository.findTestCenters();

        return allCenter.stream()
                .map(String::trim)
                .filter(center -> {

                    String mappedRegion = CENTER_GROUP_MAP.getOrDefault(center, "기타");
                    return mappedRegion.equals(cityGroup);

                })
                .map(center -> DriverCenterMapResponseDTO.builder()
                        .testCenterName(center)
                        .cityGroup(cityGroup)
                        .build())
                .toList();

    }

    @Override
    public String getCityGroup(String testCenterName) {

        return CENTER_GROUP_MAP.getOrDefault(testCenterName.trim(), "기타");

    }

}
