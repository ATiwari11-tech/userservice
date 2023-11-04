package dev.abhishekt.userservicetestfinal.controllers;

import dev.abhishekt.userservicetestfinal.dtos.CreateRoleRequestDTO;
import dev.abhishekt.userservicetestfinal.models.Role;
import dev.abhishekt.userservicetestfinal.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private RoleService roleService;
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequestDTO requestDTO){
        Role role = roleService.createRole(requestDTO.getName());
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
