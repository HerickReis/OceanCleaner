package br.com.fiap.voluntarios_ms.service;

import br.com.fiap.voluntarios_ms.dto.RelatorioColetaDto;
import br.com.fiap.voluntarios_ms.dto.VoluntarioDto;
import br.com.fiap.voluntarios_ms.dto.VoluntarioExibicaoDto;
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
public class VoluntarioService {

    @Autowired
    private VoluntarioRepository repository;

    @Autowired
    private RelatorioColetaRepository relatorioRepository;

    public VoluntarioExibicaoDto cadastrar(VoluntarioDto dto) {
        repository.findByEmail(dto.getEmail()).ifPresent(v -> {
            throw new IllegalStateException("E-mail já cadastrado: " + dto.getEmail());
        });
        Voluntario voluntario = new Voluntario();
        BeanUtils.copyProperties(dto, voluntario);
        voluntario.setDataCadastro(LocalDate.now());
        repository.save(voluntario);
        return toExibicao(voluntario);
    }

    public List<VoluntarioExibicaoDto> listar() {
        return repository.findAll()
                .stream()
                .map(this::toExibicao)
                .toList();
    }

    public VoluntarioExibicaoDto buscarPorId(Long id) {
        Voluntario voluntario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Voluntário não encontrado com id: " + id));
        return toExibicao(voluntario);
    }

    public VoluntarioExibicaoDto atualizar(Long id, VoluntarioDto dto) {
        Voluntario voluntario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Voluntário não encontrado com id: " + id));
        repository.findByEmail(dto.getEmail()).ifPresent(v -> {
            if (!v.getId().equals(id)) {
                throw new IllegalStateException("E-mail já cadastrado: " + dto.getEmail());
            }
        });
        BeanUtils.copyProperties(dto, voluntario);
        voluntario.setId(id);
        repository.save(voluntario);
        return toExibicao(voluntario);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException(
                    "Voluntário não encontrado com id: " + id);
        }
        List<RelatorioColeta> relatorios = relatorioRepository.findByVoluntarioId(id);
        if (!relatorios.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível excluir o voluntário pois existem " +
                            relatorios.size() + " relatório(s) vinculado(s) a ele. " +
                            "Exclua os relatórios primeiro.");
        }
        repository.deleteById(id);
    }

    private VoluntarioExibicaoDto toExibicao(Voluntario voluntario) {
        VoluntarioExibicaoDto dto = new VoluntarioExibicaoDto();
        BeanUtils.copyProperties(voluntario, dto);
        return dto;
    }
}