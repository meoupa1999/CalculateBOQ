package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_type")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String formula;

    @Column(name = "order_index")
    private Double orderIndex;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String unit;

    private Double labor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "productType")
    private List<Product> products = new ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addProduct(Product product) {
        products.add(product);
        product.setProductType(this);
    }

    public void addCategory(Category category) {
        category.getProductTypes().add(this);
        this.setCategory(category);
    }

}
