package br.com.juliancambraia.api.aws.cognito.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserToken {
  String idToken;
  String accessToken;
  String refreshToken;
}
