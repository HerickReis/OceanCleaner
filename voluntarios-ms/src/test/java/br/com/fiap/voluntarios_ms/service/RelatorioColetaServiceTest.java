package br.com.fiap.voluntarios_ms.service;

import br.com.fiap.voluntarios_ms.dto.RelatorioColetaDto;
import br.com.fiap.voluntarios_ms.dto.RelatorioColetaExibicaoDto;
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
class RelatorioColetaServiceTest {

    @Mock
    private RelatorioColetaRepository relatorioRepository;

    @Mock
    private VoluntarioRepository voluntarioRepository;

    @InjectMocks
    private RelatorioColetaService service;

    private Voluntario voluntario() {
        Voluntario voluntario = new Voluntario();
        voluntario.setId(2L);
        voluntario.setNome("Ana");
        return voluntario;
    }

    private RelatorioColetaDto novoRelatorio() {
        RelatorioColetaDto dto = new RelatorioColetaDto();
        dto.setIdOperacao(5L);
        dto.setIdVoluntario(2L);
        dto.setQuantidadeResiduos(50);
        dto.setTipoResiduo("Plastico");
        return dto;
    }

    @Test
    void cadastrarDeveVincularVoluntarioESalvar() {
        when(voluntarioRepository.findById(2L)).thenReturn(Optional.of(voluntario()));

        RelatorioColetaExibicaoDto resultado = service.cadastrar(novoRelatorio());

        verify(relatorioRepository).save(any(RelatorioColeta.class));
        assertThat(resultado.getNomeVoluntario()).isEqualTo("Ana");
        assertThat(resultado.getIdVoluntario()).isEqualTo(2L);
        assertThat(resultado.getDataRelatorio()).isEqualTo(LocalDate.now());
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoVoluntarioNaoExiste() {
        when(voluntarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(novoRelatorio()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(relatorioRepository, never()).save(any());
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        when(relatorioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listarPorOperacaoDeveRetornarRelatorios() {
        RelatorioColeta relatorio = new RelatorioColeta();
        relatorio.setTipoResiduo("Plastico");
        relatorio.setVoluntario(voluntario());
        when(relatorioRepository.findByIdOperacao(5L)).thenReturn(List.of(relatorio));

        List<RelatorioColetaExibicaoDto> resultado = service.listarPorOperacao(5L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTipoResiduo()).isEqualTo("Plastico");
        assertThat(resultado.get(0).getNomeVoluntario()).isEqualTo("Ana");
    }

    @Test
    void deletarDeveLancarExcecaoQuandoNaoExiste() {
        when(relatorioRepository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> service.deletar(9L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(relatorioRepository, never()).deleteById(any());
    }

    @Test
    void deletarDeveExcluirQuandoExiste() {
        when(relatorioRepository.existsById(5L)).thenReturn(true);

        service.deletar(5L);

        verify(relatorioRepository).deleteById(5L);
    }
}
