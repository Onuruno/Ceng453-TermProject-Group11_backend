package com.group11.server;

import com.group11.server.service.UsersDetailsService;
import com.group11.server.utils.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
    private final UsersDetailsService usersDetailsService;

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfigurer(UsersDetailsService usersDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.usersDetailsService = usersDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * This method configures authentication part of Spring Security by passing
     * DaoAuthenticationProvider instance to AuthenticationManagerBuilder instance.
     * This way authentication provider is added to the Spring Security system
     * which will be used on user authentication
     *
     * @param auth AuthenticationManagerBuilder instance that is main authentication system
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }

    /**
     * This method configures HTTP security part of Spring Security by passing
     * disabling CSRF and authorizing everyone to reach Swagger UI, login and
     * register pages. As a final effect it adds Jwttoken filter into HTTP filter
     * chain and changes session policy to stateless.
     *
     * @param http HttpSecurity instance that is web authorization system
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll()
                .anyRequest()
                .fullyAuthenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


    /**
     * This method creates a new BCryptPasswordEncoder instance which is
     * used to encode password of users to sae in database.
     *
     * @return A new BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This method creates a new DaoAuthenticationProvider instance which is
     * used to authenticate users by matching passwords encoded by BCryptPasswordEncoder.
     *
     * @return A new DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usersDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * This method creates a new AuthenticationManager bean.
     * This is added here because the Bean of this manageer cannot be created without declaring.
     *
     * @return A new AuthenticationManager instance
     * @throws Exception *
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}