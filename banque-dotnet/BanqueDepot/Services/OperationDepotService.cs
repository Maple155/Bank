using BanqueDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace BanqueDepot.Services
{
    public class OperationDepotService
    {
        private readonly BanqueDepotContext _context;

        public OperationDepotService(BanqueDepotContext context)
        {
            _context = context;
        }

        public async Task<List<OperationDepot>> GetAllAsync() =>
            await _context.Operations.ToListAsync();

        public async Task<OperationDepot?> GetByIdAsync(int id) =>
            await _context.Operations.FindAsync(id);

        public async Task<List<OperationDepot>> GetByCompteIdAsync(int compteId) =>
            await _context.Operations.Where(o => o.Compte_id == compteId).ToListAsync();

        public async Task<OperationDepot> AddAsync(OperationDepot operation)
        {
            operation.DateOperation = DateTime.SpecifyKind(operation.DateOperation.Date, DateTimeKind.Utc);
            _context.Operations.Add(operation);
            await _context.SaveChangesAsync();
            return operation;
        }

        public async Task<bool> UpdateAsync(int id, OperationDepot operation)
        {
            var existing = await GetByIdAsync(id);
            if (existing == null) return false;

            existing.Compte_id = operation.Compte_id;
            existing.Montant = operation.Montant;
            existing.DateOperation = operation.DateOperation;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var operation = await GetByIdAsync(id);
            if (operation == null) return false;

            _context.Operations.Remove(operation);
            await _context.SaveChangesAsync();
            return true;
        }

        // Retourne le solde actuel d'un compte
        public async Task<double> GetSoldeAsync(int compteId)
        {
            return await _context.Operations
                .Where(o => o.Compte_id == compteId && o.IsValidate == true)
                .SumAsync(o => o.Montant);
        }
    }
}
