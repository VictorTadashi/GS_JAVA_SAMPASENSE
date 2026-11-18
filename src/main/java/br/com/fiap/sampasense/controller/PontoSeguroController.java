package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.PontoSeguroBO;
import br.com.fiap.sampasense.model.PontoSeguro;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pontos seguros", description = "Escolas, UBS e comércios parceiros em locais altos")
@RestController
@RequestMapping("/api/pontos-seguros")
public class PontoSeguroController {

    private final PontoSeguroBO pontoSeguroBO;

    public PontoSeguroController(PontoSeguroBO pontoSeguroBO) {
        this.pontoSeguroBO = pontoSeguroBO;
    }

    @GetMapping
    public List<PontoSeguro> listar() {
        return pontoSeguroBO.listar();
    }

    @GetMapping("/{id}")
    public PontoSeguro buscar(@PathVariable Long id) {
        return pontoSeguroBO.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<PontoSeguro> cadastrar(@Valid @RequestBody PontoSeguro pontoSeguro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pontoSeguroBO.cadastrar(pontoSeguro));
    }

    @PutMapping("/{id}")
    public PontoSeguro atualizar(@PathVariable Long id, @Valid @RequestBody PontoSeguro pontoSeguro) {
        return pontoSeguroBO.atualizar(id, pontoSeguro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        pontoSeguroBO.remover(id);
        return ResponseEntity.noContent().build();
    }
}
