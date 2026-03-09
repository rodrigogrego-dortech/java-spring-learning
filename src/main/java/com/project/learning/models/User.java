package com.project.learning.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import java.util.Objects;

// Indica que esta classe é uma entidade JPA, o que significa que ela está pronta para ser
// armazenada em um banco de dados relacional
@Entity
//Especifica o nome da tabela no banco de dados. No seu código, ela utiliza a constante
//  TABLE_NAME ("user") definida dentro da classe
@Table(name = User.TABLE_NAME)
public class User {

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
    @NotNull(groups = CreateUser.class)
    @NotEmpty(groups = CreateUser.class)
// Restringe o tamanho do texto para que tenha entre 2 e 100 caracteres.
    @Size(groups = CreateUser.class, min = 2, max = 100)
    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name="password", length = 60, nullable = false)
    @NotNull(groups = {CreateUser.class, UpdateUser.class})
    @NotEmpty(groups = { CreateUser.class, UpdateUser.class})
    @Size(groups = { CreateUser.class, UpdateUser.class}, min = 8, max = 60)

    private String password;

    // private List<Task> tasks = new ArrayList<Task>();

    public User() {

    }


    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;

        if (obj == null)
            return false;

        if(!(obj instanceof User))
            return false;


        User other = (User) obj;

        if (this.id == null)
            if (other.id != null)
                return false;
            else if (!this.id.equals(other.id))
                return false;
        return Objects.equals(this.id, other.id) && Objects.equals(this.username, other.username)
                && Objects.equals(this.password, other.password);

    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());

        return result;
    }
}
