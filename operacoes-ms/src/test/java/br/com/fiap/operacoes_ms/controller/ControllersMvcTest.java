package br.com.fiap.operacoes_ms.controller;

import br.com.fiap.operacoes_ms.SecurityConfig;
import br.com.fiap.operacoes_ms.dto.AreaMaritimaDto;
import br.com.fiap.operacoes_ms.dto.AreaMaritimaExibicaoDto;
import br.com.fiap.operacoes_ms.dto.OperacaoDto;
import br.com.fiap.operacoes_ms.dto.OperacaoExibicaoDto;
import br.com.fiap.operacoes_ms.exception.RecursoNaoEncontradoException;
import br.com.fiap.operacoes_ms.service.AreaMaritimaService;
import br.com.fiap.operacoes_ms.service.OperacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({OperacoesController.class, AreaMaritmaController.class})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ControllersMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OperacaoService operacaoService;

    @MockitoBean
    private AreaMaritimaService areaMaritimaService;

    @Test
    void listarOperacoesSemAuthRetorna200() throws Exception {
        OperacaoExibicaoDto dto = new OperacaoExibicaoDto();
        dto.setTitulo("Limpeza da praia");
        when(operacaoService.listar()).thenReturn(List.of(dto));

        mvc.perform(get("/operacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Limpeza da praia"));
    }

    @Test
    void buscarOperacaoInexistenteRetorna404() throws Exception {
        when(operacaoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Operação não encontrada com id: 99"));

        mvc.perform(get("/operacoes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrarOperacaoSemAuthRetorna401() throws Exception {
        mvc.perform(post("/operacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Limpeza\",\"dataOperacao\":\"2026-10-01\",\"status\":\"PLANEJADA\",\"idArea\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastrarOperacaoComAuthRetorna201() throws Exception {
        OperacaoExibicaoDto exibicao = new OperacaoExibicaoDto();
        exibicao.setIdArea(1L);
        exibicao.setNomeArea("Baia de Guanabara");
        when(operacaoService.cadastrar(any(OperacaoDto.class))).thenReturn(exibicao);

        mvc.perform(post("/operacoes")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Limpeza\",\"dataOperacao\":\"2026-10-01\",\"status\":\"PLANEJADA\",\"idArea\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeArea").value("Baia de Guanabara"));
        verify(operacaoService).cadastrar(any(OperacaoDto.class));
    }

    @Test
    void cadastrarOperacaoInvalidaRetorna400() throws Exception {
        mvc.perform(post("/operacoes")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataOperacao\":\"2026-10-01\",\"status\":\"PLANEJADA\",\"idArea\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarAreasSemAuthRetorna200() throws Exception {
        AreaMaritimaExibicaoDto dto = new AreaMaritimaExibicaoDto();
        dto.setNome("Baia de Guanabara");
        when(areaMaritimaService.listar()).thenReturn(List.of(dto));

        mvc.perform(get("/areas-maritimas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Baia de Guanabara"));
    }

    @Test
    void cadastrarAreaComAuthRetorna201() throws Exception {
        AreaMaritimaExibicaoDto exibicao = new AreaMaritimaExibicaoDto();
        exibicao.setNome("Baia de Guanabara");
        when(areaMaritimaService.cadastrar(any(AreaMaritimaDto.class))).thenReturn(exibicao);

        mvc.perform(post("/areas-maritimas")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Baia de Guanabara\",\"localizacao\":\"RJ\",\"nivelPoluicao\":\"ALTO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Baia de Guanabara"));
    }
}
