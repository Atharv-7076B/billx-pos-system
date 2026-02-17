package com.BillX.controller;

import com.BillX.Service.AuthService;
import com.BillX.configuration.SecurityConfig;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Payload.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    public void testSignup_Success() throws Exception {
        when(authService.signup(any(UserDto.class))).thenReturn(new AuthResponse("jwt-token", "Success", null));

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\", \"password\":\"password\", \"fullName\":\"Test User\", \"role\":\"ROLE_CUSTOMER\"}"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testLogin_Success() throws Exception {
        when(authService.login(any(UserDto.class))).thenReturn(new AuthResponse("jwt-token", "Success", null));

         mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk());
    }
}
