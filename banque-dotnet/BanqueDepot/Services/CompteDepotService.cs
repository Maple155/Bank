using BanqueDepot.Models;
using System.Collections.Generic;
using System.Linq;

namespace BanqueDepot.Services
{
    public class CompteDepotService
    {
        private readonly List<CompteDepot> _comptes = new();
        private int _nextId = 1;

        public IEnumerable<CompteDepot> GetAll() => _comptes;

        public CompteDepot? GetById(int id) => _comptes.FirstOrDefault(c => c.Id == id);

        public CompteDepot Add(CompteDepot compte)
        {
            compte.Id = _nextId++;
            _comptes.Add(compte);
            return compte;
        }

        public bool Update(int id, CompteDepot compte)
        {
            var existing = GetById(id);
            if (existing == null)
                return false;
            existing.Client = compte.Client;
            existing.Solde = compte.Solde;
            return true;
        }

        public bool Delete(int id)
        {
            var compte = GetById(id);
            if (compte == null)
                return false;
            _comptes.Remove(compte);
            return true;
        }
    }
}