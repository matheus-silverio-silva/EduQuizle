package entidades;

import entidades.enums.Continente;
import jakarta.persistence.*;

@Entity
@Table(name = "respostas_geografia",
        indexes = {
                @Index(name="idx_geo_cont_sub", columnList="continente, sub_regiao"),
                @Index(name="idx_geo_nome", columnList="nome")
        })
public class RespostaGeografia extends Resposta {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Continente continente;

    @Column(name="sub_regiao", length = 60)
    private String subRegiao;

    @Column(length = 80)
    private String capital;

    @Column(name="idioma_principal", length = 60)
    private String idiomaPrincipal;

    @Column(length = 60)
    private String moeda;

    @Column(name="populacao_aprox")
    private Long populacaoAprox;

    protected RespostaGeografia() {}

    public RespostaGeografia(Long id, String nome, Continente continente, String subRegiao,
                             String capital, String idiomaPrincipal, String moeda, Long populacaoAprox) {
        super(id, nome);
        this.continente = continente;
        this.subRegiao = subRegiao;
        this.capital = capital;
        this.idiomaPrincipal = idiomaPrincipal;
        this.moeda = moeda;
        this.populacaoAprox = populacaoAprox;
    }
    public Continente getContinente() {
        return continente;
    }
    public void setContinente(Continente continente) {
        this.continente = continente;
    }
    public String getSubRegiao() {
        return subRegiao;
    }
    public void setSubRegiao(String subRegiao) {
        this.subRegiao = subRegiao;
    }
    public String getCapital() {
        return capital;
    }
    public void setCapital(String capital) {
        this.capital = capital;
    }
    public String getIdiomaPrincipal() {
        return idiomaPrincipal;
    }
    public void setIdiomaPrincipal(String idiomaPrincipal) {
        this.idiomaPrincipal = idiomaPrincipal;
    }
    public String getMoeda() {
        return moeda;
    }
    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }
}