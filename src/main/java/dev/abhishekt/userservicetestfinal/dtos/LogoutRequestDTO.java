package dev.abhishekt.userservicetestfinal.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {
    private String token;
    private Long userId;
}
