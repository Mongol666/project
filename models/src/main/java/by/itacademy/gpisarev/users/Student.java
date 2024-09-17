package by.itacademy.gpisarev.users;

import by.itacademy.gpisarev.credentials.Credentials;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.secondary.Mark;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("Студент")
@Entity
public class Student extends Person {

    @Transient
    private final Role role = Role.STUDENT;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}
            , orphanRemoval = true
            , fetch = FetchType.EAGER
    )
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private Set<Mark> marks = new HashSet<>();

    public Student withId(int id) {
        setId(id);
        return this;
    }

    public Student withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public Student withLastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public Student withPatronymic(String patronymic) {
        setPatronymic(patronymic);
        return this;
    }

    public Student withDateOfBirth(LocalDate dateOfBirth) {
        setDateOfBirth(dateOfBirth);
        return this;
    }

    public Student withCredentials(Credentials credentials) {
        setCredentials(credentials);
        return this;
    }

    public Student withMarks(Set<Mark> marks) {
        setMarks(marks);
        return this;
    }

    public void addMark(Mark mark) {
        this.marks.add(mark);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return role == student.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }
}
