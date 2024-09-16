<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница Администратора</title>
</head>
<body>
    <h1>Авторизация прошла успешно!</h1>
    <h2>
        <form action="<c:url value="/StudentServlet"/>" method="get">
            <input type="hidden" name="method" value="get">
            <button type="submit" style="align-content: center">На страницу со всеми студентами</button>
        </form>
        <form action="<c:url value="/TeacherServlet"/>" method="get">
            <input type="hidden" name="method" value="get">
            <button type="submit" style="align-content: center">На страницу со всеми учителями</button>
        </form>
        <form action="<c:url value="/GroupServlet"/>" method="get">
            <input type="hidden" name="method" value="get">
            <button type="submit" style="align-content: center">На страницу со всеми группами</button>
        </form>
        <form action="<c:url value="/SubjectServlet"/>" method="get">
            <input type="hidden" name="method" value="get">
            <button type="submit" style="align-content: center">На страницу со всеми предметами</button>
        </form>
    </h2>
    <h3>
        <form action="<c:url value="/LogoutServlet"/>" method="post">
            <button type="submit" style="display: block">Выход из аккаунта</button>
        </form>
    </h3>
</body>
</html>
