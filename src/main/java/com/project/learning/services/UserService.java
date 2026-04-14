package com.project.learning.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.learning.models.User;
import com.project.learning.repositories.UserRepository;
import com.project.learning.services.exceptions.DataBindingViolationException;
import com.project.learning.services.exceptions.ObjectNotFoundException;

/**
 * Service responsável pelas regras de negócio da entidade User.
 *
 * RELAÇÕES IMPORTANTES:
 * - Usa UserRepository para persistência
 * - Lança exceções customizadas que são tratadas pelo GlobalExceptionHandler
 *
 * PADRÃO:
 * - Nunca retorna null
 * - Sempre valida existência antes de operações críticas
 */


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Busca um usuário pelo ID.
     *
     * REGRA:
     * - Se não existir, lança ObjectNotFoundException
    */

    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
            "Usuário não encontado: Id: " + id + ", Tipo: "+ User.class.getName()
        ));
    }

    /**
     * Cria um novo usuário.
     *
     * REGRA:
     * - ID deve ser null para evitar update acidental
    */
    @Transactional
    public User create(User obj){
         // Garante que será uma nova entidade no banco
        obj.setId(null);

        return this.userRepository.save(obj);
    }

    /**
     * Atualiza dados do usuário.
     *
     * PADRÃO:
     * - Busca entidade atual no banco
     * - Atualiza apenas campos permitidos
     *
     * IMPORTANTE:
     * Atualmente só atualiza senha (ver observações abaixo)
     */

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword((obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete (Long id){
        findById(id);

        try {
            this.userRepository.deleteById(id);
        }catch (Exception e){
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }

}
