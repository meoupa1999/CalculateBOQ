package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.ShiftHistoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "shift_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(nullable = false)
    private LocalDate actualStartDate;

    @Column(nullable = false)
    private LocalTime actualStartTime;

    private LocalTime actualEndTime;

    @Enumerated(EnumType.STRING)
    private ShiftHistoryStatus finalStatus;

    @Builder.Default
    private boolean isProcessing = false;

    private java.math.BigDecimal startMileage;
    private java.math.BigDecimal endMileage;
    private Integer startFuelLevel;
    private Integer endFuelLevel;
    
    @Column(length = 1000)
    private String handoverNotes;

    @OneToMany(mappedBy = "shiftHistory", fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<Image> images = new java.util.ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addShift(Shift shift) {
        if (shift.getShiftHistories() == null) {
            shift.setShiftHistories(new java.util.ArrayList<>());
        }
        shift.getShiftHistories().add(this);
        this.setShift(shift);
    }
}
