package model;

public class ClienteFornecedor extends Pessoa {

    public ClienteFornecedor(String codigo, String nome) {
        super(codigo, nome);
    }

    @Override
    public TipoPessoa getTipo() {
        return TipoPessoa.AMBOS;
    }
}
