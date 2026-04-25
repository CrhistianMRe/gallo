package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

class ExerciseResponseDto {

    private Long id;

    private String name;

    private String description;

    private boolean weightRequired;

    private String imageUrl;

    ExerciseResponseDto(){}

    ExerciseResponseDto(Long id, String name, String description, boolean weightRequired, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weightRequired = weightRequired;
        this.imageUrl = imageUrl;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    boolean isWeightRequired() {
        return weightRequired;
    }

    void setWeightRequired(boolean weightRequired) {
        this.weightRequired = weightRequired;
    }

    String getImageUrl() {
        return imageUrl;
    }

    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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
        ExerciseResponseDto other = (ExerciseResponseDto) obj;
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
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ExerciseResponseDto [id=" + id + ", name=" + name + ", description=" + description + ", weightRequired="
                + weightRequired + ", imageUrl=" + imageUrl + "]";
    }

}
