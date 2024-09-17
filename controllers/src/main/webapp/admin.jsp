<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница Администратора</title>
</head>
<body>
    <h1>Авторизация прошла успешно!</h1>

    <h3>
        <c:out value="${messageToPerson}"/>
    </h3>

    <h2>
        <form action="<c:url value="/admin/students"/>" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми студентами</button>
        </form>
        <form action="<c:url value="/admin/teachers"/>" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми учителями</button>
        </form>
        <form action="<c:url value="/admin/groups"/>" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми группами</button>
        </form>
        <form action="<c:url value="/admin/subjects"/>" method="get">
            <button type="submit" style="align-content: center">На страницу со всеми предметами</button>
        </form>
    </h2>
    <form action="<c:url value="/logout"/>" method="get">
        <button type="submit" style="align-content: center">Выход из аккаунта</button>
    </form>
</body>
</html>
