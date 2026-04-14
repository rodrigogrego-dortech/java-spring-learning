package com.project.learning.services.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * Exceção customizada para representar erros de inconsistência
 * no binding/relacionamento de dados entre entidades.
 *
 * CONTEXTO:
 * - Estende DataIntegrityViolationException para manter compatibilidade
 *   com exceções de banco de dados do Spring.
 * - Usada quando há problemas de associação lógica (não apenas SQL puro).
 *
 * RELAÇÃO COM GlobalExceptionHandler:
 * - Essa exceção é capturada no GlobalExceptionHandler
 * - Lá, ela é convertida em um ErrorResponse padronizado para o cliente
 */

public class DataBindingViolationException extends DataIntegrityViolationException {
    
    /**
     * Cria uma exceção com mensagem descritiva do problema.
     *
     * Essa mensagem será usada no ErrorResponse retornado pela API.
     */
    public DataBindingViolationException(String message) {
        super(message);
    }
}
