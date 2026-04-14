package com.project.learning.services.exceptions;

import javax.persistence.EntityNotFoundException;

/**
 * Exceção customizada para representar quando um recurso
 * não é encontrado na aplicação.
 *
 * CONTEXTO:
 * - Muito utilizada na camada de serviço (Service)
 * - Lançada quando uma entidade não é encontrada no banco
 *
 * RELAÇÃO COM GlobalExceptionHandler:
 * - É capturada pelo GlobalExceptionHandler
 * - Convertida em um ErrorResponse com status 404
 *
 * EXEMPLO DE USO:
 * userRepository.findById(id)
 *     .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
 */

public class ObjectNotFoundException extends  EntityNotFoundException{
    
     /**
         * Mensagem descritiva do erro.
         * Essa mensagem será retornada para o cliente via ErrorResponse.
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
