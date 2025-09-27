using Microsoft.AspNetCore.Mvc;
using BanqueDepot.Models;
using BanqueDepot.Services;
using System.Collections.Generic;

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

        [HttpGet]
        public ActionResult<IEnumerable<CompteDepot>> GetAll()
        {
            return Ok(_service.GetAll());
        }

        [HttpGet("{id}")]
        public ActionResult<CompteDepot> GetById(int id)
        {
            var compte = _service.GetById(id);
            if (compte == null)
                return NotFound();
            return Ok(compte);
        }

        [HttpPost]
        public ActionResult<CompteDepot> Create([FromBody] CompteDepot compte)
        {
            var created = _service.Add(compte);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        [HttpPut("{id}")]
        public IActionResult Update(int id, [FromBody] CompteDepot compte)
        {
            var updated = _service.Update(id, compte);
            if (!updated)
                return NotFound();
            return NoContent();
        }

        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            var deleted = _service.Delete(id);
            if (!deleted)
                return NotFound();
            return NoContent();
        }
    }
}