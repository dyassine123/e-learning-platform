package com.elearning.elearning_platform.category.domain;

import jakarta.persistence.*;

@Entity
@Table(
    name = "categories",
    uniqueConstraints = @UniqueConstraint(name = "uk_category_name", columnNames = "name")
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 300)
    private String description;

    protected Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}