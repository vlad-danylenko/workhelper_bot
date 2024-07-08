package com.danylenko.workhelper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "object_count")
public class ObjectCount {
    @Id
    @Column(name = "hday")
    private String hday;
    @Column(name = "cnt")
    private int cnt;
}
