package com.crhistianm.springboot.gallo.springboot_gallo.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Embedded
    private Audit audit = new Audit();

    @OneToOne
    private Person person;

    //Created inverse relationship as i need to query how many users have roles 
    @ManyToMany
    @JoinTable(
        name = "account_role",
        joinColumns = @JoinColumn(name = "account_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id, role_id"})}
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "account")
    private List<Workout> workouts;


    public Account() {
        this.workouts = new ArrayList<>();
        this.roles = new ArrayList<>();
    }

    public Account(String email, String password, Person person) {
        this();
        this.email = email;
        this.password = password;
        this.person = person;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public Account addRole(Role role){
        this.roles.add(role);
        return this;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public Audit getAudit() {
        return audit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
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
        Account other = (Account) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return true;
    }

    //person and roles here and as the main bidirectional relationship
    @Override
    public String toString() {
        return "Account [id=" + id + ", email=" + email + ", password=" + password + ", createdAt=" + audit.getCreatedAt() 
                + ", updatedAt=" + audit.getUpdatedAt()+ ", enabled=" + audit.isEnabled()+ ", person=" + person + ", roles=" + roles
                 + "}";
    }





}
