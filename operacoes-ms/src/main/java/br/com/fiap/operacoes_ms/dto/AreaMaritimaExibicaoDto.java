package br.com.fiap.operacoes_ms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AreaMaritimaExibicaoDto {
    private Long id;
    private String nome;
    private String descricao;
    private String localizacao;
    private String nivelPoluicao;
    private LocalDate dataCadastro;
}