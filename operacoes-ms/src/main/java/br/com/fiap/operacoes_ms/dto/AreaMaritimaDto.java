package br.com.fiap.operacoes_ms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AreaMaritimaDto {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;

    @NotBlank(message = "A localização é obrigatória")
    private String localizacao;

    @NotBlank(message = "Nível de poluição é obrigatório")
    private String nivelPoluicao;
}