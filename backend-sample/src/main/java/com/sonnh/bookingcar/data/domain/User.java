package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @Column(unique = true, nullable = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String email;
    private String phone;
    private String notes;
    private String profileImage;

    // Driver specific fields
    @Builder.Default
    private Double driverRating = 0.0;
    @Enumerated(EnumType.STRING)
    private DriverStatus driverStatus; // AVAILABLE, BUSY

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Shift> shiftList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CheckinTourHistory> checkinTourHistories = new ArrayList<>();

    public void addCheckinHistory(CheckinTourHistory history) {
        this.checkinTourHistories.add(history);
        history.setUser(this);
    }

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addRole(Role role) {
        role.getUserList().add(this);
        this.setRole(role);
    }
}
