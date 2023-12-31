package dev.abhishekt.userservicetestfinal.services;

import dev.abhishekt.userservicetestfinal.dtos.UserDTO;
import dev.abhishekt.userservicetestfinal.models.Session;
import dev.abhishekt.userservicetestfinal.models.SessionStatus;
import dev.abhishekt.userservicetestfinal.models.User;
import dev.abhishekt.userservicetestfinal.repositories.SessionRepository;
import dev.abhishekt.userservicetestfinal.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    public AuthService(UserRepository userRepository,SessionRepository sessionRepository){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    public ResponseEntity<UserDTO> login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();
        if(!user.getPassword().equals(password)){
            return null;
        }
        String token = RandomStringUtils.randomAlphanumeric(30);
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);
        UserDTO userDTO = new UserDTO();
        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+token);
        ResponseEntity<UserDTO> response = new ResponseEntity<>(userDTO,headers, HttpStatus.OK);
        return response;
    }
    public ResponseEntity<Void> logout(String token,Long userId){
        Optional<Session> sessionOptional =sessionRepository.findByTokenAndUser_Id(token,userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }
    public UserDTO signUp(String email,String password){
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        User savedUser = userRepository.save(user);
        return UserDTO.from(savedUser);
    }
    public SessionStatus validate(String token,Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        return SessionStatus.ACTIVE;
    }
}
