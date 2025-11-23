package com.mycompany.projeto_a3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class Jogadores {
    public String nome;
    protected int frotas[]; // protected int → visível para classes filhas; int → tipo inteiro; frotas[] → vetor de inteiros
    MeuTabuleiro inimigo;
    MeuTabuleiro inimigo;
    MeuTabuleiro tabuleiro;

    public Jogadores(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        this.nome = nome; // this → refere-se ao campo da instância atual
        this.frotas = new int[]{2, 3, 4}; // new int[] → cria um novo vetor de inteiros na memória
        this.tabuleiro = tabuleiro; // this → associa o parâmetro ao campo da instância
        this.inimigo = inimigo;
    }

    abstract void ataque(); // abstract void → método sem implementação aqui; subclasses devem implementar
    abstract void posicao();
}

// ============================ JOGADOR HUMANO ============================

class Jogador1 extends Jogadores { // extends → Jogador1 herda comportamento de Jogadores

    public Jogador1(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        super(nome, inimigo, tabuleiro); // super → chama o construtor da classe mãe
    }

    @Override
    void posicao() {
        Scanner tecla = new Scanner(System.in);
        System.out.println("Vamos posicionar as frotas!");
        System.out.println("Essas sao as suas frotas: " + frotas.length + " navios para posicionar.");

        for (int i = 0; i < frotas.length; i++) { // for → laço; int i → contador; i++ → incrementa
            int tamanhoN = frotas[i]; // int → inteiro que recebe tamanho do navio
            System.out.println("\nPosicione o navio de tamanho " + tamanhoN);

            for (int j = 0; j < tamanhoN; j++) { // for → laço interno; int j → contador de posições do navio
                boolean posicaoValida = false; // boolean → true/false para controlar o loop
                String entrada;

                do { // do → executa o bloco ao menos uma vez
                    System.out.println("Digite a coordenada " + (j + 1) + " (Ex: A1, B5):");
                    entrada = tecla.nextLine().toUpperCase().trim();

                    // valida formato Letra+Número
                    if (!entrada.matches("^[A-J](10|[1-9])$")) { // if → testa formato inválido
                        System.out.println("Formato invalido! Use formato Letra+Numero (ex: A1, B10).");
                        continue; // continue → pula para a próxima iteração do loop mais interno (do/while)
                    }

                    int linha = entrada.charAt(0) - 'A'; // char 'A' → usado para converter letra em índice
                    int coluna = Integer.parseInt(entrada.substring(1)) - 1; // int → parse do número

                    if (linha < 0 || linha >= tabuleiro.gettamanhoX() || coluna < 0 || coluna >= tabuleiro.gettamanhoY()) {
                        // if → verifica limites do tabuleiro
                        System.out.println("Posicao fora do limite!");
                        continue;
                    }

                    if (tabuleiro.posicaoDisponivel(linha, coluna)) { // if → verifica disponibilidade
                        tabuleiro.getcampo()[linha][coluna] = 'N'; // char 'N' → marca navio
                        posicaoValida = true; // boolean troca para true para sair do do/while
                    } else {
                        System.out.println("Posicao ja ocupada. Digite novamente.");
                    }

                } while (!posicaoValida); //while → repete até posicaoValida ser true
            }
        }
    }

    @Override
    void ataque() {
        Scanner tecla = new Scanner(System.in);
        boolean tiroValido = false; // boolean → controla o loop do ataque

        while (!tiroValido) { // while → repete enquanto tiroValido for false
            System.out.println("Digite a coordenada do ataque (Ex: A1, B5):");
            String entrada = tecla.nextLine().toUpperCase().trim();

            if (!entrada.matches("^[A-J](10|[1-9])$")) { // if → valida formato
                System.out.println("Formato invalido! Use formato Letra+Número (ex: A1, B10).");
                continue;
            }

            int linha = entrada.charAt(0) - 'A'; // char usado para converter letra
            int coluna = Integer.parseInt(entrada.substring(1)) - 1;

            if (linha < 0 || linha >= inimigo.gettamanhoX() || coluna < 0 || coluna >= inimigo.gettamanhoY()) {
                // if → verifica limites
                System.out.println("Posicao fora do limite!");
                continue;
            }

            char valorAtual = inimigo.getcampo()[linha][coluna]; // char → valor atual da célula ('N','X','O')

            if (valorAtual == 'X' || valorAtual == 'O') { // if → já atacado
                System.out.println("Posicao ja atacada!");
                continue;
            }

            if (valorAtual == 'N') { // if → acertou navio
                System.out.println(" Acertou um navio!");
                inimigo.getcampo()[linha][coluna] = 'X'; // char 'X' → marca acerto
            } else {
                System.out.println(" Acertou a agua!");
                inimigo.getcampo()[linha][coluna] = 'O'; // char 'O' → marca água
            }

            tiroValido = true;
        }
    }
}

