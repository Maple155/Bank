using BanqueDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace BanqueDepot.Services
{
    public class CompteDepotService
    {
        private readonly BanqueDepotContext _context;

        public CompteDepotService(BanqueDepotContext context)
        {
            _context = context;
        }

        public async Task<List<CompteDepot>> GetAllAsync() =>
            await _context.Comptes.ToListAsync();

        public async Task<CompteDepot?> GetByIdAsync(int id) =>
            await _context.Comptes.FindAsync(id);

        public async Task<List<CompteDepot>> GetByClientIdAsync(int clientId) =>
            await _context.Comptes.Where(c => c.ClientId == clientId).ToListAsync();

        public async Task<CompteDepot?> GetByNumeroAsync(string numero) =>
            await _context.Comptes.FirstOrDefaultAsync(c => c.Numero == numero);

        public async Task<CompteDepot> AddAsync(CompteDepot compte)
        {
            // On prend juste la date, pas l'heure
            compte.DateOuverture = DateTime.SpecifyKind(compte.DateOuverture.Date, DateTimeKind.Utc);

            _context.Comptes.Add(compte);
            await _context.SaveChangesAsync();
            return compte;
        }


        public async Task<bool> UpdateAsync(int id, CompteDepot compte)
        {
            var existing = await GetByIdAsync(id);
            if (existing == null) return false;

            existing.Numero = compte.Numero;
            existing.CodeSecret = compte.CodeSecret;
            existing.ClientId = compte.ClientId;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var compte = await GetByIdAsync(id);
            if (compte == null) return false;

            _context.Comptes.Remove(compte);
            await _context.SaveChangesAsync();
            return true;
        }
    }
}
