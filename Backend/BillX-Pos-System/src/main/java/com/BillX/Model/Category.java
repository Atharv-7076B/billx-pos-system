package com.BillX.Model;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    private Store store;
}
