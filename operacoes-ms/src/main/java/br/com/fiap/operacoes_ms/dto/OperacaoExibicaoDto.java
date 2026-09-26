package br.com.fiap.operacoes_ms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OperacaoExibicaoDto {
    private Long id;
    private String titulo;
    private String descricao;
    private LocalDate dataOperacao;
    private String status;
    private Long idArea;
    private String nomeArea;
}
