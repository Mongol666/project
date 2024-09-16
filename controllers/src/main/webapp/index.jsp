<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница входа</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Добро пожаловать!</h1>
<h2>Пожалуйста, авторизуйтесь для входа в систему</h2>

<form action="<c:url value="/LogInServlet"/>" method="post">
    <label>
        Логин:
        <input style="display: block" type="text" name="login">
    </label>
    <label>
        Пароль:
        <input style="display: block" type="text" name="password">
    </label>
    <input style="display: block" type="submit" title="Войти">
    <input style="display: block" type="reset" title="Очистить">
</form>

<h3>
    <c:if test="${not empty requestScope.error}">
        <c:out value="${requestScope.error}"/>
    </c:if>
    <c:if test="${not empty requestScope.create}">
        <c:out value="${requestScope.create}"/>
    </c:if>
    <a style="display: block" href="registration.jsp">Регистрация</a>
</h3>
</body>
</html>
