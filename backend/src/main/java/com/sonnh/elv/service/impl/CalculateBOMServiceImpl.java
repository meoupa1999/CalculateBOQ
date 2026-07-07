package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.repository.ConfigRepository;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO.FloorRequest;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;
import com.sonnh.elv.service.CalculateBOMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateBOMServiceImpl implements CalculateBOMService {

    private final ConfigRepository configRepository;

    @Override
    public CalculateBOMResponseDTO calculateBOM(CalculateBOQRequestDTO dto) {
        List<CalculateBOMResponseDTO.FloorBOMInfo> floorInfos = new ArrayList<>();
        int totalCamera = 0;
        int totalCamDome = 0;
        int totalCamBullet = 0;
        int totalSw16 = 0;
        int totalSw24 = 0;
        int totalCabinet = 0;
        int totalUPS = 0;
        int totalPDU = 0;
        int totalConverter = 0;

        if (dto.getFloors() != null) {
            for (FloorRequest floor : dto.getFloors()) {
                int cams = floor.getCamerasCount() != null ? floor.getCamerasCount() : 0;
                int dome = floor.getDomeCount() != null ? floor.getDomeCount() : (int) Math.round(cams * 0.5);
                int bullet = floor.getBulletCount() != null ? floor.getBulletCount() : (cams - dome);

                totalCamera += cams;
                totalCamDome += dome;
                totalCamBullet += bullet;

                CalculateBOMResponseDTO.FloorBOMInfo floorInfo = CalculateBOMResponseDTO.FloorBOMInfo.builder()
                        .floorIndex(floor.getFloorIndex())
                        .label(floor.getLabel())
                        .isCabinetPlaced(false)
                        .camerasCount(cams)
                        .domeCount(dome)
                        .bulletCount(bullet)
                        .cameraQuantityInCabinet(0)
                        .sw24Count(0)
                        .sw16Count(0)
                        .upsCount(0)
                        .pduCount(0)
                        .convCount(0)
                        .build();
                floorInfos.add(floorInfo);
            }
        }

        return CalculateBOMResponseDTO.builder()
                .totalCamera(totalCamera)
                .totalCamDome(totalCamDome)
                .totalCamBullet(totalCamBullet)
                .totalSw16(totalSw16)
                .totalSw24(totalSw24)
                .totalSwichPOE(totalSw16 + totalSw24)
                .totalCabinet(totalCabinet)
                .totalUPS(totalUPS)
                .totalPDU(totalPDU)
                .totalConverter(totalConverter)
                .floors(floorInfos)
                .build();
    }
}
