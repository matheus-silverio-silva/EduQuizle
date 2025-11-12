package br.com.eduquizle.api.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ranking_usuario")
public class RankingUsuario {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long pontuacaoTotal = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long pontuacaoGeografia = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long pontuacaoHistoria = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long pontuacaoBiologia = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long pontuacaoQuimica = 0L;

    public RankingUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
