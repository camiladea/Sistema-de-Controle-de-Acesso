package model;

public abstract class Usuario {

    protected int id;
    protected String nome;
    protected String cpf;
    protected String email;
    protected byte[] digitalTemplate;
    protected byte[] digitalTemplate1;
    protected byte[] digitalTemplate2;
    protected boolean ativo;

    public Usuario(String nome, String cpf, String email, byte[] digitalTemplate, byte[] digitalTemplate1, byte[] digitalTemplate2) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.digitalTemplate = digitalTemplate;
        this.digitalTemplate1 = digitalTemplate1;
        this.digitalTemplate2 = digitalTemplate2;
        this.ativo = true;
    }

    // Getters e Setters
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
    public byte[] getDigitalTemplate1() { return digitalTemplate1; }
    public void setDigitalTemplate1(byte[] digitalTemplate1) { this.digitalTemplate1 = digitalTemplate1; }
    public byte[] getDigitalTemplate2() { return digitalTemplate2; }
    public void setDigitalTemplate2(byte[] digitalTemplate2) { this.digitalTemplate2 = digitalTemplate2; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}