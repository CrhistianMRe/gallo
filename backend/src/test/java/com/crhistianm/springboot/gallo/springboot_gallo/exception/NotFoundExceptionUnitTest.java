package com.crhistianm.springboot.gallo.springboot_gallo.exception;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class NotFoundExceptionUnitTest {

    NotFoundException exception;

    @Test
    void testMessage(){
        exception = new NotFoundException(Object.class);
        assertThat(exception).hasMessageContaining("Object not found");
    }
    
}
