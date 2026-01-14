package com.crhistianm.springboot.gallo.springboot_gallo.shared;

import static com.crhistianm.springboot.gallo.springboot_gallo.shared.Data.givenFieldInfoErrorOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.shared.Data.givenFieldInfoErrorTwo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.annotation.ElementType;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.HandlerExceptionController;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;


@WebMvcTest(HandlerExceptionControllerTest.TestExceptionController.class)
class HandlerExceptionControllerTest {

    private MockMvc mockmvc;

    TestExceptionController testController;


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

    @Nested
    class ValidationExceptionHandlerMethod {

        //This dummy is for MethodParameter
        public void dummy(String test){};

        @Test
        void testMethodArgumentNotValidException() throws Exception{
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
        void testHandlerMethodValidationException() throws Exception{
            List<MessageSourceResolvable> listError = new ArrayList<>();
            listError.add(new FieldError("test", "example", "is an example test"));

            List<ParameterValidationResult> listParameter = new ArrayList<>();
            MethodParameter methodParam = new MethodParameter(this.getClass().getMethod("dummy", String.class), 0,0);
            listParameter.add(new ParameterValidationResult(methodParam, null, listError, null, null, null, null));

            MethodValidationResult methodValidationResult = MethodValidationResult
                .create(ElementType.PARAMETER, this.getClass().getMethod("dummy", String.class), listParameter);

            testController = new TestExceptionController(new HandlerMethodValidationException(methodValidationResult));
            mockmvc = MockMvcBuilders.standaloneSetup(testController).setControllerAdvice(HandlerExceptionController.class).build();

            mockmvc.perform(get("/exception"))
                .andExpect(jsonPath("$.message").value("is an example test"))
                .andExpect(status().isForbidden());
        }

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

    @Test
    void shouldHandleValidationServiceException() throws Exception {
        List<FieldInfoError> fields = new ArrayList<>();
        fields.add(givenFieldInfoErrorOne().orElseThrow());
        fields.add(givenFieldInfoErrorTwo().orElseThrow());

        testController = new TestExceptionController(new ValidationServiceException("test", "somethingthatdoesntmakesense", fields));

        mockmvc = MockMvcBuilders.standaloneSetup(testController).setControllerAdvice(HandlerExceptionController.class).build();

        mockmvc.perform(get("/exception"))
            .andExpect(jsonPath("$.test1").value("the field test1 test error message 1"))
            .andExpect(jsonPath("$.test2").value("the field test2 test error message 2"))
            .andExpect(jsonPath("$.status").value(String.valueOf(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.location").value("method name not found"));
    }


}
