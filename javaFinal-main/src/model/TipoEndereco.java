package model;

public enum TipoEndereco {
    COMERCIAL("Comercial"),
    RESIDENCIAL("Residencial"),
    ENTREGA("Entrega"),
    CORRESPONDENCIA("Correspondencia");

    private final String descricao;

    TipoEndereco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
