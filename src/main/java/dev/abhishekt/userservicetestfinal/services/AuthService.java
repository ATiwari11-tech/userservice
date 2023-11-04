package dev.abhishekt.userservicetestfinal.services;

import dev.abhishekt.userservicetestfinal.dtos.UserDTO;
import dev.abhishekt.userservicetestfinal.models.Role;
import dev.abhishekt.userservicetestfinal.models.Session;
import dev.abhishekt.userservicetestfinal.models.SessionStatus;
import dev.abhishekt.userservicetestfinal.models.User;
import dev.abhishekt.userservicetestfinal.repositories.SessionRepository;
import dev.abhishekt.userservicetestfinal.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.swing.text.html.Option;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public AuthService(UserRepository userRepository,SessionRepository sessionRepository,BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    public ResponseEntity<UserDTO> login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){//if this password user.getPassword() the
            //hashed one was not generated through has string password then do not allow this
            //return null;
            throw new RuntimeException("Wrong User Name And Password");
        }
//        if(!user.getPassword().equals(password)){
//            return null;
//        }
//        String token = RandomStringUtils.randomAlphanumeric(30);
        //JWT Token Code
        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm alg = Jwts.SIG.HS512; //or HS384 or HS256
        SecretKey key = alg.key().build();
        System.out.println("Secret key:"+key);
        //Put following in JWT Token
        //user_id
        //user_email
        //roles
//        String message = "{\n" +
//                "  \"email\": \"a.t@gmail.com\",\n" +
//                " \"roles\":[\n" +
//                " \"Student\",\n" +
//                " \"TA\"\n" +
//                " ],\n" +
//                "}";
        Map<String,Object> jsonForJWT = new HashMap<>();
        jsonForJWT.put("email",user.getEmail());
        jsonForJWT.put("roles",user.getRoles());
        jsonForJWT.put("createdAt",new Date());

        jsonForJWT.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);

// Create the compact JWS:
//        String jws = Jwts.builder().content(content, "text/plain").signWith(key, alg).compact();
        String jws = Jwts.builder().claims(jsonForJWT)
                .signWith(key, alg).compact();

// Parse the compact JWS:
//        content = Jwts.parser().verifyWith(key).build().parseSignedContent(jws).getPayload();

//        assert message.equals(new String(content, StandardCharsets.UTF_8));

        //
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
//        session.setToken(token);
        session.setToken(jws);
        session.setUser(user);
        sessionRepository.save(session);
        UserDTO userDTO = UserDTO.from(user);
        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+jws);
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
//        user.setPassword(password);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return UserDTO.from(savedUser);
    }
    public SessionStatus validate(String token,Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,userId);
        if(sessionOptional.isEmpty()){
            return SessionStatus.ENDED;
//            return null;
        }
        Session session = sessionOptional.get();
        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return SessionStatus.ENDED;
        }
        //Read the data from JWT below
        Jws<Claims> claimsJws = Jwts.parser().build().parseSignedClaims(token);
        String email  = (String) claimsJws.getPayload().get("email");
        List<Role> roles = (List<Role>) claimsJws.getPayload().get("roles");
        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");
        Date expiryAt = (Date) claimsJws.getPayload().get("expiryAT");
        if(createdAt.before(new Date()))
            return SessionStatus.ENDED;
        return SessionStatus.ACTIVE;
    }
}


//auth-token:eyJhbGciOiJIUzUxMiJ9.eyJjcmVhdGVkQXQiOjE2OTgyNTgwODE5MzksInJvbGVzIjpbXSwiZXhwaXJ5QXQiOjE5NjU4LCJlbWFpbCI6ImEudEBnbWFpbC5jb20ifQ.Vgi6sgu0ZDas96Q1g1CxvWg71ZFpVokLMkeIVbn8rcTFL8JW8hg6qKciSbSNbLvvMJVBAsPAQUW-6XuHHfidpg