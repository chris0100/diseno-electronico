package com.proyecto.diselec;

import com.proyecto.diselec.auth.handler.LoginSuccessHandler;
import com.proyecto.diselec.services.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    //Clase de login exitoso
    @Autowired
    private LoginSuccessHandler successHandler;

    // Clase donde se toman los datos del usuario
    @Autowired
    private JpaUserDetailsService userDetailService;

    //Encripatdor para password
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/img/**", "/locale", "/servicios",
                "/catalogo/**", "/contactenos", "/registro", "/info-producto/**","/nuevo-usuario/**",
                "/form-recuperar-password", "/generar-token-passsword/**","/reestablecer-password/**" , "/validar-token/**", "/panel-admin/uploads/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successHandler)
                .loginPage("/login")
                .permitAll()
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error_403");
    }



    //envia los datos de usuario y codifica el password ingresado para compararlo y enviar a loginsuccessHandler
    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception {
        build.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
    }


}
