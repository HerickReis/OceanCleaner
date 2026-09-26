package br.com.fiap.voluntarios_ms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RelatorioColetaExibicaoDto {
    private Long id;
    private Long idOperacao;
    private Long idVoluntario;
    private String nomeVoluntario;
    private Integer quantidadeResiduos;
    private String tipoResiduo;
    private String observacoes;
    private LocalDate dataRelatorio;
}
