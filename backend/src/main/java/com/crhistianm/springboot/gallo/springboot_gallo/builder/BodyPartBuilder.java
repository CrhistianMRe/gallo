package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.BodyPart;

public class BodyPartBuilder {

    private Long id;

    private String name;

    public BodyPartBuilder() {
    }

    public BodyPartBuilder id(Long id){
        this.id = id;
        return this;
    }

    public BodyPartBuilder name(String name){
        this.name = name;
        return this;
    }

    public BodyPart build(){
        BodyPart bodyPart = new BodyPart();
        bodyPart.setId(this.id);
        bodyPart.setName(this.name);
        return bodyPart;
    }
    
}


