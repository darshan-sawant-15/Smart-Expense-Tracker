package com.springboot.expensetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class MySecurityConfiguration {
	@Bean
	UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider getAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(this.getPasswordEncoder());
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		return daoAuthenticationProvider;
	}

	@Bean
	SecurityFilterChain getFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authenticationProvider(getAuthenticationProvider());
        httpSecurity
                .authorizeHttpRequests(requests -> requests.requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER").requestMatchers("/**").permitAll())
				.formLogin(formLogin -> formLogin.loginPage("/login").defaultSuccessUrl("/user/dashboard")
						.loginProcessingUrl("/login"))
				.csrf(csrf -> csrf.disable());

		return httpSecurity.build();
	}

}
