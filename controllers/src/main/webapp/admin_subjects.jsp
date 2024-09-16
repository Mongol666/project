<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Страница предметов для Администратора</title>
</head>
<body>
 <h1>Страница предметов</h1>
 <h3>Все предметы</h3>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Изменить</th>
        <th>Удалить</th>
    </tr>

    <c:forEach var="subject" items="${requestScope.subjects}">
        <tr style="text-align: center">
            <td><c:out value="${subject.id}"/></td>
            <td><c:out value="${subject.name}"/></td>
            <td>
                <form action="<c:url value="/SubjectServlet"/>" method="post">
                    <input type="hidden" name="method" value="put">
                    <input type="hidden" name="ID" value="${subject.id}">
                    <label>
                        <input style="text-align: center; display: block" type="text" name="newName" placeholder="Новое название">
                    </label>
                    <button style="align-content: center" type="submit">Изменить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/SubjectServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="ID" value="${subject.id}">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<h4>Создать предмет</h4>
<form action="<c:url value="/SubjectServlet"/>" method="post">
    <input type="hidden" name="method" value="post">
    <label>
        Новое название:
        <input style="text-align: center; display: block" type="text" name="newName">
    </label>
    <button style="align-content: center" type="submit">Создать</button>
</form>

 <a style="display: block" href="admin.jsp">Назад</a>
</body>
</html>
