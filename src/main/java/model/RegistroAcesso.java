package model;

import java.time.LocalDateTime;

public class RegistroAcesso {
    private int id;
    private LocalDateTime dataHora;
    private int usuarioId;
    private String nomeUsuario;
    private String status;
    private String origem;

    public RegistroAcesso(LocalDateTime dataHora, int usuarioId, String status, String origem) {
        if (dataHora == null || status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Data/Hora e Status são campos obrigatórios.");
        }
        this.dataHora = dataHora;
        this.usuarioId = usuarioId;
        this.status = status;
        this.origem = origem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }
}