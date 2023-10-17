package dev.abhishekt.userservicetestfinal.controllers;

import dev.abhishekt.userservicetestfinal.dtos.*;
import dev.abhishekt.userservicetestfinal.models.SessionStatus;
import dev.abhishekt.userservicetestfinal.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO requestDTO){
        return authService.login(requestDTO.getEmail(),requestDTO.getPassword());
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDTO request){
        return authService.logout(request.getToken(),request.getUserId());
    }
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignUpRequestDTO requestDTO){
        UserDTO userDTO = authService.signUp(requestDTO.getEmail(),requestDTO.getPassword());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(ValidateTokenRequestDTO requestDTO){
        SessionStatus sessionStatus = authService.validate(requestDTO.getToken(),requestDTO.getUserId());
        return new ResponseEntity<>(sessionStatus,HttpStatus.OK);
    }
}
