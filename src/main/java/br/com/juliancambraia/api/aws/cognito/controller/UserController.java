package br.com.juliancambraia.api.aws.cognito.controller;

import br.com.juliancambraia.api.aws.cognito.dto.UserSignUpRequest;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/cognitos")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
  private final UserService userService;
  public static final String SUCCESS = "Success";
  
  @PostMapping(value = "adminCreateUser")
  public ResponseEntity<String> adminCreateUser(@RequestBody UserSignUpRequest userSignUpRequest) {
    userService.adminCreateUser(userSignUpRequest);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }
}
