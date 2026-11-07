package com.example.mybatistest01.config;

import com.example.mybatistest01.security.JwtAuthenticationTokenFilter;
import com.example.mybatistest01.security.RestAccessDeniedHandler;
import com.example.mybatistest01.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 无状态（JWT）安全配置。
 * <ul>
 *     <li>不再使用 formLogin / 内存用户：登录由 /publicrs/auth/login 签发 token</li>
 *     <li>除白名单外，所有请求都必须携带有效 token</li>
 *     <li>未登录返回 JSON 401，权限不足返回 JSON 403</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 不需要认证即可访问的路径。
     * <p>
     * 注意：只放行认证入口和演示接口。角色 / 用户角色管理虽然在 /publicrs 前缀下，
     * 但**不在**白名单内，必须登录且具备 ADMIN 角色（见各 Controller 上的 @PreAuthorize）。
     */
    private static final String[] WHITELIST = {
            "/",
            "/hello",
            "/publicrs/auth/**",
            "/publicrs/user/test",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html",
            "/favicon.ico"
    };

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * 认证：改为从数据库加载用户（user + user_role + role）。
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 授权：无状态 + 白名单放行，其余一律需要认证。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(WHITELIST).permitAll()
                .anyRequest().authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler);

        http.formLogin().disable();
        http.httpBasic().disable();
        http.logout().disable();

        // JWT 过滤器要排在用户名密码过滤器之前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
