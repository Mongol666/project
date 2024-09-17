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
        <th>Изменить</th>
        <th>Удалить</th>
    </tr>

    <c:forEach var="subject" items="${allSubjects}">
        <tr style="text-align: center">
            <td><c:out value="${subject.id}"/></td>
            <td><c:out value="${subject.name}"/></td>
            <td>
                <form action="<c:url value="/admin/subjects/put/${subject.id}"/>" method="post">
                    <label>
                        <input style="text-align: center; display: block" type="text" name="newName" placeholder="Новое название">
                    </label>
                    <button style="align-content: center" type="submit">Изменить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/admin/subjects/delete/${subject.id}"/>" method="post">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<h4>Создать предмет</h4>
<form action="<c:url value="/admin/subjects"/>" method="post">
    <label>
        Новое название:
        <input style="text-align: center; display: block" type="text" name="newName">
    </label>
    <button style="align-content: center" type="submit">Создать</button>
</form>

 <a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">Назад</a>
</body>
</html>
