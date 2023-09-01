package br.com.juliancambraia.api.aws.cognito.controller;

import br.com.juliancambraia.api.aws.cognito.dto.UserSignUpRequest;
import br.com.juliancambraia.api.aws.cognito.dto.UserToken;
import br.com.juliancambraia.api.aws.cognito.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/cognitos")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
  private final UserService userService;
  public static final String SUCCESS = "Success";
  
  @PostMapping(value = "/adminCreateUser")
  public ResponseEntity<String> adminCreateUser(@RequestBody UserSignUpRequest userSignUpRequest) {
    userService.adminCreateUser(userSignUpRequest);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }
  
  @PostMapping(value = "/adminInitiateAuth")
  public ResponseEntity<UserToken> adminInitiateAuth(@RequestParam("username") String username,
                                                     @RequestParam("password") String password)
      throws NoSuchAlgorithmException, InvalidKeyException {
    UserToken userToken = userService.adminInitiateAuth(username, password);
    return new ResponseEntity<>(userToken, HttpStatus.OK);
  }
  
  @PostMapping(value = "/adminResetUserPassword")
  public ResponseEntity<String> adminResetUserPassword(@RequestParam("username") String username) {
    userService.adminResetUserPassword(username);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }
  
  @PostMapping(value = "/forgotPassword")
  public ResponseEntity<String> forgotPassword(@RequestParam("username") String username)
      throws NoSuchAlgorithmException, InvalidKeyException {
    userService.forgotPassword(username);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }
  
  @PostMapping(value = "/confirmforgotPassword")
  public ResponseEntity<String> confirmForgotPassword(@RequestParam("confirmationCode") String confirmationCode,
                                                      @RequestParam("password") String password,
                                                      @RequestParam("username") String username)
      throws NoSuchAlgorithmException, InvalidKeyException {
    userService.confirmForgotPassword(confirmationCode, password, username);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }
  
  @PostMapping(value = "/adminUpdateUserAttributes")
  public ResponseEntity<String> adminUpdateUserAttributes(@RequestParam("oldName") String oldName,
                                                          @RequestParam("newName") String newName)
      throws NoSuchAlgorithmException, InvalidKeyException {
    userService.adminUpdateUserAttributes(oldName, newName);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }
}
