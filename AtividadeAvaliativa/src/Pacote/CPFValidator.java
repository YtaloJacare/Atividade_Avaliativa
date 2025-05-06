package Pacote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class CPFValidator {

	public static final int NUM_THREADS = 1; // Altere conforme necessário

    public static boolean validaCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;
        for (int i = 9; i <= 10; i++) {
            int soma = 0;
            for (int j = 0; j < i; j++) {
                soma += (cpf.charAt(j) - '0') * ((i + 1) - j);
            }
            int digito = (soma * 10) % 11;
            if (digito == 10) digito = 0;
            if (digito != (cpf.charAt(i) - '0')) return false;
        }
        return true;
    }

    public static void processaArquivos(File[] arquivos, AtomicInteger validos, AtomicInteger invalidos, String nomeThread) {
        System.out.println("[" + nomeThread + "] vai ler " + arquivos.length + " arquivo(s).");

        for (File arquivo : arquivos) {
            try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    linha = linha.trim();
                    boolean valido = validaCPF(linha);
                    if (valido) validos.incrementAndGet();
                    else invalidos.incrementAndGet();
                }
            } catch (IOException e) {
                System.out.println("Erro ao abrir o arquivo " + arquivo.getName() + ": " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();

        File pasta = new File("cpfs");
        File[] arquivosTxt = pasta.listFiles((dir, nome) -> nome.endsWith(".txt"));

        if (arquivosTxt == null || arquivosTxt.length == 0) {
            System.out.println("Nenhum arquivo .txt encontrado.");
            return;
        }

        int totalArquivos = arquivosTxt.length;
        int arquivosPorThread = totalArquivos / NUM_THREADS;
        int sobra = totalArquivos % NUM_THREADS;

        System.out.println("Número Threads: " + NUM_THREADS);
        System.out.println("Total de arquivos: " + totalArquivos);
        System.out.println("Divisão:");

        AtomicInteger validos = new AtomicInteger(0);
        AtomicInteger invalidos = new AtomicInteger(0);

        Thread[] threads = new Thread[NUM_THREADS];
        int inicioIndice = 0;

        for (int i = 0; i < NUM_THREADS; i++) {
            int quantidade = arquivosPorThread + (i < sobra ? 1 : 0);
            File[] parte = new File[quantidade];
            System.arraycopy(arquivosTxt, inicioIndice, parte, 0, quantidade);

            String nomeThread = "Thread-" + (i + 1);
            threads[i] = new Thread(() -> processaArquivos(parte, validos, invalidos, nomeThread), nomeThread);
            threads[i].start();

            inicioIndice += quantidade;
        }

        for (Thread cada : threads) {
            try {
                cada.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nTotal de CPF's válidados: " + validos.get());
        System.out.println("Total de CPF's inválidados: " + invalidos.get());

        long fim = System.currentTimeMillis();
        long duracao = fim - inicio;

        Cronometro.tempoExec(duracao, NUM_THREADS); //Para Salvar o tempo
    }

}
