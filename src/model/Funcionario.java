package model;

public class Funcionario extends Usuario {

    private String cargo;

    /**
     * @param nome       Nome completo do funcionário.
     * @param cpf        CPF do funcionário.
     * @param email      E-mail de contato.
     * @param digitalFIR Template biométrico da impressão digital.
     * @param cargo      Cargo que o funcionário ocupa.
     */
    public Funcionario(String nome, String cpf, String email, String digitalFIR, String cargo) {
        super(nome, cpf, email, digitalFIR); // [cite: 311]
        this.cargo = cargo; // [cite: 311]
    }

    public String getCargo() {
        return cargo; // [cite: 313]
    }

    public void setCargo(String cargo) {
        this.cargo = cargo; // [cite: 314]
    }
}