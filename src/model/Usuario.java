package model;

/**
 * Representa um usuário do sistema, incluindo dados biométricos.
 */
public class Usuario {

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String tipoUsuario;
    private String cargo;
    private String login;
    private String senhaHash;
    private boolean ativo;
    private byte[] digitalTemplate;

    public Usuario() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public byte[] getDigitalTemplate() { return digitalTemplate; }
    public void setDigitalTemplate(byte[] digitalTemplate) { this.digitalTemplate = digitalTemplate; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", login='" + login + '\'' +
                ", ativo=" + ativo +
                ", hasDigital=" + (digitalTemplate != null ? "sim" : "não") +
                '}';
    }
}
