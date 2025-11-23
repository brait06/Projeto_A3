package com.mycompany.projeto_a3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Banco_Do_Jogo {
    private static final String URL = "jdbc:mysql://localhost:3306/batalha_naval?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "228529";

    public Connection conectar() throws SQLException { // throws → indica que o método pode gerar um erro
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void criarTabelaPlacar() {
        String sql = "CREATE TABLE IF NOT EXISTS placar ("
                   + "nome VARCHAR(50) PRIMARY KEY, "
                   + "vitorias INT DEFAULT 0, "
                   + "derrotas INT DEFAULT 0);";
        try (Connection conn = conectar(); Statement st = conn.createStatement()) { // try → tenta executar algo que pode dar erro
            st.execute(sql);
        } catch (SQLException e) {  // catch → captura o erro caso aconteça dentro do try;// e → mostra o erro
            System.err.println("Erro criarTabelaPlacar: " + e.getMessage());
        }
    }

    public boolean jogadorExiste(String nome) { // boolean → retorna true ou false
        String sql = "SELECT 1 FROM placar WHERE nome = ?";
        try (Connection conn = conectar(); // try com recursos → fecha automaticamente conexão, statement e resultset
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // return → devolve resultado
            }
        } catch (SQLException e) { // executado caso aconteça erro SQL
            System.err.println("Erro jogadorExiste: " + e.getMessage());
            return false; // return false → se deu erro, considera que não existe jogador
        }
    }

    public void registrarJogador(String nome) {
        if (!jogadorExiste(nome)) { // if → testa condição; ! significa "não"
            String sql = "INSERT INTO placar (nome) VALUES (?)";
            try (Connection conn = conectar();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nome);
                ps.executeUpdate();
                System.out.println("Novo jogador adicionado ao placar: " + nome);
            } catch (SQLException e) {
                System.err.println("Erro registrarJogador: " + e.getMessage());
            }
        }
    }

    public void adicionarVitoria(String nome) {
        String sql = "UPDATE placar SET vitorias = vitorias + 1 WHERE nome = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro adicionarVitoria: " + e.getMessage());
        }
    }

    public void adicionarDerrota(String nome) {
        String sql = "UPDATE placar SET derrotas = derrotas + 1 WHERE nome = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro adicionarDerrota: " + e.getMessage());
        }
    }

    public void mostrarPlacarIndividual(String nome) {
        String sql = "SELECT nome, vitorias, derrotas FROM placar WHERE nome = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // if → executa se encontrou o jogador
                    System.out.printf("\n Jogador: %s | Vitorias: %d | Derrotas: %d\n",
                            rs.getString("nome"),
                            rs.getInt("vitorias"),
                            rs.getInt("derrotas"));
                } else { // else → executa se não encontrou
                    System.out.println("Nenhum registro encontrado para: " + nome);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro mostrarPlacarIndividual: " + e.getMessage());
        }
    }

    public void mostrarPlacarGeral() {
        String sql = "SELECT nome, vitorias, derrotas FROM placar ORDER BY vitorias DESC, nome ASC";
        try (Connection conn = conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== PLACAR GERAL =====");
            while (rs.next()) { // while → repete enquanto houver resultados no banco
                System.out.printf("%-15s | Vitorias: %-3d | Derrotas: %-3d\n",
                                  rs.getString("nome"),
                                  rs.getInt("vitorias"),
                                  rs.getInt("derrotas"));
            }
        } catch (SQLException e) {
            System.err.println("Erro mostrarPlacarGeral: " + e.getMessage());
        }
    }

    public void criarTabelaMaquinaInteligencia() {
        String sql = "CREATE TABLE IF NOT EXISTS maquina_inteligencia ("
                   + "dificuldade INT NOT NULL, "
                   + "linha INT NOT NULL, "
                   + "coluna INT NOT NULL, "
                   + "resultado VARCHAR(10) NOT NULL, "
                   + "quantidade INT DEFAULT 0, "
                   + "PRIMARY KEY (dificuldade, linha, coluna)"
                   + ");";
        try (Connection conn = conectar(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro criarTabelaMaquinaInteligencia: " + e.getMessage());
        }
    }

    public void salvarJogada(int dificuldade, int linha, int coluna, String resultado) {
        boolean acertou = "HIT".equalsIgnoreCase(resultado) || "X".equalsIgnoreCase(resultado); // boolean → variável que recebe true ou false
        registrarJogadaIA(dificuldade, linha, coluna, acertou);
    }


    public void registrarJogadaIA(int dificuldade, int linha, int coluna, boolean acertou) {
        
        if (!acertou) return; // if → só salva se acertou. return → sai do método.


        String sql = "INSERT INTO maquina_inteligencia (dificuldade, linha, coluna, resultado, quantidade) "
                   + "VALUES (?, ?, ?, 'HIT', 1) "
                   + "ON DUPLICATE KEY UPDATE quantidade = quantidade + 1";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dificuldade); // int → número inteiro
            ps.setInt(2, linha);
            ps.setInt(3, coluna);

            ps.executeUpdate();

        } catch (SQLException e) {  // catch → executado quando ocorre um erro no banco
            System.err.println("Erro registrarJogadaIA: " + e.getMessage());
        }
    }

    /**
     * Retorna a coordenada com mais HITS para uma dada dificuldade.
     * Se não houver dados, retorna null.
     */
    public int[] melhorJogada(int dificuldade) { // int[] → vetor de inteiros
        String sql = "SELECT linha, coluna FROM maquina_inteligencia "
                   + "WHERE dificuldade = ? AND resultado = 'HIT' "
                   + "ORDER BY quantidade DESC LIMIT 1";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dificuldade);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // if → retorna a coordenada mais comum
                    return new int[]{ rs.getInt("linha"), rs.getInt("coluna") };
                    // new int[] → cria um novo vetor com linha e coluna
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro melhorJogada: " + e.getMessage());
        }

        return null; // se não houver dados
    }

    
    public List<int[]> buscarVizinhosMaisComuns(int dificuldade, int linha, int coluna) {
        String sql =
            "SELECT linha, coluna FROM maquina_inteligencia " +
            "WHERE dificuldade = ? AND resultado = 'HIT' AND ( " +
            " (linha = ? AND ABS(coluna - ?) = 1) " +
            " OR (coluna = ? AND ABS(linha - ?) = 1) " +
            ") ORDER BY quantidade DESC";

        List<int[]> lista = new ArrayList<>(); // new ArrayList → cria nova lista

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dificuldade);
            ps.setInt(2, linha);
            ps.setInt(3, coluna);
            ps.setInt(4, coluna);
            ps.setInt(5, linha);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { // while → repete enquanto houver vizinhos
                    lista.add(new int[]{ rs.getInt("linha"), rs.getInt("coluna") });
                    // new int[] → cria vetor para cada vizinho
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro buscarVizinhosMaisComuns: " + e.getMessage());
        }

        return lista; // return → devolve a lista
    }
}
