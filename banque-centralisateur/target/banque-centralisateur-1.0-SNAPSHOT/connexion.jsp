<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.courant.dto.UtilisateurDTO" %>
<%@ page import="com.banque.courant.dto.DirectionDTO" %>
<%@ page import="com.banque.courant.dto.ActionRoleDTO" %>

<%

    UtilisateurDTO utilisateur = (UtilisateurDTO) request.getAttribute("utilisateur");
    List<DirectionDTO> directions = (List<DirectionDTO>) request.getAttribute("directions");
    List<ActionRoleDTO> actionRoles = (List<ActionRoleDTO>) request.getAttribute("actionRoles");

%>

<html>
<head>
    <title>Authentification</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/connexion.css">

    <style>
        .admin-info-panel {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .admin-info-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        
        .admin-title {
            font-size: 1.5em;
            font-weight: 600;
            margin: 0;
        }
        
        .admin-badge {
            background: rgba(255,255,255,0.2);
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: 500;
        }
        
        .admin-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-top: 10px;
        }
        
        .admin-detail-item {
            background: rgba(255,255,255,0.1);
            padding: 10px;
            border-radius: 6px;
        }
        
        .admin-detail-label {
            font-size: 0.8em;
            opacity: 0.8;
            margin-bottom: 5px;
        }
        
        .admin-detail-value {
            font-size: 1em;
            font-weight: 500;
        }
        
        .directions-list, .roles-list {
            margin-top: 10px;
        }
        
        .direction-item, .role-item {
            background: rgba(255,255,255,0.1);
            padding: 8px 12px;
            margin: 5px 0;
            border-radius: 5px;
            font-size: 0.9em;
        }
    </style>

</head>
<body>
    <% if(utilisateur != null) { %>
        <div class="admin-info-panel">
            <div class="admin-info-header">
                <h2 class="admin-title">👨‍💼 Tableau de Bord Administrateur</h2>
                <span class="admin-badge">Administrateur</span>
            </div>
            
            <div class="admin-details">
                <div class="admin-detail-item">
                    <div class="admin-detail-label">Utilisateur</div>
                    <div class="admin-detail-value">
                        <strong><%= utilisateur.getNom() %></strong>
                    </div>
                </div>
                
                <div class="admin-detail-item">
                    <div class="admin-detail-label">Direction</div>
                    <div class="admin-detail-value">
                        <%= utilisateur.getDirection() != null ? utilisateur.getDirection().getLibelle() : "Non assigné" %>
                    </div>
                </div>
                
                <div class="admin-detail-item">
                    <div class="admin-detail-label">Rôle</div>
                    <div class="admin-detail-value">
                        <%= utilisateur.getRole() == 1 ? "Administrateur" : "Utilisateur" %>
                    </div>
                </div>
                
                <div class="admin-detail-item">
                    <div class="admin-detail-label">ID Utilisateur</div>
                    <div class="admin-detail-value">#<%= utilisateur.getId() %></div>
                </div>
            </div>
            
            <!-- Affichage des directions disponibles -->
            <% if(directions != null && !directions.isEmpty()) { %>
                <div class="directions-list">
                    <div class="admin-detail-label">Directions disponibles:</div>
                    <% for(DirectionDTO direction : directions) { %>
                        <div class="direction-item">
                            <%= direction.getLibelle() %> (Niveau: <%= direction.getNiveau() %>)
                        </div>
                    <% } %>
                </div>
            <% } %>
            
            <!-- Affichage des rôles d'action -->
            <% if(actionRoles != null && !actionRoles.isEmpty()) { %>
                <div class="roles-list">
                    <div class="admin-detail-label">Permissions:</div>
                    <% for(ActionRoleDTO role : actionRoles) { %>
                        <div class="role-item">
                            <%= role.getNomTable() %> - <%= role.getAction() %> (Role: <%= role.getRole() %>)
                        </div>
                    <% } %>
                </div>
            <% } %>
        </div>
    <% } %>

<%-- <%@ include file="sidebar.jsp" %> --%>
<div class="container">
    <!-- Formulaire de connexion -->
    <div id="loginForm">
        <h2>Choisir un client</h2>
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
        <button class="toggle-btn" onclick="showRegister()"> Creer client </button>
    </div>

    <!-- Formulaire d'inscription -->
    <div id="registerForm" class="hidden">
        <h2>Creer un client</h2>
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
