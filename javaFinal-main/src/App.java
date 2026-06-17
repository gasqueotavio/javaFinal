import repositorio.*;
import servico.LogServico;
import visao.*;
import java.util.Scanner;

public class App {

    private static final String DADOS_PESSOAS   = "dados/pessoas.txt";
    private static final String DADOS_ENDERECOS = "dados/enderecos.txt";
    private static final String DADOS_PRODUTOS  = "dados/produtos.txt";
    private static final String DADOS_PEDIDOS   = "dados/pedidos.txt";
    private static final String DADOS_ITENS     = "dados/itens_pedido.txt";
    private static final String LOG_SISTEMA     = "logs/sistema.log";

    private static final String MENU_PRINCIPAL  = "menus/menu_principal.txt";
    private static final String MENU_PESSOAS    = "menus/menu_pessoas.txt";
    private static final String MENU_ENDERECOS  = "menus/menu_enderecos.txt";
    private static final String MENU_PRODUTOS   = "menus/menu_produtos.txt";
    private static final String MENU_PEDIDOS    = "menus/menu_pedidos.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   SISTEMA DE GESTAO - Iniciando...");
        System.out.println("============================================");

        PessoaRepositorio pessoaRepo     = new PessoaRepositorio(DADOS_PESSOAS);
        EnderecoRepositorio enderecoRepo = new EnderecoRepositorio(DADOS_ENDERECOS);
        ProdutoRepositorio produtoRepo   = new ProdutoRepositorio(DADOS_PRODUTOS);
        PedidoRepositorio pedidoRepo     = new PedidoRepositorio(DADOS_PEDIDOS, DADOS_ITENS);

        LogServico log = new LogServico(LOG_SISTEMA);

        PessoaVisao pessoaVisao = new PessoaVisao(
                pessoaRepo, enderecoRepo, log, scanner, MENU_PESSOAS, MENU_ENDERECOS);

        ProdutoVisao produtoVisao = new ProdutoVisao(
                produtoRepo, pessoaRepo, log, scanner, MENU_PRODUTOS);

        PedidoVisao pedidoVisao = new PedidoVisao(
                pedidoRepo, pessoaRepo, enderecoRepo, produtoRepo, log, scanner, MENU_PEDIDOS);

        System.out.println("Sistema carregado.");
        System.out.printf("  Pessoas: %d | Produtos: %d | Pedidos: %d%n",
                pessoaRepo.listarTodos().size(),
                produtoRepo.listarTodos().size(),
                pedidoRepo.listarTodos().size());

        int opcao;
        do {
            servico.MenuServico.exibirMenu(MENU_PRINCIPAL);
            opcao = lerInteiro(scanner, "Opcao: ");
            switch (opcao) {
                case 1: pessoaVisao.exibirMenu();  break;
                case 2: produtoVisao.exibirMenu(); break;
                case 3: pedidoVisao.exibirMenu();  break;
                case 0:
                    System.out.println("Encerrando o sistema. Ate logo!");
                    break;
                default:
                    System.out.println("Opcao invalida. Digite um numero do menu.");
            }
        } while (opcao != 0);

        scanner.close();
    }

    private static int lerInteiro(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Digite um numero.");
            }
        }
    }
}
