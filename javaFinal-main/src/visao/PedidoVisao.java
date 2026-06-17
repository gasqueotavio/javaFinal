package visao;

import model.*;
import repositorio.*;
import servico.LogServico;
import servico.MenuServico;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PedidoVisao {

    private final PedidoRepositorio repositorio;
    private final PessoaRepositorio pessoaRepositorio;
    private final EnderecoRepositorio enderecoRepositorio;
    private final ProdutoRepositorio produtoRepositorio;
    private final LogServico log;
    private final Scanner scanner;
    private final String caminhoMenu;

    public PedidoVisao(PedidoRepositorio repositorio,
                       PessoaRepositorio pessoaRepositorio,
                       EnderecoRepositorio enderecoRepositorio,
                       ProdutoRepositorio produtoRepositorio,
                       LogServico log, Scanner scanner, String caminhoMenu) {
        this.repositorio = repositorio;
        this.pessoaRepositorio = pessoaRepositorio;
        this.enderecoRepositorio = enderecoRepositorio;
        this.produtoRepositorio = produtoRepositorio;
        this.log = log;
        this.scanner = scanner;
        this.caminhoMenu = caminhoMenu;
    }

    public void exibirMenu() {
        int opcao;
        do {
            MenuServico.exibirMenu(caminhoMenu);
            opcao = lerInteiro("Opcao: ");
            switch (opcao) {
                case 1: incluir();   break;
                case 2: consultar(); break;
                case 3: alterar();   break;
                case 4: excluir();   break;
                case 0: break;
                default: System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    private void incluir() {
        System.out.println("\n--- NOVO PEDIDO DE VENDA ---");

        List<Pessoa> clientes = pessoaRepositorio.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println("ERRO: Nenhum cliente cadastrado. Cadastre um cliente primeiro.");
            return;
        }
        System.out.println("Clientes disponiveis:");
        for (Pessoa c : clientes) System.out.println("  " + c);

        String codigoCliente = lerObrigatorio("Codigo do cliente: ");
        Pessoa cliente = pessoaRepositorio.buscarPorCodigo(codigoCliente);
        if (cliente == null) {
            System.out.println("ERRO: Cliente nao encontrado.");
            return;
        }
        if (cliente.getTipo() == TipoPessoa.FORNECEDOR) {
            System.out.println("ERRO: Esta pessoa e apenas Fornecedor, nao pode ser cliente de pedido.");
            return;
        }

        List<Endereco> enderecos = enderecoRepositorio.listarPorPessoa(codigoCliente);
        if (enderecos.isEmpty()) {
            System.out.println("ERRO: O cliente nao possui enderecos. Cadastre um endereco primeiro.");
            return;
        }
        System.out.println("Enderecos de " + cliente.getNome() + ":");
        for (Endereco e : enderecos) System.out.println(e);

        int idEndereco = lerInteiro("ID do endereco de entrega: ");
        Endereco endEntrega = enderecoRepositorio.buscarPorId(idEndereco);
        if (endEntrega == null || !endEntrega.getCodigoPessoa().equalsIgnoreCase(codigoCliente)) {
            System.out.println("ERRO: Endereco nao pertence a este cliente.");
            return;
        }

        if (produtoRepositorio.listarTodos().isEmpty()) {
            System.out.println("ERRO: Nenhum produto cadastrado. Cadastre produtos primeiro.");
            return;
        }

        int numeroPedido = repositorio.proximoNumero();
        PedidoVenda pedido = new PedidoVenda(numeroPedido, codigoCliente, idEndereco);

        System.out.println("\nAdicione os produtos ao pedido (digite 0 para finalizar):");
        while (true) {
            System.out.println("\nProdutos disponiveis:");
            for (Produto p : produtoRepositorio.listarTodos()) System.out.println("  " + p);

            String codProduto = lerObrigatorio("Codigo do produto (0=finalizar): ");
            if (codProduto.equals("0")) break;

            Produto produto = produtoRepositorio.buscarPorCodigo(codProduto);
            if (produto == null) {
                System.out.println("  Produto nao encontrado. Tente novamente.");
                continue;
            }

            int qtd = lerInteiroPositivo("Quantidade: ");
            ItemPedido item = new ItemPedido(codProduto, qtd, produto.getPrecoVenda());
            pedido.adicionarItem(item);
            System.out.printf("  Adicionado: %s x%d = R$ %.2f%n",
                    produto.getDescricao(), qtd, item.getSubtotal());
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("Pedido cancelado: nenhum item adicionado.");
            return;
        }

        System.out.printf("%nResumo do pedido:%n%s%n", pedido);
        System.out.print("Confirma inclusao do pedido? (S/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Pedido cancelado.");
            return;
        }

        repositorio.incluir(pedido);
        log.registrar("INCLUSAO", "PedidoVenda",
                "Numero=" + numeroPedido + " | Cliente=" + codigoCliente
                        + " | Itens=" + pedido.getItens().size()
                        + " | Total=R$" + String.format("%.2f", pedido.getMontante()));
        System.out.println("Pedido #" + numeroPedido + " incluido com sucesso!");
    }

    private void consultar() {
        System.out.println("\n--- CONSULTAR PEDIDOS ---");
        System.out.println("1. Listar todos");
        System.out.println("2. Filtrar por cliente");
        System.out.println("3. Buscar por numero");
        int opcao = lerInteiro("Opcao: ");

        List<PedidoVenda> resultado = new ArrayList<>();
        switch (opcao) {
            case 1:
                resultado = repositorio.listarTodos();
                break;
            case 2:
                resultado = repositorio.filtrarPorCliente(lerObrigatorio("Codigo do cliente: "));
                break;
            case 3:
                PedidoVenda pv = repositorio.buscarPorNumero(lerInteiro("Numero do pedido: "));
                if (pv != null) resultado.add(pv);
                break;
            default:
                System.out.println("Opcao invalida.");
                return;
        }

        if (resultado.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
        } else {
            System.out.println("\n--- RESULTADO (" + resultado.size() + " pedido(s)) ---");
            for (PedidoVenda pv : resultado) {
                System.out.println(pv);
                System.out.println("-------------------------------");
            }
        }
    }

    private void alterar() {
        System.out.println("\n--- ALTERAR PEDIDO ---");
        int numero = lerInteiro("Numero do pedido: ");
        PedidoVenda existente = repositorio.buscarPorNumero(numero);

        if (existente == null) {
            System.out.println("ERRO: Pedido #" + numero + " nao encontrado.");
            return;
        }

        System.out.println("Pedido atual:\n" + existente);
        System.out.println("\nO que deseja alterar?");
        System.out.println("1. Endereco de entrega");
        System.out.println("2. Itens do pedido (substituir todos)");
        System.out.println("0. Cancelar");
        int opcao = lerInteiro("Opcao: ");

        if (opcao == 1) {
            alterarEndereco(existente);
        } else if (opcao == 2) {
            alterarItens(existente);
        }
    }

    private void alterarEndereco(PedidoVenda pedido) {
        List<Endereco> enderecos = enderecoRepositorio.listarPorPessoa(pedido.getCodigoCliente());
        if (enderecos.isEmpty()) {
            System.out.println("Cliente nao possui enderecos.");
            return;
        }
        for (Endereco e : enderecos) System.out.println(e);
        int novoId = lerInteiro("Novo ID de endereco: ");
        Endereco novoEnd = enderecoRepositorio.buscarPorId(novoId);
        if (novoEnd == null || !novoEnd.getCodigoPessoa().equalsIgnoreCase(pedido.getCodigoCliente())) {
            System.out.println("ERRO: Endereco nao encontrado para este cliente.");
            return;
        }
        pedido.setIdEnderecoEntrega(novoId);
        repositorio.alterar(pedido);
        log.registrar("ALTERACAO", "PedidoVenda",
                "Numero=" + pedido.getNumero() + " | NovoEnderecoID=" + novoId);
        System.out.println("Endereco de entrega atualizado!");
    }

    private void alterarItens(PedidoVenda pedidoAntigo) {
        PedidoVenda pedidoNovo = new PedidoVenda(
                pedidoAntigo.getNumero(),
                pedidoAntigo.getCodigoCliente(),
                pedidoAntigo.getIdEnderecoEntrega());

        System.out.println("Adicione os novos itens (0=finalizar):");
        while (true) {
            String cod = lerObrigatorio("Codigo do produto (0=finalizar): ");
            if (cod.equals("0")) break;
            Produto p = produtoRepositorio.buscarPorCodigo(cod);
            if (p == null) { System.out.println("  Produto nao encontrado."); continue; }
            int qtd = lerInteiroPositivo("Quantidade: ");
            pedidoNovo.adicionarItem(new ItemPedido(cod, qtd, p.getPrecoVenda()));
        }

        if (pedidoNovo.getItens().isEmpty()) {
            System.out.println("Alteracao cancelada: nenhum item.");
            return;
        }

        repositorio.alterar(pedidoNovo);
        log.registrar("ALTERACAO", "PedidoVenda",
                "Numero=" + pedidoAntigo.getNumero()
                        + " | ItensAtualizados | NovoTotal=R$"
                        + String.format("%.2f", pedidoNovo.getMontante()));
        System.out.println("Pedido atualizado! Novo total: R$ " + String.format("%.2f", pedidoNovo.getMontante()));
    }

    private void excluir() {
        System.out.println("\n--- EXCLUIR PEDIDO ---");
        int numero = lerInteiro("Numero do pedido: ");
        PedidoVenda existente = repositorio.buscarPorNumero(numero);

        if (existente == null) {
            System.out.println("ERRO: Pedido #" + numero + " nao encontrado.");
            return;
        }

        System.out.println("Pedido a excluir:\n" + existente);
        System.out.print("Confirma exclusao? (S/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Exclusao cancelada.");
            return;
        }

        repositorio.excluir(numero);
        log.registrar("EXCLUSAO", "PedidoVenda",
                "Numero=" + numero + " | Cliente=" + existente.getCodigoCliente());
        System.out.println("Pedido #" + numero + " excluido com sucesso!");
    }

    private String lerObrigatorio(String prompt) {
        String v;
        do {
            System.out.print(prompt);
            v = scanner.nextLine().trim();
            if (v.isEmpty()) System.out.println("  Campo obrigatorio.");
        } while (v.isEmpty());
        return v;
    }

    private int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Entrada invalida.");
            }
        }
    }

    private int lerInteiroPositivo(String prompt) {
        while (true) {
            int v = lerInteiro(prompt);
            if (v > 0) return v;
            System.out.println("  Deve ser maior que zero.");
        }
    }
}
