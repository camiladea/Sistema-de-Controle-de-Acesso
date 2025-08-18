package model;

public class Funcionario extends Usuario {
    private String cargo;
    private String matricula;

    public Funcionario(String nome, String cpf, String email, String digitalHash, String cargo, String matricula) {
        super(nome, cpf, email, digitalHash);
        this.cargo = cargo;
        this.matricula = matricula;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
}