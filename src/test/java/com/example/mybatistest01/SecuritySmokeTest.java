package com.example.mybatistest01;

import com.example.mybatistest01.dto.AuthorityDto;
import com.example.mybatistest01.entity.Role;
import com.example.mybatistest01.entity.User;
import com.example.mybatistest01.entity.UserRole;
import com.example.mybatistest01.service.RoleService;
import com.example.mybatistest01.service.UserRoleService;
import com.example.mybatistest01.service.UserService;
import com.example.mybatistest01.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 鉴权冒烟测试。
 * <p>
 * 不连数据库：UserService / UserRoleService / RoleService 全部用 Mock 替代，
 * 只验证 Spring Security 过滤器链与 @PreAuthorize 的行为是否被改坏。
 */
@SpringBootTest(properties = {
        "jwt.secret=test-only-secret-0123456789abcdef0123456789abcdef0123456789abcdef",
        "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/not_used_by_tests?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai",
        "spring.datasource.password=not-used"
})
@AutoConfigureMockMvc
class SecuritySmokeTest {

    private static final String RAW_PASSWORD = "123456";
    private static final long ADMIN_ID = 1L;
    private static final long PLAIN_ID = 2L;
    private static final long ADMIN_ROLE_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRoleService userRoleService;

    @MockBean
    private RoleService roleService;

    private User admin;
    private User plain;

    @BeforeEach
    void setUp() {
        admin = user(ADMIN_ID, "admin");
        plain = user(PLAIN_ID, "plain");

        when(userService.getUsersByUserName(any(User.class))).thenAnswer(invocation -> {
            User probe = invocation.getArgument(0);
            String username = probe == null ? null : probe.getUsername();
            if ("admin".equals(username)) {
                return admin;
            }
            if ("plain".equals(username)) {
                return plain;
            }
            return null;
        });

        when(userRoleService.getRoles(any(UserRole.class))).thenAnswer(invocation -> {
            UserRole probe = invocation.getArgument(0);
            if (probe != null && probe.getUserId() == ADMIN_ID) {
                UserRole binding = new UserRole();
                binding.setUserId(ADMIN_ID);
                binding.setRoleId(ADMIN_ROLE_ID);
                return Collections.singletonList(binding);
            }
            return Collections.emptyList();
        });

        when(roleService.getRoleById(ADMIN_ROLE_ID)).thenReturn(role(ADMIN_ROLE_ID, "admin"));
    }

    @Test
    @DisplayName("公开接口：/hello 不需要 token")
    void helloIsPublic() throws Exception {
        mockMvc.perform(get("/hello").param("name", "x"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello x"));
    }

    @Test
    @DisplayName("无 token 访问受保护接口：401")
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("无效 token：401")
    void invalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/user").header("Authorization", "Bearer not.a.jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("已登录但无 ADMIN 角色：403")
    void plainUserGets403() throws Exception {
        mockMvc.perform(get("/user").header("Authorization", "Bearer " + tokenFor(plain)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("ADMIN 可查看用户列表，且响应不含 password 字段")
    void adminCanListUsers() throws Exception {
        when(userService.findAll()).thenReturn(Collections.singletonList(admin));

        mockMvc.perform(get("/user").header("Authorization", "Bearer " + tokenFor(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    @DisplayName("登录成功返回 token")
    void loginReturnsToken() throws Exception {
        mockMvc.perform(post("/publicrs/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"" + RAW_PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenHead").value("Bearer"));
    }

    private String tokenFor(User user) {
        return jwtTokenUtil.generateToken(new AuthorityDto(user));
    }

    private User user(long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(RAW_PASSWORD));
        return u;
    }

    private Role role(long id, String roleName) {
        Role r = new Role();
        r.setId(id);
        r.setRoleName(roleName);
        return r;
    }
}
