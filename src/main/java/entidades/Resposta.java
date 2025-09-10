package entidades;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class Resposta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // cada subclasse herdará esta PK

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    protected Resposta() {}
    protected Resposta(Long id, String nome) { this.id = id; this.nome = nome; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resposta)) return false;
        Resposta that = (Resposta) o;
        return id != null && id.equals(that.id);
    }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}