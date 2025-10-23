<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.centralisateur.model.CompteDepot" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    List<CompteDepot> comptes = (List<CompteDepot>) request.getAttribute("comptes");
    String error = (String) request.getAttribute("error");
%>

<html>
<head>
    <title>Connexion Compte Dépôt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/connexion.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
</head>
<body>
<%-- Sidebar --%>
<%@ include file="sidebar.jsp" %>

<div class="container">
    <!-- Formulaire de connexion -->
    <div id="loginForm">
        <h2>Se connecter</h2>
        <form action="${pageContext.request.contextPath}/connexionDepot" method="POST">
            <input type="hidden" name="action" value="login">
            <% if (compte!= null) { %>
                <input type="hidden" name="courant" value="<%= compte.getId() %>">
            <% } %>

            <%
                Object messageObj = request.getAttribute("message");
                if (messageObj != null) {
            %>
                <h3><%= messageObj.toString() %></h3>
            <%
                }
            %>  
            <% if (error != null) { %>
                <h3 class="error"><%= error %></h3>
            <% } %>

            <label for="numero">Choisir un compte :</label>
            <select name="numero" id="numero" required>
                <option value="">-- Sélectionnez un compte --</option>
                <%
                    if (comptes != null) {
                        for (CompteDepot c : comptes) {
                %>
                            <option value="<%= c.getNumero() %>">
                                <%= c.getNumero() %> - <%= compte.getClient().getNom() %>
                            </option>
                <%
                        }
                    }
                %>
            </select>

            <input type="submit" value="Connexion">
        </form>
        <button class="toggle-btn" onclick="showRegister()">Creer un compte </button>
    </div>

    <!-- Formulaire d'inscription -->
    <div id="registerForm" class="hidden">
        <h2>Creer un compte</h2>
        <form action="${pageContext.request.contextPath}/connexionDepot" method="POST">
            <input type="hidden" name="action" value="register">
            <% if (compte!= null) { %>
                <input type="hidden" name="courant" value="<%= compte.getId() %>">
            <% } %>
            <input type="password" name="pwd" placeholder="Mot de passe" required>
            <input type="submit" value="Créer un compte">
        </form>
        <button class="toggle-btn" onclick="showLogin()">Déjà un compte ? Se connecter</button>
    </div>
</div>

<script>
    function showRegister() {
        document.getElementById("loginForm").classList.add("hidden");
        document.getElementById("registerForm").classList.remove("hidden");
    }
    function showLogin() {
        document.getElementById("registerForm").classList.add("hidden");
        document.getElementById("loginForm").classList.remove("hidden");
    }
</script>
</body>
</html>
