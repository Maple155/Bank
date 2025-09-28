<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Banque Centralisateur</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%-- Sidebar --%>
    <%-- <%@ include file="sidebar.jsp" %> --%>

    <div class="main-content" style="text-align:center; padding-top:50px;">
        <h1>Bienvenue sur la Banque Centralisateur</h1>
        <a class="button-link" href="${pageContext.request.contextPath}/listeClient">Liste des clients</a>
        <a class="button-link" href="${pageContext.request.contextPath}/connexion">Se connecter</a>
    </div>
</body>
</html>
