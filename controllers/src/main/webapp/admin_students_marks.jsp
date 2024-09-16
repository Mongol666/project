<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница оценок студента</title>
</head>
<body>
<h1>Оценки студента</h1>
<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Оценка</th>
        <th>Дата</th>
        <th>Предмет</th>
        <th>Изменить</th>
        <th>Удалить</th>
    </tr>

    <c:set var="student" value="${requestScope.student}"/>

    <c:forEach var="mark" items="${student.marks}">
        <tr style="text-align: center">
            <td><c:out value="${mark.id}"/></td>
            <td><c:out value="${mark.mark}"/></td>
            <td><c:out value="${mark.dateOfMark}"/></td>
            <td><c:out value="${mark.subject.name}"/></td>
            <td>
                <form action="<c:url value="/MarksServlet"/>" method="post">
                    <input type="hidden" name="method" value="put">
                    <input type="hidden" name="studentID" value="${student.id}">
                    <input type="hidden" name="ID" value="${mark.id}">
                    <label style="display: block; align-content: center; text-align: center">
                        Новая оценка (если не изменяется - ввести прошлую):
                        <input style="text-align: center; display: block" type="text" name="newMark">
                    </label>
                    <label style="display: block; align-content: center; text-align: center">
                        Новая дата: (если не изменятся - ввести прошлую):
                        <input style="text-align: center; display: block" type="date" name="newDate">
                    </label>
                    <label style="display: block; align-content: center; text-align: center">
                        Новый предмет: (если не изменяется - ввести прошлый)
                        <input style="text-align: center; display: block" type="text" name="newSubjectName">
                    </label>
                    <button style="align-content: center" type="submit">Изменить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/MarksServlet"/>" method="post">
                    <input type="hidden" name="method" value="delete">
                    <input type="hidden" name="studentID" value="${student.id}">
                    <input type="hidden" name="ID" value="${mark.id}">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<h4>Добавить оценку студенту</h4>
<form action="<c:url value="/MarksServlet"/>" method="post">
    <input type="hidden" name="studentID" value="${student.id}">
    <input type="hidden" name="method" value="post">
    <label>
        Новая оценка:
        <input style="text-align: center; display: block" type="text" name="newMark">
    </label>
    <label>
        Новая дата:
        <input style="text-align: center; display: block" type="date" name="newDate">
    </label>
    <label>
        Новый предмет:
        <input style="text-align: center; display: block" type="text" name="newSubjectName">
    </label>
    <button style="align-content: center" type="submit">Создать</button>
</form>

<form action="<c:url value="/StudentServlet"/>" method="get">
    <input type="hidden" name="method" value="get">
    <button type="submit" style="align-content: center">Назад</button>
</form>
</body>
</html>
