package br.com.eduquizle.api.entidades;

import br.com.eduquizle.api.entidades.enums.Continente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class Geografia extends Resposta {

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
}