package com.project.learning.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.learning.models.User;
import com.project.learning.repositories.UserRepository;
import com.project.learning.security.UserSpringSecurity;

/**
 * Implementação do UserDetailsService do Spring Security.
 *
 * RESPONSABILIDADE:
 * - Buscar o usuário no banco durante o processo de autenticação (login)
 *
 * FLUXO:
 * - Spring Security chama automaticamente esse método ao tentar autenticar um usuário
 * - O username é recebido (ex: via login)
 * - O usuário é buscado no banco
 * - Retorna um UserDetails (adaptado para o Spring)
 *
 * RELAÇÃO:
 * - Usa UserRepository para acessar o banco
 * - Converte User → UserSpringSecurity
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Método chamado automaticamente pelo Spring Security durante o login.
     *
     * - O parâmetro "username" vem da requisição de autenticação
     * - Deve retornar um objeto que implementa UserDetails
     * - Se não encontrar o usuário, deve lançar UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados
        User user = this.userRepository.findByUsername(username);

        // Se não encontrar, o Spring interrompe o processo de autenticação
        if(Objects.isNull(user))
            throw new UsernameNotFoundException("Usuário não encontrado " + username);

        /**
         * Converte a entidade User para UserSpringSecurity.
         *
         * RELAÇÃO:
         * - User: entidade da aplicação
         * - UserSpringSecurity: formato que o Spring Security entende
         */
        return new UserSpringSecurity(user.getId(), user.getUsername(), user.getPassword(), user.getProfiles());
    }
    
}
