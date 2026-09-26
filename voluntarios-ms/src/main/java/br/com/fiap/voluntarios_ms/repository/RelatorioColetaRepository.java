package br.com.fiap.voluntarios_ms.repository;

import br.com.fiap.voluntarios_ms.model.RelatorioColeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RelatorioColetaRepository extends JpaRepository<RelatorioColeta, Long> {
    List<RelatorioColeta> findByIdOperacao(Long idOperacao);
    List<RelatorioColeta> findByVoluntarioId(Long idVoluntario);
}
