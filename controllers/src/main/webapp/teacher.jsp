<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Страница Учителя</title>
</head>
<body>
<h1>Авторизация прошла успешно!</h1>

<h3>
    <c:out value="${messageToPerson}"/>
</h3>

<h2>
    <form action="<c:url value="/teacher/salary"/>" method="get">
        <button type="submit" style="align-content: center">На страницу ваших зарплат</button>
    </form>
    <form action="<c:url value="/teacher/students"/>" method="get">
        <button type="submit" style="align-content: center">На страницу со студентами из вашей группы</button>
    </form>
    <form action="<c:url value="/teacher/teachers"/>" method="get">
        <button type="submit" style="align-content: center">На страницу со всеми учителями</button>
    </form>
    <form action="<c:url value="/teacher/groups"/>" method="get">
        <button type="submit" style="align-content: center">На страницу со всеми группами</button>
    </form>
    <form action="<c:url value="/teacher/subjects"/> " method="get">
        <button type="submit" style="align-content: center">На страницу со всеми предметами</button>
    </form>
</h2>
<h3>
    <form action="<c:url value="/logout"/>" method="get">
        <button type="submit" style="align-content: center">Выход из аккаунта</button>
    </form>
</h3>
</body>
</html>
