package com.crhistianm.springboot.gallo.springboot_gallo.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Data {


    public static Optional<FieldInfoError> givenFieldInfoErrorOne(){
        return Optional.of(new FieldInfoErrorBuilder()
                    .name("test1")
                    .value("testvalue1")
                    .type(String.class)
                    .ownerClass(String.class)
                    .errorMessage("test error message 1")
                    .build());
    }

    public static Optional<FieldInfoError> givenFieldInfoErrorTwo(){
            return Optional.of(new FieldInfoErrorBuilder()
                    .name("test2")
                    .value(2L)
                    .type(Long.class)
                    .ownerClass(Long.class)
                    .errorMessage("test error message 2")
                    .build());
    }

    public static class SampleClass extends DummyBaseClass {

        private Long testObjectLong;

        private boolean testPrimitiveBoolean;

        private int testPrimitiveInt;

        private String testObjectString;

        private Boolean testObjectBoolean;
    
    }

    public static class DummyBaseClass {

        private String name;

        private List<String> surnames = new ArrayList<>();

        public DummyBaseClass() {
        }

        public DummyBaseClass(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSurnames(List<String> surnames) {
            this.surnames = surnames;
        }

        public List<String> getSurnames() {
            return surnames;
        }

    }

    public static class ChildClass extends DummyBaseClass{

        Set<String> cars = new HashSet<>();

        Boolean hasLicense;

        public ChildClass() {
        }
        
        public ChildClass(Boolean hasLicense, String name) {
            super(name);
            this.hasLicense = hasLicense;
        }

        public void setCars(Set<String> cars) {
            this.cars = cars;
        }

        public Set<String> getCars() {
            return cars;
        }

        public void setHasLicense(Boolean hasLicense) {
            this.hasLicense = hasLicense;
        }

        public Boolean getHasLicense() {
            return hasLicense;
        }

    }

}
