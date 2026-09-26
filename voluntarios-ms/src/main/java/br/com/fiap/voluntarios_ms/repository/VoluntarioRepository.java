package br.com.fiap.voluntarios_ms.repository;

import br.com.fiap.voluntarios_ms.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {
}