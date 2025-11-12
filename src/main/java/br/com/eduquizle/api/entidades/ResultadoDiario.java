package br.com.eduquizle.api.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resultado_diario", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "desafio_diario_id"}))
public class ResultadoDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desafio_diario_id", nullable = false)
    private DesafioDiario desafio;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer pontuacao;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer tentativas;

    @NotNull
    @Column(nullable = false)
    private Long tempoGastoSegundos;

    @NotNull
    @Column(name = "data_conclusao", nullable = false)
    private LocalDateTime dataConclusao = LocalDateTime.now();
}
