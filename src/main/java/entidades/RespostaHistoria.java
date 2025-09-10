package entidades;

import entidades.enums.TemaHistorico;
import jakarta.persistence.*;

@Entity
@Table(name = "respostas_historia",
        indexes = { @Index(name="idx_hist_nome", columnList="nome"),
                @Index(name="idx_hist_sec_pais", columnList="seculo, pais_regiao") })
public class RespostaHistoria extends Resposta {

    @Column(name="periodo_epoca", length = 80)
    private String periodoEpoca;

    private Integer seculo;

    @Column(name="pais_regiao", length = 80)
    private String paisRegiao;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TemaHistorico tema;

    @Column(length = 120)
    private String protagonista;

    protected RespostaHistoria() {}

    public RespostaHistoria(Long id, String nome, String periodoEpoca, Integer seculo,
                            String paisRegiao, TemaHistorico tema, String protagonista) {
        super(id, nome);
        this.periodoEpoca = periodoEpoca;
        this.seculo = seculo;
        this.paisRegiao = paisRegiao;
        this.tema = tema;
        this.protagonista = protagonista;
    }
    public String getPeriodoEpoca() {
        return periodoEpoca;
    }
    public void setPeriodoEpoca(String periodoEpoca) {
        this.periodoEpoca = periodoEpoca;
    }
    public Integer getSeculo() {
        return seculo;
    }
    public void setSeculo(Integer seculo) {
        this.seculo = seculo;
    }
    public String getPaisRegiao() {
        return paisRegiao;
    }
    public void setPaisRegiao(String paisRegiao) {
        this.paisRegiao = paisRegiao;
    }
    public TemaHistorico getTema() {
        return tema;
    }
    public void setTema(TemaHistorico tema) {
        this.tema = tema;
    }
    public String getProtagonista() {
        return protagonista;
    }
    public void setProtagonista(String protagonista) {
        this.protagonista = protagonista;
    }
}
