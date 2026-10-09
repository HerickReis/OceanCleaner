package br.com.fiap.voluntarios_ms.controller;

import br.com.fiap.voluntarios_ms.SecurityConfig;
import br.com.fiap.voluntarios_ms.dto.RelatorioColetaDto;
import br.com.fiap.voluntarios_ms.dto.RelatorioColetaExibicaoDto;
import br.com.fiap.voluntarios_ms.dto.VoluntarioDto;
import br.com.fiap.voluntarios_ms.dto.VoluntarioExibicaoDto;
import br.com.fiap.voluntarios_ms.exception.RecursoNaoEncontradoException;
import br.com.fiap.voluntarios_ms.service.RelatorioColetaService;
import br.com.fiap.voluntarios_ms.service.VoluntarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

@WebMvcTest({VoluntarioController.class, RelatorioColetaController.class})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ControllersMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private VoluntarioService voluntarioService;

    @MockitoBean
    private RelatorioColetaService relatorioService;

    @Test
    void listarVoluntariosSemAuthRetorna200() throws Exception {
        VoluntarioExibicaoDto dto = new VoluntarioExibicaoDto();
        dto.setNome("Ana");
        when(voluntarioService.listar()).thenReturn(List.of(dto));

        mvc.perform(get("/voluntarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"));
    }

    @Test
    void buscarVoluntarioInexistenteRetorna404() throws Exception {
        when(voluntarioService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Voluntário não encontrado com id: 99"));

        mvc.perform(get("/voluntarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrarVoluntarioSemAuthRetorna401() throws Exception {
        mvc.perform(post("/voluntarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"ana@teste.com\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastrarVoluntarioComAuthRetorna201() throws Exception {
        VoluntarioExibicaoDto exibicao = new VoluntarioExibicaoDto();
        exibicao.setNome("Ana");
        exibicao.setEmail("ana@teste.com");
        when(voluntarioService.cadastrar(any(VoluntarioDto.class))).thenReturn(exibicao);

        mvc.perform(post("/voluntarios")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"ana@teste.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@teste.com"));
        verify(voluntarioService).cadastrar(any(VoluntarioDto.class));
    }

    @Test
    void cadastrarVoluntarioInvalidoRetorna400() throws Exception {
        mvc.perform(post("/voluntarios")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"invalido\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarRelatoriosSemAuthRetorna200() throws Exception {
        RelatorioColetaExibicaoDto dto = new RelatorioColetaExibicaoDto();
        dto.setTipoResiduo("Plastico");
        when(relatorioService.listar()).thenReturn(List.of(dto));

        mvc.perform(get("/relatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoResiduo").value("Plastico"));
    }

    @Test
    void cadastrarRelatorioComAuthRetorna201() throws Exception {
        RelatorioColetaExibicaoDto exibicao = new RelatorioColetaExibicaoDto();
        exibicao.setTipoResiduo("Plastico");
        when(relatorioService.cadastrar(any(RelatorioColetaDto.class))).thenReturn(exibicao);

        mvc.perform(post("/relatorios")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idOperacao\":5,\"idVoluntario\":2,\"quantidadeResiduos\":50,\"tipoResiduo\":\"Plastico\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoResiduo").value("Plastico"));
    }
}
