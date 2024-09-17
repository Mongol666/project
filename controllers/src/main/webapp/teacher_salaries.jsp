<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница зарплат</title>
</head>
<body>

<h1>Страница зарплат</h1>

<form action="<c:url value="/teacher/salary/average"/>" method="get">
    <button style="align-content: center" type="submit">Рассчитать среднюю зарплату</button>
</form>

<c:if test="${not empty messageFromSalary}">
    <c:out value="${messageFromSalary}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Дата получения</th>
        <th>Зарплата</th>
    </tr>

    <c:forEach var="salary" items="${teacher.salaries}">
        <tr style="text-align: center">
            <td><c:out value="${salary.id}"/></td>
            <td><c:out value="${salary.dateOfSalary}"/></td>
            <td><c:out value="${salary.salary}"/></td>
        </tr>
    </c:forEach>
</table>

<a style="display: block" href="${pageContext.request.contextPath}/teacher.jsp">Назад</a>

</body>
</html>
