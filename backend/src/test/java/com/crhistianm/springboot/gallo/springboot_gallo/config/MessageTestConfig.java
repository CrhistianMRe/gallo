package com.crhistianm.springboot.gallo.springboot_gallo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@TestConfiguration
public class MessageTestConfig {


    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        return messageSource;
    }


    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean local = new LocalValidatorFactoryBean();
        local.setValidationMessageSource(messageSource());
        return local;
    }

    
}
