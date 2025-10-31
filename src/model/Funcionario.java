package model;

public class Funcionario extends Usuario {

    public Funcionario(String nome, String cpf, String email, String cargo, String login, String senhaHash, byte[] digitalTemplate) {
        super(nome, cpf, email, "Funcionario", cargo, login, senhaHash, digitalTemplate);
    }

    public Funcionario() {
        super();
        this.setTipoUsuario("Funcionario");
    }
}
