<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница студентов и предметов в группе</title>
</head>
<body>
<h1>Страница студентов и предметов в группе</h1>
<h3>Таблица предметов группы</h3>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Удалить</th>
    </tr>
    <c:forEach var="subject" items="${requestScope.group.subjects}">
        <tr style="text-align: center">
            <td><c:out value="${subject.id}"/></td>
            <td><c:out value="${subject.name}"/></td>
            <td>
                <form action="<c:url value="/GroupSubjectServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="groupID" value="${requestScope.group.id}">
                    <input type="hidden" name="subjectID" value="${subject.id}">
                    <button style="align-content: center" type="submit">Удалить предмет</button>
                </form>
            </td>
        </tr>
    </c:forEach>

</table>

<h4>Добавить предмет к группе</h4>
<form action="<c:url value="/GroupSubjectServlet"/>" method="post">
    <input type="hidden" name="groupID" value="${requestScope.group.id}">
    <input type="hidden" name="method" value="post">
    <label>
        Название: <input style="text-align: center; display: block" type="text" name="newName">
    </label>
    <button style="align-content: center" type="submit">Добавить предмет к группе</button>
</form>

<h3>Таблица студентов в группе</h3>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Фамилия</th>
        <th>Имя</th>
        <th>Отчество</th>
        <th>Логин</th>
        <th>Пароль</th>
        <th>Дата рождения</th>
        <th>Удалить</th>
    </tr>
    <c:forEach var="student" items="${requestScope.group.students}">
        <tr style="text-align: center">
            <td><c:out value="${student.id}"/></td>
            <td><c:out value="${student.lastName}"/></td>
            <td><c:out value="${student.firstName}"/></td>
            <td><c:out value="${student.patronymic}"/></td>
            <td><c:out value="${student.credentials.login}"/></td>
            <td><c:out value="${student.credentials.password}"/></td>
            <td><c:out value="${student.dateOfBirth}"/></td>
            <td>
                <form action="<c:url value="/GroupStudentServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="groupID" value="${requestScope.group.id}">
                    <input type="hidden" name="studentID" value="${student.id}">
                    <button style="align-content: center" type="submit">Удалить студента</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<h4>Добавить студента к группе</h4>
<form action="<c:url value="/GroupStudentServlet"/>" method="post">
    <input type="hidden" name="method" value="post">
    <input type="hidden" name="groupID" value="${requestScope.group.id}">
    <label>
        Фамилия: <input style="text-align: center; display: block" type="text" name="newLastName">
    </label>
    <label>
        Имя: <input style="text-align: center; display: block" type="text" name="newFirstName">
    </label>
    <label>
        Отчество: <input style="text-align: center; display: block" type="text" name="newPatronymic">
    </label>
    <button style="align-content: center" type="submit">Добавить студента к группе</button>
</form>

<form action="<c:url value="/GroupServlet"/>" method="get">
    <input type="hidden" name="method" value="get">
    <button type="submit" style="align-content: center">Назад</button>
</form>

</body>
</html>
