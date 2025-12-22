package com.project.user_service.domain.entity;

import com.project.user_service.domain.enums.Roles;
import com.project.user_service.service.RolePermissionMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Builder
public class UserDetail implements UserDetails {

    private UUID id;
    private Roles role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authoritySet = new HashSet<>(RolePermissionMapping.getAuthoritiesForRole(this.role));
        authoritySet.add(new SimpleGrantedAuthority("ROLE_"+this.role.toString()));
        return authoritySet;
    }

    @Override
    public @Nullable String getPassword() {
        return " ";
    }

    @Override
    public String getUsername() {
        return " ";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
