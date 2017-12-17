package com.rest.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="USER_LIST")
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(name="Name", nullable=false)
    private String name;

    @Column(name = "AGE", nullable=false)
    private Integer age;

    @Column(name="SALARY", nullable=false)
    private double salary;

    //getters/setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public double getSalary() {
        return salary;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
