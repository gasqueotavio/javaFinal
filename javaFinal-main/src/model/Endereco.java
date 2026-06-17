package model;

public class Endereco {

    private int id;
    private String codigoPessoa;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private TipoEndereco tipo;

    public Endereco(int id, String codigoPessoa, String cep, String logradouro,
                    String numero, String complemento, TipoEndereco tipo) {
        this.id = id;
        this.codigoPessoa = codigoPessoa;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoPessoa() { return codigoPessoa; }
    public void setCodigoPessoa(String codigoPessoa) { this.codigoPessoa = codigoPessoa; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public TipoEndereco getTipo() { return tipo; }
    public void setTipo(TipoEndereco tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        String comp = complemento.isEmpty() ? "" : " (" + complemento + ")";
        return String.format("  [ID:%d] %s, N %s%s - CEP: %s | %s",
                id, logradouro, numero, comp, cep, tipo.getDescricao());
    }

    public String serializar() {
        return id + "|" + codigoPessoa + "|" + cep + "|"
                + logradouro + "|" + numero + "|" + complemento + "|" + tipo.name();
    }
}
