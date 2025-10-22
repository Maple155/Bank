using Microsoft.EntityFrameworkCore;
using BanqueDepot.Models;

namespace BanqueDepot.Models
{
    public class BanqueDepotContext : DbContext
    {
        public BanqueDepotContext(DbContextOptions<BanqueDepotContext> options)
            : base(options)
        {
        }

        public DbSet<CompteDepot> Comptes { get; set; }
        public DbSet<OperationDepot> Operations { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Configuration table CompteDepot
            modelBuilder.Entity<CompteDepot>(entity =>
            {
                entity.ToTable("compte_depot");

                entity.HasKey(e => e.Id);

                entity.Property(e => e.Id)
                      .HasColumnName("id");

                entity.Property(e => e.Numero)
                      .HasColumnName("numero")
                      .HasMaxLength(20)
                      .ValueGeneratedOnAdd();

                entity.Property(e => e.CodeSecret)
                      .HasColumnName("code_secret")
                      .HasMaxLength(100)
                      .IsRequired();

                entity.Property(e => e.DateOuverture)
                      .HasColumnName("date_ouverture")
                      .IsRequired();

                entity.Property(e => e.ClientId)
                      .HasColumnName("client_id")
                      .IsRequired();

                entity.Property(e => e.Etat)
                      .HasColumnName("etat")
                      .IsRequired();

                // // Relation avec les opérations
                // entity.HasMany(e => e.Operations)
                //       .WithOne(o => o.Compte)
                //       .HasForeignKey(o => o.Compte_id)
                //       .OnDelete(DeleteBehavior.Cascade);
            });

            // Configuration table OperationDepot
            modelBuilder.Entity<OperationDepot>(entity =>
            {
                entity.ToTable("operation_depot");

                entity.HasKey(e => e.Id);

                entity.Property(e => e.Id)
                      .HasColumnName("id");

                entity.Property(e => e.Compte_id)
                      .HasColumnName("compte_id")
                      .IsRequired();

                entity.Property(e => e.Montant)
                      .HasColumnName("montant")
                      .HasColumnType("numeric(15,2)")
                      .IsRequired();

                entity.Property(e => e.DateOperation)
                      .HasColumnName("date_operation")
                      .IsRequired();

                entity.Property(e => e.IsValidate)
                      .HasColumnName("isValidate")
                      .IsRequired();
            });
        }
    }
}
