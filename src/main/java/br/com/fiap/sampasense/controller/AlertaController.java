package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.AlertaBO;
import br.com.fiap.sampasense.model.Alerta;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Somente leitura: alertas são disparados exclusivamente pelo sistema. */
@Tag(name = "Alertas", description = "Histórico de avisos disparados por canal")
@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaBO alertaBO;

    public AlertaController(AlertaBO alertaBO) {
        this.alertaBO = alertaBO;
    }

    @GetMapping
    public List<Alerta> listar(@RequestParam(required = false) Long eventoId) {
        return alertaBO.listar(eventoId);
    }

    @GetMapping("/{id}")
    public Alerta buscar(@PathVariable Long id) {
        return alertaBO.buscarPorId(id);
    }
}
