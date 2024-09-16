package by.itacademy.pisarev.person;

import users.Person;
import users.Student;

import java.util.Set;

public interface PersonRepository {
    //Create
    boolean createPerson(Person person);

    //Read
    Person getPersonById(int id);
    Person getPersonByName(String firstName, String lastName, String patronymic);
    Person getPersonByCredentials(String login, String password);
    Set<Student> getStudentsByGroupID(int groupID);
    Set<Person> getAllPersons();

    //Update
    boolean updateAllPersonProperties(Person newPerson);
    boolean updatePerson(Person person);

    //Delete
    boolean deletePersonById(int id);

    boolean deletePersonByName(String firstName, String lastName, String patronymic);
}
