package br.com.fiap.operacoes_ms.service;
import br.com.fiap.operacoes_ms.dto.AreaMaritimaDto;
import br.com.fiap.operacoes_ms.dto.AreaMaritimaExibicaoDto;
import br.com.fiap.operacoes_ms.exception.RecursoNaoEncontradoException;
import br.com.fiap.operacoes_ms.model.AreaMaritima;
import br.com.fiap.operacoes_ms.model.Operacao;
import br.com.fiap.operacoes_ms.repository.AreaMaritimaRepository;
import br.com.fiap.operacoes_ms.repository.OperacaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AreaMaritimaService {

    @Autowired
    private AreaMaritimaRepository repository;

    @Autowired
    private OperacaoRepository operacaoRepository;

    public AreaMaritimaExibicaoDto cadastrar(AreaMaritimaDto dto) {
        AreaMaritima area = new AreaMaritima();
        BeanUtils.copyProperties(dto, area);
        area.setDataCadastro(LocalDate.now());
        repository.save(area);
        return toExibicao(area);
    }

    public List<AreaMaritimaExibicaoDto> listar() {
        return repository.findAll()
                .stream()
                .map(this::toExibicao)
                .toList();
    }

    public AreaMaritimaExibicaoDto buscarPorId(Long id) {
        AreaMaritima area = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Área marítima não encontrada com id: " + id));
        return toExibicao(area);
    }

    public AreaMaritimaExibicaoDto atualizar(Long id, AreaMaritimaDto dto) {
        AreaMaritima area = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Área marítima não encontrada com id: " + id));
        BeanUtils.copyProperties(dto, area);
        repository.save(area);
        return toExibicao(area);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException(
                    "Área marítima não encontrada com id: " + id);
        }
        List<Operacao> operacoes = operacaoRepository.findByAreaId(id);
        if (!operacoes.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível excluir a área marítima pois existem " +
                            operacoes.size() + " operação(ões) vinculada(s) a ela. " +
                            "Exclua as operações primeiro.");
        }
        repository.deleteById(id);
    }

    private AreaMaritimaExibicaoDto toExibicao(AreaMaritima area) {
        AreaMaritimaExibicaoDto dto = new AreaMaritimaExibicaoDto();
        BeanUtils.copyProperties(area, dto);
        return dto;
    }
}