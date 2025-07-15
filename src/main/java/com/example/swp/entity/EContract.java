package com.example.swp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="econtract")
public class EContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private LocalDate createdDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean signed;

    // ————— Thêm dòng này —————
    /** Giá thuê theo ngày (bắt buộc NOT NULL) **/
    @Column(nullable = false)
    private Double pricePerDay;
    private String contractUrl;

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }
    @Lob
    private String terms;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
