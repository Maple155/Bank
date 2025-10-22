<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.entity.TypesStatut" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.courant.entity.OperationCourant" %>
<%@ page import="com.banque.pret.entity.Pret" %>
<%@ page import="com.banque.pret.entity.PretStatut" %>
<%@ page import="com.banque.pret.entity.Remboursement" %>
<%@ page import="com.banque.courant.entity.Transaction" %>
<%@ page import="com.banque.courant.dto.UtilisateurDTO" %>
<%@ page import="com.banque.courant.dto.DirectionDTO" %>
<%@ page import="com.banque.courant.dto.ActionRoleDTO" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    double solde = (double) request.getAttribute("solde");

    List<OperationCourant> operationsCourant = (List<OperationCourant>) request.getAttribute("operationsCourant");
    List<Pret> prets = (List<Pret>) request.getAttribute("prets");
    Pret pretImpaye = (Pret) request.getAttribute("pretImpaye");
    List<Remboursement> remboursements = (List<Remboursement>) request.getAttribute("remboursements");
    double resteAPaye = (double) request.getAttribute("resteAPaye");

    List<Transaction> transactionsSender = (List<Transaction>) request.getAttribute("sender");
    List<Transaction> transactionsReceiver = (List<Transaction>) request.getAttribute("receiver");

    List<PretStatut> pretStatuts = (List<PretStatut>) request.getAttribute("pretStatus"); 

    UtilisateurDTO utilisateur = (UtilisateurDTO) request.getAttribute("utilisateur");
    List<DirectionDTO> directions = (List<DirectionDTO>) request.getAttribute("directions");
    List<ActionRoleDTO> actionRoles = (List<ActionRoleDTO>) request.getAttribute("actionRoles");
    
    Boolean estUtilisateur = (Boolean) request.getAttribute("estUtilisateur");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Espace client - Gestion de compte bancaire">
    <title>Espace Client - Banque</title>
    
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/client.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">

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
<!-- AFFICHAGE DES DONNÉES UTILISATEUR ADMIN -->
<% if(estUtilisateur != null && estUtilisateur && utilisateur != null) { %>
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
<% if(compte != null) { %>
    <%@ include file="sidebar.jsp" %>
    <main class="main-content">
        <!-- Header avec solde -->
        <header class="welcome-header">
            <div class="welcome-text">
                <h1>Espace bancaire de <%= compte.getClient().getNom() %></h1>
                <%-- <p class="subtitle">Votre espace bancaire personnel</p> --%>
                <strong><%= utilisateur.getNom() %></strong>
                
            </div>
            <div class="balance-card" role="status">
                <p class="balance-label">Solde actuel</p>
                <p class="balance-amount"><%= String.format("%,.2f", solde) %> <span class="currency">MGA</span></p>
                <p class="balance-footer">Compte N° <%= compte.getNumero() %></p>
            </div>
        </header>
        <br>
        <!-- Opérations -->
        <% if (operationsCourant != null && !operationsCourant.isEmpty()) { %>
        <section class="operations-section">
            <h2>📋 Opérations courantes</h2>
            <br>
            <div class="cards-grid">
                <% 
                    for(OperationCourant op : operationsCourant) { 
                        if (op.getIsValidate()) {
                %>
                <article class="operation-card <%= op.getMontant() >= 0 ? "credit" : "debit" %>">
                    <header class="card-header">
                        <span class="operation-icon"><%= op.getMontant() >= 0 ? "➕" : "➖" %></span>
                        <span class="operation-amount"><%= String.format("%,.2f", Math.abs(op.getMontant())) %> MGA</span>
                    </header>
                    <footer class="card-footer">
                        <time class="operation-date"><%= op.getDateOperation() %></time>
                        <span class="operation-type"><%= op.getMontant() >= 0 ? "Crédit" : "Débit" %></span>
                    </footer>
                </article>
                <% } } %>
            </div>
        </section>
        <% } %>
        <br>
        <!-- Transactions envoyées -->
        <% if (transactionsSender != null && !transactionsSender.isEmpty()) { %>
        <section class="transactions-sent-section">
            <h2>💸 Transactions envoyées</h2>
            <br>
            <div class="transactions-list">
                <% 
                    for(Transaction t : transactionsSender) { 
                        if (t.isValidate()) {
                %>
                <article class="transaction-card sent">
                    <div class="transaction-left">
                        <div class="transaction-avatar"><%= t.getReceiver().getClient().getNom().substring(0, 1).toUpperCase() %></div>
                        <div class="transaction-info">
                            <div class="transaction-name"><%= t.getReceiver().getClient().getNom() %></div>
                            <time class="transaction-date"><%= t.getDateTransaction() %></time>
                        </div>
                    </div>
                    <div class="transaction-amount negative">-<%= String.format("%,.2f", t.getMontant()) %> MGA</div>
                </article>
                <% } } %>
            </div>
        </section>
        <% } %>
        <br>
        <!-- Transactions reçues -->
        <% if (transactionsReceiver != null && !transactionsReceiver.isEmpty()) { %>
        <section class="transactions-received-section">
            <h2>💰 Transactions reçues</h2>
            <br>
            <div class="transactions-list">
                <% 
                    for(Transaction t : transactionsReceiver) { 
                        if (t.isValidate()) {
                %>
                <article class="transaction-card received">
                    <div class="transaction-left">
                        <div class="transaction-avatar"><%= t.getSender().getClient().getNom().substring(0, 1).toUpperCase() %></div>
                        <div class="transaction-info">
                            <div class="transaction-name"><%= t.getSender().getClient().getNom() %></div>
                            <time class="transaction-date"><%= t.getDateTransaction() %></time>
                        </div>
                    </div>
                    <div class="transaction-amount positive">+<%= String.format("%,.2f", t.getMontant()) %> MGA</div>
                </article>
                <% } } %>
            </div>
        </section>
        <% } %>
        <br>
        <!-- Prêts -->
        <% if (prets != null && !prets.isEmpty()) { %>
        <section class="loans-section">
            <h2>🏦 Prêts associés</h2>
            <br>
            <div class="loans-grid">
                <% for(Pret p : prets) { 
                    double reste = (pretImpaye != null && p.getId() == pretImpaye.getId()) ? resteAPaye : 0;
                    boolean isPaid = reste == 0;
                %>
                <%-- <article class="loan-card <%= isPaid ? "paid" : "active" %>"> --%>
                <article class="loan-card <%= isPaid ? "paid" : "paid" %>">
                    <header class="loan-header">
                        <p class="loan-amount-main"><%= String.format("%,.2f", p.getMontant()) %> MGA</p>
                    </header>
                    <div class="loan-details">
                        <p>Date accord : <%= p.getDate_accord() %></p>
                        <p>Taux d'intérêt : <%= String.format("%.2f", p.getTaux()) %>%</p>
                    </div>
                </article>
                <% } %>
            </div>
        </section>
        <% } %>
        <br>
        <% if (pretStatuts != null && !pretStatuts.isEmpty()) { %>
        <section class="pret-impayes-section">
            <h2>💼 Prêts impayés</h2>
            <br>
            <div class="loans-grid">
                <% 
                for (PretStatut ps : pretStatuts) { 
                    Pret pretImpayeX = (Pret) request.getAttribute("pretImpaye_" + ps.getId());
                    List<Remboursement> remboursementsX = (List<Remboursement>) request.getAttribute("remboursements_" + ps.getId());
                    double resteAPayeX = (double) request.getAttribute("resteAPaye_" + ps.getId());
                %>
                <article class="loan-card">
                    <header class="loan-header">
                        <span class="loan-badge badge-warning">⏳ En cours</span>
                        <br>
                        <p class="loan-amount-main"><%= String.format("%,.2f", pretImpayeX.getMontant()) %> MGA</p>
                    </header>
                    <br>
                    <div class="loan-details">
                        <p>Date accord : <%= pretImpayeX.getDate_accord() %></p>
                        <p>Taux d'intérêt : <%= String.format("%.2f", pretImpayeX.getTaux()) %>%</p>
                        <p>Reste à payer : <span class="due-text"><%= String.format("%,.2f", resteAPayeX) %> MGA</span></p>
                    </div>
                    <br>
                    <% if (remboursementsX != null && !remboursementsX.isEmpty()) { %>
                    <div class="remboursements">
                        <h4>Remboursements :</h4>
                        <ul>
                        <br>
                            <% for (Remboursement r : remboursementsX) { %>
                            <li>
                                <%= r.getDateRemboursement() %> – 
                                <%= String.format("%,.2f", r.getMontant()) %> MGA
                            </li>
                            <br>
                            <% } %>
                        </ul>
                    </div>
                    <% } %>
                </article>
                <% } %>
            </div>
        </section>
        <% } %>

    </main>
<% } else { %>
    <div class="error-container">
        <h2>⚠️ Erreur : Compte non trouvé</h2>
        <p>Veuillez vous reconnecter ou contacter le support.</p>
    </div>
<% } %>
</body>
</html>
