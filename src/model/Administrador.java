package model;

public class Administrador extends Usuario {

    public Administrador(String nome, String cpf, String email, String cargo, String login, String senhaHash, byte[] digitalTemplate) {
        super(nome, cpf, email, "Administrador", cargo, login, senhaHash, digitalTemplate);
    }

    public Administrador() {
        super();
        this.setTipoUsuario("Administrador");
    }
}