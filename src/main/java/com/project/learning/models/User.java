package com.project.learning.models;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.project.learning.models.enums.ProfileEnum;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
// Indica que esta classe é uma entidade JPA, o que significa que ela está pronta para ser
// armazenada em um banco de dados relacional
@Entity
//Especifica o nome da tabela no banco de dados. No seu código, ela utiliza a constante
//  TABLE_NAME ("user") definida dentro da classe
@Table(name = User.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode

public class User {

    //Essas interfaces são "marcadores" usados para agrupar regras de validação. Isso permite que você aplique regras diferentes dependendo da situação. 
    // Por exemplo, você pode exigir certos dados na criação do usuário que não são obrigatórios ou que não podem ser alterados em uma atualização.
    public interface CreateUser {}
    public interface UpdateUser{}

    public static final String TABLE_NAME = "user";


    @Id
//Define a estratégia de geração automática do ID.
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="id", unique = true)
//O tipo Long é utilizado por ser capaz de armazenar uma grande quantidade de registros
    private Long id;

//Configura a coluna no banco de dados com tamanho máximo de 100 caracteres, 
// obrigatoriedade de preenchimento (nullable = false) e valor único.
    @Column(name="username", length = 100, nullable = false, unique=true)
    //Anotações de validação que impedem que o campo seja nulo ou vazio ao nível da aplicação
    //As validações só ocorrem quando o grupo for CreateUser.class. 
    // Isso significa que, se houver uma atualização onde o nome de usuário não é enviado, a validação não travará o processo.
    @NotNull(groups = CreateUser.class)
    @NotEmpty(groups = CreateUser.class)
// Restringe o tamanho do texto para que tenha entre 2 e 100 caracteres.
    @Size(groups = CreateUser.class, min = 2, max = 100)
    private String username;

    //Esta é uma anotação da biblioteca Jackson (usada pelo Spring para converter objetos em JSON). 
    // O acesso WRITE_ONLY (apenas escrita) garante que a senha possa ser enviada para a aplicação (para criar ou atualizar o usuário),
    //  mas nunca será enviada de volta na resposta JSON ao cliente, protegendo o dado sensível.
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name="password", length = 60, nullable = false)
    //As validações ocorrem tanto em CreateUser.class quanto em UpdateUser.class, garantindo que uma senha válida sempre seja fornecida em ambos os casos.
    @NotNull(groups = {CreateUser.class, UpdateUser.class})
    @NotEmpty(groups = { CreateUser.class, UpdateUser.class})
    @Size(groups = { CreateUser.class, UpdateUser.class}, min = 8, max = 60)

    private String password;

    /**
     * Relação 1:N com Task.
     *
     * mappedBy = "user" indica que o lado dono da relação está na entidade Task.
     *
     * WRITE_ONLY evita:
     * - loop infinito em serialização (User -> Task -> User...)
     * - exposição desnecessária de dados
     */
    @OneToMany(mappedBy = "user")
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Task> tasks = new ArrayList<Task>();


     /**
     * Representa os perfis (roles) do usuário.
     *
     * - ElementCollection: não é entidade separada, apenas valores simples (Integer)
     * - Armazenado em tabela auxiliar "user_profile"
     * - FetchType.EAGER: sempre carregado junto com User para que ao buscar o usuário, busque também o perfil
     *
     * - Armazena Integer no banco
     * - Converte para Enum na aplicação
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @CollectionTable(name = "user_profile")
    @Column(name="profile", nullable = false)
    private Set<Integer> profiles = new HashSet<>();

    /**
     * Converte os perfis armazenados como Integer para Enum.
     *
     * CONTEXTO:
     * - O banco armazena valores numéricos (mais leve e eficiente)
     * - A aplicação trabalha com Enum (mais seguro e legível)
     *
     * RELAÇÃO:
     * - Usa ProfileEnum.toEnum() para mapear os valores
    */
    public Set<ProfileEnum> getProfiles() {
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profileEnum) {
        this.profiles.add(profileEnum.getCode());
    }


   
}
