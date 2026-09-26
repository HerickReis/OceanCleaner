package br.com.fiap.voluntarios_ms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RelatorioColetaDto {

    @NotNull(message = "ID da operação é obrigatório")
    private Long idOperacao;

    @NotNull(message = "ID do voluntário é obrigatório")
    private Long idVoluntario;

    @NotNull(message = "Quantidade de resíduos é obrigatória")
    private Integer quantidadeResiduos;

    @NotBlank(message = "Tipo de resíduo é obrigatório")
    private String tipoResiduo;

    private String observacoes;
}