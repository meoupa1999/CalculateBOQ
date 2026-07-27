package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "category")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(name = "order_index")
    private Double orderIndex;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    @OrderBy("orderIndex ASC")
    private List<Category> children = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "category")
    @OrderBy("orderIndex ASC")
    private List<ProductType> productTypes = new ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    public void addProductType(ProductType productType) {
        productTypes.add(productType);
        productType.setCategory(this);
    }
}
