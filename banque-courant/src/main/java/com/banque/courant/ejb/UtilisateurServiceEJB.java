package com.banque.courant.ejb;

import java.util.List;
import java.util.stream.Collectors;
import com.banque.courant.remote.UtilisateurRemote;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.UtilisateurDTO;
import com.banque.courant.dto.ActionRoleDTO;
import com.banque.entity.Direction;
import com.banque.entity.Utilisateur;
import com.banque.entity.Action_role;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateful
public class UtilisateurServiceEJB implements UtilisateurRemote {

    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    private Utilisateur utilisateurConnecte;
    private List<Direction> directions;
    private List<Action_role> actionRoles;

    // ========================================
    // PostConstruct : préchargement des données
    // ========================================
    @PostConstruct
    public void ejbCreate() {
        try {
            System.out.println("Chargement des donnees en cache...");

            List<Direction> dirList = em.createQuery("SELECT d FROM Direction d", Direction.class).getResultList();
            directions = dirList;

            List<Action_role> arList = em.createQuery("SELECT a FROM Action_role a", Action_role.class).getResultList();
            actionRoles = arList;

            System.out.println("Donnees chargees en cache avec succes !");

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des donnees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================
    // Fonctions CRUD et login
    // ========================================

    @Override
    public UtilisateurDTO find(int id) {
        Utilisateur utilisateur = em.find(Utilisateur.class, id);
        if (utilisateur == null) return null;
        
        return convertToDTO(utilisateur);
    }

    @Override
    public List<UtilisateurDTO> all() {
        List<Utilisateur> utilisateurs = em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
        return utilisateurs.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public boolean login(String nom, String motDePasse) {
        try {
            TypedQuery<Utilisateur> q = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.nom = :nom AND u.mdp = :mdp",
                Utilisateur.class
            );
            q.setParameter("nom", nom);
            q.setParameter("mdp", motDePasse);

            List<Direction> dirList = em.createQuery("SELECT d FROM Direction d", Direction.class).getResultList();
            directions = dirList;

            List<Action_role> arList = em.createQuery("SELECT a FROM Action_role a", Action_role.class).getResultList();
            actionRoles = arList;

            
            utilisateurConnecte = q.getSingleResult();
            System.out.println("Connexion reussie : " + utilisateurConnecte.getNom());
            return true;
        } catch (NoResultException e) {
            System.out.println("Login ou mot de passe incorrect");
            return false;
        }
    }

    @Override
    public UtilisateurDTO getUtilisateurConnecte() {
        if (utilisateurConnecte == null) return null;
        return convertToDTO(utilisateurConnecte);
    }

    @Override
    public void creerUtilisateur(UtilisateurDTO u) {
        // Convertir DTO en entité
        Direction direction = em.find(Direction.class, u.getDirection().getId());
        Utilisateur utilisateur = new Utilisateur(u.getNom(), u.getMdp(), direction, u.getRole());
        em.persist(utilisateur);
    }

    @Override
    public List<DirectionDTO> getDirections() {
        // Convertir les entités Direction en DTO
        return directions.stream()
            .map(d -> new DirectionDTO(d.getId(), d.getLibelle(), d.getNiveau()))
            .collect(Collectors.toList());
    }

    @Override
    public List<ActionRoleDTO> getActionRoles() {
        // Convertir les entités Action_role en DTO
        return actionRoles.stream()
            .map(a -> new ActionRoleDTO(a.getId(), a.getNom_table(), a.getAction(), a.getRole()))
            .collect(Collectors.toList());
    }

    // Méthode utilitaire pour convertir Utilisateur en DTO
    private UtilisateurDTO convertToDTO(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        
        Direction direction = utilisateur.getDirection();
        DirectionDTO directionDTO = new DirectionDTO(
            direction.getId(), 
            direction.getLibelle(), 
            direction.getNiveau()
        );
        
        return new UtilisateurDTO(
            utilisateur.getId(),
            utilisateur.getNom(),
            utilisateur.getMdp(),
            directionDTO,
            utilisateur.getRole()
        );
    }

    @PreDestroy
    public void ejbRemove() {
        try {
            System.out.println("Suppression des donnees...");
            em.createQuery("DELETE FROM Action_role").executeUpdate();
            em.createQuery("DELETE FROM Utilisateur").executeUpdate();
            em.createQuery("DELETE FROM Direction").executeUpdate();
            System.out.println("Tables videes avec succes !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }
}