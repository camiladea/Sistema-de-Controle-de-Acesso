package view;

import controller.TerminalController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaEdicaoUsuario extends JDialog {

    private static final long serialVersionUID = 1L;

    private final TerminalController controller;
    private final Usuario usuario;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtEmail;
    private JCheckBox chkAtivo;

    public TelaEdicaoUsuario(Window owner, TerminalController controller, Usuario usuario) {
        super(owner, "Editar Usuário", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.usuario = usuario;

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Painel dos campos
        JPanel painelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Nome:"));
        txtNome = new JTextField(usuario.getNome());
        painelCampos.add(txtNome);

        painelCampos.add(new JLabel("CPF:"));
        txtCpf = new JTextField(usuario.getCpf());
        txtCpf.setEditable(false); // normalmente o CPF não é editável
        painelCampos.add(txtCpf);

        painelCampos.add(new JLabel("Email:"));
        txtEmail = new JTextField(usuario.getEmail());
        painelCampos.add(txtEmail);

        painelCampos.add(new JLabel("Ativo:"));
        chkAtivo = new JCheckBox();
        chkAtivo.setSelected(usuario.isAtivo());
        painelCampos.add(chkAtivo);

        add(painelCampos, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void salvar() {
        usuario.setNome(txtNome.getText());
        usuario.setEmail(txtEmail.getText());
        usuario.setAtivo(chkAtivo.isSelected());

        try {
            controller.editarUsuario(usuario); // não retorna nada
            JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar usuário: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
