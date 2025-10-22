package com.banque.courant.remote;

import java.util.List;
import com.banque.courant.dto.DirectionDTO;
import com.banque.courant.dto.UtilisateurDTO;
import com.banque.courant.dto.ActionRoleDTO;
import jakarta.ejb.Remote;

@Remote
public interface UtilisateurRemote {
    
    public boolean login(String nom, String motDePasse);
    public UtilisateurDTO getUtilisateurConnecte();
    public void creerUtilisateur(UtilisateurDTO u);
    public List<DirectionDTO> getDirections();
    public List<ActionRoleDTO> getActionRoles();
    public List<UtilisateurDTO> all();
    public UtilisateurDTO find(int id);
}