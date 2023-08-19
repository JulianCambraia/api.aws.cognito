package br.com.juliancambraia.api.aws.cognito.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminResetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;

@Configuration
@Data
public class CommonConfiguration {
  @Value("${aws.cognito.client-id}")
  private String cognitoClientId;
  @Value("${aws.region}")
  private String region;
  @Value("${aws.cognito.user.pool-id}")
  private String userPoolId;
  @Value("${aws.access-key}")
  private String accessKey;
  @Value("${aws.secret-key}")
  private String secretKey;
  @Value("${aws.cognito.client-secret}")
  private String cognitoClientSecretKey;
  
  @Bean
  public CognitoIdentityProviderClient cognitoClient() {
    return CognitoIdentityProviderClient.builder()
        .region(Region.of(region))
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }
  
  @Bean
  public AdminResetUserPasswordRequest.Builder adminListGroupsForUserRequest() {
    return AdminResetUserPasswordRequest.builder()
        .userPoolId(userPoolId);
  }
  
  @Bean
  public ForgotPasswordRequest.Builder forgotPasswordRequest() {
    return ForgotPasswordRequest.builder()
        .clientId(cognitoClientId);
  }
  
  @Bean
  public ConfirmForgotPasswordRequest.Builder confirmForgotPasswordRequest() {
    return ConfirmForgotPasswordRequest.builder()
        .clientId(cognitoClientId);
  }
  
  @Bean
  public AdminCreateUserRequest.Builder signUpRequest() {
    return AdminCreateUserRequest.builder()
        .userPoolId(userPoolId);
  }
  
  @Bean
  public AttributeType.Builder attributeType() {
    return AttributeType.builder();
  }
  
  @Bean
  public AdminUpdateUserAttributesRequest.Builder adminUpdateUserAttributesRequest() {
    return AdminUpdateUserAttributesRequest.builder()
        .userPoolId(userPoolId);
  }
  
  @Bean
  public AdminInitiateAuthRequest.Builder adminInitiateAuthRequest() {
    return AdminInitiateAuthRequest.builder()
        .clientId(cognitoClientId)
        .userPoolId(userPoolId);
  }
}
