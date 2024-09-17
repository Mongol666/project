<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Страница студентов для Администратора</title>
</head>
<body>
<h1>Страница студентов</h1>

<c:if test="${not empty messageFromStudents}">
    <c:out value="${messageFromStudents}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Логин</th>
        <th>Пароль</th>
        <th>Фамилия</th>
        <th>Имя</th>
        <th>Отчество</th>
        <th>Дата рождения</th>
        <th>Удалить</th>
        <th>Изменить</th>
        <th>Оценки</th>
    </tr>
    <c:forEach var="student" items="${allStudents}">
            <tr style="text-align: center">
                <td><c:out value="${student.id}"/></td>
                <td><c:out value="${student.credentials.login}"/></td>
                <td><c:out value="${student.credentials.password}"/></td>
                <td><c:out value="${student.lastName}"/></td>
                <td><c:out value="${student.firstName}"/></td>
                <td><c:out value="${student.patronymic}"/></td>
                <td><c:out value="${student.dateOfBirth}"/></td>
                <td>
                    <form action="<c:url value="/admin/students/delete/${student.id}"/>" method="post">
                        <button style="align-content: center" type="submit">Удалить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/admin/students/put/${student.id}"/>" method="post">
                        <input type="hidden" name="credentialID" value="${student.credentials.id}">
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newLastName" placeholder="Новая фамилия">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newFirstName" placeholder="Новое имя">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newPatronymic" placeholder="Новое отчество">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newLogin" placeholder="Новый логин">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newPassword" placeholder="Новый пароль">
                        </label>
                        <label>
                            <input style="align-content: center; display: block" type="date" name="newDateOfBirth"
                                   placeholder="Новая дата рождения">
                        </label>
                        <button type="submit">Изменить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/admin/students/${student.id}/marks"/>" method="get">
                        <button style="align-content: center" type="submit">Оценки</button>
                    </form>
                </td>
            </tr>
    </c:forEach>
</table>
<h4>
    Добавление нового студента
</h4>
<form action="<c:url value="/admin/students"/>" method="post">
    <label>
        Фамилия: <input style="text-align: center; display: block" type="text" name="newLastName">
    </label>
    <label>
        Имя:<input style="text-align: center; display: block" type="text" name="newFirstName">
    </label>
    <label>
        Отчество:<input style="text-align: center; display: block" type="text" name="newPatronymic">
    </label>
    <label>
        Логин:<input style="text-align: center; display: block" type="text" name="newLogin">
    </label>
    <label>
        Пароль:<input style="text-align: center; display: block" type="text" name="newPassword">
    </label>
    <label>
        Дата рождения:<input style="align-content: center; display: block" type="date" name="newDateOfBirth">
    </label>
    <button style="align-content: center; display: block" type="submit">Создать</button>
</form>

<a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">Назад</a>
</body>
</html>