package com.sonnh.bookingcar.controller;

import com.sonnh.bookingcar.dto.request.auth.LoginReqDto;
import com.sonnh.bookingcar.dto.request.auth.TouristLoginReqDto;
import com.sonnh.bookingcar.dto.request.driver.DriverRegisterReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.dto.request.tourist.TouristRegisterReqDto;
import com.sonnh.bookingcar.dto.response.auth.AuthResDto;
import com.sonnh.bookingcar.security.JwtUtils;
import com.sonnh.bookingcar.service.interfaces.UserService;
import com.sonnh.bookingcar.security.MyUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils tokenProvider;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống (SĐT hoặc Username)")
    public ResponseEntity<AuthResDto> authenticateUser(@Valid @RequestBody LoginReqDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        MyUserDetails userPrincipal = (MyUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(AuthResDto.builder()
                .accessToken(jwt)
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                .build());
    }

    @PostMapping("/login/tourist")
    @Operation(summary = "Đăng nhập cho Tourist bằng Số điện thoại")
    public ResponseEntity<AuthResDto> authenticateTourist(@Valid @RequestBody TouristLoginReqDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getPhone(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        MyUserDetails userPrincipal = (MyUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(AuthResDto.builder()
                .accessToken(jwt)
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản Tourist")
    public ResponseEntity<String> registerTourist(@Valid @RequestBody TouristRegisterReqDto registerRequest) {
        userService.touristRegister(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping(value = "/register/driver", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Đăng ký tài khoản Driver (Chờ duyệt)")
    public ResponseEntity<String> registerDriver(
            @RequestPart("driver") String driverJson,
            @RequestPart(value = "driverImage", required = false) MultipartFile driverImage,
            @RequestPart(value = "idCardFront", required = false) MultipartFile idCardFront,
            @RequestPart(value = "idCardBack", required = false) MultipartFile idCardBack,
            @RequestPart(value = "licenseImage", required = false) MultipartFile licenseImage) throws Exception {
        DriverRegisterReqDto registerRequest = objectMapper.readValue(driverJson, DriverRegisterReqDto.class);
        userService.driverRegister(registerRequest, driverImage, idCardFront, idCardBack, licenseImage);
        return ResponseEntity.ok("Driver registration submitted successfully. Please wait for admin approval.");
    }
}
