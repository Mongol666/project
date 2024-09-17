<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница групп для Администратора</title>
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
        <th>Обновить</th>
        <th>Удалить</th>
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
                <form action="<c:url value="/admin/groups/${group.id}/subjects"/>" method="get">
                    <button style="align-content: center; display: block" type="submit">Предметы</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/admin/groups/${group.id}/students"/>" method="get">
                    <button style="align-content: center; display: block" type="submit">Студенты</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/admin/groups/put/${group.id}"/>" method="post">
                    <label style="display: block; text-align: center; align-content: center">
                        Новый учитель ('фамилия' 'имя' 'отчество'):
                        <input style="display: block; text-align: center; align-content: center"
                               type="text"
                               name="newLastFirstPatronymic"
                               placeholder="Новый учитель">
                    </label>
                    <label style="display: block; text-align: center; align-content: center">
                        Новое название:
                        <input style="display: block; text-align: center; align-content: center"
                               type="text" name="newName"
                               placeholder="Новое название">
                    </label>
                    <button style="align-content: center; display: block" type="submit">Изменить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/admin/groups/delete/${group.id}"/>" method="post">
                    <button style="align-content: center; display: block" type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
<h4>
    Добавление новой группы
</h4>
<form action="<c:url value="/admin/groups"/>" method="post">
    <label>
        Название: <input style="text-align: center; display: block" type="text" name="newName">
    </label>
    <label>
        Учитель ('фамилия' 'имя' 'отчество'): <input style="text-align: center; display: block" type="text"
                                                     name="lastFirstPatronymic">
    </label>
    <button style="align-content: center; display: block" type="submit">Создать</button>
</form>
<a style="display: block" href="${pageContext.request.contextPath}/admin.jsp">Назад</a>
</body>
</html>
