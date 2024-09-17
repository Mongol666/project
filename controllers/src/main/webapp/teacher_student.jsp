<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница студентов для Учителя</title>
</head>
<body>
<h1>Страница студентов</h1>

<c:if test="${not empty messageFromStudents}">
    <c:out value="${messageFromStudents}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Фамилия</th>
        <th>Имя</th>
        <th>Отчество</th>
        <th>Дата рождения</th>
        <th>Оценки</th>
    </tr>
    <c:forEach var="student" items="${allStudents}">
        <tr style="text-align: center">
            <td><c:out value="${student.id}"/></td>
            <td><c:out value="${student.lastName}"/></td>
            <td><c:out value="${student.firstName}"/></td>
            <td><c:out value="${student.patronymic}"/></td>
            <td><c:out value="${student.dateOfBirth}"/></td>
            <td>
                <form action="<c:url value="/teacher/students/${student.id}/marks"/>" method="get">
                    <button style="align-content: center" type="submit">Оценки</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<a style="display: block" href="${pageContext.request.contextPath}/teacher.jsp">Назад</a>
</body>
</html>
