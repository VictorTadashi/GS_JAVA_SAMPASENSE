package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.SensorBO;
import br.com.fiap.sampasense.model.Sensor;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sensores", description = "Pluviômetros e sensores de nível d'água")
@RestController
@RequestMapping("/api/sensores")
public class SensorController {

    private final SensorBO sensorBO;

    public SensorController(SensorBO sensorBO) {
        this.sensorBO = sensorBO;
    }

    @GetMapping
    public List<Sensor> listar(@RequestParam(required = false) Long pontoId) {
        return sensorBO.listar(pontoId);
    }

    @GetMapping("/{id}")
    public Sensor buscar(@PathVariable Long id) {
        return sensorBO.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Sensor> cadastrar(@Valid @RequestBody Sensor sensor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorBO.cadastrar(sensor));
    }

    @PutMapping("/{id}")
    public Sensor atualizar(@PathVariable Long id, @Valid @RequestBody Sensor sensor) {
        return sensorBO.atualizar(id, sensor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        sensorBO.remover(id);
        return ResponseEntity.noContent().build();
    }
}
