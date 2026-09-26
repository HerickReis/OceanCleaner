package br.com.fiap.voluntarios_ms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "TB_RELATORIO_COLETA")
public class RelatorioColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RELATORIO")
    private Long id;

    @Column(name = "ID_OPERACAO", nullable = false)
    private Long idOperacao;

    @ManyToOne
    @JoinColumn(name = "ID_VOLUNTARIO", nullable = false)
    private Voluntario voluntario;

    @Column(name = "QUANTIDADE_RESIDUOS", nullable = false)
    private Integer quantidadeResiduos;

    @Column(name = "TIPO_RESIDUO", nullable = false, length = 100)
    private String tipoResiduo;

    @Column(name = "OBSERVACOES", length = 255)
    private String observacoes;

    @Column(name = "DATA_RELATORIO")
    private LocalDate dataRelatorio;
}