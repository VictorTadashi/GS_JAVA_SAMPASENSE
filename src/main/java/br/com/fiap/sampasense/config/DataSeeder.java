package br.com.fiap.sampasense.config;

import br.com.fiap.sampasense.dao.LeituraDAO;
import br.com.fiap.sampasense.dao.PontoMonitoradoDAO;
import br.com.fiap.sampasense.dao.PontoSeguroDAO;
import br.com.fiap.sampasense.dao.SensorDAO;
import br.com.fiap.sampasense.dao.UsuarioDAO;
import br.com.fiap.sampasense.model.Leitura;
import br.com.fiap.sampasense.model.PontoMonitorado;
import br.com.fiap.sampasense.model.PontoSeguro;
import br.com.fiap.sampasense.model.Sensor;
import br.com.fiap.sampasense.model.Usuario;
import br.com.fiap.sampasense.model.enums.StatusSensor;
import br.com.fiap.sampasense.model.enums.TipoPonto;
import br.com.fiap.sampasense.model.enums.TipoPontoSeguro;
import br.com.fiap.sampasense.model.enums.TipoSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrega dados SIMULADOS na primeira execução (banco vazio), para a API já nascer demonstrável.
 * Nenhum dado pessoal real é usado. Desative com SEED_ENABLED=false.
 */
@Component
@ConditionalOnProperty(name = "sampasense.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PontoMonitoradoDAO pontoDAO;
    private final SensorDAO sensorDAO;
    private final LeituraDAO leituraDAO;
    private final PontoSeguroDAO pontoSeguroDAO;
    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;
    private final String demoEmail;
    private final String demoSenha;

    public DataSeeder(PontoMonitoradoDAO pontoDAO, SensorDAO sensorDAO, LeituraDAO leituraDAO,
                      PontoSeguroDAO pontoSeguroDAO, UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder,
                      @Value("${sampasense.demo.email}") String demoEmail,
                      @Value("${sampasense.demo.senha}") String demoSenha) {
        this.pontoDAO = pontoDAO;
        this.sensorDAO = sensorDAO;
        this.leituraDAO = leituraDAO;
        this.pontoSeguroDAO = pontoSeguroDAO;
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
        this.demoEmail = demoEmail;
        this.demoSenha = demoSenha;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Usuário de demonstração para testar o login (criado só se ainda não existir)
        if (!usuarioDAO.existsByEmail(demoEmail)) {
            usuarioDAO.save(new Usuario("Operador CET 01", demoEmail, passwordEncoder.encode(demoSenha)));
            log.info("Usuário de demonstração criado: {}", demoEmail);
        }

        if (pontoDAO.count() > 0) {
            log.info("Banco já possui dados. Seed ignorado.");
            return;
        }

        // Limites: chuva 30 mm/h (ATENÇÃO) | nível 30 cm (alagado) | subida de 10 cm entre leituras (ALERTA)
        PontoMonitorado vale = pontoDAO.save(new PontoMonitorado("Vale do Anhangabaú", TipoPonto.VALE, "Centro",
                -23.5455, -46.6370, false, 30.0, 30.0, 10.0));
        PontoMonitorado tunel = pontoDAO.save(new PontoMonitorado("Túnel do Anhangabaú", TipoPonto.TUNEL, "Centro",
                -23.5487, -46.6362, true, 30.0, 30.0, 10.0));
        PontoMonitorado glicerio = pontoDAO.save(new PontoMonitorado("Baixada do Glicério", TipoPonto.VALE, "Liberdade",
                -23.5530, -46.6280, false, 30.0, 30.0, 10.0));

        // 3 sensores por ponto: 1 pluviômetro + 2 sensores de nível (permitem a confirmação dupla)
        List<Sensor> sensores = new ArrayList<>();
        sensores.addAll(sensoresPara(vale, "VAL"));
        sensores.addAll(sensoresPara(tunel, "TUN"));
        sensores.addAll(sensoresPara(glicerio, "GLI"));
        sensores = sensorDAO.saveAll(sensores);

        // Uma leitura inicial "normal" por sensor (referência para calcular a subida da água)
        LocalDateTime inicio = LocalDateTime.now().minusMinutes(10);
        List<Leitura> leituras = new ArrayList<>();
        for (Sensor s : sensores) {
            double valor = s.getTipo() == TipoSensor.PLUVIOMETRO ? 2.0 : 5.0;
            leituras.add(new Leitura(s, valor, inicio));
        }
        leituraDAO.saveAll(leituras);

        // Pontos seguros (nomes genéricos: parceiros simulados)
        pontoSeguroDAO.saveAll(List.of(
                new PontoSeguro("Escola Parceira Centro", TipoPontoSeguro.ESCOLA, "Rua Líbero Badaró (simulado)",
                        -23.5468, -46.6345, true, 200, vale),
                new PontoSeguro("Comércio Parceiro Vale", TipoPontoSeguro.COMERCIO, "Rua Formosa (simulado)",
                        -23.5440, -46.6385, false, 40, vale),
                new PontoSeguro("UBS Parceira Sé", TipoPontoSeguro.UBS, "Praça da Sé (simulado)",
                        -23.5503, -46.6339, true, 120, tunel),
                new PontoSeguro("Escola Parceira Sé", TipoPontoSeguro.ESCOLA, "Rua Benjamin Constant (simulado)",
                        -23.5495, -46.6350, false, 150, tunel),
                new PontoSeguro("UBS Parceira Liberdade", TipoPontoSeguro.UBS, "Rua da Glória (simulado)",
                        -23.5560, -46.6330, true, 100, glicerio),
                new PontoSeguro("Comércio Parceiro Glicério", TipoPontoSeguro.COMERCIO, "Rua do Glicério (simulado)",
                        -23.5545, -46.6300, false, 30, glicerio)
        ));

        log.info("Seed concluído: 3 pontos, 9 sensores, 9 leituras e 6 pontos seguros (dados simulados).");
    }

    private List<Sensor> sensoresPara(PontoMonitorado ponto, String sigla) {
        LocalDate instalacao = LocalDate.of(2026, 10, 1);
        return List.of(
                new Sensor("PLV-" + sigla + "-01", TipoSensor.PLUVIOMETRO, StatusSensor.ATIVO, instalacao, ponto),
                new Sensor("NIV-" + sigla + "-01", TipoSensor.NIVEL_AGUA, StatusSensor.ATIVO, instalacao, ponto),
                new Sensor("NIV-" + sigla + "-02", TipoSensor.NIVEL_AGUA, StatusSensor.ATIVO, instalacao, ponto)
        );
    }
}
