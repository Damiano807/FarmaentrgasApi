package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.infrastucture.models.PontoMapa;
import com.example.FarmaentrgasApi.infrastucture.models.Usuario;
import com.example.FarmaentrgasApi.infrastucture.Repository.*;
import com.example.FarmaentrgasApi.infrastucture.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final LocalizacaoAleatoriaService localizacaoAleatoriaService;

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

    /**
     * Realiza o login do utilizador.
     * Ao fazer login com sucesso, gera automaticamente uma localização
     * aleatória dentro de Luanda e associa ao perfil do utilizador.
     * Este ponto simula a posição GPS do utilizador enquanto estiver logado.
     */
    @Transactional
    public Optional<Usuario> login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository
                .findByEmail(email)
                .filter(u -> u.getSenha().equals(senha));

        // Se login bem-sucedido → atribui localização aleatória
        usuarioOpt.ifPresent(usuario -> {
            PontoMapa pontoAleatorio = localizacaoAleatoriaService.gerarPontoAleatorio();
            usuario.setPontoNoMapa(pontoAleatorio);
            usuarioRepository.save(usuario);
        });

        return usuarioOpt;
    }

    /**
     * Actualiza a localização do utilizador manualmente (ex.: quando o GPS
     * do dispositivo devolve uma posição mais precisa).
     */
    @Transactional
    public Usuario atualizarLocalizacao(Long id, Double latitude, Double longitude, String enderecoFormatado) {
        Usuario usuario = buscarPorId(id);
        PontoMapa ponto = usuario.getPontoNoMapa();
        if (ponto == null) {
            ponto = new PontoMapa();
        }
        ponto.setLatitude(latitude);
        ponto.setLongitude(longitude);
        if (enderecoFormatado != null) {
            ponto.setEnderecoFormatado(enderecoFormatado);
        }
        usuario.setPontoNoMapa(ponto);
        return usuarioRepository.save(usuario);
    }
}
