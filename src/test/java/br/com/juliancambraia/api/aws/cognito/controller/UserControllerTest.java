package br.com.juliancambraia.api.aws.cognito.controller;

import br.com.juliancambraia.api.aws.cognito.dto.UserSignUpRequest;
import br.com.juliancambraia.api.aws.cognito.dto.UserToken;
import br.com.juliancambraia.api.aws.cognito.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
  public static final String TOKEN = "jaspdof8asud90fiasndjf0a98sdfna0s9dmfua09dsfu8a0d9sfu8jads98ja-dsf9unmfáskdfjoinasydf5rva586sd487a5s4";
  @Mock
  UserService service;
  
  UserController subject;
  
  @BeforeEach
  void setUp() {
    subject = new UserController(service);
  }
  
  @Test
  void shouldReturnSuccess_When_AdminCreateUser_IsOk() throws NoSuchAlgorithmException, InvalidKeyException {
    var request = mock(UserSignUpRequest.class);
    var result = subject.adminCreateUser(request);
    
    verify(service).adminCreateUser(request);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }
  
  @ParameterizedTest
  @CsvSource({"nobody.user,password-123"})
  void shouldReturnToken_When_AdminInitiateAuth_IsOk(String username, String password) throws NoSuchAlgorithmException, InvalidKeyException {
    var tokenUser = mock(UserToken.class);
    
    when(service.adminInitiateAuth(username, password)).thenReturn(tokenUser);
    when(tokenUser.getIdToken()).thenReturn(TOKEN);
    
    var result = subject.adminInitiateAuth(username, password);
    
    verify(service).adminInitiateAuth(username, password);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertTrue(Objects.requireNonNull(result.getBody()).getIdToken().contains(TOKEN));
  }
  
  @ParameterizedTest
  @CsvSource({"nobody.user"})
  void shouldReturnSuccess_When_AdminResetUserPassword_IsOk(String username) throws NoSuchAlgorithmException, InvalidKeyException {
    var result = subject.adminResetUserPassword(username);
    
    verify(service).adminResetUserPassword(username);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }
  
  @ParameterizedTest
  @CsvSource({"nobody.user"})
  void shouldReturnSuccess_When_ForgotPassword_IsOk(String username) throws NoSuchAlgorithmException, InvalidKeyException {
    var result = subject.forgotPassword(username);
    
    verify(service).forgotPassword(username);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }
  
  @ParameterizedTest
  @CsvSource({"123456, nobody.user, password-123"})
  void shouldReturnSuccess_When_ConfirmForgotPassword_IsOk(String codeParameter, String password, String username) throws NoSuchAlgorithmException, InvalidKeyException {
    var result = subject.confirmForgotPassword(codeParameter, password, username);
    
    verify(service).confirmForgotPassword(codeParameter, password, username);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }
  
  @ParameterizedTest
  @CsvSource({"newName, oldName"})
  void shouldReturnSuccess_When_AdminUpdateUserAttributes_IsOk(String oldName, String newName) throws NoSuchAlgorithmException, InvalidKeyException {
    var result = subject.adminUpdateUserAttributes(oldName, newName);
    
    verify(service).adminUpdateUserAttributes(oldName, newName);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }
}