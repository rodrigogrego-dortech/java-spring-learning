package com.project.learning.models.enums;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum que representa os perfis (roles) de um usuário no sistema.
 *
 * CONTEXTO:
 * - Usado para controle de autorização (Spring Security)
 * - Persistido no banco como Integer (code)
 * - Convertido para Enum na aplicação (ver User.getProfiles())
 *
 * EXEMPLOS:
 * - ADMIN → acesso total
 * - USER → acesso padrão
 */

@AllArgsConstructor
@Getter
public enum ProfileEnum {

    ADMIN(1, "ROLE_ADMIN"),
    USER(2, "ROLE_USER");

    private Integer code;
    private String description;


    /**
     * Converte um código Integer do banco para o Enum correspondente.
     *
     * RELAÇÃO:
     * - Usado na entidade User ao transformar Set<Integer> em Set<ProfileEnum>
     *
     * REGRA:
     * - Retorna null se o código for nulo
     * - Lança exceção se o código não for válido
     */

    public static ProfileEnum toEnum(Integer code){
        if(Objects.isNull(code))
            return null;

        for(ProfileEnum c : ProfileEnum.values()){
            if(code.equals(c.getCode()))
                return c;
        }

        // Erro de integridade: código inválido no banco
        throw new IllegalArgumentException("Invalid Code" + code);
    }


    
}
