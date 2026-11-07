package com.example.mybatistest01.service.impl;

import com.example.mybatistest01.dto.AuthorityDto;
import com.example.mybatistest01.entity.Role;
import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.entity.UserRole;
import com.example.mybatistest01.service.RoleService;
import com.example.mybatistest01.service.UserRoleService;
import com.example.mybatistest01.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 从数据库加载用户：user 表取账号，user_role + role 表取角色，转成 ROLE_xxx 权限。
 * <p>
 * JWT 过滤器和小票（login）都复用它，保证两条路径拿到的权限完全一致。
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User probe = new User();
        probe.setUsername(username);

        User user = userService.getUsersByUserName(probe);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码不正确!");
        }
        return new AuthorityDto(user, loadAuthorities(user));
    }

    private List<GrantedAuthority> loadAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        UserRole probe = new UserRole();
        probe.setUserId(user.getId());
        List<UserRole> userRoles = userRoleService.getRoles(probe);
        if (userRoles == null || userRoles.isEmpty()) {
            return authorities;
        }

        for (UserRole userRole : userRoles) {
            Role role = roleService.getRoleById(userRole.getRoleId());
            if (role == null || role.getRoleName() == null) {
                continue;
            }
            String roleName = role.getRoleName().trim();
            if (roleName.isEmpty()) {
                continue;
            }
            roleName = roleName.toUpperCase();
            if (!roleName.startsWith(ROLE_PREFIX)) {
                roleName = ROLE_PREFIX + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
        }
        return authorities;
    }
}
