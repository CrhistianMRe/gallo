package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.time.LocalDate;

class WorkoutResponseDto {

    private Long id;

    private LocalDate workoutDate;

    private short workoutLength;
    
    private String exerciseName;

    private String imageUrl;

    WorkoutResponseDto(){}

    WorkoutResponseDto(Long id, LocalDate workoutDate, short workoutLength, String exerciseName, String imageUrl) {
        this.id = id;
        this.workoutDate = workoutDate;
        this.workoutLength = workoutLength;
        this.exerciseName = exerciseName;
        this.imageUrl = imageUrl;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    LocalDate getWorkoutDate() {
        return workoutDate;
    }

    void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    short getWorkoutLength() {
        return workoutLength;
    }

    void setWorkoutLength(short workoutLength) {
        this.workoutLength = workoutLength;
    }

    String getExerciseName() {
        return exerciseName;
    }

    void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
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
        result = prime * result + ((workoutDate == null) ? 0 : workoutDate.hashCode());
        result = prime * result + workoutLength;
        result = prime * result + ((exerciseName == null) ? 0 : exerciseName.hashCode());
        result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
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
        WorkoutResponseDto other = (WorkoutResponseDto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (workoutDate == null) {
            if (other.workoutDate != null)
                return false;
        } else if (!workoutDate.equals(other.workoutDate))
            return false;
        if (workoutLength != other.workoutLength)
            return false;
        if (exerciseName == null) {
            if (other.exerciseName != null)
                return false;
        } else if (!exerciseName.equals(other.exerciseName))
            return false;
        if (imageUrl == null) {
            if (other.imageUrl != null)
                return false;
        } else if (!imageUrl.equals(other.imageUrl))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkoutResponseDto [id=" + id + ", workoutDate=" + workoutDate + ", workoutLength=" + workoutLength
                + ", exerciseName=" + exerciseName + ", imageUrl=" + imageUrl + "]";
    }

}
