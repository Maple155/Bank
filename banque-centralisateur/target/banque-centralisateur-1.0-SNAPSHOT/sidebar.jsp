<div class="sidebar">
    <h3>Menu</h3>
    <ul>
        <li>
            <form method="POST" action="${pageContext.request.contextPath}/connexion">
                <input type="hidden" value="login" name="action">
                <input type="hidden" value="<%= compte. getNumero() %>" name="numero">
                <input type="submit" value="Accueil">
            </form>
        </li>
        <li><a href="${pageContext.request.contextPath}/operation?compte=<%= compte.getId() %>">Debiter / Crediter le compte</a></li>
        <li><a href="${pageContext.request.contextPath}/transaction?compte=<%= compte.getId() %>">Envoyer de l'argent </a></li>
        <li><a href="${pageContext.request.contextPath}/connexionDepot?compte=<%= compte.getId() %>">Creer / Utiliser un compte depot</a></li>
        <li><a href="${pageContext.request.contextPath}/pret?compte=<%= compte.getId() %>">Demander / Rembourser un pret</a></li>
    </ul>
</div>
