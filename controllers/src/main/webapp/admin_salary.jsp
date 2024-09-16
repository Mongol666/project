<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница зарплат для Администратора</title>
</head>
<body>
<h1>Страница зарплат</h1>
<h3>Все зарплаты</h3>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>ФИО учителя</th>
        <th>Дата получения</th>
        <th>Зарплата</th>
        <th>Удалить</th>
        <th>Изменить</th>
    </tr>

    <c:forEach var="salary" items="${requestScope.teacher.salaries}">
        <tr style="text-align: center">
            <td><c:out value="${salary.id}"/></td>
            <td><c:out value="
                    ${requestScope.teacher.firstName}
                    ${requestScope.teacher.lastName}
                    ${requestScope.teacher.patronymic}"/>
            </td>
            <td><c:out value="${salary.dateOfSalary}"/></td>
            <td><c:out value="${salary.salary}"/></td>
            <td>
                <form action="<c:url value="/SalaryServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="teacherID" value="${requestScope.teacher.id}">
                    <input type="hidden" name="ID" value="${salary.id}">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/SalaryServlet"/>" method="post">
                    <input type="hidden" name="method" value="put">
                    <input type="hidden" name="teacherID" value="${requestScope.teacher.id}">
                    <input type="hidden" name="ID" value="${salary.id}">
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
<form action="<c:url value="/SalaryServlet"/>" method="post">
    <input type="hidden" name="method" value="post">
    <input type="hidden" name="teacherID" value="${requestScope.teacher.id}">
    <label>
        Дата выдачи: <input style="display: block; text-align: center" type="date" name="newDateOfSalary">
    </label>
    <label>
        Зарплата в размере: <input style="display: block; text-align: center" type="text" name="newCount">
    </label>
    <button style="display: block; align-content: center" type="submit">Добавить зарплату</button>
</form>

<form action="<c:url value="/TeacherServlet"/>" method="get">
    <input type="hidden" name="method" value="get">
    <button type="submit" style="align-content: center">Назад</button>
</form>
</body>
</html>
