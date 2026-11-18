package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.AlertaBO;
import br.com.fiap.sampasense.bo.ConfirmacaoCidadaBO;
import br.com.fiap.sampasense.bo.EventoAlagamentoBO;
import br.com.fiap.sampasense.model.Alerta;
import br.com.fiap.sampasense.model.ConfirmacaoCidada;
import br.com.fiap.sampasense.model.EventoAlagamento;
import br.com.fiap.sampasense.model.enums.StatusEvento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Eventos são abertos e escalados apenas pelo motor de risco (não há POST de evento).
 * O operador pode consultar e encerrar; moradores podem confirmar.
 */
@Tag(name = "Eventos de alagamento", description = "Ocorrências abertas pelo motor de risco")
@RestController
@RequestMapping("/api/eventos")
public class EventoAlagamentoController {

    private final EventoAlagamentoBO eventoBO;
    private final ConfirmacaoCidadaBO confirmacaoBO;
    private final AlertaBO alertaBO;

    public EventoAlagamentoController(EventoAlagamentoBO eventoBO, ConfirmacaoCidadaBO confirmacaoBO,
                                      AlertaBO alertaBO) {
        this.eventoBO = eventoBO;
        this.confirmacaoBO = confirmacaoBO;
        this.alertaBO = alertaBO;
    }

    @GetMapping
    public List<EventoAlagamento> listar(@RequestParam(required = false) StatusEvento status) {
        return eventoBO.listar(status);
    }

    @GetMapping("/{id}")
    public EventoAlagamento buscar(@PathVariable Long id) {
        return eventoBO.buscarPorId(id);
    }

    @Operation(summary = "Encerra manualmente um evento (operador)")
    @PutMapping("/{id}/encerrar")
    public EventoAlagamento encerrar(@PathVariable Long id) {
        return eventoBO.encerrar(id);
    }

    @GetMapping("/{id}/alertas")
    public List<Alerta> alertas(@PathVariable Long id) {
        eventoBO.buscarPorId(id);
        return alertaBO.listar(id);
    }

    @GetMapping("/{id}/confirmacoes")
    public List<ConfirmacaoCidada> confirmacoes(@PathVariable Long id) {
        return confirmacaoBO.listarPorEvento(id);
    }

    @Operation(summary = "Confirmação anônima de morador",
            description = "Rota pública e anônima. Corpo: {\"tipo\": \"ALAGOU\"} ou {\"tipo\": \"BAIXOU\"}. Pode escalar ou encerrar o evento.")
    @SecurityRequirements
    @PostMapping("/{id}/confirmacoes")
    public ResponseEntity<ConfirmacaoCidada> confirmar(@PathVariable Long id,
                                                       @Valid @RequestBody ConfirmacaoCidada confirmacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(confirmacaoBO.registrar(id, confirmacao));
    }
}
