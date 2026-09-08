package com.uam.facturationapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Integer id;
    private String names;
    private String lastname;
    private Position position;
    private LocalDate hireDate;
    private boolean active;
}
