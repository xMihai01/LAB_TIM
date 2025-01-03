package com.gmail.doloiu22.dfss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /* Unrestricted access */
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/passwordReset", "/register")
                        .permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/home/**").hasAuthority("AUTH")
                        .requestMatchers("/images/public/**").permitAll()
                        .requestMatchers("/*")
                        .authenticated()
                )


                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .successHandler(customAuthenticationSuccessHandler) /* https://www.baeldung.com/spring-redirect-after-login */
                        .failureUrl("/login/error")
                        .permitAll())
                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout") // Specify the logout URL
                        .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                        .invalidateHttpSession(true) // Invalidate session
                );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder(); /* Accept int parameter as complexity strength, by default 10*/
    }
}