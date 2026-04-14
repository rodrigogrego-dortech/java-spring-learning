package com.project.learning.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * DTO responsável por padronizar as respostas de erro da API.
 *
 * Essa classe geralmente é utilizada em conjunto com um @ControllerAdvice
 * (handler global de exceções), garantindo que todos os erros retornem
 * no mesmo formato para o cliente.
 *
 * Exemplo de resposta:
 * {
 *   "status": 400,
 *   "message": "Validation error",
 *   "errors": [
 *     { "field": "email", "message": "must not be null" }
 *   ]
 * }
 */

@Getter
@Setter
@RequiredArgsConstructor 
@JsonInclude(JsonInclude.Include.NON_NULL) // Evita enviar campos nulos na resposta JSON
public class ErrorResponse {
     // Código HTTP da resposta (ex: 400, 404, 500)
    private final int status;
      // Mensagem principal do erro (resumo)
    private final String message;
    // Stack trace opcional (usado geralmente apenas em ambiente de desenvolvimento/debug)
    private String stackTrace;
     // Lista de erros de validação (ex: erros de @Valid em DTOs)
    private List<ValidationError> errors;


     /**
     * Classe interna que representa um erro de validação específico por campo.
     *
     * Exemplo:
     * field = "email"
     * message = "must not be null"
     *
     * Essa estrutura é usada principalmente quando o Spring retorna
     * MethodArgumentNotValidException.
     */
    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }


    /**
     * Adiciona um erro de validação na lista.
     *
     * - Inicializa a lista apenas quando necessário (lazy initialization)
     *   para evitar alocação desnecessária de memória.
     *
     * - Esse método normalmente é chamado dentro de um ExceptionHandler
     *   que trata erros de validação do Spring (ex: MethodArgumentNotValidException).
     */
    public void addValidationError(String field, String message){
        if(Objects.isNull(errors)){
            this.errors = new ArrayList<>();
        }
        this.errors.add(new ValidationError(field, message));

    }
}
