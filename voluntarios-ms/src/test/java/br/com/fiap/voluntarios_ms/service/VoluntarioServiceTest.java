package br.com.fiap.voluntarios_ms.service;

import br.com.fiap.voluntarios_ms.dto.VoluntarioDto;
import br.com.fiap.voluntarios_ms.dto.VoluntarioExibicaoDto;
import br.com.fiap.voluntarios_ms.exception.RecursoNaoEncontradoException;
import br.com.fiap.voluntarios_ms.model.RelatorioColeta;
import br.com.fiap.voluntarios_ms.model.Voluntario;
import br.com.fiap.voluntarios_ms.repository.RelatorioColetaRepository;
import br.com.fiap.voluntarios_ms.repository.VoluntarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoluntarioServiceTest {

    @Mock
    private VoluntarioRepository repository;

    @Mock
    private RelatorioColetaRepository relatorioRepository;

    @InjectMocks
    private VoluntarioService service;

    @Test
    void cadastrarDevePreencherDataCadastroESalvar() {
        VoluntarioDto dto = new VoluntarioDto();
        dto.setNome("Ana");
        dto.setEmail("ana@teste.com");

        VoluntarioExibicaoDto resultado = service.cadastrar(dto);

        verify(repository).save(any(Voluntario.class));
        assertThat(resultado.getNome()).isEqualTo("Ana");
        assertThat(resultado.getEmail()).isEqualTo("ana@teste.com");
        assertThat(resultado.getDataCadastro()).isEqualTo(LocalDate.now());
    }

    @Test
    void buscarPorIdDeveRetornarVoluntarioExistente() {
        Voluntario voluntario = new Voluntario();
        voluntario.setId(1L);
        voluntario.setNome("Ana");
        when(repository.findById(1L)).thenReturn(Optional.of(voluntario));

        VoluntarioExibicaoDto resultado = service.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Ana");
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletarDeveImpedirExclusaoComRelatoriosVinculados() {
        when(repository.existsById(1L)).thenReturn(true);
        when(relatorioRepository.findByVoluntarioId(1L)).thenReturn(List.of(new RelatorioColeta()));

        assertThatThrownBy(() -> service.deletar(1L))
                .isInstanceOf(IllegalStateException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deletarDeveExcluirVoluntarioSemRelatorios() {
        when(repository.existsById(1L)).thenReturn(true);
        when(relatorioRepository.findByVoluntarioId(1L)).thenReturn(List.of());

        service.deletar(1L);

        verify(repository).deleteById(1L);
    }
}
