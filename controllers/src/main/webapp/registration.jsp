<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Страница регистрации</title>
</head>
<body>
    <h1>Вы перешли на страницу регистрации</h1>
    <h2>Введите свои данные:</h2>

    <form action="<c:url value="/RegistrationServlet"/>" method="post">
        <label>
            Имя:
            <input style="display: block" type="text" name="firstName">
        </label>
        <label>
            Фамилия:
            <input style="display: block" type="text" name="lastName">
        </label>
        <label>
            Отчество
            <input style="display: block" type="text" name="patronymic">
        </label>
        <label>
            Дата рождения:
            <input style="display: block" type="date" name="dateOfBirth">
        </label>
        <label>
            Выберите статус:
        </label>
        <label>
            <select name="role" style="display: block">
                <option value="-">-</option>
                <option value="Учитель">Учитель</option>
                <option value="Студент">Студент</option>
            </select>
        </label>
        <label>
            Логин:
            <input style="display: block" type="text" name="login">
        </label>
        <label>
            Пароль:
            <input style="display: block" type="text" name="password">
        </label>
        <input type="submit" title="Зарегистрироваться">
        <input type="reset" title="Стереть данные">
    </form>
</body>
</html>
