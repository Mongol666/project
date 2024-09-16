package by.itacademy.pisarev.fabric;

import by.itacademy.pisarev.credential.CredentialRepository;
import by.itacademy.pisarev.group.GroupRepository;
import by.itacademy.pisarev.mark.MarkRepository;
import by.itacademy.pisarev.person.PersonRepository;
import by.itacademy.pisarev.salary.SalaryRepository;
import by.itacademy.pisarev.subject.SubjectRepository;

public interface RepositoryFactory {
    GroupRepository getGroupRepository();
    MarkRepository getMarkRepository();
    PersonRepository getPersonRepository();
    SalaryRepository getSalaryRepository();
    SubjectRepository getSubjectRepository();
    CredentialRepository getCredentialRepository();

}
