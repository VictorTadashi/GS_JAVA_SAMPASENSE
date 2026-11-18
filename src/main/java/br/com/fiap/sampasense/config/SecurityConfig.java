package br.com.fiap.sampasense.config;

import br.com.fiap.sampasense.dao.UsuarioDAO;
import br.com.fiap.sampasense.exception.ErroResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Segurança da API com Spring Security + JWT (padrão da Aula 18).
 *
 * Rotas PÚBLICAS (cidadão, sem login):
 *  - GET  /api/pontos/{id}/risco e /api/pontos/{id}/pontos-seguros  (portal do Wi-Fi e totem)
 *  - POST /api/eventos/{id}/confirmacoes                            (confirmação anônima "alagou aqui")
 *  - POST /api/auth/login e POST /api/usuarios                      (login e cadastro)
 *  - Swagger e a rota raiz "/"
 * Todas as demais exigem o token JWT (operador, administrador e sensores).
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        return http
                // API stateless com token: proteção CSRF não se aplica
                .csrf(csrf -> csrf.disable())
                // CORS liberado para o front-end (totem, portal do Wi-Fi, painel do operador)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                // Não guarda sessão: cada requisição se autentica pelo token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Regras de acesso
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pontos/*/risco", "/api/pontos/*/pontos-seguros").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/eventos/*/confirmacoes").permitAll()
                        .anyRequest().authenticated()
                )
                // Valida o JWT enviado em Authorization: Bearer {token}
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint((req, res, ex) -> escreverErro(objectMapper, req, res, HttpStatus.UNAUTHORIZED,
                                "Token ausente, inválido ou expirado. Faça login em POST /api/auth/login.")))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> escreverErro(objectMapper, req, res, HttpStatus.UNAUTHORIZED,
                                "Esta rota exige autenticação. Faça login em POST /api/auth/login."))
                        .accessDeniedHandler((req, res, e) -> escreverErro(objectMapper, req, res, HttpStatus.FORBIDDEN,
                                "Acesso negado.")))
                .build();
    }

    /** Ensina o Spring Security a buscar o usuário pelo e-mail no banco. */
    @Bean
    UserDetailsService userDetailsService(UsuarioDAO usuarioDAO) {
        return email -> {
            var usuario = usuarioDAO.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(email));
            return User.withUsername(usuario.getEmail())
                    .password(usuario.getSenha())
                    .authorities(List.of())
                    .build();
        };
    }

    /** Criptografa e confere senhas com BCrypt. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Autentica e-mail e senha usando o UserDetailsService e o PasswordEncoder acima. */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /** Gera e assina os tokens com a chave secreta (HS256). */
    @Bean
    JwtEncoder jwtEncoder(@Value("${jwt.secret}") String secret) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(secret)));
    }

    /** Valida e decodifica os tokens recebidos com a mesma chave. */
    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        return NimbusJwtDecoder.withSecretKey(secretKey(secret))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private SecretKey secretKey(String secret) {
        return new SecretKeySpec(Base64.getDecoder().decode(secret), "HmacSHA256");
    }

    /** Responde 401/403 no mesmo formato JSON (ErroResponse) usado pelo GlobalExceptionHandler. */
    private void escreverErro(ObjectMapper mapper, HttpServletRequest req, HttpServletResponse res,
                              HttpStatus status, String mensagem) throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        mapper.writeValue(res.getOutputStream(), new ErroResponse(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI(), null));
    }
}
