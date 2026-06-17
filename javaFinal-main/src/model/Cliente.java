package model;

public class Cliente extends Pessoa {

    public Cliente(String codigo, String nome) {
        super(codigo, nome);
    }

    @Override
    public TipoPessoa getTipo() {
        return TipoPessoa.CLIENTE;
    }
}
