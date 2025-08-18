package view;

import controller.TerminalController;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import model.Administrador;

public class TelaLoginAdmin extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final transient TerminalController controller;
    private final JPasswordField txtSenha;
    private boolean loginSucedido = false;

    public TelaLoginAdmin(JFrame parent, TerminalController controller) {
        super(parent, "Autenticação de Administrador", true);
        this.controller = controller;
        setSize(400, 220);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(50, 50, 50));
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Autorização Necessária", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(new Color(50, 50, 50));
        painelForm.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.gridx = 0; painelForm.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1; JTextField txtLogin = new JTextField("admin"); painelForm.add(txtLogin, gbc);
        gbc.gridy = 1; gbc.gridx = 0; painelForm.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; txtSenha = new JPasswordField(); painelForm.add(txtSenha, gbc);
        add(painelForm, BorderLayout.CENTER);

        JButton btnLogin = new JButton("AUTORIZAR");
        btnLogin.addActionListener(e -> {
            Optional<Administrador> adminOpt = controller.solicitarAutenticacaoAdmin(txtLogin.getText(), new String(txtSenha.getPassword()));
            if (adminOpt.isPresent()) {
                loginSucedido = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login ou Senha inválidos.", "Falha na Autenticação", JOptionPane.ERROR_MESSAGE);
                txtSenha.setText("");
                txtLogin.requestFocus();
            }
        });
        
        JPanel painelBotao = new JPanel(new BorderLayout());
        painelBotao.setBackground(new Color(50, 50, 50));
        painelBotao.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        painelBotao.add(btnLogin, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnLogin);
    }

    public boolean isLoginSucedido() { return loginSucedido; }

	public TerminalController getController() {
		return controller;
	}
}