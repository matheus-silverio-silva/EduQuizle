package entidades;

import entidades.enums.ReinoBiologico;
import entidades.enums.Habitat;
import entidades.enums.Alimentacao;
import jakarta.persistence.*;

@Entity
@Table(name = "respostas_biologia",
        indexes = { @Index(name="idx_bio_nome", columnList="nome"),
                @Index(name="idx_bio_classe", columnList="classe") })
public class RespostaBiologia extends Resposta {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReinoBiologico reino;

    @Column(name="filo_divisao", length = 60)
    private String filoDivisao;

    @Column(length = 60)
    private String classe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Habitat habitat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Alimentacao alimentacao;

    protected RespostaBiologia() {}

    public RespostaBiologia(Long id, String nome, ReinoBiologico reino, String filoDivisao,
                            String classe, Habitat habitat, Alimentacao alimentacao) {
        super(id, nome);
        this.reino = reino;
        this.filoDivisao = filoDivisao;
        this.classe = classe;
        this.habitat = habitat;
        this.alimentacao = alimentacao;
    }
    public ReinoBiologico getReino() {
        return reino;
    }
    public void setReino(ReinoBiologico reino) {
        this.reino = reino;
    }
    public String getFiloDivisao() {
        return filoDivisao;
    }
    public void setFiloDivisao(String filoDivisao) {
        this.filoDivisao = filoDivisao;
    }
    public String getClasse() {
        return classe;
    }
    public void setClasse(String classe) {
        this.classe = classe;
    }
    public Habitat getHabitat() {
        return habitat;
    }
    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }
    public Alimentacao getAlimentacao() {
        return alimentacao;
    }
    public void setAlimentacao(Alimentacao alimentacao) {
        this.alimentacao = alimentacao;
    }
}