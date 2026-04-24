package com.project.learning.security;

import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


/**
 * Classe utilitária responsável por operações com JWT.
 *
 * RESPONSABILIDADES:
 * - Gerar token JWT
 * - Validar token
 * - Extrair informações (claims)
 *
 * CONTEXTO:
 * - Usada no fluxo de autenticação (login)
 * - Integrada com filtros de segurança (JWT Filter)
 */

@Component
public class JWTUtil {

    /**
     * Chave secreta usada para assinar o token.
     
     * Deve ser configurada no application.properties
     */

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Tempo de expiração do token (em milissegundos).
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Gera um token JWT a partir do username.
     *
     * FLUXO:
     * - Define o subject (username)
     * - Define data de expiração
     * - Assina o token com chave secreta
     */
    public String generateToken(String username) {
        SecretKey key = getKeyBySecret();
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date (System.currentTimeMillis() + this.expiration))
            .signWith(key)
            .compact();
    }

    /**
     * Gera a chave criptográfica a partir da string secreta.
     *
     */

    public SecretKey getKeyBySecret() {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());
        return key;
    }

    /**
     * Valida se o token é válido.
     *
     * REGRAS:
     * - Token precisa existir e ser parseável
     * - Deve conter username
     * - Não pode estar expirado
     */
    public boolean isValidToken(String token) {
        Claims claims = getClaims(token);
        if(Objects.nonNull(claims)){
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if(Objects.nonNull(username) && Objects.nonNull(expirationDate) && now.before(expirationDate))
                return true;
        }
        return false;
    }

    /**
     * Extrai os dados (claims) do token.
     *
     * OBS:
     * - Se o token for inválido, retorna null
     * - Evita quebrar fluxo com exception
     */
    private Claims getClaims(String token) {
        SecretKey key = getKeyBySecret();
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch (Exception e){
            return null;
        }
    }

}
