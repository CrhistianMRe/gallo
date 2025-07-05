package com.crhistianm.springboot.gallo.springboot_gallo.entity;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "exercise")
public class Exercise {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Boolean weightRequired;

    private String imageUrl;

    //Not bidirectional as i only expect to need bodyparts of a exercise, not inverse
    @ManyToMany
    @JoinTable(
        name = "exercise_body_part",
        joinColumns = @JoinColumn (name = "exercise_id"),
        inverseJoinColumns = @JoinColumn(name = "body_part_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"exercise_id", "body_part_id"})}
    )
    private List<BodyPart> bodyParts;

    public Exercise() {
        this.bodyParts = new ArrayList<>();
    }

    public Exercise(String name, String description, Boolean weightRequired, String imageUrl) {
        this();
        this.name = name;
        this.description = description;
        this.weightRequired = weightRequired;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getWeightRequired() {
        return weightRequired;
    }

    public void setWeightRequired(Boolean weightRequired) {
        this.weightRequired = weightRequired;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<BodyPart> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(List<BodyPart> bodyParts) {
        this.bodyParts = bodyParts;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Exercise other = (Exercise) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Exercise [id=" + id + ", name=" + name + ", description=" + description + ", weightRequired="
                + weightRequired + ", imageUrl=" + imageUrl + ", bodyParts=" + bodyParts + "]";
    }

}
