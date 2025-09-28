using Microsoft.AspNetCore.Mvc;
using BanqueDepot.Models;
using BanqueDepot.Services;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BanqueDepot.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class OperationDepotController : ControllerBase
    {
        private readonly OperationDepotService _service;

        public OperationDepotController(OperationDepotService service)
        {
            _service = service;
        }

        // GET: api/OperationDepot
        [HttpGet]
        public async Task<ActionResult<IEnumerable<OperationDepot>>> GetAll()
        {
            var operations = await _service.GetAllAsync();
            return Ok(operations);
        }

        // GET: api/OperationDepot/5
        [HttpGet("{id}")]
        public async Task<ActionResult<OperationDepot>> GetById(int id)
        {
            var operation = await _service.GetByIdAsync(id);
            if (operation == null)
                return NotFound();
            return Ok(operation);
        }

        // GET: api/OperationDepot/by-compte/3
        [HttpGet("by-compte/{compteId}")]
        public async Task<ActionResult<IEnumerable<OperationDepot>>> GetByCompte(int compteId)
        {
            var operations = await _service.GetByCompteIdAsync(compteId);
            return Ok(operations);
        }

        // GET: api/OperationDepot/solde/3
        [HttpGet("solde/{compteId}")]
        public async Task<ActionResult<double>> GetSolde(int compteId)
        {
            var solde = await _service.GetSoldeAsync(compteId);
            return Ok(solde);
        }

        // POST: api/OperationDepot
        [HttpPost]
        public async Task<ActionResult<OperationDepot>> Create([FromBody] OperationDepot operation)
        {
            var created = await _service.AddAsync(operation);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        // PUT: api/OperationDepot/5
        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] OperationDepot operation)
        {
            var updated = await _service.UpdateAsync(id, operation);
            if (!updated)
                return NotFound();
            return NoContent();
        }

        // DELETE: api/OperationDepot/5
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
