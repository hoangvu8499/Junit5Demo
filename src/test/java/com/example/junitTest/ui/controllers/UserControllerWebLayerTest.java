package com.example.junitTest.ui.controllers;

import com.example.junitTest.service.UsersService;
import com.example.junitTest.shared.UserDto;
import com.example.junitTest.ui.request.UserDetailsRequestModel;
import com.example.junitTest.ui.response.UserRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

@WebMvcTest(controllers = UsersController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@AutoConfigureMockMvc(addFilters = false)
//@MockBean({UsersServiceImpl.class})
public class UserControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    private UserDetailsRequestModel userDetailsRequestModel;

    @BeforeEach
    void setup() {
        userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Sergey");
        userDetailsRequestModel.setLastName("Kargopolow");
        userDetailsRequestModel.setEmail("email@test.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");
    }

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails() throws Exception {
        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        Mockito.when(usersService.createUser(ArgumentMatchers.any(UserDto.class))).thenReturn(userDto);


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);

        //Assert
        Assertions.assertEquals(userDetailsRequestModel.getFirstName(), createdUser.getFirstName(), "The return user first name is most likely");
        // Lastname, emai same like this

    }

    @Test
    @DisplayName("First name can not shorter more than 2 characters")
    void testCreateUser_whenFirstNameHaveOneCharacter_return404StatusCode() throws Exception {
        //arrange
        userDetailsRequestModel.setFirstName("a");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(),
                "HTTP request is not set status 404");
    }

}
