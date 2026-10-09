package br.com.fiap.operacoes_ms;

import br.com.fiap.operacoes_ms.dto.RelatorioColetaRequestDto;
import br.com.fiap.operacoes_ms.http.VoluntarioClient;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OperacaoFluxoTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private VoluntarioClient voluntarioClient;

    @Test
    void fluxoAreaOperacaoConclusaoRelatorioExclusao() throws Exception {
        MvcResult areaResult = mvc.perform(post("/areas-maritimas")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Baia de Guanabara\",\"localizacao\":\"RJ\",\"nivelPoluicao\":\"ALTO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Baia de Guanabara"))
                .andReturn();
        long idArea = ((Number) JsonPath.read(areaResult.getResponse().getContentAsString(), "$.id")).longValue();

        MvcResult operacaoResult = mvc.perform(post("/operacoes")
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Limpeza da praia\",\"dataOperacao\":\"2026-10-01\",\"status\":\"PLANEJADA\",\"idArea\":" + idArea + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeArea").value("Baia de Guanabara"))
                .andReturn();
        long idOperacao = ((Number) JsonPath.read(operacaoResult.getResponse().getContentAsString(), "$.id")).longValue();

        mvc.perform(put("/operacoes/" + idOperacao)
                        .with(httpBasic("test", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Limpeza da praia\",\"dataOperacao\":\"2026-10-01\",\"status\":\"CONCLUIDA\",\"idArea\":" + idArea + ",\"idVoluntario\":2,\"quantidadeResiduos\":50,\"tipoResiduo\":\"Plastico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        ArgumentCaptor<RelatorioColetaRequestDto> captor = ArgumentCaptor.forClass(RelatorioColetaRequestDto.class);
        verify(voluntarioClient).registrarRelatorio(captor.capture());
        assertThat(captor.getValue().getIdOperacao()).isEqualTo(idOperacao);
        assertThat(captor.getValue().getIdVoluntario()).isEqualTo(2L);

        mvc.perform(get("/operacoes/status/CONCLUIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CONCLUIDA"));

        when(voluntarioClient.listarPorOperacao(idOperacao)).thenReturn(List.of(Map.of("id", 7)));
        mvc.perform(delete("/operacoes/" + idOperacao)
                        .with(httpBasic("test", "test")))
                .andExpect(status().isConflict());

        when(voluntarioClient.listarPorOperacao(idOperacao)).thenReturn(List.of());
        mvc.perform(delete("/operacoes/" + idOperacao)
                        .with(httpBasic("test", "test")))
                .andExpect(status().isNoContent());
    }
}
