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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperacaoServiceTest {

    @Mock
    private OperacaoRepository operacaoRepository;

    @Mock
    private AreaMaritimaRepository areaMaritimaRepository;

    @Mock
    private VoluntarioClient voluntarioClient;

    @InjectMocks
    private OperacaoService service;

    private AreaMaritima area;

    @BeforeEach
    void setUp() {
        area = new AreaMaritima();
        area.setId(1L);
        area.setNome("Baia de Guanabara");
    }

    private OperacaoDto novaOperacao(String status) {
        OperacaoDto dto = new OperacaoDto();
        dto.setTitulo("Limpeza da praia");
        dto.setDataOperacao(LocalDate.of(2026, 10, 1));
        dto.setStatus(status);
        dto.setIdArea(1L);
        return dto;
    }

    @Test
    void cadastrarDeveVincularAreaESalvar() {
        when(areaMaritimaRepository.findById(1L)).thenReturn(Optional.of(area));

        OperacaoExibicaoDto resultado = service.cadastrar(novaOperacao("PLANEJADA"));

        verify(operacaoRepository).save(any(Operacao.class));
        assertThat(resultado.getIdArea()).isEqualTo(1L);
        assertThat(resultado.getNomeArea()).isEqualTo("Baia de Guanabara");
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoAreaNaoExiste() {
        when(areaMaritimaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(novaOperacao("PLANEJADA")))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(operacaoRepository, never()).save(any());
    }

    @Test
    void atualizarParaConcluidaDeveRegistrarRelatorioNoVoluntariosMs() {
        when(operacaoRepository.findById(5L)).thenReturn(Optional.of(new Operacao()));
        when(areaMaritimaRepository.findById(1L)).thenReturn(Optional.of(area));
        OperacaoDto dto = novaOperacao("CONCLUIDA");
        dto.setIdVoluntario(2L);
        dto.setQuantidadeResiduos(50);
        dto.setTipoResiduo("Plastico");

        service.atualizar(5L, dto);

        ArgumentCaptor<RelatorioColetaRequestDto> captor = ArgumentCaptor.forClass(RelatorioColetaRequestDto.class);
        verify(voluntarioClient).registrarRelatorio(captor.capture());
        assertThat(captor.getValue().getIdOperacao()).isEqualTo(5L);
        assertThat(captor.getValue().getIdVoluntario()).isEqualTo(2L);
        assertThat(captor.getValue().getQuantidadeResiduos()).isEqualTo(50);
    }

    @Test
    void atualizarSemConcluirNaoDeveChamarVoluntariosMs() {
        when(operacaoRepository.findById(5L)).thenReturn(Optional.of(new Operacao()));
        when(areaMaritimaRepository.findById(1L)).thenReturn(Optional.of(area));

        service.atualizar(5L, novaOperacao("EM_ANDAMENTO"));

        verify(voluntarioClient, never()).registrarRelatorio(any());
    }

    @Test
    void deletarDeveLancarExcecaoQuandoOperacaoNaoExiste() {
        when(operacaoRepository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> service.deletar(9L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(operacaoRepository, never()).deleteById(any());
    }
}
