package com.payment.service.cntroller;

import com.payment.service.controller.PaymentController;
import com.payment.service.exception.GlobalExceptionHandler;
import com.payment.service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())

                .build();
    }

    @Test
    void testGetCheapestRoute_InvalidInput() throws Exception {
        // Test with empty origin and destination parameters
        mockMvc.perform(get("/api/payments/route")
                        .param("origin", "")
                        .param("destination", ""))
                .andExpect(status().isBadRequest()) // Expect a 400 Bad Request status
                .andExpect(content().string("Origin and destination branches must not be empty."));
    }

    @Test
    void testGetCheapestRoute_NullInput() throws Exception {
        // Test with missing origin and destination parameters
        mockMvc.perform(get("/api/payments/route"))
                .andExpect(status().isBadRequest()); // Expect a 400 Bad Request status
    }
}