package com.sonnh.bookingcar.controller.driver;

import com.sonnh.bookingcar.dto.request.driver.DriverLocationReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LocationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/location")
    public void receiveLocation(DriverLocationReqDto dto) {
        log.info("IS SENDING LOCATION !!!!!");
        // push toàn bộ driver lên cho admin
        messagingTemplate.convertAndSend(
                "/topic/drivers",
                dto);
    }

    @MessageMapping("/location/singleDriver")
    public void receiveSingleDriverLocation(DriverLocationReqDto dto) {

        // chỉ push cho admin đang theo dõi driver này
        messagingTemplate.convertAndSend(
                "/topic/driver/" + dto.getDriverId(),
                dto);
    }
}
