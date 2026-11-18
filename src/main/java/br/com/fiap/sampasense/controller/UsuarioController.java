package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.bo.UsuarioBO;
import br.com.fiap.sampasense.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Login e geração do token JWT")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioBO usuarioBO;

    public UsuarioController(UsuarioBO usuarioBO) {
        this.usuarioBO = usuarioBO;
    }

    @Operation(summary = "Cadastra um usuário (rota pública)",
            description = "Exemplo: {\"nome\": \"Operador CET 02\", \"email\": \"cet02@sampasense.com\", \"senha\": \"senha123\"}. A senha é gravada com BCrypt e não é devolvida.")
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioBO.cadastrar(usuario));
    }
}
