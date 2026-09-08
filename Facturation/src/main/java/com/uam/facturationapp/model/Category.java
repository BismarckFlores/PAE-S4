package com.uam.facturationapp.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Integer id;
    private String name;
    private boolean active;

    @Override
    public String toString() {
        return name;
    }
}
