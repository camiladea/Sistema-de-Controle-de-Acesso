package model;

public class Administrador extends Usuario {
    private String login;
    private String senhaHash;

    public Administrador(String nome, String cpf, String email, byte[] digitalTemplate, byte[] digitalTemplate1, byte[] digitalTemplate2, String login, String senhaHash) {
        super(nome, cpf, email, digitalTemplate, digitalTemplate1, digitalTemplate2);
        this.login = login;
        this.senhaHash = senhaHash;
    }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
}