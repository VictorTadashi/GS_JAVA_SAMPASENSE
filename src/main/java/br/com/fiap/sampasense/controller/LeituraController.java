package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.LeituraBO;
import br.com.fiap.sampasense.dto.ResultadoLeituraResponse;
import br.com.fiap.sampasense.model.Leitura;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Leituras", description = "Medições enviadas pelos sensores")
@RestController
@RequestMapping("/api/leituras")
public class LeituraController {

    private final LeituraBO leituraBO;

    public LeituraController(LeituraBO leituraBO) {
        this.leituraBO = leituraBO;
    }

    @GetMapping
    public List<Leitura> listar(@RequestParam(required = false) Long sensorId) {
        return leituraBO.listar(sensorId);
    }

    @GetMapping("/{id}")
    public Leitura buscar(@PathVariable Long id) {
        return leituraBO.buscarPorId(id);
    }

    @Operation(summary = "Registra uma leitura e roda o motor de risco",
            description = "Exemplo: {\"sensor\": {\"id\": 1}, \"valor\": 42.5}. A resposta traz o nível de risco resultante no ponto.")
    @PostMapping
    public ResponseEntity<ResultadoLeituraResponse> registrar(@Valid @RequestBody Leitura leitura) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leituraBO.registrar(leitura));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        leituraBO.remover(id);
        return ResponseEntity.noContent().build();
    }
}
