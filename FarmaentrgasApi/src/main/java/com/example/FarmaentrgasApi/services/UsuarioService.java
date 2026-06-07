package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.infrastucture.models.Usuario;
import com.example.FarmaentrgasApi.infrastucture.Repository.*;
import  com.example.FarmaentrgasApi.infrastucture.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }

    @Transactional
    public Usuario criar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Já existe um usuário com o email: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

  /*  @Transactional
    public Usuario atualizar(Long id, Usuario dadosAtualizados) {
        Usuario usuario = buscarPorId(id);
        usuario.setName(dadosAtualizados.getName());
        usuario.setEmail(dadosAtualizados.getEmail());
        usuario.setTelefone(dadosAtualizados.getTelefone());
        usuario.setTipo(dadosAtualizados.getTipo());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void adicionarSaldo(Long id, Double valor) {
        Usuario usuario = buscarPorId(id);
        if (valor <= 0) throw new RuntimeException("Valor deve ser positivo.");
        usuario.setCarteira(usuario.getCarteira() + valor);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void debitarSaldo(Long id, Double valor) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getCarteira() < valor) {
            throw new RuntimeException("Saldo insuficiente.");
        }
        usuario.setCarteira(usuario.getCarteira() - valor);
        usuarioRepository.save(usuario);
    }

   */

    public Optional<Usuario> login(String email, String senha) {
        return usuarioRepository
                .findByEmail(email)                        // query method do Spring Data
                .filter(u -> u.getSenha().equals(senha));  // compara senha (texto simples por agora)
    }
}
