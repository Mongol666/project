<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница зарплат</title>
</head>
<body>

<h1>Страница зарплат</h1>

<form action="<c:url value="/student/marks/average"/>" method="get">
    <button style="align-content: center" type="submit">Рассчитать среднюю оценку</button>
</form>

<c:if test="${not empty messageFromMarks}">
    <c:out value="${messageFromMarks}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Дата получения</th>
        <th>Оценка</th>
        <th>Предмет</th>
    </tr>

    <c:forEach var="mark" items="${student.marks}">
        <tr style="text-align: center">
            <td><c:out value="${mark.id}"/></td>
            <td><c:out value="${mark.dateOfMark}"/></td>
            <td><c:out value="${mark.mark}"/></td>
            <td><c:out value="${mark.subject.name}"/></td>
        </tr>
    </c:forEach>
</table>

<a style="display: block" href="${pageContext.request.contextPath}/student.jsp">Назад</a>

</body>
</html>
