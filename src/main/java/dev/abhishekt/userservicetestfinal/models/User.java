package dev.abhishekt.userservicetestfinal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.abhishekt.userservicetestfinal.security.CustomSpringUserDetails;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonDeserialize(as = User.class)
public class User extends BaseModel{
    private String email;
    private String password;
    @ManyToMany(fetch= FetchType.EAGER)//By Default it will be fetched lazily and we will get
    // error while fetching the user details from the database because the roles will not be fetched as they are
    // not from the same transaction i.e. Roles are coming from CustomGrantedAuthority class and User is coming from
    // CustomSpringUserDetails classy
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();
}
