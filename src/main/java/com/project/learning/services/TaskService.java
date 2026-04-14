package com.project.learning.services;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.learning.models.Task;
import com.project.learning.models.User;
import com.project.learning.repositories.TaskRepository;
import com.project.learning.services.exceptions.DataBindingViolationException;
import com.project.learning.services.exceptions.ObjectNotFoundException;

/**
 * Service responsável pelas regras de negócio relacionadas à entidade Task.
 *
 * RELAÇÕES IMPORTANTES:
 * - Usa TaskRepository para acesso ao banco
 * - Depende de UserService para validar e recuperar o usuário associado
 *
 * OBS:
 * Toda lógica de consistência deve ficar aqui (não no controller)
 */

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    /**
     * Busca uma tarefa pelo ID.
     *
     * REGRA:
     * - Se não existir, lança exceção 
    */

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
             "Tarefa não encontada! Id: " + id + ", Tipo: "+ Task.class.getName()
        ));
    }


    /**
     * Retorna todas as tarefas associadas a um usuário.
     *
     * OBS:
     * - Usa query derivada do Spring Data JPA (findByUser_Id)
    */
    public List<Task> findAllByUserId(Long userId) {
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }

    /**
     * Cria uma nova tarefa.
     *
     * REGRAS IMPORTANTES:
     * - Garante que o usuário existe antes de associar
     * - Força ID nulo para evitar sobrescrita acidental (update)
     *
     * TRANSACTIONAL:
     * - Garante consistência da operação no banco
    */
    @Transactional
    public Task create(Task obj) {
        // Valida e recupera o usuário do banco
        User user = this.userService.findById(obj.getUser().getId());
        // Garante que será uma nova entidade
        obj.setId(null);
        // Associa entidade gerenciada (evita problemas com JPA)
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;

    }

    /**
     * Atualiza uma tarefa existente.
     *
     * PADRÃO:
     * - Busca entidade atual no banco (garante existência)
     * - Atualiza apenas campos permitidos (evita sobrescrita indevida)
    */
    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    /**
     * Remove uma tarefa pelo ID.
     *
     * FLUXO:
     * - Verifica se existe
     * - Tenta excluir
     * - Em caso de erro (ex: FK), lança exceção amigável
    */
    public void delete(Long id) {
        // Garante que a entidade existe antes de deletar
        findById(id);
        try{
            this.taskRepository.deleteById(id);
        }catch (DataBindingViolationException e){
            throw new DataBindingViolationException(
                "Não é possível excluir pois há entidades relacionadas"
            );
        }
    }


}
