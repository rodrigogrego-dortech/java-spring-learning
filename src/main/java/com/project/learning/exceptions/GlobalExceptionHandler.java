package com.project.learning.exceptions;


import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.project.learning.services.exceptions.DataBindingViolationException;
import com.project.learning.services.exceptions.ObjectNotFoundException;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler global de exceções da aplicação.
 *
 * - Centraliza o tratamento de erros da API
 * - Garante que todas as respostas sigam o padrão definido em ErrorResponse
 *
 * RELAÇÃO COM ErrorResponse:
 * Essa classe é responsável por construir e retornar instâncias de ErrorResponse
 * para o cliente, padronizando o formato dos erros.
 */

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
    
    /**
     * Define se o stack trace deve ser incluído na resposta.
     * Normalmente habilitado apenas em ambiente de desenvolvimento.
     */
    @Value("${server.error.include-exception}")
    private boolean printStackTrace;

    /**
     * Trata erros de validação de DTOs anotados com @Valid.
     *
     * Esse método sobrescreve o comportamento padrão do Spring para:
     * - Customizar o formato da resposta
     * - Retornar lista de erros por campo usando ErrorResponse
     */
    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error. Check 'errors' field for details.");

         // Percorre todos os erros de validação e adiciona no ErrorResponse
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

     /**
     * Captura qualquer exceção não tratada explicitamente.
     *
     * IMPORTANTE:
     * Serve como fallback para evitar que erros escapem sem padronização.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception exception,
            WebRequest request) {
        final String errorMessage = "Unknown error occurred";

        // Log completo para análise posterior
        log.error(errorMessage, exception);
        return buildErrorResponse(
                exception,
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }
    
     /**
     * Trata erros de integridade de banco de dados.
     *
     * Exemplo:
     * - Violação de chave única
     * - FK inválida
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request) {

        // Extrai a causa mais específica do erro (geralmente mais útil)
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Failed to save entity with integrity problems: " + errorMessage, dataIntegrityViolationException);
        return buildErrorResponse(
                dataIntegrityViolationException,
                errorMessage,
                HttpStatus.CONFLICT,
                request);
    }

      /**
     * Trata erros de validação em parâmetros (ex: @RequestParam, @PathVariable).
     *
     * Diferente de MethodArgumentNotValidException (que é para DTOs).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request) {
        log.error("Failed to validate element", constraintViolationException);
        return buildErrorResponse(
                constraintViolationException,
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    /**
     * Trata exceções de entidade não encontrada (camada de serviço).
     *
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException objectNotFoundException,
            WebRequest request) {
        log.error("Failed to find the requested element", objectNotFoundException);
        return buildErrorResponse(
                objectNotFoundException,
                HttpStatus.NOT_FOUND,
                request);
    }

    /**
     * Trata erros relacionados a inconsistência de dados entre entidades.
     *
     * Exemplo:
     * - Relacionamentos inválidos
     * - Associação incorreta de objetos
    */
    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(
            DataBindingViolationException dataBindingViolationException,
            WebRequest request) {
        log.error("Failed to save entity with associated data", dataBindingViolationException);
        return buildErrorResponse(
                dataBindingViolationException,
                HttpStatus.CONFLICT,
                request);
    }

    /**
     * Método auxiliar que usa a mensagem padrão da exceção.
     */
    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    /**
     * Método central responsável por montar o ErrorResponse.
     *
     * - Aplica o padrão da API
     * - Inclui stack trace opcionalmente
     * - Evita duplicação de código nos handlers
     */
    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);

         // Inclui stack trace apenas se configurado (ex: ambiente dev)
        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

}
