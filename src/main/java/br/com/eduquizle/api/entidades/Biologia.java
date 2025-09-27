package br.com.eduquizle.api.entidades;

import br.com.eduquizle.api.entidades.enums.ReinoBiologico;
import br.com.eduquizle.api.entidades.enums.Habitat;
import br.com.eduquizle.api.entidades.enums.Alimentacao;
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
public class Biologia extends Resposta {

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
}