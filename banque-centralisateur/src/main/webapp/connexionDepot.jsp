<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.centralisateur.model.CompteDepot" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
%>
<html>
<head>
    <title>Authentification</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background: #f5f5f5;
        }
        .container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            width: 350px;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
        }
        input {
            width: 100%;
            padding: 10px;
            margin: 8px 0;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .toggle-btn {
            background: none;
            border: none;
            color: blue;
            cursor: pointer;
            margin-top: 10px;
        }
        .hidden {
            display: none;
        }
        input[type="submit"] {
            background: #007BFF;
            color: white;
            border: none;
            cursor: pointer;
        }
        input[type="submit"]:hover {
            background: #0056b3;
        }
    </style>
</head>
<body>
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
    
            <label for="numero">Choisir un compte :</label>
            <select name="numero" id="numero" required>
                <option value="">-- Sélectionnez un compte --</option>
                <%
                    List<CompteDepot> comptes = (List<CompteDepot>) request.getAttribute("comptes");
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
        <button class="toggle-btn" onclick="showRegister()">Pas encore de compte ? S’inscrire</button>
    </div>

    <!-- Formulaire d'inscription -->
    <div id="registerForm" class="hidden">
        <h2>S’inscrire</h2>
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
