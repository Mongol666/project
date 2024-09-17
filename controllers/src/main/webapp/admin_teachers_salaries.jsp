<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница зарплат для Администратора</title>
</head>
<body>
<h1>Страница зарплат</h1>

<form action="<c:url value="/admin/teachers/${teacher.id}/salaries/patch"/>" method="post">
    <button style="align-content: center" type="submit">Рассчитать среднюю зарплату</button>
</form>

<c:if test="${not empty messageFromSalary}">
    <c:out value="${messageFromSalary}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>ФИО учителя</th>
        <th>Дата получения</th>
        <th>Зарплата</th>
        <th>Удалить</th>
        <th>Изменить</th>
    </tr>

    <c:forEach var="salary" items="${teacher.salaries}">
        <tr style="text-align: center">
            <td><c:out value="${salary.id}"/></td>
            <td><c:out value="
                    ${teacher.firstName}
                    ${teacher.lastName}
                    ${teacher.patronymic}"/>
            </td>
            <td><c:out value="${salary.dateOfSalary}"/></td>
            <td><c:out value="${salary.salary}"/></td>
            <td>
                <form action="<c:url value="/admin/teachers/${teacher.id}/salaries/${salary.id}/delete"/>" method="post">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/admin/teachers/${teacher.id}/salaries/${salary.id}/put"/>" method="post">
                    <label>
                        <input style="text-align: center; display: block" type="text"
                               name="newSalary" placeholder="Новая зарплата">
                    </label>
                    <label>
                        <input style="text-align: center; display: block" type="date"
                               name="newDateOfSalary" placeholder="Новая дата выдачи">
                    </label>
                    <button style="align-content: center" type="submit">Изменить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<h4>Добавить зарплату</h4>
<form action="<c:url value="/admin/teachers/${teacher.id}/salaries"/>" method="post">
    <label>
        Дата выдачи: <input style="display: block; text-align: center" type="date" name="newDateOfSalary">
    </label>
    <label>
        Зарплата в размере: <input style="display: block; text-align: center" type="text" name="newSalary">
    </label>
    <button style="display: block; align-content: center" type="submit">Добавить зарплату</button>
</form>

<form action="<c:url value="/admin/teachers"/>" method="get">
    <button type="submit" style="align-content: center">Назад</button>
</form>
</body>
</html>
