package br.com.fiap.operacoes_ms.model;

import jakarta.persistence.Column;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;


@Data
@Entity
@Table(name = "TB_OPERACAO")
public class Operacao {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID_OPERACAO")
        private Long id;

        @Column(name = "TITULO", nullable = false, length = 100)
        private String titulo;

        @Column(name = "DESCRICAO", length = 255)
        private String descricao;

        @Column(name = "DATA_OPERACAO", nullable = false)
        private LocalDate dataOperacao;

        @Column(name = "STATUS", nullable = false, length = 50)
        private String status;

        @ManyToOne
        @JoinColumn(name = "ID_AREA", nullable = false)
        private AreaMaritima area;

}
