package br.com.juliancambraia.api.aws.cognito.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserSignUpRequest {
  private String name;
  private String password;
  private String email;
  private String phoneNumber;
  private String dateOfBirth;
}
