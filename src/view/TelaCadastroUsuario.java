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
        setSize(1188, 744);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Dados do Novo Funcionário", SwingConstants.CENTER);
        lblTitulo.setBounds(0, 27, 1172, 27);
        lblTitulo.setFont(new Font("Rubik", Font.BOLD, 26));
        getContentPane().add(lblTitulo);

        JPanel painelForm = new JPanel();
        painelForm.setBounds(0, 42, 1172, 610);
        painelForm.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        painelForm.setLayout(null);
        
        JLabel label = new JLabel("Nome Completo:");
        label.setBounds(250, 69, 561, 110);
        label.setFont(new Font("Noto Sans Georgian Bold", Font.PLAIN, 20));
        painelForm.add(label); txtNome = new JTextField(); 
 txtNome.setBounds(458, 110, 514, 35);painelForm.add(txtNome);
        JLabel label_1 = new JLabel("CPF:");
        label_1.setBounds(250, 157, 561, 110);
        label_1.setFont(new Font("Noto Sans Georgian Bold", Font.PLAIN, 20));
        painelForm.add(label_1); txtCpf = new JTextField(); 
 txtCpf.setBounds(458, 198, 514, 35);painelForm.add(txtCpf);
        JLabel label_2 = new JLabel("Email:");
        label_2.setBounds(250, 334, 561, 110);
        label_2.setFont(new Font("Noto Sans Georgian Bold", Font.PLAIN, 20));
        painelForm.add(label_2); txtEmail = new JTextField(); 
 txtEmail.setBounds(458, 287, 514, 35);painelForm.add(txtEmail);
        JLabel label_3 = new JLabel("Cargo:");
        label_3.setBounds(250, 243, 561, 110);
        label_3.setFont(new Font("Noto Sans Georgian Bold", Font.PLAIN, 20));
        painelForm.add(label_3); txtCargo = new JTextField(); 
 txtCargo.setBounds(458, 375, 514, 35);painelForm.add(txtCargo);
        JLabel label_4 = new JLabel("Matrícula:");
        label_4.setBounds(250, 425, 561, 110);
        label_4.setFont(new Font("Noto Sans Georgian Bold", Font.PLAIN, 20));
        painelForm.add(label_4); txtMatricula = new JTextField(); 
 txtMatricula.setBounds(458, 466, 514, 35);painelForm.add(txtMatricula);
        getContentPane().add(painelForm);

        btnSalvar = new JButton("CAPTURAR DIGITAL E SALVAR");
        btnSalvar.setBackground(new Color(255, 255, 255));
        btnSalvar.setBounds(20, 0, 1132, 23);
        btnSalvar.addActionListener(e -> executarCadastro());
        JPanel painelBotao = new JPanel();
        painelBotao.setBounds(0, 667, 1172, 38);
        painelBotao.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        painelBotao.setLayout(null);
        painelBotao.add(btnSalvar);
        getContentPane().add(painelBotao);
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
