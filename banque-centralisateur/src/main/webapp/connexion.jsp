<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>

<html>
<head>
    <title>Authentification</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/connexion.css">
</head>
<body>
<%-- <%@ include file="sidebar.jsp" %> --%>
<div class="container">
    <!-- Formulaire de connexion -->
    <div id="loginForm">
        <h2>Se connecter</h2>
        <form action="${pageContext.request.contextPath}/connexion" method="POST">
            <input type="hidden" name="action" value="login">
            <%
                Object messageObj = request.getAttribute("message");
                if (messageObj != null) {
            %>
                <h3><%= messageObj.toString() %></h3>
            <%
                }
            %>

            <label for="numero">Choisir un compte :</label>
            <select name="numero" id="numero" required>
                <option value="">-- Sélectionnez un compte --</option>
                <%
                    List<CompteCourant> comptes = (List<CompteCourant>) request.getAttribute("comptes");
                    if (comptes != null) {
                        for (CompteCourant c : comptes) {
                %>
                            <option value="<%= c.getNumero() %>">
                                <%= c.getNumero() %> - <%= c.getClient().getNom() %>
                            </option>
                <%
                        }
                    }
                %>
            </select>

            <input type="submit" value="Connexion">
        </form>
        <button class="toggle-btn" onclick="showRegister()">Pas encore de compte ? S’inscrire</button>
    </div>

    <!-- Formulaire d'inscription -->
    <div id="registerForm" class="hidden">
        <h2>S’inscrire</h2>
        <form action="${pageContext.request.contextPath}/connexion" method="POST">
            <input type="hidden" name="action" value="register">
            <input type="text" name="nom" placeholder="Nom" required>
            <input type="text" name="prenom" placeholder="Prénom" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="text" name="adresse" placeholder="Adresse" required>
            <input type="date" name="date" placeholder="Date de naissance">
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
