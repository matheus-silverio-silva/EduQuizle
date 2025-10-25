package br.com.eduquizle.api.entidades;

import br.com.eduquizle.api.entidades.enums.TemaHistorico;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "historia")
public class Historia extends Resposta {

    @NotNull
    @Column(name = "ano_acontecimento")
    private Integer anoAcontecimento;

    @NotBlank
    @Size(max = 100)
    @Column(name = "pais_regiao", length = 100)
    private String paisRegiao;

    @NotBlank
    @Size(max = 100)
    @Column(name = "figura_chave", length = 100)
    private String figuraChave;

    @NotBlank
    @Size(max = 100)
    @Column(name = "periodo_epoca", length = 100)
    private String periodoEpoca;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private TemaHistorico tema;
}