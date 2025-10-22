<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Banque Centralisateur</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <%-- Sidebar CSS si besoin --%>
    <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css"> --%>
</head>
<body>
    <%-- Sidebar --%>
    <%-- <%@ include file="sidebar.jsp" %> --%>

    <div class="main-content" style="text-align:center; padding-top:50px;">
        <h1>Bienvenue sur la Banque Centralisateur</h1>
        <div style="margin-top: 2rem; display: flex; justify-content: center; gap: 1rem; flex-wrap: wrap;">
            <%-- <a class="button-link" href="${pageContext.request.contextPath}/banque">Banque</a> --%>
            <a class="button-link" href="${pageContext.request.contextPath}/login">Utilisateur</a>
        </div>
    </div>
</body>
</html>
