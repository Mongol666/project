<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница групп для Администратора</title>
</head>
<body>
<h1>Страница групп</h1>
<h3>Все группы</h3>
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

    <c:forEach var="group" items="${requestScope.groups}">
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
                <form action="<c:url value="/GroupSubjectServlet"/>" method="get">
                    <input type="hidden" name="method" value="get">
                    <input type="hidden" name="groupID" value="${group.id}">
                    <button style="align-content: center; display: block"
                            type="submit">Предметы
                    </button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/GroupStudentServlet"/>" method="get">
                    <input type="hidden" name="method" value="get">
                    <input type="hidden" name="groupID" value="${group.id}">
                    <button style="align-content: center; display: block"
                            type="submit">Студенты
                    </button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/GroupServlet"/>" method="get">
                    <input type="hidden" name="method" value="put">
                    <input type="hidden" name="ID" value="${group.id}">
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
                <form action="<c:url value="/GroupServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="ID" value="${group.id}">
                    <button style="align-content: center; display: block" type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
<h3 style="color: crimson">
    <c:if test="${not empty requestScope.message}">
        <c:out value="${requestScope.message}"/>
    </c:if>
</h3>
<h4>
    Добавление новой группы
</h4>
<form action="<c:url value="/GroupServlet"/>" method="post">
    <input type="hidden" name="method" value="post">
    <label>
        Название: <input style="text-align: center; display: block" type="text" name="newName">
    </label>
    <label>
        Учитель ('фамилия' 'имя' 'отчество'): <input style="text-align: center; display: block" type="text"
                                                     name="lastFirstPatronymic">
    </label>
    <button style="align-content: center; display: block" type="submit">Создать</button>
</form>
<a style="display: block" href="admin.jsp">Назад</a>
</body>
</html>
