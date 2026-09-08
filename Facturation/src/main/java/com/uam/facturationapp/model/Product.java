package com.uam.facturationapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer id;
    private String code;
    private String name;
    private Category category;
    private BigDecimal salePrice;
    private int stock;
    private String imagePath;
    private boolean active;
}
