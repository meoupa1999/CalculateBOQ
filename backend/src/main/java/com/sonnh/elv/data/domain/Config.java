package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "config")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    private Integer conditionLength;

    private Integer sw24ConditionQuanity;

    private Integer sw16ConditionQuanity;

    private Integer ups; // default 1 cái 1K

    private Integer pdu; // default 6 lỗ

    private Integer converter; // default 1 cái

    @Builder.Default
    @OneToMany(mappedBy = "config")
    private List<Tower> towers = new ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
