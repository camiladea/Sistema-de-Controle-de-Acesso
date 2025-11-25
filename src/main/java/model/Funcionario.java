package model;

public class Funcionario extends Usuario {
    private String cargo;

    public Funcionario(String nome, String cpf, String email, byte[] digitalTemplate, byte[] digitalTemplate1, byte[] digitalTemplate2, String cargo) {
        super(nome, cpf, email, digitalTemplate, digitalTemplate1, digitalTemplate2);
        this.cargo = cargo;
    }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}