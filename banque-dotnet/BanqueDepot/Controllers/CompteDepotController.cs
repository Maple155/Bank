using Microsoft.AspNetCore.Mvc;
using BanqueDepot.Models;
using BanqueDepot.Services;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BanqueDepot.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CompteDepotController : ControllerBase
    {
        private readonly CompteDepotService _service;

        public CompteDepotController(CompteDepotService service)
        {
            _service = service;
        }

        // GET: api/CompteDepot
        [HttpGet]
        public async Task<ActionResult<IEnumerable<CompteDepot>>> GetAll()
        {
            var comptes = await _service.GetAllAsync();
            return Ok(comptes);
        }

        // GET: api/CompteDepot/5
        [HttpGet("{id}")]
        public async Task<ActionResult<CompteDepot>> GetById(int id)
        {
            var compte = await _service.GetByIdAsync(id);
            if (compte == null)
                return NotFound();
            return Ok(compte);
        }

        // GET: api/CompteDepot/by-numero/XXX
        [HttpGet("by-numero/{numero}")]
        public async Task<ActionResult<CompteDepot>> GetByNumero(string numero)
        {
            var compte = await _service.GetByNumeroAsync(numero);
            if (compte == null)
                return NotFound();
            return Ok(compte);
        }

        // GET: api/CompteDepot/by-client/3
        [HttpGet("by-client/{clientId}")]
        public async Task<ActionResult<IEnumerable<CompteDepot>>> GetByClient(int clientId)
        {
            var comptes = await _service.GetByClientIdAsync(clientId);
            return Ok(comptes);
        }

        // POST: api/CompteDepot
        [HttpPost]
        public async Task<ActionResult<CompteDepot>> Create([FromBody] CompteDepot compte)
        {
            var created = await _service.AddAsync(compte);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        // PUT: api/CompteDepot/5
        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] CompteDepot compte)
        {
            var updated = await _service.UpdateAsync(id, compte);
            if (!updated)
                return NotFound();
            return NoContent();
        }

        // DELETE: api/CompteDepot/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _service.DeleteAsync(id);
            if (!deleted)
                return NotFound();
            return NoContent();
        }
    }
}
