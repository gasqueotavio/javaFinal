package model;

public enum TipoPessoa {
    CLIENTE("Cliente"),
    FORNECEDOR("Fornecedor"),
    AMBOS("Cliente e Fornecedor");

    private final String descricao;

    TipoPessoa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
