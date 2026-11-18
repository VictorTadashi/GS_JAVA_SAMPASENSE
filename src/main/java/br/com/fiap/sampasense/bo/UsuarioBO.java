package br.com.fiap.sampasense.bo;

import br.com.fiap.sampasense.dao.UsuarioDAO;
import br.com.fiap.sampasense.exception.RegraNegocioException;
import br.com.fiap.sampasense.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioBO {

    private final UsuarioDAO dao;
    private final PasswordEncoder passwordEncoder;

    public UsuarioBO(UsuarioDAO dao, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    /** Cadastra o usuário com a senha criptografada (BCrypt). */
    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        String email = usuario.getEmail().trim().toLowerCase();
        if (dao.existsByEmail(email)) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com o e-mail " + email + ".");
        }
        usuario.setId(null);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return dao.save(usuario);
    }
}
