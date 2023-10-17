package dev.abhishekt.userservicetestfinal.services;

import dev.abhishekt.userservicetestfinal.models.Role;
import dev.abhishekt.userservicetestfinal.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    public Role createRole(String name){
        Role role = new Role();
        role.setRole(name);
        return roleRepository.save(role);
    }
}
