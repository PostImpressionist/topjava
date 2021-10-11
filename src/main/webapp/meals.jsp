<%--@elvariable id="mealList" type="java.util.ArrayList"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<p>Meals List</p>

<hr>
<style>
    TABLE {
        border-collapse: collapse;
    }
    TD, TH {
        padding: 3px;
        border: 1px solid black;
    }
    TH {
        background: #0a6200;
    }
</style>
<table>
    <thead>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    </thead>
    <c:forEach var="mealList" items="${mealList}">
        <tbody style="background-color: ${mealList.excess ? 'red':'greenyellow'}">
        <td>
            <fmt:parseDate value="${ mealList.dateTime }" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime"
                           type="both"/>
            <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${ parsedDateTime }"/>
        </td>
        <td>${mealList.description}</td>
        <td>${mealList.calories}</td>
        <td>Update</td>
        <td>Delete</td>
        </tbody>
    </c:forEach>

</table>
</body>
</html>