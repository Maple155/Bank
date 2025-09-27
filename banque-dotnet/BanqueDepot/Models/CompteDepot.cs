namespace BanqueDepot.Models
{
    public class CompteDepot
    {
        public int Id { get; set; }
        public string Client { get; set; } = string.Empty;
        public double Solde { get; set; }
        // Ajoute d'autres propriétés si besoin
    }
}