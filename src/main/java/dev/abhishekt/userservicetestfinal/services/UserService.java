package dev.abhishekt.userservicetestfinal.services;

import dev.abhishekt.userservicetestfinal.dtos.UserDTO;
import dev.abhishekt.userservicetestfinal.models.Role;
import dev.abhishekt.userservicetestfinal.models.User;
import dev.abhishekt.userservicetestfinal.repositories.RoleRepository;
import dev.abhishekt.userservicetestfinal.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    public UserService(UserRepository userRepository,RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDTO getUserDetails(Long userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty())
            return null;
        return UserDTO.from(userOptional.get());
    }
    public UserDTO setUserRoles(Long userId, List<Long> roleIds){
        Optional<User> userOptional =userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByIdIn(roleIds);
        if(userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();
        user.setRoles(Set.copyOf(roles));
        User savedUser = userRepository.save(user);
        return UserDTO.from(savedUser);
    }
}
