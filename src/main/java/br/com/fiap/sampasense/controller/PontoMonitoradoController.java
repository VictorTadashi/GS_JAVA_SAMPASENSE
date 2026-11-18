package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.AvaliacaoRiscoBO;
import br.com.fiap.sampasense.bo.PontoMonitoradoBO;
import br.com.fiap.sampasense.bo.PontoSeguroBO;
import br.com.fiap.sampasense.dto.PontoSeguroProximoResponse;
import br.com.fiap.sampasense.dto.RiscoResponse;
import br.com.fiap.sampasense.model.PontoMonitorado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pontos monitorados", description = "Vales e túneis com sensores")
@RestController
@RequestMapping("/api/pontos")
public class PontoMonitoradoController {

    private final PontoMonitoradoBO pontoBO;
    private final AvaliacaoRiscoBO avaliacaoRiscoBO;
    private final PontoSeguroBO pontoSeguroBO;

    public PontoMonitoradoController(PontoMonitoradoBO pontoBO, AvaliacaoRiscoBO avaliacaoRiscoBO,
                                     PontoSeguroBO pontoSeguroBO) {
        this.pontoBO = pontoBO;
        this.avaliacaoRiscoBO = avaliacaoRiscoBO;
        this.pontoSeguroBO = pontoSeguroBO;
    }

    @GetMapping
    public List<PontoMonitorado> listar() {
        return pontoBO.listar();
    }

    @GetMapping("/{id}")
    public PontoMonitorado buscar(@PathVariable Long id) {
        return pontoBO.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<PontoMonitorado> cadastrar(@Valid @RequestBody PontoMonitorado ponto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pontoBO.cadastrar(ponto));
    }

    @PutMapping("/{id}")
    public PontoMonitorado atualizar(@PathVariable Long id, @Valid @RequestBody PontoMonitorado ponto) {
        return pontoBO.atualizar(id, ponto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        pontoBO.remover(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Situação atual do ponto",
            description = "Rota pública. Nível de risco, orientação ao cidadão e pontos seguros mais próximos (conteúdo do totem e do Wi-Fi em modo emergência).")
    @SecurityRequirements
    @GetMapping("/{id}/risco")
    public RiscoResponse risco(@PathVariable Long id) {
        return avaliacaoRiscoBO.consultarRisco(id);
    }

    @Operation(summary = "Pontos seguros do entorno, do mais próximo ao mais distante",
            description = "Rota pública. Use acessivel=true para listar apenas locais sem escadas ou ladeiras (idosos e PcD).")
    @SecurityRequirements
    @GetMapping("/{id}/pontos-seguros")
    public List<PontoSeguroProximoResponse> pontosSeguros(@PathVariable Long id,
                                                          @RequestParam(defaultValue = "false") boolean acessivel) {
        return pontoSeguroBO.listarProximos(id, acessivel);
    }
}
