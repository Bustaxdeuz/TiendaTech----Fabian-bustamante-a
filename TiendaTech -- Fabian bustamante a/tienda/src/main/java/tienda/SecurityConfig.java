package tienda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tienda.domain.Ruta;
import tienda.services.RutaService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy RutaService rutaService) throws Exception {
        var rutas = rutaService.getRutas();

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authorize -> {
            authorize
                    .requestMatchers(
                            "/webjars/**",
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/static/**",
                            "/login",
                            "/registro/**")
                    .permitAll();

            for (Ruta ruta : rutas) {
                if (ruta.isRequiereRol() && ruta.getRol() != null) {
                    authorize.requestMatchers(ruta.getRuta()).hasRole(ruta.getRol().getRol());
                } else {
                    authorize.requestMatchers(ruta.getRuta()).permitAll();
                }
            }

            authorize.anyRequest().authenticated();
        });

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso_denegado")
        ).sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}

