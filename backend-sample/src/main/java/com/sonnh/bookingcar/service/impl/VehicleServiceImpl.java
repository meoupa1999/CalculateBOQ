package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.Image;
import com.sonnh.bookingcar.data.domain.Vehicle;
import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
import com.sonnh.bookingcar.data.repository.DocumentRepository;
import com.sonnh.bookingcar.data.repository.ImageRepository;
import com.sonnh.bookingcar.data.repository.VehicleRepository;
import com.sonnh.bookingcar.data.specification.VehicleSpecification;
import com.sonnh.bookingcar.dto.request.admin.VehicleCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.VehicleUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.VehicleDashboardDTO;
import com.sonnh.bookingcar.dto.response.admin.VehicleResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.VehicleMapper;
import com.sonnh.bookingcar.pattern.impl.DocumentDtoMapperFactory;
import com.sonnh.bookingcar.pattern.impl.DocumentEntityMapperFactory;
import com.sonnh.bookingcar.pattern.interfaces.DocumentDtoMapper;
import com.sonnh.bookingcar.service.interfaces.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final DocumentDtoMapperFactory documentDtoMapperFactory;
    private final DocumentEntityMapperFactory entityMapperFactory;
    private final DocumentRepository documentRepository;
    private final ImageRepository imageRepository;
    private final com.sonnh.bookingcar.data.repository.VehicleTypeRepository vehicleTypeRepository;


    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper,
            DocumentDtoMapperFactory documentDtoMapperFactory,
            DocumentEntityMapperFactory entityMapperFactory, DocumentRepository documentRepository,
            ImageRepository imageRepository,
            com.sonnh.bookingcar.data.repository.VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.documentDtoMapperFactory = documentDtoMapperFactory;
        this.entityMapperFactory = entityMapperFactory;
        this.documentRepository = documentRepository;
        this.imageRepository = imageRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }


    @Override
    public PageImplResDto<VehicleResDto> getAllVehicles(Integer page, Integer size, String search, Boolean missingDocuments) {
        Pageable pageable = PageRequest.of(
            page != null && page > 0 ? page - 1 : 0,
            size != null && size > 0 ? size : 10,
            Sort.by(Sort.Direction.DESC, "audit.updatedAt")
        );
        
        Specification<Vehicle> spec = VehicleSpecification.isActive();
        if (search != null && !search.isEmpty()) {
            spec = spec.and(VehicleSpecification.search(search));
        }

        if (Boolean.TRUE.equals(missingDocuments)) {
            spec = spec.and(VehicleSpecification.hasMissingDocuments());
        }
        
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);
        List<VehicleResDto> vehicles = vehiclePage.getContent().stream()
                .map(this::mapToResDto)
                .collect(Collectors.toList());
        
        return PageImplResDto.<VehicleResDto>builder()
                .content(vehicles)
                .pageNumber(vehiclePage.getNumber() + 1)
                .pageSize(vehiclePage.getSize())
                .totalElements(vehiclePage.getTotalElements())
                .totalPages(vehiclePage.getTotalPages())
                .last(vehiclePage.isLast())
                .build();
    }

    private VehicleResDto mapToResDto(Vehicle vehicle) {
        VehicleResDto dto = vehicleMapper.toVehicleResDto(vehicle);
        
        
        for (Document doc : vehicle.getDocuments()) {
            DocumentDtoMapper mapper = documentDtoMapperFactory.getMapper(doc.getDocumentType());
            if (mapper != null) {
                mapper.map(dto, doc);
            }
        }

        
        // Map main vehicle image
        vehicle.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.VEHICLE_FRONT)
                .findFirst()
                .ifPresent(img -> dto.setVehicleImage(img.getFilePath()));
                
        return dto;
    }

    @Override
    public VehicleResDto getById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .filter(v -> v.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found or inactive with id: " + id));
        
        return mapToResDto(vehicle);
    }

    @Override
    public VehicleDashboardDTO getVehicleDashboard() {
        return VehicleDashboardDTO.builder()
                .totalVehicles(vehicleRepository.countByStatus(null))
                .availableVehicles(vehicleRepository.countByStatus(VehicleStatus.AVAILABLE))
                .busyVehicles(vehicleRepository.countByStatus(VehicleStatus.BUSY))
                .maintenanceVehicles(vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE))
                .build();
    }

    @Override
    @Transactional
    public VehicleResDto create(VehicleCreateReqDto dto, MultipartFile insuranceImage, MultipartFile registrationImage, MultipartFile badgeImage, MultipartFile vehicleImage) {
        Vehicle vehicle = vehicleMapper.toVehicle(dto);
        
        if (dto.getVehicleType() != null) {
            com.sonnh.bookingcar.data.domain.VehicleType vt = vehicleTypeRepository.findById(dto.getVehicleType())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle type not found"));
            vehicle.addVehicleType(vt);
        }
        
        vehicleRepository.save(vehicle); // Now vehicle is managed
        
        createDocument(vehicle, dto.getMandatoryInsurance(), DocumentType.MANDATORY_INSURANCE, ImageType.MANDATORY_INSURANCE_FRONT, "insurance_" + vehicle.getPlateNumber(), insuranceImage);
        createDocument(vehicle, dto.getRegistration(), DocumentType.REGISTRATION, ImageType.REGISTRATION_FRONT, "registration_" + vehicle.getPlateNumber(), registrationImage);
        createDocument(vehicle, dto.getBadge(), DocumentType.BADGE, ImageType.BADGE_FRONT, "badge_" + vehicle.getPlateNumber(), badgeImage);
        saveOrUpdateImage(vehicle.getImages(), ImageType.VEHICLE_FRONT, vehicleImage, "vehicle_" + vehicle.getPlateNumber(), img -> img.addVehicle(vehicle));
        
        return mapToResDto(vehicle);
    }

    @Override
    @Transactional
    public VehicleResDto update(UUID id, VehicleUpdateReqDto dto, MultipartFile insuranceImage, MultipartFile registrationImage, MultipartFile badgeImage, MultipartFile vehicleImage) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .filter(v -> v.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found or inactive with id: " + id));

        vehicleMapper.updateVehicleFromDto(dto, vehicle);
        
        if (dto.getVehicleType() != null) {
            com.sonnh.bookingcar.data.domain.VehicleType vt = vehicleTypeRepository.findById(dto.getVehicleType())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle type not found"));
            vehicle.addVehicleType(vt);
        }
        
        vehicleRepository.save(vehicle); // Synchronize state first
        
        updateDocument(vehicle, dto.getMandatoryInsurance(), DocumentType.MANDATORY_INSURANCE, ImageType.MANDATORY_INSURANCE_FRONT, "insurance_" + vehicle.getPlateNumber(), insuranceImage);
        updateDocument(vehicle, dto.getRegistration(), DocumentType.REGISTRATION, ImageType.REGISTRATION_FRONT, "registration_" + vehicle.getPlateNumber(), registrationImage);
        updateDocument(vehicle, dto.getBadge(), DocumentType.BADGE, ImageType.BADGE_FRONT, "badge_" + vehicle.getPlateNumber(), badgeImage);
        saveOrUpdateImage(vehicle.getImages(), ImageType.VEHICLE_FRONT, vehicleImage, "vehicle_" + vehicle.getPlateNumber(), img -> img.addVehicle(vehicle));
        
        return mapToResDto(vehicle);
    }

    private void createDocument(Vehicle vehicle, Object dto, DocumentType docType, ImageType imgType, String imgPrefix, MultipartFile image) {
        if (dto != null) {
            Document doc = Document.builder()
                    .documentType(docType)
                    .ownerType(DocumentOwnerType.VEHICLE)
                    .build();
            doc.addVehicle(vehicle);
            entityMapperFactory.getMapper(docType).mapToEntity(doc, dto);
            documentRepository.save(doc); // Must save transient object
            
            saveOrUpdateImage(doc.getImages(), imgType, image, imgPrefix, img -> img.addDocument(doc));
        }
    }

    private void updateDocument(Vehicle vehicle, Object dto, DocumentType docType, ImageType imgType, String imgPrefix, MultipartFile image) {
        if (dto != null) {
            vehicle.getDocuments().stream()
                    .filter(d -> d.getDocumentType() == docType)
                    .findFirst()
                    .ifPresentOrElse(
                        doc -> {
                            entityMapperFactory.getMapper(docType).mapToEntity(doc, dto);
                            documentRepository.save(doc);
                            saveOrUpdateImage(doc.getImages(), imgType, image, imgPrefix, img -> img.addDocument(doc));
                        },
                        () -> {
                            Document newDoc = Document.builder()
                                    .documentType(docType)
                                    .ownerType(DocumentOwnerType.VEHICLE)
                                    .build();
                            newDoc.addVehicle(vehicle);
                            entityMapperFactory.getMapper(docType).mapToEntity(newDoc, dto);
                            documentRepository.save(newDoc);
                            saveOrUpdateImage(newDoc.getImages(), imgType, image, imgPrefix, img -> img.addDocument(newDoc));
                        }
                    );
        }
    }

    private void saveOrUpdateImage(List<Image> images, ImageType type, MultipartFile file, String prefix, Consumer<Image> ownerSetter) {
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, prefix);
            final boolean[] isNew = {false};
            Image img = images.stream()
                    .filter(i -> i.getImageType() == type)
                    .findFirst()
                    .orElseGet(() -> {
                        isNew[0] = true;
                        Image newImg = new Image();
                        newImg.setImageType(type);
                        ownerSetter.accept(newImg);
                        return newImg;
                    });
            img.setFilePath(fileName);
            if (isNew[0]) {
                imageRepository.save(img); // Save if transient (new)
            }
        }
    }

    private String saveFile(MultipartFile file, String prefix) {
        try {
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : ".jpg";
            String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Path uploadPath = Paths.get("images/vehicle");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not save file", e);
        }
    }

    @Override
    public void delete(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .filter(v -> v.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found or inactive with id: " + id));
        
        vehicle.getAudit().setIsActive(false);
        vehicleRepository.save(vehicle);
    }
}
