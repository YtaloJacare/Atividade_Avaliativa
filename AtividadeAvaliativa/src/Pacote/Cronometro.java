package Pacote;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Cronometro {

    public static void tempoExec(long tempoMillis, int numThreads) {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            String nomeArquivo = "versao_" + numThreads + "_threads.txt";
            fw = new FileWriter(nomeArquivo, false); // sobrescreve caso rode novamente
            pw = new PrintWriter(fw);
            pw.println("Número de threads: " + numThreads);
            pw.println("Tempo total de execução: " + tempoMillis + " ms" + " / "+ (tempoMillis / 1000.0) + " segundos");
        } catch (IOException e) {
            System.out.println("Erro ao escrever o relatório: " + e.getMessage());
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar o arquivo.");
            }
        }
    }
}
