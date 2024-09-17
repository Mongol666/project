<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Страница со всеми учителями</title>
</head>
<body>
<h1>Страница учителей</h1>

<c:if test="${not empty messageFromTeachers}">
    <c:out value="${messageFromTeachers}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Фамилия</th>
        <th>Имя</th>
        <th>Отчество</th>
        <th>Дата рождения</th>
    </tr>
    <c:forEach var="teacher" items="${allTeachers}">
        <tr style="text-align: center">
            <td><c:out value="${teacher.id}"/></td>
            <td><c:out value="${teacher.lastName}"/></td>
            <td><c:out value="${teacher.firstName}"/></td>
            <td><c:out value="${teacher.patronymic}"/></td>
            <td><c:out value="${teacher.dateOfBirth}"/></td>
        </tr>
    </c:forEach>
</table>
<a style="display: block" href="${pageContext.request.contextPath}/teacher.jsp">Назад</a>
</body>
</html>
