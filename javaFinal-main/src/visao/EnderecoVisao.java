package visao;

import model.*;
import repositorio.EnderecoRepositorio;
import servico.LogServico;
import servico.MenuServico;
import java.util.List;
import java.util.Scanner;

public class EnderecoVisao {

    private final EnderecoRepositorio repositorio;
    private final LogServico log;
    private final Scanner scanner;
    private final Pessoa pessoa;
    private final String caminhoMenu;

    public EnderecoVisao(EnderecoRepositorio repositorio, LogServico log,
                         Scanner scanner, Pessoa pessoa, String caminhoMenu) {
        this.repositorio = repositorio;
        this.log = log;
        this.scanner = scanner;
        this.pessoa = pessoa;
        this.caminhoMenu = caminhoMenu;
    }

    public void exibirMenu() {
        System.out.println("\n>>> Enderecos de: " + pessoa.getNome() + " <<<");
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
        System.out.println("\n--- INCLUIR ENDERECO ---");
        String cep        = lerObrigatorio("CEP: ");
        String logradouro = lerObrigatorio("Logradouro: ");
        String numero     = lerObrigatorio("Numero: ");
        System.out.print("Complemento (deixe em branco se nao houver): ");
        String complemento = scanner.nextLine().trim();
        TipoEndereco tipo = lerTipoEndereco();

        int id = repositorio.proximoId();
        Endereco e = new Endereco(id, pessoa.getCodigo(), cep, logradouro, numero, complemento, tipo);
        repositorio.incluir(e);
        log.registrar("INCLUSAO", "Endereco",
                "ID=" + id + " | Pessoa=" + pessoa.getCodigo() + " | " + logradouro + ", " + numero);
        System.out.println("Endereco incluido com sucesso! (ID=" + id + ")");
    }

    private void consultar() {
        List<Endereco> enderecos = repositorio.listarPorPessoa(pessoa.getCodigo());
        if (enderecos.isEmpty()) {
            System.out.println("Nenhum endereco cadastrado para esta pessoa.");
        } else {
            System.out.println("\n--- ENDERECOS DE " + pessoa.getNome().toUpperCase() + " ---");
            for (Endereco e : enderecos) System.out.println(e);
        }
    }

    private void alterar() {
        consultar();
        List<Endereco> enderecos = repositorio.listarPorPessoa(pessoa.getCodigo());
        if (enderecos.isEmpty()) return;

        int id = lerInteiro("ID do endereco a alterar (0=cancelar): ");
        if (id == 0) return;

        Endereco existente = repositorio.buscarPorId(id);
        if (existente == null || !existente.getCodigoPessoa().equalsIgnoreCase(pessoa.getCodigo())) {
            System.out.println("ERRO: Endereco ID=" + id + " nao encontrado para esta pessoa.");
            return;
        }

        System.out.println("Pressione ENTER para manter o valor atual.");
        String cep = lerOpcional("CEP [" + existente.getCep() + "]: ", existente.getCep());
        String logradouro = lerOpcional("Logradouro [" + existente.getLogradouro() + "]: ", existente.getLogradouro());
        String numero = lerOpcional("Numero [" + existente.getNumero() + "]: ", existente.getNumero());
        String complemento = lerOpcional("Complemento [" + existente.getComplemento() + "]: ", existente.getComplemento());
        System.out.println("Tipo atual: " + existente.getTipo().getDescricao() + " | 0=Manter");
        TipoEndereco tipo = lerTipoEnderecoOpcional(existente.getTipo());

        Endereco atualizado = new Endereco(id, pessoa.getCodigo(), cep, logradouro, numero, complemento, tipo);
        repositorio.alterar(atualizado);
        log.registrar("ALTERACAO", "Endereco", "ID=" + id + " | Pessoa=" + pessoa.getCodigo());
        System.out.println("Endereco alterado com sucesso!");
    }

    private void excluir() {
        consultar();
        List<Endereco> enderecos = repositorio.listarPorPessoa(pessoa.getCodigo());
        if (enderecos.isEmpty()) return;

        int id = lerInteiro("ID do endereco a excluir (0=cancelar): ");
        if (id == 0) return;

        Endereco existente = repositorio.buscarPorId(id);
        if (existente == null || !existente.getCodigoPessoa().equalsIgnoreCase(pessoa.getCodigo())) {
            System.out.println("ERRO: Endereco nao encontrado para esta pessoa.");
            return;
        }

        repositorio.excluir(id);
        log.registrar("EXCLUSAO", "Endereco", "ID=" + id + " | Pessoa=" + pessoa.getCodigo());
        System.out.println("Endereco ID=" + id + " excluido com sucesso!");
    }

    private TipoEndereco lerTipoEndereco() {
        System.out.println("Tipo: 1-Comercial  2-Residencial  3-Entrega  4-Correspondencia");
        int op = lerInteiro("Opcao: ");
        switch (op) {
            case 2:  return TipoEndereco.RESIDENCIAL;
            case 3:  return TipoEndereco.ENTREGA;
            case 4:  return TipoEndereco.CORRESPONDENCIA;
            default: return TipoEndereco.COMERCIAL;
        }
    }

    private TipoEndereco lerTipoEnderecoOpcional(TipoEndereco atual) {
        System.out.println("Tipo: 1-Comercial  2-Residencial  3-Entrega  4-Correspondencia  0-Manter");
        int op = lerInteiro("Opcao: ");
        switch (op) {
            case 1:  return TipoEndereco.COMERCIAL;
            case 2:  return TipoEndereco.RESIDENCIAL;
            case 3:  return TipoEndereco.ENTREGA;
            case 4:  return TipoEndereco.CORRESPONDENCIA;
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
