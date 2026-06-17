package repositorio;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Repositorio<T> {

    protected String caminhoArquivo;

    protected List<T> dados;

    public Repositorio(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        this.dados = new ArrayList<>();
        garantirArquivo();
    }

    private void garantirArquivo() {
        File arquivo = new File(caminhoArquivo);
        arquivo.getParentFile().mkdirs();
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Nao foi possivel criar o arquivo: " + caminhoArquivo);
            }
        }
    }

    public abstract void carregar();

    protected abstract T desserializar(String linha);

    protected abstract String serializar(T item);

    public void salvar() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (T item : dados) {
                writer.write(serializar(item));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + caminhoArquivo);
        }
    }

    public List<T> listarTodos() {
        return new ArrayList<>(dados);
    }

    protected List<String> lerLinhas() {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String l = linha.trim();
                if (!l.isEmpty()) linhas.add(l);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler: " + caminhoArquivo);
        }
        return linhas;
    }
}
