package model;

public class Produto {

    private String codigo;
    private String descricao;
    private double custo;
    private double precoVenda;
    private String codigoFornecedor;

    public Produto(String codigo, String descricao, double custo,
                   double precoVenda, String codigoFornecedor) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.custo = custo;
        this.precoVenda = precoVenda;
        this.codigoFornecedor = codigoFornecedor;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getCusto() { return custo; }
    public void setCusto(double custo) { this.custo = custo; }

    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public String getCodigoFornecedor() { return codigoFornecedor; }
    public void setCodigoFornecedor(String codigoFornecedor) { this.codigoFornecedor = codigoFornecedor; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Custo: R$ %.2f | Venda: R$ %.2f | Fornecedor: %s",
                codigo, descricao, custo, precoVenda, codigoFornecedor);
    }

    public String serializar() {
        return codigo + "|" + descricao + "|" + custo + "|" + precoVenda + "|" + codigoFornecedor;
    }
}
