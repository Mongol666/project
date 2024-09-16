package secondary;

import entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import users.Student;
import users.Teacher;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups")
@Entity
@NamedQueries({
        @NamedQuery(name = "getGroupByID", query = "from Group g where g.id = :id"),
        @NamedQuery(name = "getGroupByName", query = "from Group g where g.name = :name")
})
public class Group extends AbstractEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "group_student",
               joinColumns = {@JoinColumn(name = "group_id")},
               inverseJoinColumns = {@JoinColumn(name = "student_id")})
    @ToString.Exclude
    private Set<Student> students = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "group_subject",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "subject_id")})
    @ToString.Exclude
    private Set<Subject> subjects = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Group group = (Group) o;
        return name.equals(group.name) && Objects.equals(teacher, group.teacher) && Objects.equals(students, group.students) && Objects.equals(subjects, group.subjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, teacher, students, subjects);
    }

    public Group withId(int id) {
        setId(id);
        return this;
    }

    public Group withName(String name) {
        setName(name);
        return this;
    }

    public Group withTeacher(Teacher teacher) {
        setTeacher(teacher);
        return this;
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }

    public void removeSubject(Subject subject) {
        this.subjects.remove(subject);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
    }

}
