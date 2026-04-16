package com.project.learning.configs;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuração de segurança da aplicação.
 *
 * RESPONSABILIDADES:
 * - Definir regras de autenticação/autorização
 * - Configurar política de sessão (stateless para APIs REST)
 * - Configurar CORS
 * - Definir encoder de senha
 *
 * CONTEXTO:
 * - Preparado para uso com JWT (stateless)
 * - Integra com Spring Security
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

     /**
     * Endpoints públicos acessíveis sem autenticação (GET por padrão)
     */
    private static final String[] PUBLIC_MATCHES = {
        "/"
    };

    /**
     * Endpoints públicos para requisições POST
     * Ex: criação de usuário e autenticação (login)
     */

    private static final String[] PUBLIC_MATCHES_POST = {
        "/user",
        "/login"
    };

     /**
     * Configuração principal de segurança HTTP.
     *
     * FLUXO:
     * - Desabilita CSRF (necessário para APIs stateless)
     * - Configura permissões de endpoints
     * - Define política de sessão como STATELESS (JWT)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

         // Habilita CORS e desabilita CSRF (não usamos sessão)
        http.cors().and().csrf().disable();

        http.authorizeRequests()
            // Permite acesso público aos endpoints POST definidos
            .antMatchers(HttpMethod.POST, PUBLIC_MATCHES_POST).permitAll()

            // Permite acesso público aos endpoints GET definidos
            .antMatchers(PUBLIC_MATCHES).permitAll()

            // Qualquer outra requisição precisa de autenticação
            .anyRequest().authenticated();
        
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

     /**
     * Configuração de CORS para permitir chamadas externas (ex: frontend).
     *
     * OBS:
     * - applyPermitDefaultValues libera:
     *   - origens padrão
     *   - headers comuns
     *
     * - Aqui customizamos métodos permitidos
     */

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
         // Define métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

     /**
     * Bean responsável por criptografar senhas.
     *
     * RELAÇÃO COM UserService:
     * - Deve ser usado ao salvar ou atualizar senha do usuário
     * - Evita armazenamento de senha em texto puro
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
