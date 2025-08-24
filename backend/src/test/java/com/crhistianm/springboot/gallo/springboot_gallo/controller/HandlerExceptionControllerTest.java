package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;


@WebMvcTest(HandlerExceptionControllerTest.TestExceptionController.class)
public class HandlerExceptionControllerTest {

    private MockMvc mockmvc;

    TestExceptionController testController;

    //This dummy is for MethodParameter
    public void dummy(String test){};

    @RestController
    @RequestMapping("exception")
    public class TestExceptionController {

        private Exception ex;

        public TestExceptionController(Exception ex) {
            this.ex = ex;
        }

        @GetMapping
        public void throwException() throws Exception {
            throw ex;
        }

    }

    @Test
    void testValidationExceptionHandler() throws Exception{
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(Object.class, "test");
        bindingResult.addError(new FieldError("test", "example", "is an example test"));

        MethodParameter parameter = new MethodParameter(this.getClass().getMethod("dummy", String.class), 0);

        testController = new TestExceptionController(new MethodArgumentNotValidException(parameter, bindingResult));
        mockmvc = MockMvcBuilders.standaloneSetup(testController).setControllerAdvice(HandlerExceptionController.class).build();

        mockmvc.perform(get("/exception"))
            .andExpect(jsonPath("$.example").value("the field example is an example test"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDateTimeJsonParserExceptionHandler() throws Exception{
        testController = new TestExceptionController(new DateTimeParseException("example message", new StringBuilder("exampleCharSquence"), 0));
        mockmvc = MockMvcBuilders.standaloneSetup(testController).setControllerAdvice(HandlerExceptionController.class).build();

        mockmvc.perform(get("/exception"))
            .andExpect(jsonPath("$.exampleCharSquence").value("not valid, use yyyy/mm/dd"));

    }

    @Test
    void testNotFoundExceptionHandler() throws Exception {
        testController = new TestExceptionController(new NotFoundException(Object.class));
        mockmvc = MockMvcBuilders.standaloneSetup(testController).setControllerAdvice(HandlerExceptionController.class).build();
        mockmvc.perform(get("/exception"))
            .andExpect(jsonPath("$.message").value("Object not found"))
            .andExpect(jsonPath("$.status").value("404"))
            .andExpect(status().isNotFound());
    }


}
