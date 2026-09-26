package br.com.fiap.operacoes_ms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "TB_AREA_MARITIMA")
public class AreaMaritima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AREA")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @Column(name = "LOCALIZACAO", nullable = false, length = 255)
    private String localizacao;

    @Column(name = "NIVEL_POLUICAO", nullable = false, length = 50)
    private String nivelPoluicao;

    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

}
