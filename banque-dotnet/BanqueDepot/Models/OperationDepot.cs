using System.ComponentModel.DataAnnotations.Schema;

namespace BanqueDepot.Models
{
    public class OperationDepot
    {
        [Column("id")]        
        public int Id { get; set; }

        [Column("compte_id")]
        public int Compte_id { get; set; }

        [Column("montant")]
        public double Montant { get; set; }

        [Column("date_operation")]
        public DateTime DateOperation { get; set; }

        // public CompteDepot Compte { get; set; }
    }
}
