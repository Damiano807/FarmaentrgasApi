package com.example.FarmaentrgasApi.infrastucture.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.FarmaentrgasApi.infrastucture.models.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository   extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

   // List<Usuario> findByTipo(Perfil tipo);

}
