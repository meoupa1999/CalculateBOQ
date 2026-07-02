package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "images")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void addUser(User user) {
        this.user = user;
        if (user != null && !user.getImages().contains(this)) {
            user.getImages().add(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    public void addDocument(Document document) {
        this.document = document;
        if (document != null && !document.getImages().contains(this)) {
            document.getImages().add(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public void addVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (vehicle != null && !vehicle.getImages().contains(this)) {
            vehicle.getImages().add(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_history_id")
    private ShiftHistory shiftHistory;

    public void addShiftHistory(ShiftHistory shiftHistory) {
        this.shiftHistory = shiftHistory;
        if (shiftHistory != null && !shiftHistory.getImages().contains(this)) {
            shiftHistory.getImages().add(this);
        }
    }

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
