package br.com.fiap.voluntarios_ms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VoluntarioExibicaoDto {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String especialidade;
    private LocalDate dataCadastro;
}