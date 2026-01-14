package com.crhistianm.springboot.gallo.springboot_gallo.shared.exception;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class NotFoundExceptionUnitTest {

    NotFoundException exception;

    @Test
    void testMessage(){
        exception = new NotFoundException(Object.class);
        assertThat(exception).hasMessageContaining("Object not found");
    }
    
}
