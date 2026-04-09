package com.project.learning.configs;

// Importa a anotação que marca a classe como uma configuração do Spring
import org.springframework.context.annotation.Configuration;

// Classes usadas para configurar CORS no Spring MVC
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Indica que essa classe contém configurações do Spring (equivalente a um XML de config antigo)
@Configuration
// Habilita configurações customizadas do Spring MVC
// IMPORTANTE: essa anotação sobrescreve várias configurações automáticas do Spring Boot
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    public void addCorsMappings(CorsRegistry registry){
        
        // Define que TODAS as rotas da aplicação (/**)
        // aceitarão requisições de outras origens (CORS liberado globalmente)
        registry.addMapping("/**");
    }
}