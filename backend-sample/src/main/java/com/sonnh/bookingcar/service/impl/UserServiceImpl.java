package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.Image;
import com.sonnh.bookingcar.data.domain.Role;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.enums.DriverStatus;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.data.repository.DocumentRepository;
import com.sonnh.bookingcar.data.repository.ImageRepository;
import com.sonnh.bookingcar.data.repository.RoleRepository;
import com.sonnh.bookingcar.data.repository.UserRepository;
import com.sonnh.bookingcar.data.specification.UserSpecification;
import com.sonnh.bookingcar.dto.request.admin.DriverCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DriverUpdateReqDto;
import com.sonnh.bookingcar.dto.request.driver.DriverRegisterReqDto;
import com.sonnh.bookingcar.dto.request.tourist.TouristRegisterReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.DriverResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.UserEntityMapper;
import com.sonnh.bookingcar.pattern.impl.DocumentDtoMapperFactory;
import com.sonnh.bookingcar.pattern.impl.DocumentEntityMapperFactory;
import com.sonnh.bookingcar.pattern.interfaces.DocumentDtoMapper;
import com.sonnh.bookingcar.service.interfaces.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserEntityMapper userEntityMapper;
    private final DocumentDtoMapperFactory documentDtoMapperFactory;
    private final DocumentEntityMapperFactory entityMapperFactory;
    private final DocumentRepository documentRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            UserEntityMapper userEntityMapper,
            DocumentDtoMapperFactory documentDtoMapperFactory, DocumentEntityMapperFactory entityMapperFactory,
            DocumentRepository documentRepository, ImageRepository imageRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userEntityMapper = userEntityMapper;
        this.documentDtoMapperFactory = documentDtoMapperFactory;
        this.entityMapperFactory = entityMapperFactory;
        this.documentRepository = documentRepository;
        this.imageRepository = imageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageImplResDto<DriverResDto> getAllDrivers(Integer page, Integer size, String search) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("audit.updatedAt").descending());
        Specification<User> spec = UserSpecification.isActive()
                .and(UserSpecification.hasRoleName("DRIVER"))
                .and(UserSpecification.hasDriverStatus(List.of(DriverStatus.AVAILABLE, DriverStatus.BUSY)));

        if (search != null && !search.isEmpty()) {
            spec = spec.and(UserSpecification.search(search));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<DriverResDto> drivers = userPage.getContent().stream()
                .map(user -> {
                    DriverResDto dto = userEntityMapper.toDriverResDto(user);
                    user.getImages().stream()
                            .filter(img -> img.getImageType() == ImageType.PROFILE_IMAGE)
                            .findFirst()
                            .ifPresent(img -> dto.setProfileImage(img.getFilePath()));
                    return dto;
                })
                .collect(Collectors.toList());

        return PageImplResDto.<DriverResDto>builder()
                .content(drivers)
                .pageNumber(userPage.getNumber() + 1)
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Override
    public DriverResDto getDriverById(UUID id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getAudit().getIsActive())
                .filter(u -> u.getRole().getName().equals("DRIVER"))
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found or inactive with id: " + id));

        DriverResDto driverResDto = userEntityMapper.toDriverResDto(user);

        // Map Profile Image
        user.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.PROFILE_IMAGE)
                .findFirst()
                .ifPresent(img -> driverResDto.setProfileImage(img.getFilePath()));

        for (Document doc : user.getDocuments()) {
            DocumentDtoMapper mapper = documentDtoMapperFactory.getMapper(doc.getDocumentType());
            if (mapper != null) {
                mapper.map(driverResDto, doc);
            }
        }

        return driverResDto;
    }

    @Override
    @Transactional(readOnly = true)
    public PageImplResDto<DriverResDto> getDriverRegistrations(Integer page, Integer size, String search) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("audit.createdAt").descending());
        Specification<User> spec = UserSpecification.isActive()
                .and(UserSpecification.hasRoleName("DRIVER"))
                .and(UserSpecification.hasDriverStatus(List.of(DriverStatus.PENDING_APPROVAL, DriverStatus.REJECTED)));

        if (search != null && !search.isEmpty()) {
            spec = spec.and(UserSpecification.search(search));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<DriverResDto> drivers = userPage.getContent().stream()
                .map(user -> {
                    DriverResDto dto = userEntityMapper.toDriverResDto(user);
                    user.getImages().stream()
                            .filter(img -> img.getImageType() == ImageType.PROFILE_IMAGE)
                            .findFirst()
                            .ifPresent(img -> dto.setProfileImage(img.getFilePath()));
                    return dto;
                })
                .collect(Collectors.toList());

        return PageImplResDto.<DriverResDto>builder()
                .content(drivers)
                .pageNumber(userPage.getNumber() + 1)
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void approveDriver(UUID id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getAudit().getIsActive())
                .filter(u -> u.getRole().getName().equals("DRIVER"))
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        user.setDriverStatus(DriverStatus.AVAILABLE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void rejectDriver(UUID id, String reason) {
        User user = userRepository.findById(id)
                .filter(u -> u.getAudit().getIsActive())
                .filter(u -> u.getRole().getName().equals("DRIVER"))
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        user.setDriverStatus(DriverStatus.REJECTED);
        user.setNotes(reason);
        user.getAudit().setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public DriverResDto createDriver(DriverCreateReqDto dto, MultipartFile driverImage, MultipartFile idCardFront,
            MultipartFile idCardBack, MultipartFile licenseImage) {
        userRepository.findByUsernameOrPhone(dto.getUsername(), dto.getPhone())
                .ifPresent(u -> {
                    throw new RuntimeException("User with username or phone already exists");
                });

        Role driverRole = roleRepository.findByName("DRIVER")
                .orElseThrow(() -> new ResourceNotFoundException("Role DRIVER not found"));

        User user = userEntityMapper.toUser(dto);
        user.addRole(driverRole);
        user.setDriverStatus(DriverStatus.AVAILABLE);
        user.setDriverRating(5.0); // Initial rating
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user first to make it managed
        User savedUser = userRepository.save(user);

        // Save driver profile image
        if (driverImage != null && !driverImage.isEmpty()) {
            String fileName = saveFile(driverImage, "driver_" + savedUser.getUsername());
            Image profileImg = Image.builder()
                    .filePath(fileName)
                    .imageType(ImageType.PROFILE_IMAGE)
                    .user(savedUser)
                    .build();
            savedUser.getImages().add(profileImg);
            imageRepository.save(profileImg); // Must save transient image
        }

        // Handle initial documents
        createDocuments(savedUser, dto.getIdCard(), dto.getLicense(), idCardFront, idCardBack, licenseImage);
        return userEntityMapper.toDriverResDto(savedUser);
    }

    @Override
    @Transactional
    public DriverResDto driverRegister(DriverRegisterReqDto dto, MultipartFile driverImage, MultipartFile idCardFront,
            MultipartFile idCardBack, MultipartFile licenseImage) {
        userRepository.findByUsernameOrPhone(dto.getUsername(), dto.getPhone())
                .ifPresent(u -> {
                    System.out.println("Conflict found: Existing user [" + u.getUsername() + "] with phone ["
                            + u.getPhone() + "]");
                    throw new RuntimeException("User with username [" + u.getUsername() + "] or phone [" + u.getPhone()
                            + "] already exists");
                });

        Role driverRole = roleRepository.findByName("DRIVER")
                .orElseThrow(() -> new ResourceNotFoundException("Role DRIVER not found"));

        User user = userEntityMapper.toUser(dto);
        user.addRole(driverRole);
        user.setDriverStatus(DriverStatus.PENDING_APPROVAL);
        user.setDriverRating(5.0); // Initial rating
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user first to make it managed
        User savedUser = userRepository.save(user);

        // Save driver profile image
        if (driverImage != null && !driverImage.isEmpty()) {
            String fileName = saveFile(driverImage, "driver_" + savedUser.getUsername());
            Image profileImg = Image.builder()
                    .filePath(fileName)
                    .imageType(ImageType.PROFILE_IMAGE)
                    .user(savedUser)
                    .build();
            savedUser.getImages().add(profileImg);
            imageRepository.save(profileImg);
        }

        // Handle initial documents
        createDocuments(savedUser, dto.getIdCard(), dto.getLicense(), idCardFront, idCardBack, licenseImage);

        return userEntityMapper.toDriverResDto(savedUser);
    }

    private String saveFile(MultipartFile file, String prefix) {
        try {
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : ".jpg";
            String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Path uploadPath = Paths.get("images/driver");
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
    @Transactional
    public DriverResDto updateDriver(UUID id, DriverUpdateReqDto dto, MultipartFile driverImage,
            MultipartFile idCardFront,
            MultipartFile idCardBack, MultipartFile licenseImage) {
        User user = userRepository.findById(id)
                .filter(u -> u.getAudit().getIsActive())
                .filter(u -> u.getRole().getName().equals("DRIVER"))
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found or inactive with id: " + id));

        userEntityMapper.updateUserFromDto(dto, user);

        // Handle password update if provided
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Save driver profile image
        if (driverImage != null && !driverImage.isEmpty()) {
            String fileName = saveFile(driverImage, "driver_" + user.getUsername());

            // Find existing profile image and update it, or create a new one
            user.getImages().stream()
                    .filter(img -> img.getImageType() == ImageType.PROFILE_IMAGE)
                    .findFirst()
                    .ifPresentOrElse(
                            profileImg -> {
                                profileImg.setFilePath(fileName);
                                imageRepository.save(profileImg);
                            },
                            () -> {
                                Image newImg = new Image();
                                newImg.setImageType(ImageType.PROFILE_IMAGE);
                                newImg.setUser(user);
                                newImg.setFilePath(fileName);
                                user.getImages().add(newImg);
                                imageRepository.save(newImg);
                            });
        }

        // Handle document updates
        updateDocuments(user, dto.getIdCard(), dto.getLicense(), idCardFront, idCardBack, licenseImage);

        User savedUser = userRepository.save(user);
        return userEntityMapper.toDriverResDto(savedUser);
    }

    private void createDocuments(User user, DriverCreateReqDto.IdCardReqDto idCardDto,
            DriverCreateReqDto.LicenseReqDto licenseDto, MultipartFile idCardFront, MultipartFile idCardBack,
            MultipartFile licenseImage) {
        if (idCardDto != null) {
            Document idCard = Document.builder()
                    .documentType(DocumentType.ID_CARD)
                    .ownerType(DocumentOwnerType.DRIVER)
                    .build();
            idCard.addUser(user);
            entityMapperFactory.getMapper(DocumentType.ID_CARD).mapToEntity(idCard, idCardDto);
            documentRepository.save(idCard); // Must save transient document

            saveNewImage(idCard, idCardFront, ImageType.ID_CARD_FRONT, "idcard_front_" + user.getUsername());
            saveNewImage(idCard, idCardBack, ImageType.ID_CARD_BACK, "idcard_back_" + user.getUsername());
        }

        if (licenseDto != null) {
            Document license = Document.builder()
                    .documentType(DocumentType.LICENSE)
                    .ownerType(DocumentOwnerType.DRIVER)
                    .build();
            license.addUser(user);
            entityMapperFactory.getMapper(DocumentType.LICENSE).mapToEntity(license, licenseDto);
            documentRepository.save(license); // Must save transient document

            saveNewImage(license, licenseImage, ImageType.LICENSE_FRONT, "license_" + user.getUsername());
        }
    }

    private void createDocuments(User user, DriverRegisterReqDto.IdCardReqDto idCardDto,
            DriverRegisterReqDto.LicenseReqDto licenseDto, MultipartFile idCardFront, MultipartFile idCardBack,
            MultipartFile licenseImage) {
        if (idCardDto != null) {
            Document idCard = Document.builder()
                    .documentType(DocumentType.ID_CARD)
                    .ownerType(DocumentOwnerType.DRIVER)
                    .build();
            idCard.addUser(user);
            entityMapperFactory.getMapper(DocumentType.ID_CARD).mapToEntity(idCard, idCardDto);
            documentRepository.save(idCard);

            saveNewImage(idCard, idCardFront, ImageType.ID_CARD_FRONT, "idcard_front_" + user.getUsername());
            saveNewImage(idCard, idCardBack, ImageType.ID_CARD_BACK, "idcard_back_" + user.getUsername());
        }

        if (licenseDto != null) {
            Document license = Document.builder()
                    .documentType(DocumentType.LICENSE)
                    .ownerType(DocumentOwnerType.DRIVER)
                    .build();
            license.addUser(user);
            entityMapperFactory.getMapper(DocumentType.LICENSE).mapToEntity(license, licenseDto);
            documentRepository.save(license);

            saveNewImage(license, licenseImage, ImageType.LICENSE_FRONT, "license_" + user.getUsername());
        }
    }

    private void saveNewImage(Document doc, MultipartFile file, ImageType type, String prefix) {
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, prefix);
            Image img = new Image();
            img.setImageType(type);
            img.setFilePath(fileName);
            img.addDocument(doc);
            imageRepository.save(img); // Must save transient image
        }
    }

    private void updateDocuments(User user, DriverUpdateReqDto.IdCardReqDto idCardDto,
            DriverUpdateReqDto.LicenseReqDto licenseDto, MultipartFile idCardFront, MultipartFile idCardBack,
            MultipartFile licenseImage) {
        if (idCardDto != null) {
            user.getDocuments().stream()
                    .filter(d -> d.getDocumentType() == DocumentType.ID_CARD)
                    .findFirst()
                    .ifPresentOrElse(
                            doc -> {
                                entityMapperFactory.getMapper(DocumentType.ID_CARD).mapToEntity(doc, idCardDto);
                                documentRepository.save(doc);
                                updateOrSaveImage(doc, idCardFront, ImageType.ID_CARD_FRONT,
                                        "idcard_front_" + user.getUsername());
                                updateOrSaveImage(doc, idCardBack, ImageType.ID_CARD_BACK,
                                        "idcard_back_" + user.getUsername());
                            },
                            () -> {
                                Document newDoc = Document.builder()
                                        .documentType(DocumentType.ID_CARD)
                                        .ownerType(DocumentOwnerType.DRIVER)
                                        .build();
                                newDoc.addUser(user);
                                entityMapperFactory.getMapper(DocumentType.ID_CARD).mapToEntity(newDoc, idCardDto);
                                documentRepository.save(newDoc);
                                updateOrSaveImage(newDoc, idCardFront, ImageType.ID_CARD_FRONT,
                                        "idcard_front_" + user.getUsername());
                                updateOrSaveImage(newDoc, idCardBack, ImageType.ID_CARD_BACK,
                                        "idcard_back_" + user.getUsername());
                            });
        }

        if (licenseDto != null) {
            user.getDocuments().stream()
                    .filter(d -> d.getDocumentType() == DocumentType.LICENSE)
                    .findFirst()
                    .ifPresentOrElse(
                            doc -> {
                                entityMapperFactory.getMapper(DocumentType.LICENSE).mapToEntity(doc, licenseDto);
                                documentRepository.save(doc);
                                updateOrSaveImage(doc, licenseImage, ImageType.LICENSE_FRONT,
                                        "license_" + user.getUsername());
                            },
                            () -> {
                                Document newDoc = Document.builder()
                                        .documentType(DocumentType.LICENSE)
                                        .ownerType(DocumentOwnerType.DRIVER)
                                        .build();
                                newDoc.addUser(user);
                                entityMapperFactory.getMapper(DocumentType.LICENSE).mapToEntity(newDoc, licenseDto);
                                documentRepository.save(newDoc);
                                updateOrSaveImage(newDoc, licenseImage, ImageType.LICENSE_FRONT,
                                        "license_" + user.getUsername());
                            });
        }
    }

    private void updateOrSaveImage(Document doc, MultipartFile file, ImageType type, String prefix) {
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, prefix);
            doc.getImages().stream()
                    .filter(i -> i.getImageType() == type)
                    .findFirst()
                    .ifPresentOrElse(
                            img -> {
                                img.setFilePath(fileName);
                                imageRepository.save(img);
                            },
                            () -> {
                                Image newImg = new Image();
                                newImg.setImageType(type);
                                newImg.addDocument(doc);
                                newImg.setFilePath(fileName);
                                imageRepository.save(newImg);
                            });
        }
    }

    @Override
    public void deleteDriver(UUID id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getAudit().getIsActive())
                .filter(u -> u.getRole().getName().equals("DRIVER"))
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found or inactive with id: " + id));

        user.getAudit().setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public List<com.sonnh.bookingcar.dto.UserTouristDto> findAllTourists() {
        return userRepository.findUserByRoleName("TOURIST").stream()
                .map(userEntityMapper::toUserTouristDto)
                .collect(Collectors.toList());
    }

    @Override
    public com.sonnh.bookingcar.dto.UserTouristDto findTouristById(UUID id) {
        User user = userRepository.findUserByRoleNameAndId("TOURIST", id)
                .orElseThrow(() -> new ResourceNotFoundException("Tourist not found with id: " + id));
        return userEntityMapper.toUserTouristDto(user);
    }

    @Override
    @Transactional
    public void deleteTourist(UUID id) {
        User user = userRepository.findUserByRoleNameAndId("TOURIST", id)
                .filter(u -> u.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Tourist not found with id: " + id));
        user.getAudit().setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void touristRegister(TouristRegisterReqDto dto) {
        userRepository.findByPhone(dto.getPhone())
                .ifPresent(u -> {
                    throw new RuntimeException("Người dùng với số điện thoại này đã tồn tại");
                });

        User newTourist = User.builder()
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .fullName(dto.getFullname())
                .email(dto.getMail())
                .build();

        Role role = roleRepository.findByName("TOURIST")
                .orElseThrow(() -> new RuntimeException("Role TOURIST not found"));

        newTourist.addRole(role);
        userRepository.save(newTourist);
    }
}
