package servico;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MenuServico {

    public static void exibirMenu(String caminhoMenu) {
        System.out.println();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(caminhoMenu))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                System.out.println(linha);
            }
        } catch (IOException e) {
            System.err.println("[AVISO] Menu nao encontrado: " + caminhoMenu);
            System.out.println("=== MENU (arquivo nao encontrado) ===");
        }
    }

    public static List<String> carregarLinhas(String caminhoMenu) {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(caminhoMenu))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            System.err.println("[AVISO] Erro ao carregar menu: " + caminhoMenu);
        }
        return linhas;
    }
}
