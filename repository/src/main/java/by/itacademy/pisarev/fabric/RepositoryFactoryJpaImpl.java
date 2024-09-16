package by.itacademy.pisarev.fabric;

import by.itacademy.pisarev.credential.CredentialRepository;
import by.itacademy.pisarev.credential.CredentialRepositoryJpaImpl;
import by.itacademy.pisarev.group.GroupRepository;
import by.itacademy.pisarev.group.GroupRepositoryJpaImpl;
import by.itacademy.pisarev.mark.MarkRepository;
import by.itacademy.pisarev.mark.MarkRepositoryJpaImpl;
import org.hibernate.SessionFactory;
import by.itacademy.pisarev.person.PersonRepository;
import by.itacademy.pisarev.person.PersonRepositoryJpaImpl;
import by.itacademy.pisarev.salary.SalaryRepository;
import by.itacademy.pisarev.salary.SalaryRepositoryJpaImpl;
import by.itacademy.pisarev.subject.SubjectRepository;
import by.itacademy.pisarev.subject.SubjectRepositoryJpaImpl;

public class RepositoryFactoryJpaImpl implements RepositoryFactory {

    private final GroupRepository groupRepository;
    private final MarkRepository markRepository;
    private final PersonRepository personRepository;
    private final SalaryRepository salaryRepository;
    private final SubjectRepository subjectRepository;
    private final CredentialRepository credentialRepository;

    private static volatile RepositoryFactoryJpaImpl instance;

    private RepositoryFactoryJpaImpl(SessionFactory factory) {
        credentialRepository = CredentialRepositoryJpaImpl.getInstance(factory);
        groupRepository = GroupRepositoryJpaImpl.getInstance(factory);
        personRepository = PersonRepositoryJpaImpl.getInstance(factory, groupRepository);
        subjectRepository = SubjectRepositoryJpaImpl.getInstance(factory, groupRepository);
        markRepository = MarkRepositoryJpaImpl.getInstance(factory, personRepository);
        salaryRepository = SalaryRepositoryJpaImpl.getInstance(factory, personRepository);
    }

    public static RepositoryFactoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (RepositoryFactoryJpaImpl.class) {
                if (instance == null) {
                    instance = new RepositoryFactoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }

    @Override
    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    @Override
    public MarkRepository getMarkRepository() {
        return markRepository;
    }

    @Override
    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    @Override
    public SalaryRepository getSalaryRepository() {
        return salaryRepository;
    }

    @Override
    public SubjectRepository getSubjectRepository() {
        return subjectRepository;
    }

    @Override
    public CredentialRepository getCredentialRepository() {
        return credentialRepository;
    }
}
