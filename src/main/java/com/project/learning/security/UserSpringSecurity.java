package com.project.learning.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.learning.models.enums.ProfileEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Implementação do UserDetails do Spring Security.
 *
 * RESPONSABILIDADE:
 * - Adaptar a entidade User da aplicação para o formato esperado pelo Spring Security
 *
 * RELAÇÃO COM O SISTEMA:
 * - Usado durante autenticação (login)
 * - Armazenado no contexto de segurança (SecurityContext)
 * - Utilizado para autorização (roles/perfis)
 */

@NoArgsConstructor
@Getter
public class UserSpringSecurity implements UserDetails{
    
    private Long id;
    private String username;
    private String password;

    /**
     * Lista de permissões/roles do usuário.
     *
     * IMPORTANTE:
     * - Spring Security trabalha com GrantedAuthority
     * - Aqui convertemos ProfileEnum → SimpleGrantedAuthority
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Construtor que recebe dados da aplicação e adapta para o formato do Spring Security.
     *
     * CONVERSÃO IMPORTANTE:
     * - ProfileEnum → String ("ROLE_ADMIN", etc.)
     * - String → SimpleGrantedAuthority
     */
    public UserSpringSecurity(Long id, String username, String password, Set<ProfileEnum> profileEnums) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = profileEnums.stream().map(x -> new SimpleGrantedAuthority(x.getDescription())).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasRole(ProfileEnum profileEnum) {
        return getAuthorities().contains(new SimpleGrantedAuthority(profileEnum.getDescription()));
    }
    
}
