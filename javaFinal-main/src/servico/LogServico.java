package servico;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogServico {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String caminhoLog;

    public LogServico(String caminhoLog) {
        this.caminhoLog = caminhoLog;
        new File(caminhoLog).getParentFile().mkdirs();
    }

    public void registrar(String operacao, String entidade, String detalhe) {
        String timestamp = LocalDateTime.now().format(FORMATO);
        String linha = String.format("[%s] %-10s | %-15s | %s", timestamp, operacao, entidade, detalhe);

        try (FileWriter fw = new FileWriter(caminhoLog, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao gravar log: " + e.getMessage());
        }
    }
}
