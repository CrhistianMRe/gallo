package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.time.LocalDate;


import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

//No id as it is auto incremental on db
public class PersonRequestDto implements RequestDto {

    @NotBlank
    @Size(max = 45)
    private String firstName;

    @NotBlank
    @Size(max = 45)
    private String lastName;

    @NotBlank
    @Size(max = 16)
    private String phoneNumber;

    @NotNull
    @PastOrPresent
    private LocalDate birthDate;

    @NotBlank
    @CorrectGender
    private String gender;

    @DecimalMin("0.50")
    @DecimalMax("3.00")
    @Digits(integer = 1, fraction = 2, message = "{dto.validation.annotation.digits.height}")
    private Double height;

    @Digits(integer = 3, fraction = 1, message = "{dto.validation.annotation.digits.weight}")
    @DecimalMin("20.0")
    @DecimalMax("200.0")
    private Double weight;

    PersonRequestDto() {
    }

    PersonRequestDto(String firstName, String lastName, String phoneNumber, LocalDate birthDate, String gender,
            Double height, Double weight) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
    }

    String getFirstName() {
        return firstName;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    String getLastName() {
        return lastName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    LocalDate getBirthDate() {
        return birthDate;
    }

    void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    String getGender() {
        return gender;
    }

    void setGender(String gender) {
        this.gender = gender;
    }

    Double getHeight() {
        return height;
    }

    void setHeight(Double height) {
        this.height = height;
    }

    Double getWeight() {
        return weight;
    }

    void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
        PersonRequestDto other = (PersonRequestDto) obj;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PersonRequestDto [firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber=" + phoneNumber
                + ", birthDate=" + birthDate + ", gender=" + gender + ", height=" + height + ", weight=" + weight + "]";
    }

    
}
