package visao;

import model.*;
import repositorio.PessoaRepositorio;
import repositorio.ProdutoRepositorio;
import servico.LogServico;
import servico.MenuServico;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProdutoVisao {

    private final ProdutoRepositorio repositorio;
    private final PessoaRepositorio pessoaRepositorio;
    private final LogServico log;
    private final Scanner scanner;
    private final String caminhoMenu;

    public ProdutoVisao(ProdutoRepositorio repositorio, PessoaRepositorio pessoaRepositorio,
                        LogServico log, Scanner scanner, String caminhoMenu) {
        this.repositorio = repositorio;
        this.pessoaRepositorio = pessoaRepositorio;
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
        System.out.println("\n--- INCLUIR PRODUTO ---");
        String codigo = lerObrigatorio("Codigo: ");

        if (repositorio.existeCodigo(codigo)) {
            System.out.println("ERRO: Ja existe um produto com o codigo '" + codigo + "'.");
            return;
        }

        String descricao = lerObrigatorio("Descricao: ");

        List<Pessoa> fornecedores = pessoaRepositorio.listarFornecedores();
        if (fornecedores.isEmpty()) {
            System.out.println("[AVISO] Nenhum fornecedor cadastrado. Cadastre um fornecedor primeiro.");
        } else {
            System.out.println("Fornecedores disponiveis:");
            for (Pessoa f : fornecedores) System.out.println("  " + f);
        }

        String codigoFornecedor = lerObrigatorio("Codigo do Fornecedor: ");

        Pessoa fornecedor = pessoaRepositorio.buscarPorCodigo(codigoFornecedor);
        if (fornecedor == null) {
            System.out.println("[AVISO] Fornecedor nao encontrado no cadastro.");
        } else if (fornecedor.getTipo() == TipoPessoa.CLIENTE) {
            System.out.println("[AVISO] Esta pessoa esta cadastrada apenas como Cliente.");
        }

        double custo      = lerDouble("Custo (R$): ");
        double precoVenda = lerDouble("Preco de Venda (R$): ");

        if (precoVenda < custo) {
            System.out.print("[AVISO] Preco de venda menor que o custo. Confirma? (S/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                System.out.println("Inclusao cancelada.");
                return;
            }
        }

        Produto p = new Produto(codigo, descricao, custo, precoVenda, codigoFornecedor);
        repositorio.incluir(p);
        log.registrar("INCLUSAO", "Produto", "Codigo=" + codigo + " | " + descricao);
        System.out.println("Produto incluido com sucesso!");
    }

    private void consultar() {
        System.out.println("\n--- CONSULTAR PRODUTOS ---");
        System.out.println("1. Listar todos");
        System.out.println("2. Filtrar por descricao");
        System.out.println("3. Buscar por codigo");
        System.out.println("4. Filtrar por fornecedor");
        int opcao = lerInteiro("Opcao: ");

        List<Produto> resultado = new ArrayList<>();
        switch (opcao) {
            case 1:
                resultado = repositorio.listarTodos();
                break;
            case 2:
                resultado = repositorio.filtrarPorDescricao(lerObrigatorio("Descricao (parcial): "));
                break;
            case 3:
                Produto p = repositorio.buscarPorCodigo(lerObrigatorio("Codigo: "));
                if (p != null) resultado.add(p);
                break;
            case 4:
                resultado = repositorio.listarPorFornecedor(lerObrigatorio("Codigo do fornecedor: "));
                break;
            default:
                System.out.println("Opcao invalida.");
                return;
        }

        if (resultado.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
        } else {
            System.out.println("\n--- RESULTADO (" + resultado.size() + " produto(s)) ---");
            for (Produto p : resultado) System.out.println(p);
        }
    }

    private void alterar() {
        System.out.println("\n--- ALTERAR PRODUTO ---");
        String codigo = lerObrigatorio("Codigo do produto: ");
        Produto existente = repositorio.buscarPorCodigo(codigo);

        if (existente == null) {
            System.out.println("ERRO: Produto com codigo '" + codigo + "' nao encontrado.");
            return;
        }

        System.out.println("Produto atual: " + existente);
        System.out.println("Pressione ENTER para manter o valor atual.");

        String descricao = lerOpcional("Descricao [" + existente.getDescricao() + "]: ", existente.getDescricao());
        String codForn   = lerOpcional("Cod. Fornecedor [" + existente.getCodigoFornecedor() + "]: ", existente.getCodigoFornecedor());

        System.out.print("Custo [" + existente.getCusto() + "]: ");
        double custo = lerDoubleOpcional(existente.getCusto());

        System.out.print("Preco Venda [" + existente.getPrecoVenda() + "]: ");
        double precoVenda = lerDoubleOpcional(existente.getPrecoVenda());

        Produto atualizado = new Produto(codigo, descricao, custo, precoVenda, codForn);
        repositorio.alterar(atualizado);
        log.registrar("ALTERACAO", "Produto", "Codigo=" + codigo + " | " + descricao);
        System.out.println("Produto alterado com sucesso!");
    }

    private void excluir() {
        System.out.println("\n--- EXCLUIR PRODUTO ---");
        String codigo = lerObrigatorio("Codigo do produto: ");
        Produto existente = repositorio.buscarPorCodigo(codigo);

        if (existente == null) {
            System.out.println("ERRO: Produto nao encontrado.");
            return;
        }

        System.out.println("Produto a excluir: " + existente);
        System.out.print("Confirma? (S/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Exclusao cancelada.");
            return;
        }

        repositorio.excluir(codigo);
        log.registrar("EXCLUSAO", "Produto",
                "Codigo=" + codigo + " | " + existente.getDescricao());
        System.out.println("Produto excluido com sucesso!");
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

    private String lerOpcional(String prompt, String padrao) {
        System.out.print(prompt);
        String v = scanner.nextLine().trim();
        return v.isEmpty() ? padrao : v;
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

    private double lerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("  Valor invalido. Use ponto ou virgula (ex: 9.99 ou 9,99).");
            }
        }
    }

    private double lerDoubleOpcional(double padrao) {
        while (true) {
            try {
                String entrada = scanner.nextLine().trim();
                if (entrada.isEmpty()) return padrao;
                return Double.parseDouble(entrada.replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("  Valor invalido.");
            }
        }
    }
}