class Maquina extends Jogadores { // extends → Maquina herda Jogadores

    private int dificuldade; // int → nível de dificuldade
    private Banco_Do_Jogo banco = new Banco_Do_Jogo(); // new → cria instância de Banco_Do_Jogo

    public Maquina(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        super(nome, inimigo, tabuleiro);// super → chama construtor da classe mãe
    }

    public void setDificuldade(int dificuldade) {
        this.dificuldade = dificuldade; // this → refere-se ao campo da instância
    }

    @Override
    void posicao() {
        System.out.println("Posicionando frotas da maquina...");

        for (int i = 0; i < frotas.length; i++) { // for → itera sobre frotas; int i → contador
            int tamanhoNavio = frotas[i]; // int → tamanho do navio
            boolean posicionado = false; // boolean → controla o loop de posicionamento

            while (!posicionado) { // while → repete até posicionado ser true
                int linha = (int) (Math.random() * tabuleiro.gettamanhoX());  // int → random linha
                int coluna = (int) (Math.random() * tabuleiro.gettamanhoY()); // int → random coluna
                boolean horizontal = Math.random() < 0.5;  // boolean → orientacao aleatoria

                if (veriEspa(tabuleiro, linha, coluna, tamanhoNavio, horizontal)) { // if → verifica espaço
                    if (horizontal) {
                        for (int j = 0; j < tamanhoNavio; j++) // for → preenche horizontais; int j → contador
                            tabuleiro.getcampo()[linha][coluna + j] = 'N'; // char 'N' → coloca navio
                    } else {
                        for (int j = 0; j < tamanhoNavio; j++) // for → preenche verticais
                            tabuleiro.getcampo()[linha + j][coluna] = 'N';
                    }
                    posicionado = true; // boolean → marca que já posicionou
                }
            }
        }
    }

