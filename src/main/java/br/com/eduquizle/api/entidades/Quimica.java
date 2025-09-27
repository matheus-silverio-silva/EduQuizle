package br.com.eduquizle.api.entidades;

import br.com.eduquizle.api.entidades.enums.EstadoFisico;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class Quimica extends Resposta {

    @Column(length = 3, nullable = false)
    private String simbolo;

    @Column(name="numero_atomico", nullable = false)
    private Integer numeroAtomico;

    @Column(nullable = false)
    private Integer grupo;

    @Column(nullable = false)
    private Integer periodo;

    @Column(length = 40)
    private String familia;

    @Enumerated(EnumType.STRING)
    @Column(name="estado_25c", nullable = false, length = 10)
    private EstadoFisico estadoFisico25C;

    @Column(name="massa_atomica", precision = 8, scale = 4)
    private BigDecimal massaAtomica;

}