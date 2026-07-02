package com.sonnh.elv.controller;

import com.sonnh.elv.dto.request.CreateProjectRequestDTO;
import com.sonnh.elv.dto.request.UpdateProjectRequestDTO;
import com.sonnh.elv.dto.response.PageImplResDto;
import com.sonnh.elv.dto.response.ProjectListResponseDTO;
import com.sonnh.elv.dto.response.ProjectResponseDTO;
import com.sonnh.elv.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Manage Projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Void> createProject(@RequestBody CreateProjectRequestDTO request) {
        projectService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<PageImplResDto<ProjectListResponseDTO>> getAllProjects(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(projectService.findAll(page, size, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(
            @PathVariable UUID id,
            @RequestBody UpdateProjectRequestDTO request
    ) {
        projectService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
