<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Страница со всеми студентами</title>
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
    </tr>
    <c:forEach var="student" items="${allStudents}">
        <tr style="text-align: center">
            <td><c:out value="${student.id}"/></td>
            <td><c:out value="${student.lastName}"/></td>
            <td><c:out value="${student.firstName}"/></td>
            <td><c:out value="${student.patronymic}"/></td>
            <td><c:out value="${student.dateOfBirth}"/></td>
        </tr>
    </c:forEach>
</table>
<a style="display: block" href="${pageContext.request.contextPath}/student.jsp">Назад</a>
</body>
</html>
