<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница групп для Учителя</title>
</head>
<body>
<h1>Страница групп</h1>

<c:if test="${not empty messageFromGroups}">
    <c:out value="${messageFromGroups}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Учитель</th>
        <th>Предметы</th>
        <th>Студенты</th>
    </tr>

    <c:forEach var="group" items="${allGroups}">
        <tr style="text-align: center">
            <td><c:out value="${group.id}"/></td>
            <td><c:out value="${group.name}"/></td>
            <td>
                <c:if test="${group.teacher != null}">
                    <c:out value="${group.teacher.firstName}
                                    ${group.teacher.lastName}
                                    ${group.teacher.patronymic}"
                    />
                </c:if>

                <c:if test="${group.teacher == null}">
                    <c:out value="Нет учителя"/>
                </c:if>
            </td>
            <td>
                <form action="<c:url value="/teacher/groups/${group.id}/subjects"/>" method="get">
                    <button style="align-content: center; display: block" type="submit">Предметы</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/teacher/groups/${group.id}/students"/>" method="get">
                    <button style="align-content: center; display: block" type="submit">Студенты</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
<a style="display: block" href="${pageContext.request.contextPath}/teacher.jsp">Назад</a>

</body>
</html>
