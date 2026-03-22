package com.BillX.Model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    private Branch branch;
    @ManyToOne
    private Product product;
    @Column(nullable = false)
    private Integer quantity;
    private LocalDateTime lastUpdate;

    @PrePersist
    @PreUpdate
    protected void update(){
        lastUpdate = LocalDateTime.now();
    }
}
