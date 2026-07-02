package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.Image;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.Vehicle;
import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentStatus;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.data.repository.DocumentRepository;
import com.sonnh.bookingcar.data.repository.ImageRepository;
import com.sonnh.bookingcar.data.repository.UserRepository;
import com.sonnh.bookingcar.data.repository.VehicleRepository;
import com.sonnh.bookingcar.data.specification.DocumentSpecification;
import com.sonnh.bookingcar.dto.request.admin.DocumentCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DocumentUpdateReqDto;
import com.sonnh.bookingcar.dto.response.DocumentDashboardResponse;
import com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto;
import com.sonnh.bookingcar.dto.response.DocumentListResponseDto;
import com.sonnh.bookingcar.dto.response.DocumentMinDto;
import com.sonnh.bookingcar.dto.response.DriverDropdownResDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.VehicleDropdownResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.DocumentMapper;
import com.sonnh.bookingcar.pattern.impl.DetailMetadataHandlerFactory;
import com.sonnh.bookingcar.pattern.impl.DocumentEntityMapperFactory;
import com.sonnh.bookingcar.pattern.interfaces.DocumentEntityMapper;
import com.sonnh.bookingcar.service.interfaces.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final DocumentMapper documentMapper;
    private final DetailMetadataHandlerFactory detailMetadataHandlerFactory;
    private final DocumentEntityMapperFactory entityMapperFactory;
    private final ImageRepository imageRepository;

    private final Integer numberDocumentOfDriver = 2;
    private final Integer numberDocumentOfVehicles = 3;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    @Transactional(readOnly = true)
    public PageImplResDto<DocumentListResponseDto> getDocuments(
            DocumentOwnerType ownerType,
            DocumentType documentType,
            DocumentStatus status,
            String searchKeyword,
            Integer page,
            Integer size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("expiredDate").ascending());

        Specification<Document> spec = Specification.where(DocumentSpecification.hasOwnerType(ownerType))
                .and(DocumentSpecification.hasDocumentType(documentType))
                .and(DocumentSpecification.hasSearchKeyword(searchKeyword))
                .and(DocumentSpecification.hasStatus(status));

        Page<Document> documentPage = documentRepository.findAll(spec, pageRequest);
        Page<DocumentListResponseDto> responsePage = documentPage.map(doc -> {
            DocumentListResponseDto response = documentMapper.toListResponse(doc);
            response.setStatus(calculateStatus(doc.getExpiredDate()));
            return response;
        });

        return PageImplResDto.fromPage(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDashboardResponse getDashboard(DocumentOwnerType ownerType) {
        Specification<Document> spec = DocumentSpecification.hasOwnerType(ownerType);
        List<Document> allDocs = documentRepository.findAll(spec);

        LocalDate now = LocalDate.now();

        long total = allDocs.size();
        long expiredCount = allDocs.stream()
                .filter(doc -> doc.getExpiredDate() != null)
                .filter(doc -> ChronoUnit.DAYS.between(now, doc.getExpiredDate()) < 0)
                .count();

        long criticalCount = allDocs.stream()
                .filter(doc -> doc.getExpiredDate() != null)
                .filter(doc -> {
                    long days = ChronoUnit.DAYS.between(now, doc.getExpiredDate());
                    return days >= 0 && days <= 7;
                })
                .count();

        long warningCount = allDocs.stream()
                .filter(doc -> doc.getExpiredDate() != null)
                .filter(doc -> {
                    long days = ChronoUnit.DAYS.between(now, doc.getExpiredDate());
                    return days > 7 && days <= 90;
                })
                .count();

        return DocumentDashboardResponse.builder()
                .total(total)
                .warningCount(warningCount)
                .criticalCount(criticalCount)
                .expiredCount(expiredCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDetailResponseDto getDocumentById(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        DocumentDetailResponseDto detail = documentMapper.toDetailResponse(document);
        detail.setStatus(calculateStatus(document.getExpiredDate()));
        detail.setMetadata(detailMetadataHandlerFactory.parseMetadata(document.getDocumentType(), document));
        return detail;
    }

    @Override
    @Transactional
    public DocumentDetailResponseDto createDocument(DocumentCreateReqDto dto, MultipartFile frontImage,
            MultipartFile backImage) {
        Document document = Document.builder()
                .ownerType(dto.getOwnerType())
                .documentType(dto.getDocumentType())
                .documentNumber(dto.getDocumentNumber())
                .issuedDate(dto.getIssuedDate())
                .expiredDate(dto.getExpiredDate())
                .issuedPlace(dto.getIssuedPlace())
                .build();

        // Direct Owner Linking (if-else)
        if (dto.getOwnerType() == DocumentOwnerType.DRIVER) {
            User user = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + dto.getOwnerId()));
            document.addUser(user);
        } else if (dto.getOwnerType() == DocumentOwnerType.VEHICLE) {
            Vehicle vehicle = vehicleRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + dto.getOwnerId()));
            document.addVehicle(vehicle);
        }

        // Map Metadata using Strategy Pattern
        Object metadataSource = getMetadataSource(dto);
        if (metadataSource != null) {
            DocumentEntityMapper mapper = entityMapperFactory.getMapper(dto.getDocumentType());
            if (mapper != null) {
                mapper.mapToEntity(document, metadataSource);
            }
        }

        Document savedDoc = documentRepository.save(document);

        // Process Images
        processDocumentImages(savedDoc, frontImage, backImage);

        return getDocumentById(savedDoc.getId());
    }

    @Override
    @Transactional
    public DocumentDetailResponseDto updateDocument(UUID id, DocumentUpdateReqDto dto, MultipartFile frontImage,
            MultipartFile backImage) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        if (dto.getDocumentNumber() != null)
            document.setDocumentNumber(dto.getDocumentNumber());
        if (dto.getIssuedDate() != null)
            document.setIssuedDate(dto.getIssuedDate());
        if (dto.getExpiredDate() != null)
            document.setExpiredDate(dto.getExpiredDate());
        if (dto.getIssuedPlace() != null)
            document.setIssuedPlace(dto.getIssuedPlace());

        // Map Metadata using Strategy Pattern
        Object metadataSource = getMetadataSource(dto);
        if (metadataSource != null) {
            DocumentEntityMapper mapper = entityMapperFactory.getMapper(document.getDocumentType());
            if (mapper != null) {
                mapper.mapToEntity(document, metadataSource);
            }
        }

        Document savedDoc = documentRepository.save(document);

        // Process Images
        processDocumentImages(savedDoc, frontImage, backImage);

        return getDocumentById(savedDoc.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverDropdownResDto> getDriverDropdown() {
        return userRepository.findAllActiveDriversWithDocuments().stream()
                .filter(user -> user.getDocuments().size() < numberDocumentOfDriver)
                .map(user -> DriverDropdownResDto.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .documents(user.getDocuments().stream()
                                .map(doc -> DocumentMinDto.builder()
                                        .documentId(doc.getId())
                                        .documentType(doc.getDocumentType())
                                        .build())
                                .collect(java.util.stream.Collectors.toList()))
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDropdownResDto> getVehicleDropdown() {
        return vehicleRepository.findAllActiveVehiclesWithDocuments().stream()
                .filter(vehicle -> vehicle.getDocuments().size() < numberDocumentOfVehicles)
                .map(vehicle -> VehicleDropdownResDto.builder()
                        .id(vehicle.getId())
                        .plateNumber(vehicle.getPlateNumber())
                        .documents(vehicle.getDocuments().stream()
                                .map(doc -> DocumentMinDto.builder()
                                        .documentId(doc.getId())
                                        .documentType(doc.getDocumentType())
                                        .build())
                                .collect(java.util.stream.Collectors.toList()))
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    private void processDocumentImages(Document document, MultipartFile frontImage, MultipartFile backImage) {
        String prefix = document.getDocumentType().name().toLowerCase() + "_"
                + document.getId().toString().substring(0, 8);

        ImageType frontType;
        ImageType backType = null;

        switch (document.getDocumentType()) {
            case ID_CARD -> {
                frontType = ImageType.ID_CARD_FRONT;
                backType = ImageType.ID_CARD_BACK;
            }
            case LICENSE -> {
                frontType = ImageType.LICENSE_FRONT;
                backType = ImageType.LICENSE_BACK;
            }
            case REGISTRATION -> {
                frontType = ImageType.REGISTRATION_FRONT;
                backType = ImageType.REGISTRATION_BACK;
            }
            case MANDATORY_INSURANCE -> frontType = ImageType.MANDATORY_INSURANCE_FRONT;
            case BADGE -> frontType = ImageType.BADGE_FRONT;
            default -> frontType = ImageType.OTHER;
        }

        updateOrSaveImage(document, frontImage, frontType, prefix + "_front");
        if (backType != null) {
            updateOrSaveImage(document, backImage, backType, prefix + "_back");
        }
    }

    private void updateOrSaveImage(Document doc, MultipartFile file, ImageType type, String prefix) {
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, prefix);
            imageRepository.findByDocumentIdAndImageType(doc.getId(), type)
                    .ifPresentOrElse(
                            img -> {
                                img.setFilePath(fileName);
                                imageRepository.save(img);
                            },
                            () -> {
                                Image newImg = Image.builder()
                                        .imageType(type)
                                        .filePath(fileName)
                                        .build();
                                newImg.addDocument(doc);
                                imageRepository.save(newImg);
                            });
        }
    }

    private String saveFile(MultipartFile file, String prefix) {
        try {
            Path baseDir = Paths.get(uploadPath, "document");
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
            }

            String fileName = prefix + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = baseDir.resolve(fileName);
            Files.write(path, file.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not save file", e);
        }
    }

    private Object getMetadataSource(DocumentCreateReqDto dto) {
        if (dto.getDocumentType() == null)
            return null;
        if (dto.getDocumentType() == DocumentType.ID_CARD)
            return dto.getIdCardMetadata();
        if (dto.getDocumentType() == DocumentType.LICENSE)
            return dto.getLicenseMetadata();
        if (dto.getDocumentType() == DocumentType.REGISTRATION)
            return dto.getRegistrationMetadata();
        if (dto.getDocumentType() == DocumentType.MANDATORY_INSURANCE)
            return dto.getInsuranceMetadata();
        if (dto.getDocumentType() == DocumentType.BADGE)
            return dto.getBadgeMetadata();
        return null;
    }

    private Object getMetadataSource(DocumentUpdateReqDto dto) {
        if (dto.getIdCardMetadata() != null)
            return dto.getIdCardMetadata();
        if (dto.getLicenseMetadata() != null)
            return dto.getLicenseMetadata();
        if (dto.getRegistrationMetadata() != null)
            return dto.getRegistrationMetadata();
        if (dto.getInsuranceMetadata() != null)
            return dto.getInsuranceMetadata();
        if (dto.getBadgeMetadata() != null)
            return dto.getBadgeMetadata();
        return null;
    }

    private DocumentStatus calculateStatus(LocalDate expiredDate) {
        if (expiredDate == null) {
            return DocumentStatus.VALID;
        }
        LocalDate now = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(now, expiredDate);

        if (daysRemaining < 0) {
            return DocumentStatus.EXPIRED;
        } else if (daysRemaining <= 7) {
            return DocumentStatus.CRITICAL_7D;
        } else if (daysRemaining <= 30) {
            return DocumentStatus.WARNING_1M;
        } else if (daysRemaining <= 90) {
            return DocumentStatus.WARNING_3M;
        }
        return DocumentStatus.VALID;
    }
}
