package ir.garm.cache.domain.dto;

import lombok.Data;

@Data
public class AccessTokenDto {
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String access_token;
    private Long createAt;
}
