package br.com.juliancambraia.api.aws.cognito.service;

import br.com.juliancambraia.api.aws.cognito.dto.UserSignUpRequest;
import br.com.juliancambraia.api.aws.cognito.dto.UserToken;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public interface UserService {
  void adminCreateUser(UserSignUpRequest userSignUpRequest);
  
  void adminUpdateUserAttributes(String oldName, String newName);
  
  UserToken adminInitiateAuth(String email, String password) throws NoSuchAlgorithmException, InvalidKeyException;
  
  void adminResetUserPassword(String email);
  
  void forgotPassword(String email) throws NoSuchAlgorithmException, InvalidKeyException;
  
  void confirmForgotPassword(String confirmationCode, String password, String username) throws NoSuchAlgorithmException, InvalidKeyException;
}
