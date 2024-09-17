package by.itacademy.gpisarev.constants;

public final class Queries {
    // запросы к таблице Credentials

    // вставка Credentials в БД
    //language=SQL
    public static final String putCredentials = "INSERT INTO credentials (login, password) VALUES (?, ?);";

    // "взятие" Credentials по ID
    //language=SQL
    public static final String findCredentialsByID = "SELECT * FROM credentials WHERE id = ?;";

    // "взятие" Credentials по логину и паролю
    //language=SQL
    public static final String findCredentialsByLoginAndPassword = "SELECT * FROM credentials WHERE login = ? AND password = ?";

    // "взятие" всех Credentials
    //language=SQL
    public static final String findAllCredentials = "SELECT * FROM credentials";

    // обновление логина и пароля у Credentials
    //language=SQL
    public static final String updateCredentials = "UPDATE credentials SET login = ? AND password = ?";

    // удаление Credentials по ID
    //language=SQL
    public static final String deleteCredentialsByID = "DELETE FROM credentials WHERE id = ?";

    // удаление Credentials по логину и паролю
    //language=SQL
    public static final String deleteCredentialsByLoginAndPassword = "DELETE FROM credentials WHERE login = ? AND password = ?";


    // запросы к таблице Person

    // вставка Person в БД
    //language=SQL
    public static final String putPerson = "INSERT INTO persons (first_name, last_name, patronymic, date_of_birth, credential_id, role) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    // "взятие" Person по ID
    //language=SQL
    public static final String findPersonByID = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM persons p LEFT OUTER JOIN credentials c on p.credential_id = c.id WHERE p.id = ?";

    // "взятие" Person по Credentials
    //language=SQL
    public static final String findPersonByCredentials = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM persons p LEFT OUTER JOIN credentials c on p.credential_id = c.id WHERE c.login = ? AND c.password = ?";

    // "взятие" Person по ФИО
    //language=SQL
    public static final String findPersonByName = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM persons p LEFT OUTER JOIN credentials c on p.credential_id = c.id " +
            "WHERE first_name = ? AND last_name = ? AND patronymic = ?";

    // "взятие" всех Persons и их Credentials
    //language=SQL
    public static final String findAllPersons = "SELECT p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, p.role, c.id, c.login, c.password " +
            "FROM persons p LEFT OUTER JOIN credentials c on p.credential_id = c.id";

    // обновление ФИО по ID Person
    //language=SQL
    public static final String updatePersonNameByID = "UPDATE persons " +
            "SET first_name = ?, last_name = ?, patronymic = ? WHERE id = ?";

    // обновление даты рождения Person по ID
    //language=SQL
    public static final String updatePersonDateOfBirthByID = "UPDATE persons " +
            "SET date_of_birth = ? WHERE id = ?";

    // обновление Credentials Person по ID
    //language=SQL
    public static final String updatePersonCredentialsByID = "UPDATE credentials c SET login = ?, password = ? " +
            "FROM persons p WHERE p.credential_id = c.id AND p.id = ?";

    // удаление Person по ID
    //language=SQL
    public static final String deletePersonByID = "DELETE FROM persons WHERE id = ?";


    // запросы к таблице Group


    // вставка Group в БД
    //language=SQL
    public static final String putGroup = "INSERT INTO groups (teacher_ID, name) VALUES (?, ?)";


    // поиск Group по ID
    //language=SQL
    public static final String findGroupByID = "SELECT g.id, g.name, p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, c.id, c.login, c.password " +
            "FROM groups g " +
            "LEFT JOIN persons p on p.id = g.teacher_id " +
            "LEFT JOIN credentials c on p.credential_id = c.id " +
            "WHERE g.id = ?";

    // поиск Group по названию
    //language=SQL
    public static final String findGroupByName = "SELECT g.id, g.name, p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, c.id, c.login, c.password " +
            "FROM groups g " +
            "LEFT JOIN persons p on p.id = g.teacher_ID " +
            "LEFT JOIN credentials c on p.credential_id = c.id " +
            "WHERE g.name = ?";

    // поиск всех Group
    //language=SQL
    public static final String findAllGroups = "SELECT g.id, g.name, p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, c.id, c.login, c.password " +
            "FROM groups g " +
            "LEFT JOIN persons p on p.id = g.teacher_ID " +
            "LEFT JOIN credentials c on p.credential_id = c.id";

    // обновление названия группы по ID
    //language=SQL
    public static final String updateGroupNameByID = "UPDATE groups SET name = ? WHERE id = ?";

    // обновление Teacher у Group по ID
    //language=SQL
    public static final String updateGroupTeacherByID = "UPDATE groups SET teacher_ID = ? WHERE id = ?";

    // удаление Group по ID
    //language=SQL
    public static final String deleteGroupById = "DELETE FROM groups WHERE id = ?";


    // запросы к таблице Subjects

    // вставка Subject в БД
    //language=SQL
    public static final String putSubject = "INSERT INTO subjects (name) VALUES (?)";

