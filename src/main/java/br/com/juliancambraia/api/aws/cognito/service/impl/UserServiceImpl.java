package br.com.juliancambraia.api.aws.cognito.service.impl;

import br.com.juliancambraia.api.aws.cognito.configuration.CommonConfiguration;
import br.com.juliancambraia.api.aws.cognito.dto.UserSignUpRequest;
import br.com.juliancambraia.api.aws.cognito.dto.UserToken;
import br.com.juliancambraia.api.aws.cognito.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminResetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminResetUserPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordResponse;
import software.amazon.awssdk.utils.BinaryUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
  private static final String ALGORITHM = "HmacSH256";
  private final CommonConfiguration commonConfiguration;
  private final AdminCreateUserRequest.Builder adminCreateUser;
  private final CognitoIdentityProviderClient cognitoClient;
  private final AdminUpdateUserAttributesRequest.Builder adminUpdateUserAttributesRequest;
  private final AdminInitiateAuthRequest.Builder adminInitiateAuthRequest;
  private final AdminResetUserPasswordRequest.Builder adminResetUserPasswordRequest;
  private final ForgotPasswordRequest.Builder forgotPasswordRequest;
  private final ConfirmForgotPasswordRequest.Builder confirmForgotPasswordRequest;
  
  @Override
  public void adminCreateUser(UserSignUpRequest userSignUpRequest) {
    AdminCreateUserRequest adminCreateUserRequest = adminCreateUserRequest(userSignUpRequest);
    
    AdminCreateUserResponse response = cognitoClient.adminCreateUser(adminCreateUserRequest);
    log.info("register user for e-mail: {}", response);
  }
  
  @Override
  public void adminUpdateUserAttributes(String oldName, String newName) {
    List<AttributeType> attributes = new ArrayList<>();
    attributes.add(AttributeType.builder().name("name").value(newName).build());
    
    AdminUpdateUserAttributesRequest adminUpdateUserRequest = adminUpdateUserAttributesRequest.username(oldName).userAttributes(attributes).build();
    cognitoClient.adminUpdateUserAttributes(adminUpdateUserRequest);
  }
  
  @Override
  public UserToken adminInitiateAuth(String email, String password) throws NoSuchAlgorithmException, InvalidKeyException {
    UserToken userToken = new UserToken();
    
    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("USERNAME", email);
    requestParams.put("PASSWORD", password);
    requestParams.put("SECRET_HASH", getSecretHash(email, commonConfiguration.getCognitoClientId(), commonConfiguration.getCognitoClientSecretKey()));
    
    AdminInitiateAuthRequest adminInitiateRequest = adminInitiateAuthRequest(requestParams);
    
    AdminInitiateAuthResponse adminInitiateAuthResult = cognitoClient.adminInitiateAuth(adminInitiateRequest);
    
    if (null == adminInitiateAuthResult) {
      log.error("Error in refreshToken");
    } else {
      userToken.setAccessToken(adminInitiateAuthResult.authenticationResult().accessToken());
      userToken.setIdToken(adminInitiateAuthResult.authenticationResult().idToken());
      userToken.setRefreshToken(adminInitiateAuthResult.authenticationResult().refreshToken());
    }
    
    return userToken;
  }
  
  @Override
  public void adminResetUserPassword(String username) {
    AdminResetUserPasswordRequest adminResetUserPasswordRequest = adminResetUserPasswordRequest(username);
    AdminResetUserPasswordResponse adminResetUserPasswordResponse = cognitoClient.adminResetUserPassword(adminResetUserPasswordRequest);
    
    log.info("palavra-passe esquecida result {}", adminResetUserPasswordResponse);
  }
  
  @Override
  public void forgotPassword(String username) throws NoSuchAlgorithmException, InvalidKeyException {
    ForgotPasswordRequest forgotPasswordRequest = forgotPasswordRequest(username);
    ForgotPasswordResponse forgotPasswordResponse = cognitoClient.forgotPassword(forgotPasswordRequest);
    
    log.info("palavra-passe esquecida result {}", forgotPasswordResponse);
  }
  
  @Override
  public void confirmForgotPassword(String confirmationCode, String password, String username) throws NoSuchAlgorithmException, InvalidKeyException {
    ConfirmForgotPasswordRequest confirmForgotPasswordRequest = confirmForgotPasswordRequest(confirmationCode, password, username);
    ConfirmForgotPasswordResponse confirmForgotPasswordResponse = cognitoClient.confirmForgotPassword(confirmForgotPasswordRequest);
    
    log.info("palavra-passe esquecida result {}", confirmForgotPasswordResponse);
    
  }
  
  private AdminInitiateAuthRequest adminInitiateAuthRequest(Map<String, String> requestParams) {
    return adminInitiateAuthRequest
        .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
        .authParameters(requestParams)
        .build();
  }
  
  private String getSecretHash(String username, String clientAppId, String clientAppSecret) throws NoSuchAlgorithmException, InvalidKeyException {
    byte[] data = (username + clientAppId).getBytes(StandardCharsets.UTF_8);
    byte[] key = clientAppSecret.getBytes(StandardCharsets.UTF_8);
    
    return BinaryUtils.toBase64(hmacSHA256(data, key));
  }
  
  private byte[] hmacSHA256(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance(ALGORITHM);
    mac.init(new SecretKeySpec(key, ALGORITHM));
    
    return mac.doFinal(data);
  }
  
  private AdminResetUserPasswordRequest adminResetUserPasswordRequest(String username) {
    return adminResetUserPasswordRequest
        .username(username)
        .build();
  }
  
  private ForgotPasswordRequest forgotPasswordRequest(String username) throws NoSuchAlgorithmException, InvalidKeyException {
    return forgotPasswordRequest
        .username(username)
        .secretHash(getSecretHash(username, commonConfiguration.getCognitoClientId(), commonConfiguration.getCognitoClientSecretKey()))
        .build();
  }
  
  private ConfirmForgotPasswordRequest confirmForgotPasswordRequest(String confirmatinCode, String password, String username) throws NoSuchAlgorithmException, InvalidKeyException {
    return confirmForgotPasswordRequest
        .confirmationCode(confirmatinCode)
        .password(password)
        .username(username)
        .secretHash(getSecretHash(username, commonConfiguration.getCognitoClientId(), commonConfiguration.getCognitoClientSecretKey()))
        .build();
  }
  
  private AdminCreateUserRequest adminCreateUserRequest(UserSignUpRequest userSignUpRequest) {
    return adminCreateUser
        .username(userSignUpRequest.getName())
        .userAttributes(prepareAttributesType(userSignUpRequest))
        .build();
  }
  
  private List<AttributeType> prepareAttributesType(UserSignUpRequest userSignUpRequest) {
    List<AttributeType> attributes = new ArrayList<>();
    attributes.add(AttributeType.builder().name("name").value(userSignUpRequest.getName()).build());
    attributes.add(AttributeType.builder().name("email").value(userSignUpRequest.getEmail()).build());
    attributes.add(AttributeType.builder().name("gender").value("M").build());
    attributes.add(AttributeType.builder().name("address").value("BH").build());
    attributes.add(AttributeType.builder().name("birthdate").value(userSignUpRequest.getDateOfBirth()).build());
    return attributes;
  }
}
