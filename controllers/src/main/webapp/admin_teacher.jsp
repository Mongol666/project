<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница учителей для Администратора</title>
</head>
<body>
<h1>Страница учителей</h1>
<h3>Все учителя</h3>
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
        <th>Заплаты</th>
    </tr>
    <c:forEach var="teacher" items="${requestScope.teachers}">
            <tr style="text-align: center">
                <td><c:out value="${teacher.id}"/></td>
                <td><c:out value="${teacher.credentials.login}"/></td>
                <td><c:out value="${teacher.credentials.password}"/></td>
                <td><c:out value="${teacher.lastName}"/></td>
                <td><c:out value="${teacher.firstName}"/></td>
                <td><c:out value="${teacher.patronymic}"/></td>
                <td><c:out value="${teacher.dateOfBirth}"/></td>
                <td>
                    <form action="<c:url value="/TeacherServlet"/>" method="post">
                        <input type="hidden" name="method" value="delete">
                        <input type="hidden" name="ID" value="${teacher.id}">
                        <input type="hidden" name="firstName" value="${teacher.firstName}">
                        <input type="hidden" name="lastName" value="${teacher.lastName}">
                        <input type="hidden" name="patronymic" value="${teacher.patronymic}">
                        <button style="align-content: center" type="submit">Удалить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/TeacherServlet"/>" method="post">
                        <input type="hidden" name="method" value="put">
                        <input type="hidden" name="ID" value="${teacher.id}">
                        <input type="hidden" name="credentialID" value="${teacher.credentials.id}">
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newLastName"
                                   placeholder="Новая фамилия">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newFirstName"
                                   placeholder="Новое имя">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newPatronymic"
                                   placeholder="Новое отчество">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newLogin"
                                   placeholder="Новый логин">
                        </label>
                        <label>
                            <input style="text-align: center; display: block" type="text" name="newPassword"
                                   placeholder="Новый пароль">
                        </label>
                        <label>
                            <input style="align-content: center; display: block" type="date" name="newDateOfBirth"
                                   placeholder="Новая дата рождения">
                        </label>
                        <button style="align-content: center; display: block" type="submit">Изменить</button>
                    </form>
                </td>
                <td>
                    <form action="<c:url value="/SalaryServlet"/>" method="post">
                        <input type="hidden" name="method" value="get">
                        <input type="hidden" name="teacherID" value="${teacher.id}">
                        <button style="align-content: center" type="submit">Зарплаты</button>
                    </form>
                </td>
            </tr>
    </c:forEach>
</table>
<h3 style="color: crimson">
    <c:if test="${not empty requestScope.message}">
        <c:out value="${requestScope.message}"/>
    </c:if>
</h3>
<h4>
    Добавление нового учителя
</h4>
<form action="<c:url value="/TeacherServlet"/>">
    <input type="hidden" name="method" value="post">
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
<a style="display: block" href="admin.jsp">Назад</a>
</body>
</html>
