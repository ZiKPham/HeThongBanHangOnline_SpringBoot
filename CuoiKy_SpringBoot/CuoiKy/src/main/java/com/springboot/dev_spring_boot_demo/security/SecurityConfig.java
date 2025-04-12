package com.springboot.dev_spring_boot_demo.security;

import javax.sql.DataSource;

import com.springboot.dev_spring_boot_demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CartService cartService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Định nghĩa các loại encoder sẽ sử dụng
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        
        // Encoder mặc định là bcrypt
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        
        // Thiết lập encoder khi không có prefix (cho trường hợp mật khẩu không có prefix)
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        
        return delegatingPasswordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        
        // Cấu hình truy vấn để lấy thông tin người dùng và quyền
        users.setUsersByUsernameQuery(
            "select username, password, enabled from users where username = ?");
        users.setAuthoritiesByUsernameQuery(
            "select username, authority from authorities where username = ?");
        
        return users;
    }
    
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        SessionFixationProtectionStrategy sessionFixationProtectionStrategy = new SessionFixationProtectionStrategy();
        sessionFixationProtectionStrategy.setMigrateSessionAttributes(true);
        return new CompositeSessionAuthenticationStrategy(
            Collections.singletonList(sessionFixationProtectionStrategy)
        );
    }
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, 
                                              HttpServletResponse response, 
                                              Authentication authentication) throws IOException, ServletException {
                // Chuyển giỏ hàng từ khách sang người dùng đăng nhập
                cartService.transferGuestCartToUser(authentication.getName());
                
                // Tiếp tục xử lý mặc định sau khi đăng nhập
                String targetUrl = determineTargetUrl(request, response);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
            }
            
            @Override
            protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
                // Kiểm tra nếu đăng nhập từ trang checkout
                String referer = request.getHeader("Referer");
                if (referer != null && referer.contains("checkout=true")) {
                    return "/shop/orders/create-from-cart";
                }
                return super.determineTargetUrl(request, response);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login**", "/register**", "/process-login", "/shop/products**", "/shop/cart/**", 
                                 "/shop/products/detail/**", "/shop/products/gaming", "/shop/products/office", "/shop/products/search**", 
                                 "/static/**", "/product-images/**", "/default/**",
                                 // Cho phép truy cập các trang chính sách và tin tức
                                 "/shop/news-tech", "/shop/doi-tra", "/shop/bao-hanh", "/shop/thanh-toan", "/shop/van-chuyen", "/shop/contact", "/shop/about")
                .permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/account/**", "/shop/orders/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessUrl("/shop")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
            );
        
        return http.build();
    }
}