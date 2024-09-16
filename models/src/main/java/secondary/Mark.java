package secondary;

import entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "marks")
@Entity
@NamedQueries({
        @NamedQuery(name = "getMarkByID", query = "from Mark m where m.id = :id")
})
public class Mark extends AbstractEntity {
    @OneToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "date_of_mark")
    private LocalDate dateOfMark;

    @Column(name = "point")
    private int mark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Mark mark1 = (Mark) o;
        return mark == mark1.mark && subject.equals(mark1.subject) && dateOfMark.equals(mark1.dateOfMark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subject, dateOfMark, mark);
    }

    public Mark withId(int id) {
        setId(id);
        return this;
    }

    public Mark withSubject(Subject subject) {
        setSubject(subject);
        return this;
    }

    public Mark withDateOfMark(LocalDate dateOfMark) {
        setDateOfMark(dateOfMark);
        return this;
    }

    public Mark withMark(int mark) {
        setMark(mark);
        return this;
    }
}
