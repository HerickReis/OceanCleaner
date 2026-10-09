package br.com.fiap.voluntarios_ms.repository;

import br.com.fiap.voluntarios_ms.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {
    Optional<Voluntario> findByEmail(String email);
}