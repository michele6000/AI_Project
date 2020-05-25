package it.polito.ai.esercitazione3.security;

import it.polito.ai.esercitazione3.security.JWT.JwtConfigurer;
import it.polito.ai.esercitazione3.security.JWT.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .httpBasic()
      .disable()
      .csrf()
      .disable()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .antMatchers("/media/**")
      .permitAll()
      .antMatchers("/notification/**")
      .permitAll()
      .antMatchers("/auth/signin")
      .permitAll()
      .antMatchers(HttpMethod.GET, "/API/**")
      .authenticated()
      .antMatchers(HttpMethod.POST, "/API/**")
      .authenticated()
      .anyRequest()
      .authenticated()
      .and()
      .exceptionHandling()
      .authenticationEntryPoint(
        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
      )
      .and()
      .apply(new JwtConfigurer(jwtTokenProvider));
  }
}
