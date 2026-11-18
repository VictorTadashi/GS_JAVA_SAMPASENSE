package br.com.fiap.sampasense.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** Rota raiz: mostra que a API está no ar (também usada como health check no Render). */
@Hidden
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("aplicacao", "SampaSense API");
        info.put("descricao", "Alerta de alagamento e rota segura em tempo real para vales e túneis de São Paulo");
        info.put("status", "online");
        info.put("documentacao", "/swagger-ui.html");
        return info;
    }
}