    @Override
    void ataque() {

        int linha = -1, coluna = -1; // int → inicializa coordenadas com -1 (indicando não definidas)

     

        switch (dificuldade) { // switch/case → escolhe comportamento por dificuldade

          
            case 1: { // case → caso dificuldade == 1
                Random rnd = new Random(); // new → cria gerador de números aleatórios
                int tent = 0; // int → contador de tentativas
                do { //do → executa ao menos uma vez
                    linha = rnd.nextInt(inimigo.gettamanhoX()); // int → número aleatório para linha
                    coluna = rnd.nextInt(inimigo.gettamanhoY()); // int → número aleatório para coluna
                    tent++;
                    if (tent > 1000) break; // segurança
                } while (inimigo.getcampo()[linha][coluna] == 'X' || inimigo.getcampo()[linha][coluna] == 'O');
                // while → repete até achar célula não atacada
                break; // break → encerra este case
            }
            }

          
            case 2: {
                boolean achou = false; // boolean → controla se encontrou boa jogada

                
                for (int i = 0; i < inimigo.gettamanhoX() && !achou; i++) {  // for → varre linhas; int i → contador
                    for (int j = 0; j < inimigo.gettamanhoY() && !achou; j++) { // for → varre colunas; int j → contador
                        if (inimigo.getcampo()[i][j] == 'X') { // if → procura acertos anteriores
                            int[][] adj = {{i - 1, j}, {i, j + 1}, {i + 1, j}, {i, j - 1}}; // new int[][] → cria matriz de adjacentes
                            for (int[] p : adj) {
                                int li = p[0], co = p[1]; // int li/j → coordenadas adjacentes
                                if (li >= 0 && li < inimigo.gettamanhoX() && co >= 0 && co < inimigo.gettamanhoY()) {
                                    char c = inimigo.getcampo()[li][co]; // char → valor da célula
                                    if (c != 'X' && c != 'O') { // if → célula disponível
                                        linha = li;
                                        coluna = co;
                                        achou = true; // boolean → marca que achou
                                        break; // break → sai do for mais interno (da lista adj)
                                    }
                                }
                            }
                        }
                    }
                }

                if (!achou) { // if/else lógico: se não achou por adjacência tenta estratégia em xadrez
                    
                    for (int i = 0; i < inimigo.gettamanhoX() && !achou; i++) {
                        for (int j = 0; j < inimigo.gettamanhoY() && !achou; j++) {
                            if ((i + j) % 2 == 0) {  // if → padrão em xadrez para eficiência
                                char c = inimigo.getcampo()[i][j]; // char → célula
                                if (c != 'X' && c != 'O') {// if → disponível
                                    linha = i;
                                    coluna = j;
                                    achou = true;// boolean → marca
                                }
                            }
                        }
                    }
                }

               
                if (!achou) { // if → se ainda não achou, varre qualquer célula disponível
                    outer:
                    for (int i = 0; i < inimigo.gettamanhoX(); i++) { // for → percorre linhas
                        for (int j = 0; j < inimigo.gettamanhoY(); j++) { // for → percorre colunas
                            char c = inimigo.getcampo()[i][j]; // char → célula
                            if (c != 'X' && c != 'O') { // if → disponível
                                linha = i;
                                coluna = j;
                                break outer; // break outer → sai dos dois loops (rótulo outer)
                            }
                        }
                    }
                }
                break; // break → termina case 2
            }

  
            case 3: {
                boolean jogadaDefinida = false;  // boolean → se a jogada já foi definida

               
                outerTarget:
                for (int i = 0; i < inimigo.gettamanhoX() && !jogadaDefinida; i++) { // for → busca por Xs
                    for (int j = 0; j < inimigo.gettamanhoY() && !jogadaDefinida; j++) {
                        if (inimigo.getcampo()[i][j] == 'X') {// if → encontrou acerto anterior

                           
              try { // try → tenta buscar vizinhos do BD (pode lançar erro)
                 List<int[]> vizinhosBD = banco.buscarVizinhosMaisComuns(dificuldade, i, j); // new ArrayList etc. dentro do método
                  for (int[] p : vizinhosBD) { // for → percorre resultados vindos do BD
                   int li = p[0], co = p[1]; // int → coordenadas do BD
                     if (li >= 0 && li < inimigo.gettamanhoX() && co >= 0 && co < inimigo.gettamanhoY()) {
                       char cel = inimigo.getcampo()[li][co]; // char → célula
                         if (cel != 'X' && cel != 'O') { // if → disponível
                           linha = li;
                              coluna = co;
                                jogadaDefinida = true; // boolean → marca
                                  break outerTarget;  // break outerTarget → sai de ambos os loops
                                        }
                                    }
                                }
                            } catch (Throwable t) { // catch → captura qualquer erro que ocorra no try
                               
                            }

                          
           int[][] adj = {{i, j+1}, {i, j-1}, {i+1, j}, {i-1, j}};   // new int[][] → lista adjacentes
            for (int[] p : adj) { // for → percorre adjacentes
                int li = p[0], co = p[1];
                   if (li >= 0 && li < inimigo.gettamanhoX() && co >= 0 && co < inimigo.gettamanhoY()) {
                     char cel = inimigo.getcampo()[li][co]; // char → célula
                       if (cel != 'X' && cel != 'O') { // if → disponível
                         linha = li;
                           coluna = co;
                              jogadaDefinida = true; // boolean → encontrou jogada
                                   break outerTarget; // break outerTarget → sai dos loops rotulados
                                    }
                                }
                            }
                        }
                    }
                }

                
                if (!jogadaDefinida) { // if → tenta usar estatísticas do BD
                    try {  // try → pode lançar exceção ao acessar BD
                        int[] jog = banco.melhorJogada(dificuldade);  // return new int[] → possivelmente retornado pelo método
                        if (jog != null && jog.length >= 2) {  // if → verifica null e tamanho (null → sem dados)
                            int l = jog[0], c = jog[1]; // int → extrai linha/coluna
                            if (l >= 0 && l < inimigo.gettamanhoX() && c >= 0 && c < inimigo.gettamanhoY()) {
                                char cel = inimigo.getcampo()[l][c]; // char → célula
                                if (cel != 'X' && cel != 'O') { // if → disponível
                                    linha = l;
                                    coluna = c;
                                    jogadaDefinida = true; // boolean → marcada
                                }
                            }
                        }
                    } catch (Throwable t) { // catch → captura erro do try
                       
                    }
                }

               
                if (!jogadaDefinida) { // if → estratégia por centro e ordenação
                    int n = inimigo.gettamanhoX(); // int → linhas
                    int m = inimigo.gettamanhoY(); // int → colunas
                    int centroL = n / 2; //int → centro linha
                    int centroC = m / 2; // int → centro coluna

                    List<int[]> coords = new ArrayList<>(n * m); // new ArrayList → cria lista com capacidade inicial
                    for (int i = 0; i < n; i++) { // for → percorre linhas
                        for (int j = 0; j < m; j++) { // for → percorre colunas
                            coords.add(new int[]{i, j}); // new int[] → cria vetor coordenada
                        }
                    }

                    coords.sort((a, b) -> {
                        int da = Math.abs(a[0] - centroL) + Math.abs(a[1] - centroC);
                        int db = Math.abs(b[0] - centroL) + Math.abs(b[1] - centroC);
                        if (da != db) return Integer.compare(da, db);
                        if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
                        return Integer.compare(a[1], b[1]);
                    });

                    for (int[] p : coords) { // for → percorre coordenadas ordenadas
                        int li = p[0], co = p[1]; // int → extrai
                        char c = inimigo.getcampo()[li][co]; // char → célula
                        if (c != 'X' && c != 'O') { // if → disponível
                            linha = li;
                            coluna = co;
                            break; // break → sai do loop após definir coordenada
                        }
                    }
                }

                break; // break → termina case 3
            }

       
            default: { // default → caso nenhuma case corresponda
                Random rnd = new Random();  // new → cria Random
                int tent = 0;  // int → contador de tentativas
                do { // do → tenta ao menos uma vez
                    linha = rnd.nextInt(inimigo.gettamanhoX());
                    coluna = rnd.nextInt(inimigo.gettamanhoY());
                    tent++;
                    if (tent > 1000) break; // break → segurança contra loop infinito
                } while (inimigo.getcampo()[linha][coluna] == 'X' || inimigo.getcampo()[linha][coluna] == 'O');
            // while → repete até achar célula livre
                break; // break → final do default
            }
        } 

       
        if (linha < 0 || coluna < 0) { // if → se ainda não definiu coordenadas
            outer2:
            for (int i = 0; i < inimigo.gettamanhoX(); i++) { // for → percorre linhas
                for (int j = 0; j < inimigo.gettamanhoY(); j++) { // for → percorre colunas
                    char c = inimigo.getcampo()[i][j]; // char → célula
                    if (c != 'X' && c != 'O') { // if → disponível
                        linha = i;
                        coluna = j;
                        break outer2; // break outer2 → sai dos dois loops

                    }
                }
            }
        }

