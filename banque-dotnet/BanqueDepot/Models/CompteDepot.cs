using System.ComponentModel.DataAnnotations.Schema;

namespace BanqueDepot.Models
{
    public class CompteDepot
    {
        [Column("id")]
        public int Id { get; set; }

        [Column("numero")]
        public string? Numero { get; set; }

        [Column("code_secret")]
        public string? CodeSecret { get; set; }

        [Column("date_ouverture")]
        public DateTime DateOuverture { get; set; }

        [Column("client_id")]
        public int ClientId { get; set; }

        // public List<OperationDepot> Operations { get; set; } = new();
    }
}
