package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.Image;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    
    @Query("SELECT i FROM Image i WHERE i.user.id = :userId")
    List<Image> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT i FROM Image i WHERE i.document.id = :documentId")
    List<Image> findByDocumentId(@Param("documentId") UUID documentId);

    @Query("SELECT i FROM Image i WHERE i.user.id = :userId AND i.imageType = :imageType")
    Optional<Image> findByUserIdAndImageType(@Param("userId") UUID userId, @Param("imageType") ImageType imageType);

    @Query("SELECT i FROM Image i WHERE i.document.id = :documentId AND i.imageType = :imageType")
    Optional<Image> findByDocumentIdAndImageType(@Param("documentId") UUID documentId, @Param("imageType") ImageType imageType);
}
