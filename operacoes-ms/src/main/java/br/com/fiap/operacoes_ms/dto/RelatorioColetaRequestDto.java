package br.com.fiap.operacoes_ms.dto;

import lombok.Data;

@Data
public class RelatorioColetaRequestDto {
    private Long idOperacao;
    private Long idVoluntario;
    private Integer quantidadeResiduos;
    private String tipoResiduo;
    private String observacoes;
}
