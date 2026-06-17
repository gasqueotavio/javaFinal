package model;

import java.util.ArrayList;
import java.util.List;

public class PedidoVenda {

    private int numero;
    private String codigoCliente;
    private int idEnderecoEntrega;
    private List<ItemPedido> itens;

    public PedidoVenda(int numero, String codigoCliente, int idEnderecoEntrega) {
        this.numero = numero;
        this.codigoCliente = codigoCliente;
        this.idEnderecoEntrega = idEnderecoEntrega;
        this.itens = new ArrayList<>();
    }

    public int getNumero() { return numero; }

    public String getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(String codigoCliente) { this.codigoCliente = codigoCliente; }

    public int getIdEnderecoEntrega() { return idEnderecoEntrega; }
    public void setIdEnderecoEntrega(int idEnderecoEntrega) { this.idEnderecoEntrega = idEnderecoEntrega; }

    public List<ItemPedido> getItens() { return itens; }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public double getMontante() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pedido #%d | Cliente: %s | Endereco ID: %d | Total: R$ %.2f",
                numero, codigoCliente, idEnderecoEntrega, getMontante()));
        for (ItemPedido item : itens) {
            sb.append("\n").append(item.toString());
        }
        return sb.toString();
    }

    public String serializar() {
        return numero + "|" + codigoCliente + "|" + idEnderecoEntrega;
    }
}
