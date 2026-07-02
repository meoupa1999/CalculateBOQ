package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "documents")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "expired_date")
    private LocalDate expiredDate;

    @Column(name = "issued_place")
    private String issuedPlace;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type")
    private DocumentOwnerType ownerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @OneToMany(mappedBy = "document")
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void addUser(User user) {
        this.user = user;
        if (user != null && !user.getDocuments().contains(this)) {
            user.getDocuments().add(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public void addVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (vehicle != null && !vehicle.getDocuments().contains(this)) {
            vehicle.getDocuments().add(this);
        }
    }

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
