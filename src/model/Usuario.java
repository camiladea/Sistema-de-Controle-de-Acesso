package model;

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
    private byte[] digitalTemplate; // stored as BLOB

    public Usuario() {}

    // Existing constructor (without fingerprint)
    public Usuario(String nome, String cpf, String email, String tipoUsuario, String cargo, String login, String senhaHash) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.cargo = cargo;
        this.login = login;
        this.senhaHash = senhaHash;
        this.ativo = true;
    }

    // ✅ New constructor (for biometric registration)
    public Usuario(String nome, String cpf, String email, String tipoUsuario, String cargo, String login, String senhaHash, byte[] digitalTemplate) {
        this(nome, cpf, email, tipoUsuario, cargo, login, senhaHash);
        this.digitalTemplate = digitalTemplate;
    }

    // Getters and setters
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
}