        char valorAtual = inimigo.getcampo()[linha][coluna]; // char → valor atual na célula escolhida
        boolean acertou = (valorAtual == 'N'); // boolean → true se acertou navio ('N')

        System.out.println("Maquina atacou: " + (char)('A' + linha) + "" + (coluna + 1));

        if (acertou) {  // if → caso tenha acertado
            System.out.println("A mquina ACERTOU um navio!");
            inimigo.getcampo()[linha][coluna] = 'X'; // char 'X' → marca acerto
        } else {
            System.out.println("A maquina acertou a agua.");
            inimigo.getcampo()[linha][coluna] = 'O';  // char 'O' → marca água
        }

        try { // try → tenta registrar jogada na base (pode dar erro)
            banco.registrarJogadaIA(dificuldade, linha, coluna, acertou);
        } catch (Throwable t) { // catch → captura qualquer erro (Throwable cobre tudo)
           
        }
    }

    boolean veriEspa(MeuTabuleiro tab, int linha, int coluna, int tamanhoNavio, boolean horizontal) {
        if (horizontal) { // if → verifica espaço horizontal
            if (coluna + tamanhoNavio > tab.gettamanhoY()) return false; // if → saída rápida se ultrapassar limites
            for (int j = 0; j < tamanhoNavio; j++) // for → verifica cada posição horizontal; int j → contador
                if (!tab.posicaoDisponivel(linha, coluna + j)) return false; // if → se alguma posição não disponível retorna false
        } else { // else → caso vertical
            if (linha + tamanhoNavio > tab.gettamanhoX()) return false;  // if → verifica limite vertical
            for (int j = 0; j < tamanhoNavio; j++) // for → verifica cada posição vertical
                if (!tab.posicaoDisponivel(linha + j, coluna)) return false;   // if → retorna false se ocupado
        }
        return true; // return → tudo ok, espaço disponível
    }
}
