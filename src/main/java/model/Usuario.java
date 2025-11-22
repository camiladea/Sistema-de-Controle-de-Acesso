package model;

public abstract class Usuario {

    protected int id;
    protected String nome;
    protected String cpf;
    protected String email;
    protected byte[] digitalTemplate; // CORRETO: byte[], não String
    protected boolean ativo;

    public Usuario(String nome, String cpf, String email, byte[] digitalTemplate) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.digitalTemplate = digitalTemplate;
        this.ativo = true;
    }

    // Getters e Setters (get/set para digitalTemplate devem usar byte[])
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public byte[] getDigitalTemplate() { return digitalTemplate; }
    public void setDigitalTemplate(byte[] digitalTemplate) { this.digitalTemplate = digitalTemplate; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}