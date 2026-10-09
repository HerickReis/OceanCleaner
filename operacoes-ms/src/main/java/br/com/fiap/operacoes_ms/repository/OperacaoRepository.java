package br.com.fiap.operacoes_ms.repository;
import br.com.fiap.operacoes_ms.model.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {
    List<Operacao> findByAreaId(Long idArea);
    List<Operacao> findByStatusIgnoreCase(String status);
}

