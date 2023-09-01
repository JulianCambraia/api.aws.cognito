package br.com.juliancambraia.api.aws.cognito.service.impl;

import br.com.juliancambraia.api.aws.cognito.configuration.CommonConfiguration;
import br.com.juliancambraia.api.aws.cognito.dto.UserSignUpRequest;
import br.com.juliancambraia.api.aws.cognito.dto.UserToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {
  public static final String ACCESS_TOKEN = "eyJraWQiOiJUU1hvQUpLWVB4S09KZExpRDltbG9vMk5YeHRrQStZQnU1VnNJQW1wakQ4PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI1ZDdkNjE1OS0xMDRjLTQ1MzEtYTYxMy00NDYxYTBhZGE5ZjEiLCJjb2duaXRvOmdyb3VwcyI6WyJhZG1pbiJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuc2EtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3NhLWVhc3QtMV9EMmF6bU1pMFIiLCJjbGllbnRfaWQiOiJpYnZibGdjNGRtcG5lNDAxNzZsdmg5bGlvIiwib3JpZ2luX2p0aSI6ImViODY4ZjU4LWMzMDItNGZlZi1hMGVmLWMxMzE5NDRjNGRjMCIsImV2ZW50X2lkIjoiMThiZGYxZWUtZjViZC00MTQxLWE2ZGQtMjlmNmNmMjhkNDdlIiwidG9rZW5fdXNlIjoiYWNjZXNzIiwic2NvcGUiOiJhd3MuY29nbml0by5zaWduaW4udXNlci5hZG1pbiIsImF1dGhfdGltZSI6MTY5MzI1ODEwOSwiZXhwIjoxNjkzMjYxNzA5LCJpYXQiOjE2OTMyNTgxMDksImp0aSI6IjdjNDgyNDQzLTNiZGYtNDE2YS04ZDQzLWZhZTUwMTU5OWRhMiIsInVzZXJuYW1lIjoiZ3VpbGhlcm1lLmhhZGRhZCJ9.kzK3xu8oB5iLWYnZOmsch1xn2fBlIQi-NTfuWtTytIUjUg6mn4DSmS_ay50xy1QW0hU7VU5Etki4Cw3lKm9j6dwJt0wbQxn67AY1xFLFBXcg0-sR7ynt0hcydS4oEQU-IpljsQwE6VV7FOQgKVTL-B0UNGe7RgRcleq376PeD6UmLfvhtDkP8b1He0Hm2Vnlx_yMmW5K99durP9CyQDEX0_7wY3oyq7pqLXr74x_vfEesdqpEMV67uO9TU-aCKSE8lB97AsG52DU_fwsEkEAYbD9Z0HGOpU9C1wO6PpiZGIr8doTjU_zE6j-m6WdtSWcGesiwwEsuGDoXRYk3QOgAA";
  public static final String ID_TOKEN = "eyJraWQiOiJyQnJEYnZWV1wvdHVJOFwvQVRVS0ZCbEZUK0FhdEp4OXpLak5PTFhNeThwYjA9IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI1ZDdkNjE1OS0xMDRjLTQ1MzEtYTYxMy00NDYxYTBhZGE5ZjEiLCJjb2duaXRvOmdyb3VwcyI6WyJhZG1pbiJdLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnNhLWVhc3QtMS5hbWF6b25hd3MuY29tXC9zYS1lYXN0LTFfRDJhem1NaTBSIiwiY29nbml0bzp1c2VybmFtZSI6Imd1aWxoZXJtZS5oYWRkYWQiLCJvcmlnaW5fanRpIjoiZWI4NjhmNTgtYzMwMi00ZmVmLWEwZWYtYzEzMTk0NGM0ZGMwIiwiYXVkIjoiaWJ2YmxnYzRkbXBuZTQwMTc2bHZoOWxpbyIsImV2ZW50X2lkIjoiMThiZGYxZWUtZjViZC00MTQxLWE2ZGQtMjlmNmNmMjhkNDdlIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE2OTMyNTgxMDksImV4cCI6MTY5MzI2MTcwOSwiaWF0IjoxNjkzMjU4MTA5LCJqdGkiOiIwNGQzMDNiOC04NDA5LTQyZDAtYTlhYi01NzQzNGNlYjhlYzUiLCJlbWFpbCI6Imd1aWxoZXJtZS5oYWRkYWRAeW91c2UuY29tLmJyIn0.nG2sbNedWmAo45kt-UH97tKZDO-7kGFM-x9ygQlo3-TW19YewUmstWBdLp-T3clfoFgzug9CZDdmU3xlf60Kx9rBgQ-8zlpOJuKARd7i6JKmVpnfFXV7sbelL4TBVk5EyfrVM0SLteLBgkkxIHkjhVXJGxm5a4QK6qbV7ew67HQKfwUhQwxn9V-lrF8ySpQF4i48zr6AcoEB4kMdxa_XNSbyHYUHb8VgBqc5Tko8dkRkYtRmo5gPLOthqKaFHu8vkNF7GFI3tgh5A2p4GlKNbCKMHH_iZLO0jy-1p4P4dPz1ulMryApQvhIposdEZnvC6Pzpci-pQ6oh43KGpdnS8A";
  public static final String REFRESH_TOKEN = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.EjYIecefRju8Jt3fGGtmjLmsIpW-azI7sixta1gSzIHgxjPDPoIwLeK4TSwYP3FsJGD6g5HKWTqVDZNaSsaaVr7au7-8PiJRkOq9CtJqgkgbKCaAWMp40ibBqqQHSgdh59N48YK900EfeRHFWAakhm6fsyuu0X6e3xKarHpaWMBkg20F0eOYT1ozsa5AREIUCEwCmBnSzW1suBxVvZs4vW7MCXKLEiDKWAysRxHnpr8olVBJLOyBr9yMjDgo6CJ1Uud64CXsPPEVJi8skOPcZzUT6qZEGB3aouEoPamiYosz6WUK3UOe84kIoTQzF7njbgIYeV3wZuEROhQ_GTCrIw.yHcDmrncEPBhgvsc.ahmKiYTD9-lqmaHhzKGatiEVaJvZBT-3G3LJiPFtUwS8UN5faI8oSPjs_kFu9L3kcuLkhuNxwJEqJdAXzslq2pJAXaosK80zdCqMNjJc592GTpXKGBJTCCg1b44r5cNVa-UiNry8R-c92Mr25SRoMCdCTE3aryhDtAzQPLbiGvSpKyOJIX3pYiq_jxJUCRFGtVL3a92sVcmu0oGtiU72gJy70vJm_IlocW7FyNo0wL_ybZLa2YX2HYmI_1KamqXriQTBRTBYWKRo205l2U17uV1llRctUZgRfghVx1SM4XDQebsHYD-FoncMPemlM388cZRAVRJ8nqqEIU0dMlAyXteg1CVTSuW49wKdcg1uPqIGyzEfFkyXZuJca2ihXYg1rSRw2vBZerT5tPwAQEAtYkiGxcykZQJNr0NvjbwEimsYJXaPhf0qPWhJQy-U-ntmavYuo8RUlClC8Chp2FReonEKmynO73iOJBORRAruR4F59ls77pVolctV1WUZOkHwmV5KjWJj-AZYoZFexe1Q_lJvMu-Z2vBedjuBWArpE2oOPmo1wvE7wWQGzWx7cWsu1YRjrxm9aOXkAicBUIJrULrrmJzJPqyUJsxSvnhVQ9bIPHqXX2BKhQuKMq1heCW_kX9hZnISzdKl6LMHO44Y3r1IADQYBNkTVt0IFMb-U9LK0ggpSOht9jq5CFW2f_QBw5moQrZKLy-2Z2vZK6WEQbcGebAImemm9kCRQPZkSOfnkGyEXr5HPLdY9JlcCDhJj4lV3nlK5t-HZ6RPR4Z5kiZoqqeOgJxSuXlifDRaxEI3nOISUzInXmkldw5l6DtRiZz0FT54cLkXjgiwS-5VkC34YiOrEjCoZhtsCYblS6W7_2SoxvtwnwLzySQ1vcHXlgEwMR6Mn3L9G6CWP0dqMjQSHWbA1lI4mx_LVpYvfKSJpovpOd-UopaEgUfe2PbtiVmCnnzDAIjiiJ4MxVpf0r_B1e6osnBV-xWAOWW_WxBYMR2CWk2IDJowswNft2PQL0X_brVCpgmEXucm_vGJDZQMxm8u-iSiP9wKUXKdaawMpN3R_IZ1HuT_EGSR72CnpZpmHgYlBppvTwOnJPE3RFf1kxqQEgh4htsVIQ2p4Du33OA_CiW5DpaahcX0a3EVNzpzz0DlrnOLrMz4JoZhgrcX8ZPH4Z71w6EbD55Vz_cUcSNiyKCGDKhJlajeOBjVlTj8-H4OdwW-V058shT8X-Re4JJ25dUNlBLvX64C7fhrW1OnJ1C64Tyx0KLsxcFDERkbeW9vEtZNdiamKVca.sz4iFHRgHawMROz8BJ8vbQ";
  @Mock
  CommonConfiguration commonConfiguration;
  
  @Mock
  CognitoIdentityProviderClient cognitoIdentityProviderClient;
  
  @Mock
  AdminResetUserPasswordRequest.Builder adminResetUserPasswordRequestBuilder;
  
  @Mock
  ForgotPasswordRequest.Builder forgotPasswordRequestBuilder;
  
  @Mock
  ConfirmForgotPasswordRequest.Builder confirmForgotPasswordRequestBuilder;
  
  @Mock
  AdminCreateUserRequest.Builder userRequestBuilder;
  
  @Mock
  AdminUpdateUserAttributesRequest.Builder adminUpdateUserAttributesRequestBuilder;
  
  @Mock
  AdminInitiateAuthRequest.Builder adminInitiateAuthRequestBuilder;
  
  UserServiceImpl service;
  
  @BeforeEach
  void setUp() {
    service = new UserServiceImpl(
        commonConfiguration,
        userRequestBuilder,
        cognitoIdentityProviderClient,
        adminUpdateUserAttributesRequestBuilder,
        adminInitiateAuthRequestBuilder,
        adminResetUserPasswordRequestBuilder,
        forgotPasswordRequestBuilder,
        confirmForgotPasswordRequestBuilder);
  }
  
  @Test
  void shouldAdminCreateUser_When_UserSignUpRequest_IsOk() throws NoSuchAlgorithmException, InvalidKeyException {
    var userAttrs = new ArrayList<AttributeType>();
    var request = mock(UserSignUpRequest.class);
    var adminCreateUserRequest = AdminCreateUserRequest.builder().build();
    
    userAttrs.add(AttributeType.builder().name("name").value(request.getName()).build());
    userAttrs.add(AttributeType.builder().name("email").value(request.getEmail()).build());
    userAttrs.add(AttributeType.builder().name("gender").value("M").build());
    userAttrs.add(AttributeType.builder().name("address").value("SP").build());
    userAttrs.add(AttributeType.builder().name("birthdate").value(request.getDateOfBirth()).build());
    
    when(userRequestBuilder.username(request.getName())).thenReturn(userRequestBuilder);
    when(userRequestBuilder.userAttributes(userAttrs)).thenReturn(userRequestBuilder);
    when(userRequestBuilder.build()).thenReturn(adminCreateUserRequest);
    
    service.adminCreateUser(request);
    
    verify(cognitoIdentityProviderClient).adminCreateUser(adminCreateUserRequest);
  }
  
  @ParameterizedTest
  @CsvSource({"old.username,new.username"})
  void shouldUpdateAdminUserAttributes_WhenOldNameAndNewNameIsOk(String oldName, String newName) {
    List<AttributeType> userAttrs = new ArrayList<AttributeType>();
    var adminUpdateUserRequest = AdminUpdateUserAttributesRequest.builder().build();
    
    userAttrs.add(AttributeType.builder().name("name").value(newName).build());
    
    when(adminUpdateUserAttributesRequestBuilder.username(oldName)).thenReturn(adminUpdateUserAttributesRequestBuilder);
    when(adminUpdateUserAttributesRequestBuilder.userAttributes(userAttrs)).thenReturn(adminUpdateUserAttributesRequestBuilder);
    when(adminUpdateUserAttributesRequestBuilder.build()).thenReturn(adminUpdateUserRequest);
    
    service.adminUpdateUserAttributes(oldName, newName);
    
    verify(cognitoIdentityProviderClient).adminUpdateUserAttributes(adminUpdateUserRequest);
  }
  
  @ParameterizedTest
  @CsvSource({"nameUser,password-123"})
  void shouldNotReturnUserToken_WhenAdminInitiateAuthIsNotOk(String username, String password) throws NoSuchAlgorithmException, InvalidKeyException {
    
    var userToken = new UserToken();
    userToken.setIdToken(ID_TOKEN);
    userToken.setAccessToken(ACCESS_TOKEN);
    userToken.setRefreshToken(REFRESH_TOKEN);
    var adminInitiateAuthRequest = AdminInitiateAuthRequest.builder().build();
    
    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("USERNAME", username);
    requestParams.put("PASSWORD", password);
    requestParams.put("SECRET_HASH", "BN7tBGvUYQxMaxQAU711iW6cKsyr5pz4UQTrI60K5Ss=");
    
    when(commonConfiguration.getCognitoClientId()).thenReturn("123456");
    when(commonConfiguration.getCognitoClientSecretKey()).thenReturn("teste123");
    when(adminInitiateAuthRequestBuilder.authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)).thenReturn(adminInitiateAuthRequestBuilder);
    when(adminInitiateAuthRequestBuilder.authParameters(requestParams)).thenReturn(adminInitiateAuthRequestBuilder);
    when(adminInitiateAuthRequestBuilder.build()).thenReturn(adminInitiateAuthRequest);
    when(cognitoIdentityProviderClient.adminInitiateAuth(adminInitiateAuthRequest)).thenReturn(null);
    
    var result = service.adminInitiateAuth(username, password);
    
    verify(cognitoIdentityProviderClient).adminInitiateAuth(adminInitiateAuthRequest);
    assertNotEquals(userToken, result);
  }
  
  @ParameterizedTest
  @CsvSource({"nameUser,password-123"})
  void shouldReturnUserToken_WhenAdminInitiateAuthIsOk(String username, String password) throws NoSuchAlgorithmException, InvalidKeyException {
    
    var userToken = new UserToken();
    userToken.setIdToken(ID_TOKEN);
    userToken.setAccessToken(ACCESS_TOKEN);
    userToken.setRefreshToken(REFRESH_TOKEN);
    var adminInitiateAuthRequest = AdminInitiateAuthRequest.builder().build();
    var response = AdminInitiateAuthResponse.builder().authenticationResult(builder ->
            builder
                .accessToken(ACCESS_TOKEN)
                .idToken(ID_TOKEN)
                .refreshToken(REFRESH_TOKEN))
        .build();
    
    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("USERNAME", username);
    requestParams.put("PASSWORD", password);
    requestParams.put("SECRET_HASH", "BN7tBGvUYQxMaxQAU711iW6cKsyr5pz4UQTrI60K5Ss=");
    
    when(commonConfiguration.getCognitoClientId()).thenReturn("123456");
    when(commonConfiguration.getCognitoClientSecretKey()).thenReturn("teste123");
    when(adminInitiateAuthRequestBuilder.authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)).thenReturn(adminInitiateAuthRequestBuilder);
    when(adminInitiateAuthRequestBuilder.authParameters(requestParams)).thenReturn(adminInitiateAuthRequestBuilder);
    when(adminInitiateAuthRequestBuilder.build()).thenReturn(adminInitiateAuthRequest);
    when(cognitoIdentityProviderClient.adminInitiateAuth(adminInitiateAuthRequest)).thenReturn(response);
    
    var result = service.adminInitiateAuth(username, password);
    
    verify(cognitoIdentityProviderClient).adminInitiateAuth(adminInitiateAuthRequest);
    assertEquals(userToken, result);
  }
  
  @ParameterizedTest
  @CsvSource({"user.name"})
  void shouldResetUserPassword_WhenUsernameIsOk(String username) throws NoSuchAlgorithmException, InvalidKeyException {
    
    var request = AdminResetUserPasswordRequest.builder().build();
    var response = AdminResetUserPasswordResponse.builder().build();
    
    when(adminResetUserPasswordRequestBuilder.username(username)).thenReturn(adminResetUserPasswordRequestBuilder);
    when(adminResetUserPasswordRequestBuilder.build()).thenReturn(request);
    when(cognitoIdentityProviderClient.adminResetUserPassword(request)).thenReturn(response);
    
    service.adminResetUserPassword(username);
    
    verify(cognitoIdentityProviderClient).adminResetUserPassword(request);
  }
  
  @ParameterizedTest
  @CsvSource({"user.name"})
  void shouldExecuteForgotPassword_WhenUsernameIsOk(String username) throws NoSuchAlgorithmException, InvalidKeyException {
    
    var request = ForgotPasswordRequest.builder().build();
    var response = ForgotPasswordResponse.builder().build();
    
    when(forgotPasswordRequestBuilder.username(username)).thenReturn(forgotPasswordRequestBuilder);
    when(forgotPasswordRequestBuilder.secretHash("FDOH2VlR2/dlH9PA4fbz9oLE+EukuQbHWDdEmjdCDWE=")).thenReturn(forgotPasswordRequestBuilder);
    when(forgotPasswordRequestBuilder.build()).thenReturn(request);
    
    when(commonConfiguration.getCognitoClientId()).thenReturn("123456");
    when(commonConfiguration.getCognitoClientSecretKey()).thenReturn("teste123");
    when(cognitoIdentityProviderClient.forgotPassword(request)).thenReturn(response);
    
    service.forgotPassword(username);
    
    verify(cognitoIdentityProviderClient).forgotPassword(request);
  }
  
  @ParameterizedTest
  @CsvSource({"codeConfirmation, password-123, user.name"})
  void shouldConfirmeForgotPassword_WhenUsernameIsOk(String confirmationCode, String password, String username) throws NoSuchAlgorithmException, InvalidKeyException {
    
    var request = ConfirmForgotPasswordRequest.builder().build();
    var response = ConfirmForgotPasswordResponse.builder().build();
    
    when(confirmForgotPasswordRequestBuilder.confirmationCode(confirmationCode)).thenReturn(confirmForgotPasswordRequestBuilder);
    when(confirmForgotPasswordRequestBuilder.password(password)).thenReturn(confirmForgotPasswordRequestBuilder);
    when(confirmForgotPasswordRequestBuilder.username(username)).thenReturn(confirmForgotPasswordRequestBuilder);
    when(confirmForgotPasswordRequestBuilder.secretHash("FDOH2VlR2/dlH9PA4fbz9oLE+EukuQbHWDdEmjdCDWE=")).thenReturn(confirmForgotPasswordRequestBuilder);
    when(confirmForgotPasswordRequestBuilder.build()).thenReturn(request);
    
    when(commonConfiguration.getCognitoClientId()).thenReturn("123456");
    when(commonConfiguration.getCognitoClientSecretKey()).thenReturn("teste123");
    when(cognitoIdentityProviderClient.confirmForgotPassword(request)).thenReturn(response);
    
    service.confirmForgotPassword(confirmationCode, password, username);
    
    verify(cognitoIdentityProviderClient).confirmForgotPassword(request);
  }
}