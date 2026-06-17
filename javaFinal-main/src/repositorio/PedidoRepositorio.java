package repositorio;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class PedidoRepositorio extends Repositorio<PedidoVenda> {

    private String caminhoItens;

    public PedidoRepositorio(String caminhoPedidos, String caminhoItens) {
        super(caminhoPedidos);
        this.caminhoItens = caminhoItens;
        garantirArquivoItens();
        carregar();
    }

    private void garantirArquivoItens() {
        File arquivo = new File(caminhoItens);
        arquivo.getParentFile().mkdirs();
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Nao foi possivel criar: " + caminhoItens);
            }
        }
    }

    @Override
    public void carregar() {
        dados.clear();
        for (String linha : lerLinhas()) {
            PedidoVenda pv = desserializar(linha);
            if (pv != null) dados.add(pv);
        }
        if (caminhoItens != null) carregarItens();
    }

    private void carregarItens() {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoItens))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String l = linha.trim();
                if (!l.isEmpty()) linhas.add(l);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler itens: " + caminhoItens);
        }

        for (String linha : linhas) {
            String[] p = linha.split("\\|", -1);
            if (p.length < 4) continue;
            try {
                int numeroPedido = Integer.parseInt(p[0]);
                String codProduto = p[1];
                int qtd = Integer.parseInt(p[2]);
                double preco = Double.parseDouble(p[3]);

                PedidoVenda pedido = buscarPorNumero(numeroPedido);
                if (pedido != null) {
                    pedido.adicionarItem(new ItemPedido(codProduto, qtd, preco));
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected PedidoVenda desserializar(String linha) {
        String[] p = linha.split("\\|", -1);
        if (p.length < 3) return null;
        try {
            int numero = Integer.parseInt(p[0]);
            int idEndereco = Integer.parseInt(p[2]);
            return new PedidoVenda(numero, p[1], idEndereco);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected String serializar(PedidoVenda pv) {
        return pv.serializar();
    }

    @Override
    public void salvar() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (PedidoVenda pv : dados) {
                bw.write(pv.serializar());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar pedidos.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoItens))) {
            for (PedidoVenda pv : dados) {
                for (ItemPedido item : pv.getItens()) {
                    bw.write(pv.getNumero() + "|" + item.serializar());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar itens do pedido.");
        }
    }

    public PedidoVenda buscarPorNumero(int numero) {
        for (PedidoVenda pv : dados) {
            if (pv.getNumero() == numero) return pv;
        }
        return null;
    }

    public int proximoNumero() {
        int max = 0;
        for (PedidoVenda pv : dados) {
            if (pv.getNumero() > max) max = pv.getNumero();
        }
        return max + 1;
    }

    public boolean incluir(PedidoVenda pv) {
        if (buscarPorNumero(pv.getNumero()) != null) return false;
        dados.add(pv);
        salvar();
        return true;
    }

    public boolean alterar(PedidoVenda atualizado) {
        for (int i = 0; i < dados.size(); i++) {
            if (dados.get(i).getNumero() == atualizado.getNumero()) {
                dados.set(i, atualizado);
                salvar();
                return true;
            }
        }
        return false;
    }

    public boolean excluir(int numero) {
        for (int i = 0; i < dados.size(); i++) {
            if (dados.get(i).getNumero() == numero) {
                dados.remove(i);
                salvar();
                return true;
            }
        }
        return false;
    }

    public List<PedidoVenda> filtrarPorCliente(String codigoCliente) {
        List<PedidoVenda> resultado = new ArrayList<>();
        for (PedidoVenda pv : dados) {
            if (pv.getCodigoCliente().equalsIgnoreCase(codigoCliente)) {
                resultado.add(pv);
            }
        }
        return resultado;
    }
}
