package dev.abhishekt.userservicetestfinal.security;

import dev.abhishekt.userservicetestfinal.models.User;
import dev.abhishekt.userservicetestfinal.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service//this is a service class
public class CustomSpringUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    public CustomSpringUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Optional<User> userOptional = userRepository.findByEmail(username);
       if(userOptional.isEmpty()){
           throw new UsernameNotFoundException("User Not Found");
       }
       User user = userOptional.get();
       return new CustomSpringUserDetails(user);
    }
}
