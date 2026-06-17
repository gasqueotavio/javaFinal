package model;

public class ItemPedido {

    private String codigoProduto;
    private int quantidade;
    private double precoUnitario;

    public ItemPedido(String codigoProduto, int quantidade, double precoUnitario) {
        this.codigoProduto = codigoProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public String getCodigoProduto() { return codigoProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }

    public double getSubtotal() {
        return quantidade * precoUnitario;
    }

    @Override
    public String toString() {
        return String.format("  Produto: %-8s | Qtd: %3d | Unit: R$ %8.2f | Subtotal: R$ %10.2f",
                codigoProduto, quantidade, precoUnitario, getSubtotal());
    }

    public String serializar() {
        return codigoProduto + "|" + quantidade + "|" + precoUnitario;
    }
}
