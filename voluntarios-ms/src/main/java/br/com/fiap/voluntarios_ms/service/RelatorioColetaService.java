package br.com.fiap.voluntarios_ms.service;

import br.com.fiap.voluntarios_ms.dto.RelatorioColetaDto;
import br.com.fiap.voluntarios_ms.dto.RelatorioColetaExibicaoDto;
import br.com.fiap.voluntarios_ms.exception.RecursoNaoEncontradoException;
import br.com.fiap.voluntarios_ms.model.RelatorioColeta;
import br.com.fiap.voluntarios_ms.model.Voluntario;
import br.com.fiap.voluntarios_ms.repository.RelatorioColetaRepository;
import br.com.fiap.voluntarios_ms.repository.VoluntarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioColetaService {

    @Autowired
    private RelatorioColetaRepository relatorioRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    public RelatorioColetaExibicaoDto cadastrar(RelatorioColetaDto dto) {
        Voluntario voluntario = voluntarioRepository.findById(dto.getIdVoluntario())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Voluntário não encontrado com id: " + dto.getIdVoluntario()));
        RelatorioColeta relatorio = new RelatorioColeta();
        BeanUtils.copyProperties(dto, relatorio);
        relatorio.setVoluntario(voluntario);
        relatorio.setDataRelatorio(LocalDate.now());
        relatorioRepository.save(relatorio);
        return toExibicao(relatorio);
    }

    public List<RelatorioColetaExibicaoDto> listar() {
        return relatorioRepository.findAll()
                .stream()
                .map(this::toExibicao)
                .toList();
    }

    public RelatorioColetaExibicaoDto buscarPorId(Long id) {
        RelatorioColeta relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Relatório não encontrado com id: " + id));
        return toExibicao(relatorio);
    }

    public List<RelatorioColetaExibicaoDto> listarPorOperacao(Long idOperacao) {
        return relatorioRepository.findByIdOperacao(idOperacao)
                .stream()
                .map(this::toExibicao)
                .toList();
    }

    public List<RelatorioColetaExibicaoDto> listarPorVoluntario(Long idVoluntario) {
        return relatorioRepository.findByVoluntarioId(idVoluntario)
                .stream()
                .map(this::toExibicao)
                .toList();
    }

    public void deletar(Long id){
        if (!relatorioRepository.existsById(id)){
            throw new RecursoNaoEncontradoException(
                    "Relatório não encontrado com o id: " + id
            );
        }
        relatorioRepository.deleteById(id);
    }

    private RelatorioColetaExibicaoDto toExibicao(RelatorioColeta relatorio) {
        RelatorioColetaExibicaoDto dto = new RelatorioColetaExibicaoDto();
        BeanUtils.copyProperties(relatorio, dto);
        dto.setIdVoluntario(relatorio.getVoluntario().getId());
        dto.setNomeVoluntario(relatorio.getVoluntario().getNome());
        return dto;
    }


}