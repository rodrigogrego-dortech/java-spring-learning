package com.project.learning.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.project.learning.models.Task;
import com.project.learning.models.Task.CreateTask;
import com.project.learning.models.Task.UpdateTask;
import com.project.learning.services.TaskService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/task")
@Validated

public class TaskController {
    
    //o final vai garantir que a dependencia nunca será alterada, tornando o objeto seguro (Clean Code)]
    //Facilita os testes com o mock fácil
    //O objeto não é criado sem a dependência
    //A falha é rápida
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        Task obj = this.taskService.findById(id);
        return ResponseEntity.ok(obj);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> findAllByUserId(@PathVariable Long userId){
        List<Task> objs = this.taskService.findAllByUserId((userId));  
        return ResponseEntity.ok().body(objs);
    }

    @PostMapping
    @Validated(CreateTask.class)
    public ResponseEntity<Void> create(@Valid @RequestBody Task obj){
        this.taskService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }  

    @PutMapping("/{id}")
    @Validated(UpdateTask.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Task obj, @PathVariable Long id ){
        obj.setId(id);
        this.taskService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    

   
    
}
