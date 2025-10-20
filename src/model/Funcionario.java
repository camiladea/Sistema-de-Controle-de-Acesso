package model;

public class Funcionario extends Usuario {
    private String cargo;

    public Funcionario(String nome, String cpf, String email, byte[] digitalTemplate, String cargo) {
        super(nome, cpf, email, digitalTemplate); // CORRETO: Passando byte[]
        this.cargo = cargo;
    }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}