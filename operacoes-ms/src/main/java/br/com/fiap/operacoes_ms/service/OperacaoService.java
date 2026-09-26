package br.com.fiap.operacoes_ms.service;

import br.com.fiap.operacoes_ms.dto.OperacaoDto;
import br.com.fiap.operacoes_ms.dto.OperacaoExibicaoDto;
import br.com.fiap.operacoes_ms.dto.RelatorioColetaRequestDto;
import br.com.fiap.operacoes_ms.exception.RecursoNaoEncontradoException;
import br.com.fiap.operacoes_ms.http.VoluntarioClient;
import br.com.fiap.operacoes_ms.model.AreaMaritima;
import br.com.fiap.operacoes_ms.model.Operacao;
import br.com.fiap.operacoes_ms.repository.AreaMaritimaRepository;
import br.com.fiap.operacoes_ms.repository.OperacaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperacaoService {

    @Autowired
    private OperacaoRepository operacaoRepository;

    @Autowired
    private AreaMaritimaRepository areaMaritimaRepository;

    @Autowired
    private VoluntarioClient voluntarioClient;

    public OperacaoExibicaoDto cadastrar(OperacaoDto dto) {
        AreaMaritima area = areaMaritimaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Área marítima não encontrada com id: " + dto.getIdArea()));
        Operacao operacao = new Operacao();
        BeanUtils.copyProperties(dto, operacao);
        operacao.setArea(area);
        operacaoRepository.save(operacao);
        return toExibicao(operacao);
    }

    public List<OperacaoExibicaoDto> listar() {
        return operacaoRepository.findAll()
                .stream()
                .map(this::toExibicao)
                .toList();
    }

    public OperacaoExibicaoDto buscarPorId(Long id) {
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Operação não encontrada com id: " + id));
        return toExibicao(operacao);
    }

    public OperacaoExibicaoDto atualizar(Long id, OperacaoDto dto) {
        Operacao operacao = operacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Operação não encontrada com id: " + id));
        AreaMaritima area = areaMaritimaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Área marítima não encontrada com id: " + dto.getIdArea()));
        BeanUtils.copyProperties(dto, operacao);
        operacao.setId(id);
        operacao.setArea(area);
        operacaoRepository.save(operacao);

        if ("CONCLUIDA".equalsIgnoreCase(dto.getStatus())) {
            RelatorioColetaRequestDto relatorio = new RelatorioColetaRequestDto();
            relatorio.setIdOperacao(id);
            relatorio.setIdVoluntario(dto.getIdVoluntario());
            relatorio.setQuantidadeResiduos(dto.getQuantidadeResiduos());
            relatorio.setTipoResiduo(dto.getTipoResiduo());
            relatorio.setObservacoes(dto.getObservacoes());
            voluntarioClient.registrarRelatorio(relatorio);
        }

        return toExibicao(operacao);
    }

    public void deletar(Long id) {
        if (!operacaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException(
                    "Operação não encontrada com id: " + id);
        }
        operacaoRepository.deleteById(id);
    }

    public List<OperacaoExibicaoDto> listarPorStatus(String status) {
        return operacaoRepository.findByStatus(status)
                .stream()
                .map(this::toExibicao)
                .toList();
    }

    private OperacaoExibicaoDto toExibicao(Operacao operacao) {
        OperacaoExibicaoDto dto = new OperacaoExibicaoDto();
        BeanUtils.copyProperties(operacao, dto);
        dto.setIdArea(operacao.getArea().getId());
        dto.setNomeArea(operacao.getArea().getNome());
        return dto;
    }
}