package model;

public class Fornecedor extends Pessoa {

    public Fornecedor(String codigo, String nome) {
        super(codigo, nome);
    }

    @Override
    public TipoPessoa getTipo() {
        return TipoPessoa.FORNECEDOR;
    }
}
