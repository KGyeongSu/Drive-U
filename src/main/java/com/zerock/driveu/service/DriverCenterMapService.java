package com.zerock.driveu.service;

import com.zerock.driveu.dto.DriverCenterMapResponseDTO;

import java.util.List;

public interface DriverCenterMapService {

    // 행정구역 매핑
    List<DriverCenterMapResponseDTO> getCentersByGroup (String cityGroup);

    // 가중치 분기 목적
    String getCityGroup(String testCenterName);

}
