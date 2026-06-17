package model;

public abstract class Pessoa {

    private String codigo;
    private String nome;

    public Pessoa(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public abstract TipoPessoa getTipo();

    @Override
    public String toString() {
        return String.format("[%s] %s - Tipo: %s", codigo, nome, getTipo().getDescricao());
    }

    public String serializar() {
        return codigo + "|" + nome + "|" + getTipo().name();
    }
}
