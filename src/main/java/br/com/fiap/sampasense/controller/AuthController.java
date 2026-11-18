package br.com.fiap.sampasense.controller;

import br.com.fiap.sampasense.dto.LoginRequest;
import br.com.fiap.sampasense.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Tag(name = "Autenticação", description = "Login e geração do token JWT")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final long expiracaoHoras;

    public AuthController(JwtEncoder jwtEncoder, AuthenticationManager authenticationManager,
                          @Value("${jwt.expiracao-horas:2}") long expiracaoHoras) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
        this.expiracaoHoras = expiracaoHoras;
    }

    @Operation(summary = "Faz login e devolve o token JWT",
            description = "Use o token no botão Authorize do Swagger ou no cabeçalho Authorization: Bearer {token}.")
    @SecurityRequirements
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest login) {
        // 1. Confere e-mail e senha (UserDetailsService + BCrypt); se falhar, lança AuthenticationException (401)
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.email().trim().toLowerCase(), login.senha()));

        // 2. Monta o JWT: cabeçalho (algoritmo) + payload (claims) + assinatura
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        var agora = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("sampasense-api")
                .subject(authentication.getName())
                .issuedAt(agora)
                .expiresAt(agora.plus(expiracaoHoras, ChronoUnit.HOURS))
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", expiracaoHoras * 3600);
    }
}
