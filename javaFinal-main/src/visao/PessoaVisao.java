package visao;

import model.*;
import repositorio.EnderecoRepositorio;
import repositorio.PessoaRepositorio;
import servico.LogServico;
import servico.MenuServico;
import java.util.List;
import java.util.Scanner;

public class PessoaVisao {

    private final PessoaRepositorio repositorio;
    private final EnderecoRepositorio enderecoRepositorio;
    private final LogServico log;
    private final Scanner scanner;
    private final String caminhoMenu;
    private final String caminhoMenuEndereco;

    public PessoaVisao(PessoaRepositorio repositorio, EnderecoRepositorio enderecoRepositorio,
                       LogServico log, Scanner scanner,
                       String caminhoMenu, String caminhoMenuEndereco) {
        this.repositorio = repositorio;
        this.enderecoRepositorio = enderecoRepositorio;
        this.log = log;
        this.scanner = scanner;
        this.caminhoMenu = caminhoMenu;
        this.caminhoMenuEndereco = caminhoMenuEndereco;
    }

    public void exibirMenu() {
        int opcao;
        do {
            MenuServico.exibirMenu(caminhoMenu);
            opcao = lerInteiro("Opcao: ");
            switch (opcao) {
                case 1: incluir();            break;
                case 2: consultar();          break;
                case 3: alterar();            break;
                case 4: excluir();            break;
                case 5: gerenciarEnderecos(); break;
                case 0: break;
                default: System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    private void incluir() {
        System.out.println("\n--- INCLUIR PESSOA ---");
        String codigo = lerObrigatorio("Codigo: ");

        if (repositorio.existeCodigo(codigo)) {
            System.out.println("ERRO: Ja existe uma pessoa com o codigo '" + codigo + "'.");
            return;
        }

        String nome = lerObrigatorio("Nome: ");
        TipoPessoa tipo = lerTipoPessoa();

        Pessoa p = fabricarPessoa(codigo, nome, tipo);
        repositorio.incluir(p);
        log.registrar("INCLUSAO", "Pessoa",
                "Codigo=" + codigo + " | Nome=" + nome + " | Tipo=" + tipo.name());
        System.out.println("Pessoa incluida com sucesso!");
    }

    private void consultar() {
        System.out.println("\n--- CONSULTAR PESSOAS ---");
        System.out.println("1. Listar todas");
        System.out.println("2. Filtrar por nome");
        System.out.println("3. Buscar por codigo");
        int opcao = lerInteiro("Opcao: ");

        List<Pessoa> resultado;
        switch (opcao) {
            case 1:
                resultado = repositorio.listarTodos();
                break;
            case 2:
                String filtroNome = lerObrigatorio("Nome (parcial): ");
                resultado = repositorio.filtrarPorNome(filtroNome);
                break;
            case 3:
                String filtroCod = lerObrigatorio("Codigo: ");
                resultado = new java.util.ArrayList<>();
                Pessoa p = repositorio.buscarPorCodigo(filtroCod);
                if (p != null) resultado.add(p);
                break;
            default:
                System.out.println("Opcao invalida.");
                return;
        }

        if (resultado.isEmpty()) {
            System.out.println("Nenhuma pessoa encontrada.");
        } else {
            System.out.println("\n--- RESULTADO (" + resultado.size() + " registro(s)) ---");
            for (Pessoa p : resultado) {
                System.out.println(p);
            }
        }
    }

    private void alterar() {
        System.out.println("\n--- ALTERAR PESSOA ---");
        String codigo = lerObrigatorio("Codigo da pessoa a alterar: ");
        Pessoa existente = repositorio.buscarPorCodigo(codigo);

        if (existente == null) {
            System.out.println("ERRO: Nenhuma pessoa com codigo '" + codigo + "' foi encontrada.");
            return;
        }

        System.out.println("Registro atual: " + existente);
        System.out.println("Pressione ENTER para manter o valor atual.");

        String novoNome = lerOpcional("Nome [" + existente.getNome() + "]: ", existente.getNome());
        System.out.println("Tipo atual: " + existente.getTipo().getDescricao() + " | 0=Manter");
        TipoPessoa novoTipo = lerTipoPessoaOpcional(existente.getTipo());

        Pessoa atualizada = fabricarPessoa(codigo, novoNome, novoTipo);
        repositorio.alterar(atualizada);
        log.registrar("ALTERACAO", "Pessoa",
                "Codigo=" + codigo + " | Nome=" + novoNome + " | Tipo=" + novoTipo.name());
        System.out.println("Pessoa alterada com sucesso!");
    }

    private void excluir() {
        System.out.println("\n--- EXCLUIR PESSOA ---");
        String codigo = lerObrigatorio("Codigo da pessoa a excluir: ");
        Pessoa existente = repositorio.buscarPorCodigo(codigo);

        if (existente == null) {
            System.out.println("ERRO: Nenhuma pessoa com codigo '" + codigo + "' foi encontrada.");
            return;
        }

        System.out.println("Pessoa a excluir: " + existente);
        System.out.print("Confirma exclusao? (S/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Exclusao cancelada.");
            return;
        }

        repositorio.excluir(codigo);
        log.registrar("EXCLUSAO", "Pessoa",
                "Codigo=" + codigo + " | Nome=" + existente.getNome());
        System.out.println("Pessoa excluida com sucesso!");
    }

    private void gerenciarEnderecos() {
        System.out.println("\n--- GERENCIAR ENDERECOS ---");
        String codigo = lerObrigatorio("Codigo da pessoa: ");
        Pessoa pessoa = repositorio.buscarPorCodigo(codigo);

        if (pessoa == null) {
            System.out.println("ERRO: Pessoa nao encontrada.");
            return;
        }

        EnderecoVisao ev = new EnderecoVisao(
                enderecoRepositorio, log, scanner, pessoa, caminhoMenuEndereco);
        ev.exibirMenu();
    }

    private Pessoa fabricarPessoa(String codigo, String nome, TipoPessoa tipo) {
        switch (tipo) {
            case CLIENTE:    return new Cliente(codigo, nome);
            case FORNECEDOR: return new Fornecedor(codigo, nome);
            case AMBOS:      return new ClienteFornecedor(codigo, nome);
            default:         return new Cliente(codigo, nome);
        }
    }

    private TipoPessoa lerTipoPessoa() {
        System.out.println("Tipo: 1-Cliente  2-Fornecedor  3-Ambos");
        int op = lerInteiro("Opcao: ");
        switch (op) {
            case 2:  return TipoPessoa.FORNECEDOR;
            case 3:  return TipoPessoa.AMBOS;
            default: return TipoPessoa.CLIENTE;
        }
    }

    private TipoPessoa lerTipoPessoaOpcional(TipoPessoa atual) {
        System.out.println("Tipo: 1-Cliente  2-Fornecedor  3-Ambos  0-Manter");
        int op = lerInteiro("Opcao: ");
        switch (op) {
            case 1:  return TipoPessoa.CLIENTE;
            case 2:  return TipoPessoa.FORNECEDOR;
            case 3:  return TipoPessoa.AMBOS;
            default: return atual;
        }
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
                System.out.println("  Entrada invalida. Digite um numero.");
            }
        }
    }
}
