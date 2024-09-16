package users;

import credentials.Credentials;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import role.Role;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@DiscriminatorValue("Админ")
public class Admin extends Person {

    @Transient
    private final Role role = Role.ADMIN;

    public Admin withId(int id) {
        setId(id);
        return this;
    }

    public Admin withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public Admin withLastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public Admin withPatronymic(String patronymic) {
        setPatronymic(patronymic);
        return this;
    }

    public Admin withDateOfBirth(LocalDate dateOfBirth) {
        setDateOfBirth(dateOfBirth);
        return this;
    }

    public Admin withCredentials(Credentials credentials) {
        setCredentials(credentials);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Admin admin = (Admin) o;
        return role == admin.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }
}
