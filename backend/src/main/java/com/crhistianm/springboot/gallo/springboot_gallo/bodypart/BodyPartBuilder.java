package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

class BodyPartBuilder {

    private Long id;

    private String name;

    BodyPartBuilder() {
    }

    BodyPartBuilder id(Long id){
        this.id = id;
        return this;
    }

    BodyPartBuilder name(String name){
        this.name = name;
        return this;
    }

    BodyPart build(){
        BodyPart bodyPart = new BodyPart();
        bodyPart.setId(this.id);
        bodyPart.setName(this.name);
        return bodyPart;
    }
    
}


