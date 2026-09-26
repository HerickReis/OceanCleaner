package br.com.fiap.operacoes_ms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OperacaoDto {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "Data da operação é obrigatória")
    private LocalDate dataOperacao;

    @NotBlank(message = "Status é obrigatório")
    private String status;

    @NotNull(message = "ID da área é obrigatório")
    private Long idArea;

    private Long idVoluntario;
    private Integer quantidadeResiduos;
    private String tipoResiduo;
    private String observacoes;
}