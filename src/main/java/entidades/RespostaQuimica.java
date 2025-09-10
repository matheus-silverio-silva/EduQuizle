package entidades;

import entidades.enums.EstadoFisico;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "respostas_quimica",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_quimica_simbolo", columnNames = "simbolo"),
                @UniqueConstraint(name="uk_quimica_z", columnNames = "numero_atomico")
        },
        indexes = {
                @Index(name="idx_quimica_grupo_periodo", columnList="grupo, periodo")
        })
public class RespostaQuimica extends Resposta {

    @Column(length = 3, nullable = false)
    private String simbolo;

    @Column(name="numero_atomico", nullable = false)
    private Integer numeroAtomico;

    @Column(nullable = false)
    private Integer grupo;   // 1..18

    @Column(nullable = false)
    private Integer periodo; // 1..7

    @Column(length = 40)
    private String familia;  // ex.: "Halogênios", "Gases Nobres"

    @Enumerated(EnumType.STRING)
    @Column(name="estado_25c", nullable = false, length = 10)
    private EstadoFisico estadoFisico25C;

    @Column(name="massa_atomica", precision = 8, scale = 4)
    private BigDecimal massaAtomica;

    protected RespostaQuimica() {}

    public RespostaQuimica(Long id, String nome, String simbolo, Integer numeroAtomico,
                           Integer grupo, Integer periodo, String familia,
                           EstadoFisico estadoFisico25C, BigDecimal massaAtomica) {
        super(id, nome);
        this.simbolo = simbolo;
        this.numeroAtomico = numeroAtomico;
        this.grupo = grupo;
        this.periodo = periodo;
        this.familia = familia;
        this.estadoFisico25C = estadoFisico25C;
        this.massaAtomica = massaAtomica;
    }
    public String getSimbolo() {
        return simbolo;
    }
    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
    public Integer getNumeroAtomico() {
        return numeroAtomico;
    }
    public void setNumeroAtomico(Integer numeroAtomico) {
        this.numeroAtomico = numeroAtomico;
    }
    public Integer getGrupo() {
        return grupo;
    }
    public void setGrupo(Integer grupo) {
        this.grupo = grupo;
    }
    public Integer getPeriodo() {
        return periodo;
    }
    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }
    public String getFamilia() {
        return familia;
    }
    public void setFamilia(String familia) {
        this.familia = familia;
    }
    public EstadoFisico getEstadoFisico25C() {
        return estadoFisico25C;
    }
    public void setEstadoFisico25C(EstadoFisico estadoFisico25C) {
        this.estadoFisico25C = estadoFisico25C;
    }
    public BigDecimal getMassaAtomica() {
        return massaAtomica;
    }
    public void setMassaAtomica(BigDecimal massaAtomica) {
        this.massaAtomica = massaAtomica;
    }
}