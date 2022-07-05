package com.partner.jwt.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.partner.repository.BpiConfigRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource(name = "loginService")
	private UserDetailsService userDetailsService;

	@Autowired
    BpiConfigRepository bpiConfigRepository;
	
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;
	
	/**
	 * 	@return --> Allowed matcher value pull from database if not available, by default allow only token generation API i.e., "/auth/token"
	 */
	/*@Bean
    public String getJwtAntMatchersValue() {
		Optional<BpiConfig> optBpiConfig = bpiConfigRepository.findByConfigKey(CintapBpiConstants.JWT_AUTHORIZE_MATCHERS_KEY);
		if(optBpiConfig.isPresent()) {
			log.info("JWT AUTHORIZATION MATCHER VALUE {}",optBpiConfig.get().getConfigValue());
			return optBpiConfig.get().getConfigValue();
		}else {
			log.info("Missing "+CintapBpiConstants.JWT_AUTHORIZE_MATCHERS_KEY+" Key from Bpi config table");
			return null;
		}
	}*/
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
		.passwordEncoder(encoder());
	}

	@Bean
	public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationFilter();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().
		authorizeRequests()
		.antMatchers("/auth/**","/2fa/**","/v1/2fa/**",
				"/v2/api-docs","/v1/login/**","/v1/user/**","/v1/validateKey/**","/v1/web/sendmail/**",
                "/configuration/ui","/v1/otp/**",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**","/v1/images/**","/actuator/**").permitAll()
		.anyRequest().authenticated()
		.and()
		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http
		.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		
	}

	@Bean
	public BCryptPasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}
}
