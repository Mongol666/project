<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Страница предметов для Администратора</title>
</head>
<body>
<h1>Страница предметов</h1>

<c:if test="${not empty messageFromSubject}">
    <c:out value="${messageFromSubject}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Название</th>
    </tr>

    <c:forEach var="subject" items="${allSubjects}">
        <tr style="text-align: center">
            <td><c:out value="${subject.id}"/></td>
            <td><c:out value="${subject.name}"/></td>
        </tr>
    </c:forEach>
</table>


<a style="display: block" href="${pageContext.request.contextPath}/student.jsp">Назад</a>
</body>
</html>
