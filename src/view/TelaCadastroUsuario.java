package view;

import controller.TerminalController;
import javax.swing.*;
import java.awt.*;

public class TelaCadastroUsuario extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final transient TerminalController controller;
    private final JTextField txtNome, txtCpf, txtEmail, txtCargo, txtMatricula;
    private final JButton btnSalvar;

    public TelaCadastroUsuario(Window owner, TerminalController controller) {
        super(owner, "Cadastro de Novo Usuário", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        setSize(450, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(15, 15));

        JLabel lblTitulo = new JLabel("Dados do Novo Funcionário", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        painelForm.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        painelForm.add(new JLabel("Nome Completo:")); txtNome = new JTextField(); painelForm.add(txtNome);
        painelForm.add(new JLabel("CPF:")); txtCpf = new JTextField(); painelForm.add(txtCpf);
        painelForm.add(new JLabel("Email:")); txtEmail = new JTextField(); painelForm.add(txtEmail);
        painelForm.add(new JLabel("Cargo:")); txtCargo = new JTextField(); painelForm.add(txtCargo);
        painelForm.add(new JLabel("Matrícula:")); txtMatricula = new JTextField(); painelForm.add(txtMatricula);
        add(painelForm, BorderLayout.CENTER);

        btnSalvar = new JButton("CAPTURAR DIGITAL E SALVAR");
        btnSalvar.addActionListener(e -> executarCadastro());
        JPanel painelBotao = new JPanel(new BorderLayout());
        painelBotao.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        painelBotao.add(btnSalvar, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
    }

    private void executarCadastro() {
        if (txtNome.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "A seguir, será solicitada a captura da digital.", "Próximo Passo", JOptionPane.INFORMATION_MESSAGE);
        
        btnSalvar.setEnabled(false);
        btnSalvar.setText("PROCESSANDO...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controller.solicitarCadastroNovoFuncionario(txtNome.getText(), txtCpf.getText(), txtEmail.getText(), txtCargo.getText(), txtMatricula.getText());
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(TelaCadastroUsuario.this, "Funcionário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(TelaCadastroUsuario.this, "Falha no cadastro. Verifique se o CPF já existe.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaCadastroUsuario.this, "Erro: " + ex.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSalvar.setEnabled(true);
                    btnSalvar.setText("CAPTURAR DIGITAL E SALVAR");
                }
            }
        }.execute();
    }
}