    // "взятие" Subject по ID
    //language=SQL
    public static final String findSubjectByID = "SELECT * FROM subjects WHERE id = ?";

    // "взятие" Subject по имени
    //language=SQL
    public static final String findSubjectByName = "SELECT * FROM subjects WHERE name = ?";

    // "взятие" всех Subject
    //language=SQL
    public static final String findAllSubjects = "SELECT * FROM subjects";

    // обновление названия Subject по ID
    //language=SQL
    public static final String updateSubjectNameByID = "UPDATE subjects SET name = ? WHERE id = ?";


    // удаление Subject по ID
    //language=SQL
    public static final String deleteSubjectByID = "DELETE FROM subjects WHERE id = ?";


    // запросы к таблице Salaries

    // вставка Salary в БД
    //language=SQL
    public static final String putSalary = "INSERT INTO salaries (teacher_id, date_of_salary, count) VALUES (?, ?, ?)";

    // "взятие" Salaries по ID учителя
    //language=SQL
    public static final String findSalaryByID = "SELECT " +
            "s.id, s.date_of_salary, s.count " +
            "FROM salaries s " +
            "WHERE s.id = ?";

    // "взятие" Salaries по ID учителя
    //language=SQL
    public static final String findSalariesByTeacherID = "SELECT " +
            "s.id, s.date_of_salary, s.count" +
            " FROM salaries s " +
            "WHERE s.teacher_id = ?";

    // "взятие" Salaries по дате
    //language=SQL
    public static final String findSalariesByDate = "SELECT " +
            "s.id, s.date_of_salary, s.count" +
            " FROM salaries s " +
            "WHERE s.date_of_salary = ?";

    // "взятие" всех Salaries
    //language=SQL
    public static final String findAllSalaries = "SELECT " +
            "s.id, p.id, p.first_name, p.last_name, p.patronymic, p.date_of_birth, c.id ,c.login, c.password, s.date_of_salary, s.count" +
            " FROM salaries s " +
            "LEFT OUTER JOIN persons p on p.id = s.teacher_id" +
            " LEFT OUTER JOIN credentials c on p.credential_id = c.id";

    // обновление суммы Salary по ID
    //language=SQL
    public static final String updateSalaryByID = "UPDATE salaries SET count = ? WHERE id = ?";


    // обновление даты Salary по ID
    //language=SQL
    public static final String updateDateOfSalary = "UPDATE salaries SET date_of_salary = ? WHERE id = ?";

    // удаление Salary по ID
    //language=SQL
    public static final String deleteSalaryByID = "DELETE FROM salaries WHERE id = ?";


    // запросы к таблице Group-Student

    // вставка Group ID и Student ID в БД
    //language=SQL
    public static final String putStudentAndGroupID = "INSERT INTO group_student (student_ID, group_ID) VALUES (?, ?)";


    // "взятие" Group ID
    //language=SQL
    public static final String findGroupWithStudentsByID = "SELECT student_id FROM group_student WHERE group_ID = ?";

    // удаление Student из Group по ID
    //language=SQL
    public static final String deleteStudentFromGroupByID = "DELETE FROM group_student WHERE student_ID = ?";


    // запросы к таблице Marks

    // вставка Mark в БД
    //language=SQL
    public static final String putMark = "INSERT INTO marks (student_id, subject_id, date_of_mark, point) VALUES (?, ?, ?, ?)";

    // "взятие" Mark по ID
    //language=SQL
    public static final String findMarkByID = "SELECT * FROM marks WHERE id = ?";

    // "взятие" Marks по Student ID
    //language=SQL
    public static final String findMarksByStudentID = "SELECT * FROM marks WHERE student_id = ?";


    // "взятие" всех Marks
    //language=SQL
    public static final String findAllMarks = "SELECT * FROM marks";

    // обновление Subject у Mark по ID
    //language=SQL
    public static final String updateSubjectOfMarkByID = "UPDATE marks SET subject_id = ? WHERE id = ?";

    // обновление даты получения Mark по ID
    //language=SQL
    public static final String updateDateOfMarkByID = "UPDATE marks SET date_of_mark = ? WHERE id = ?";

    // обновление значения Mark по ID
    //language=SQL
    public static final String updateCountOfMarkByID = "UPDATE marks SET point = ? WHERE id = ?";

    // удаление Mark по ID
    //language=SQL
    public static final String deleteMarkByID = "DELETE FROM marks WHERE id = ?";


    // запросы к таблице Group-Subject

    // вставка Group ID и Subject ID в БД
    //language=SQL
    public static final String putSubjectAndGroupID = "INSERT INTO group_subject (subject_ID, group_ID) VALUES (?, ?)";

    // "взятие" Subjects по Group ID
    //language=SQL
    public static final String findSubjectsByGroupID = "SELECT gs.subject_id, s.name FROM group_subject gs LEFT OUTER JOIN subjects s ON gs.subject_id = s.id WHERE group_ID = ?";

    // удаление Subject из Group_Subject по ID
    //language=SQL
    public static final String deleteSubjectFromGroupByID = "DELETE FROM group_subject WHERE subject_ID = ?";

}
