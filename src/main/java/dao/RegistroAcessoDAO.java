package dao;

import com.google.gson.Gson;
import model.RegistroAcesso;
import util.ConexaoBancoDados;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistroAcessoDAO {

    private static final Logger LOGGER = Logger.getLogger(RegistroAcessoDAO.class.getName());

    public void salvar(RegistroAcesso registro) {
        String sql = "INSERT INTO RegistroAcesso (dataHora, usuarioId, status, origem) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoBancoDados.getConexao(); PreparedStatement pstm = conexao.prepareStatement(sql)) {
            pstm.setString(1, registro.getDataHora().toString());
            if (registro.getUsuarioId() <= 0) {
                pstm.setNull(2, Types.INTEGER);
            } else {
                pstm.setInt(2, registro.getUsuarioId());
            }
            pstm.setString(3, registro.getStatus());
            pstm.setString(4, registro.getOrigem());
            pstm.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar registro de acesso: " + e.getMessage(), e);
        }
    }

    public List<RegistroAcesso> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim, String nomeUsuario, Integer idUsuario) {
        List<RegistroAcesso> registros = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT ra.id, ra.dataHora, ra.usuarioId, u.nome AS nomeUsuario, ra.status, ra.origem " +
                "FROM RegistroAcesso ra " +
                "LEFT JOIN Usuario u ON ra.usuarioId = u.id " +
                "WHERE ra.dataHora BETWEEN ? AND ?");
        List<Object> params = new ArrayList<>();
        params.add(inicio.toString());
        params.add(fim.toString());

        if (nomeUsuario != null && !nomeUsuario.isEmpty()) {
            sql.append(" AND u.nome LIKE ?");
            params.add("%" + nomeUsuario + "%");
        }
        if (idUsuario != null && idUsuario > 0) {
            sql.append(" AND u.id = ?");
            params.add(idUsuario);
        }

        sql.append(" ORDER BY ra.dataHora DESC");

        try (Connection conexao = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conexao.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstm.setObject(i + 1, params.get(i));
            }

            try (ResultSet rset = pstm.executeQuery()) {
                while (rset.next()) {
                    RegistroAcesso registro = new RegistroAcesso(
                        LocalDateTime.parse(rset.getString("dataHora")),
                        rset.getInt("usuarioId"),
                        rset.getString("status"),
                        rset.getString("origem")
                    );
                    registro.setId(rset.getInt("id"));
                    registro.setNomeUsuario(rset.getString("nomeUsuario"));
                    registros.add(registro);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar registros por período com filtros: " + e.getMessage(), e);
        }
        return registros;
    }

    public boolean exportarParaCSV(List<String[]> dados, File arquivoSaida) {
        // O primeiro item da lista de dados é o cabeçalho
        if (dados == null || dados.isEmpty()) {
            LOGGER.log(Level.WARNING, "Não há dados para exportar.");
            return false;
        }

        String[] cabecalho = dados.get(0);
        List<String[]> registros = dados.subList(1, dados.size());

        List<Map<String, String>> dadosJson = new ArrayList<>();
        for (String[] registro : registros) {
            Map<String, String> mapa = new LinkedHashMap<>();
            for (int i = 0; i < cabecalho.length; i++) {
                mapa.put(cabecalho[i], registro[i]);
            }
            dadosJson.add(mapa);
        }

        Gson gson = new Gson();
        String jsonParaCsv = gson.toJson(dadosJson);

        // Garante que o caminho do arquivo seja absoluto e trate espaços
        String caminhoArquivo = arquivoSaida.getAbsolutePath();

        // Comando para ser executado: echo '[json]' | in2csv > 'caminho/do/arquivo.csv'
        String[] comando = {
            "/bin/bash",
            "-c",
            "echo '" + jsonParaCsv.replace("'", "'\\''") + "' | in2csv > '" + caminhoArquivo + "'"
        };

        try {
            ProcessBuilder pb = new ProcessBuilder(comando);
            Process processo = pb.start();

            int exitCode = processo.waitFor();

            if (exitCode == 0) {
                LOGGER.log(Level.INFO, "Relatório exportado com sucesso para: {0}", caminhoArquivo);
                return true;
            } else {
                // Captura a saída de erro para depuração
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getErrorStream()))) {
                    StringBuilder erro = new StringBuilder();
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        erro.append(linha).append("\n");
                    }
                    LOGGER.log(Level.SEVERE, "Erro ao executar o comando do csvkit (exit code: " + exitCode + "): " + erro.toString());
                }
                return false;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Erro ao exportar relatório para CSV via csvkit: " + e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
}