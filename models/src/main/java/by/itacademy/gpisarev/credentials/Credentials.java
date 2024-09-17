package by.itacademy.gpisarev.credentials;

import by.itacademy.gpisarev.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credentials")
@Entity
@NamedQueries({
        @NamedQuery(name = "getCredentialsByLoginAndPassword", query = "from Credentials c where c.login = :login and c.password = :password"),
        @NamedQuery(name = "getCredentialsByID", query = "from Credentials c where c.id = :id"),
        @NamedQuery(name = "deleteCredentialsByID", query = "delete from Credentials c where c.id = :id"),
        @NamedQuery(name = "deleteCredentialsByLoginAndPassword", query = "delete from Credentials c where c.login = :login and c.password = :password")
})
public class Credentials extends AbstractEntity {
    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Credentials that = (Credentials) o;
        return login.equals(that.login) && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), login, password);
    }

    public Credentials withId(int id) {
        setId(id);
        return this;
    }

    public Credentials withLogin(String login) {
        setLogin(login);
        return this;
    }

    public Credentials withPassword(String password) {
        setPassword(password);
        return this;
    }
}
