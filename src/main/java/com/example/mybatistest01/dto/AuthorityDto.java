package com.example.mybatistest01.dto;

import com.example.mybatistest01.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 用户 + 权限的适配层，供 Spring Security 使用。
 * <p>
 * 注意：这里的 isAccountNonExpired 等四个状态位统一返回 true，
 * 否则 SecurityContext 里的用户会被判定为不可用。
 */
public class AuthorityDto implements UserDetails {

    private final User user;

    private final Collection<? extends GrantedAuthority> authorities;

    public AuthorityDto(User user) {
        this(user, Collections.emptyList());
    }

    public AuthorityDto(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities == null ? Collections.emptyList() : authorities;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
