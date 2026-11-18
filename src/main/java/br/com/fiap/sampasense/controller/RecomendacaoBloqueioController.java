package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.RecomendacaoBloqueioBO;
import br.com.fiap.sampasense.dto.DecisaoBloqueioRequest;
import br.com.fiap.sampasense.model.RecomendacaoBloqueio;
import br.com.fiap.sampasense.model.enums.StatusBloqueio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bloqueios", description = "Recomendações de fechamento de semáforos (operador CET/CGE)")
@RestController
@RequestMapping("/api/bloqueios")
public class RecomendacaoBloqueioController {

    private final RecomendacaoBloqueioBO bloqueioBO;

    public RecomendacaoBloqueioController(RecomendacaoBloqueioBO bloqueioBO) {
        this.bloqueioBO = bloqueioBO;
    }

    @GetMapping
    public List<RecomendacaoBloqueio> listar(@RequestParam(required = false) StatusBloqueio status) {
        return bloqueioBO.listar(status);
    }

    @GetMapping("/{id}")
    public RecomendacaoBloqueio buscar(@PathVariable Long id) {
        return bloqueioBO.buscarPorId(id);
    }

    @Operation(summary = "Operador confirma o fechamento dos semáforos (o \"um clique\")")
    @PutMapping("/{id}/confirmar")
    public RecomendacaoBloqueio confirmar(@PathVariable Long id, @Valid @RequestBody DecisaoBloqueioRequest decisao) {
        return bloqueioBO.confirmar(id, decisao.operador());
    }

    @Operation(summary = "Operador recusa o fechamento dos semáforos")
    @PutMapping("/{id}/recusar")
    public RecomendacaoBloqueio recusar(@PathVariable Long id, @Valid @RequestBody DecisaoBloqueioRequest decisao) {
        return bloqueioBO.recusar(id, decisao.operador());
    }
}
