<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница оценок студента</title>
</head>
<body>
<h1>Страница оценок</h1>

<c:if test="${not empty messageFromMarks}">
    <c:out value="${messageFromMarks}"/>
</c:if>

<table border="1" width="100%">
    <tr>
        <th>ID</th>
        <th>Оценка</th>
        <th>Дата</th>
        <th>Предмет</th>
        <th>Изменить</th>
        <th>Удалить</th>
    </tr>

    <c:set var="student" value="${student}"/>

    <c:forEach var="mark" items="${student.marks}">
        <tr style="text-align: center">
            <td><c:out value="${mark.id}"/></td>
            <td><c:out value="${mark.mark}"/></td>
            <td><c:out value="${mark.dateOfMark}"/></td>
            <td><c:out value="${mark.subject.name}"/></td>
            <td>
                <form action="<c:url value="/admin/students/${student.id}/marks/${mark.id}/put"/>" method="post">
                    <label style="display: block; align-content: center; text-align: center">
                        Новая оценка:
                        <input style="text-align: center; display: block" type="text" name="newMark">
                    </label>
                    <label style="display: block; align-content: center; text-align: center">
                        Новая дата:
                        <input style="text-align: center; display: block" type="date" name="newDate">
                    </label>
                    <label style="display: block; align-content: center; text-align: center">
                        Новый предмет:
                        <input style="text-align: center; display: block" type="text" name="newSubjectName">
                    </label>
                    <button style="align-content: center" type="submit">Изменить</button>
                </form>
            </td>
            <td>
                <form action="<c:url value="/admin/students/${student.id}/marks/${mark.id}/delete"/>" method="post">
                    <button style="align-content: center" type="submit">Удалить</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<h4>Добавить оценку студенту</h4>
<form action="<c:url value="/admin/students/${student.id}/marks"/>" method="post">
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

<form action="<c:url value="/admin/students"/>" method="get">
    <button type="submit" style="align-content: center">Назад</button>
</form>
</body>
</html>
