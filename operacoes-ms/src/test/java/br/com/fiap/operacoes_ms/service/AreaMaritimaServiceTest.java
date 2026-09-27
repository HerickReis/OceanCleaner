package br.com.fiap.operacoes_ms.service;

import br.com.fiap.operacoes_ms.dto.AreaMaritimaDto;
import br.com.fiap.operacoes_ms.dto.AreaMaritimaExibicaoDto;
import br.com.fiap.operacoes_ms.exception.RecursoNaoEncontradoException;
import br.com.fiap.operacoes_ms.model.AreaMaritima;
import br.com.fiap.operacoes_ms.model.Operacao;
import br.com.fiap.operacoes_ms.repository.AreaMaritimaRepository;
import br.com.fiap.operacoes_ms.repository.OperacaoRepository;
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
class AreaMaritimaServiceTest {

    @Mock
    private AreaMaritimaRepository repository;

    @Mock
    private OperacaoRepository operacaoRepository;

    @InjectMocks
    private AreaMaritimaService service;

    @Test
    void cadastrarDevePreencherDataCadastroESalvar() {
        AreaMaritimaDto dto = new AreaMaritimaDto();
        dto.setNome("Baia de Guanabara");
        dto.setLocalizacao("RJ");
        dto.setNivelPoluicao("ALTO");

        AreaMaritimaExibicaoDto resultado = service.cadastrar(dto);

        verify(repository).save(any(AreaMaritima.class));
        assertThat(resultado.getNome()).isEqualTo("Baia de Guanabara");
        assertThat(resultado.getNivelPoluicao()).isEqualTo("ALTO");
        assertThat(resultado.getDataCadastro()).isEqualTo(LocalDate.now());
    }

    @Test
    void listarDeveConverterTodasAsAreas() {
        AreaMaritima a1 = new AreaMaritima();
        a1.setNome("Area 1");
        AreaMaritima a2 = new AreaMaritima();
        a2.setNome("Area 2");
        when(repository.findAll()).thenReturn(List.of(a1, a2));

        List<AreaMaritimaExibicaoDto> resultado = service.listar();

        assertThat(resultado).extracting(AreaMaritimaExibicaoDto::getNome)
                .containsExactly("Area 1", "Area 2");
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletarDeveImpedirExclusaoComOperacoesVinculadas() {
        when(repository.existsById(1L)).thenReturn(true);
        when(operacaoRepository.findByAreaId(1L)).thenReturn(List.of(new Operacao()));

        assertThatThrownBy(() -> service.deletar(1L))
                .isInstanceOf(IllegalStateException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deletarDeveExcluirAreaSemOperacoes() {
        when(repository.existsById(1L)).thenReturn(true);
        when(operacaoRepository.findByAreaId(1L)).thenReturn(List.of());

        service.deletar(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deletarDeveLancarExcecaoQuandoAreaNaoExiste() {
        when(repository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> service.deletar(9L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(repository, never()).deleteById(any());
    }
}
