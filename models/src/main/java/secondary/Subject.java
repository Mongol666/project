package secondary;

import entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subjects")
@Entity
@NamedQueries({
        @NamedQuery(name = "getSubjectByID", query = "from Subject s where s.id = :id"),
        @NamedQuery(name = "getSubjectByName", query = "from Subject s where s.name = :name")
})
public class Subject extends AbstractEntity {
    @Column(name = "name")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subject subject = (Subject) o;
        return name.equals(subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    public Subject withId(int id) {
        setId(id);
        return this;
    }

    public Subject withName(String name) {
        setName(name);
        return this;
    }
}