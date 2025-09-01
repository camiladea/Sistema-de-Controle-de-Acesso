package model;

/**
 * Classe abstrata que representa a entidade base de um usuário no sistema.
 * Contém os atributos e métodos comuns a todos os tipos de usuários.
 * Critérios Atendidos: 1, 2, 3, 4, 5, 8.
 */
public abstract class Usuario {

    protected int id;
    protected String nome;
    protected String cpf;
    protected String email;
    protected String digitalFIR; // <-- Alterado
    protected boolean ativo;

    /**
     * Construtor para inicializar um objeto Usuario.
     * @param nome Nome completo do usuário. Não pode ser nulo ou vazio.
     * @param cpf CPF do usuário. Essencial para a lógica de negócio.
     * @param email E-mail de contato.
     * @param digitalFIR O template biométrico da impressão digital.
     */
    public Usuario(String nome, String cpf, String email, String digitalFIR) {
        if (nome == null || nome.trim().isEmpty() || cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e CPF são campos obrigatórios.");
        }
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.digitalFIR = digitalFIR;
        this.ativo = true;
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDigitalFIR() { return digitalFIR; }
    public void setDigitalFIR(String digitalFIR) { this.digitalFIR = digitalFIR; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
