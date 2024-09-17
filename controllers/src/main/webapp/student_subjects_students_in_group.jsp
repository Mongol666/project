<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Страница студентов и предметов в группе</title>
</head>
<body>
<h1>Страница студентов и предметов в группе</h1>

<c:if test="${not empty messageFromGroupSubject}">
    <c:out value="${messageFromGroupSubject}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Название</th>
    </tr>
    <c:forEach var="subject" items="${group.subjects}">
        <tr style="text-align: center">
            <td><c:out value="${subject.id}"/></td>
            <td><c:out value="${subject.name}"/></td>
        </tr>
    </c:forEach>
</table>



<c:if test="${not empty messageFromGroupStudent}">
    <c:out value="${messageFromGroupStudent}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Фамилия</th>
        <th>Имя</th>
        <th>Отчество</th>
        <th>Дата рождения</th>
    </tr>
    <c:forEach var="student" items="${group.students}">
        <tr style="text-align: center">
            <td><c:out value="${student.id}"/></td>
            <td><c:out value="${student.lastName}"/></td>
            <td><c:out value="${student.firstName}"/></td>
            <td><c:out value="${student.patronymic}"/></td>
            <td><c:out value="${student.dateOfBirth}"/></td>
        </tr>
    </c:forEach>
</table>


<form action="<c:url value="/student/groups"/>" method="get">
    <button type="submit" style="align-content: center">Назад</button>
</form>

</body>
</html>
