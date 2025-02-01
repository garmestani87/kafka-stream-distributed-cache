package ir.garm.cache.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenErrorDto {
    private String error;
    private String description;
}
