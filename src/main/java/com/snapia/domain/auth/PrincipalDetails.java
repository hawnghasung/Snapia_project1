package com.snapia.domain.auth;

import com.snapia.domain.entity.Users;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class PrincipalDetails implements UserDetails {
    private final Users users;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + users.getRole().name());
    }

    @Override
    public String getPassword() {

        return users.getPassword();
    }

    public PrincipalDetails(Users users) {
        this.users = users;
    }

    @Override
    public String getUsername() {
        return users.getSnapiaId();
    }

    public String getName() {
        return users.getName();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }


}
