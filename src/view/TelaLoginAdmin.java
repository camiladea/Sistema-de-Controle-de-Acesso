package view;

import controller.TerminalController;
import java.awt.*;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import model.Administrador;

public class TelaLoginAdmin extends JDialog {

    private static final long serialVersionUID = 1L;

    // As variáveis originais permanecem as mesmas
    private final transient TerminalController controller;
    private JTextField txtLogin; 
    private JPasswordField txtSenha;
    private boolean loginSucedido = false;

    // --- PALETA DE CORES E FONTES (sem alteração) ---
    private static final Color COR_FUNDO_ESCURO = new Color(45, 45, 45);
    private static final Color COR_INPUT = new Color(70, 70, 70);
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_TEXTO_PLACEHOLDER = Color.WHITE;
    private static final Color COR_BOTAO_ACAO = new Color(24, 119, 242);
    private static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);

    public TelaLoginAdmin(JFrame parent, TerminalController controller) {
        super(parent, "Autenticação de Administrador", true);
        this.controller = controller;

        configurarJanela();
        inicializarComponentes();

        // --- REMOVA AS LINHAS ABAIXO DAQUI ---
        // pack(); 
        // setLocationRelativeTo(parent); 
    
    }

    private void configurarJanela() {
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO_ESCURO);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        setSize(400, 430); 
        setLocationRelativeTo(getParent()); // Centraliza usando getParent()
    }

    private void inicializarComponentes() {
        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO_ESCURO);
        painel.setBorder(new EmptyBorder(10, 0, 15, 0));

        JLabel lblIcone = new JLabel("🔒");
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblIcone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Autorização Necessária");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(new EmptyBorder(10, 0, 0, 0));

        painel.add(lblIcone);
        painel.add(lblTitulo);
        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_FUNDO_ESCURO);
        painel.setBorder(new EmptyBorder(5, 20, 5, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        JLabel lblLogin = new JLabel("Login:");
        estilizarLabel(lblLogin);
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.2;
        painel.add(lblLogin, gbc);

        txtLogin = new JTextField("admin");
        estilizarCampoDeTexto(txtLogin);
        gbc.gridx = 1; gbc.weightx = 0.8;
        painel.add(txtLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        estilizarLabel(lblSenha);
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.2;
        painel.add(lblSenha, gbc);

        txtSenha = new JPasswordField();
        estilizarCampoDeTexto(txtSenha);
        gbc.gridx = 1; gbc.weightx = 0.8;
        painel.add(txtSenha, gbc);
        
        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_FUNDO_ESCURO);
        painel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JButton btnLogin = new JButton("AUTORIZAR");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(COR_BOTAO_ACAO);
        btnLogin.setForeground(COR_TEXTO);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(new EmptyBorder(12, 0, 12, 0));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> autenticar());
        painel.add(btnLogin, BorderLayout.CENTER);
        
        getRootPane().setDefaultButton(btnLogin);
        
        return painel;
    }
    
    private void estilizarLabel(JLabel label) {
        label.setFont(FONTE_PADRAO);
        label.setForeground(COR_TEXTO_PLACEHOLDER);
    }
    
    private void estilizarCampoDeTexto(JTextField campo) {
        campo.setFont(FONTE_PADRAO);
        campo.setBackground(COR_INPUT);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(Color.WHITE);
        Border bordaExterna = BorderFactory.createLineBorder(new Color(90, 90, 90));
        Border bordaInterna = new EmptyBorder(8, 10, 8, 10);
        campo.setBorder(BorderFactory.createCompoundBorder(bordaExterna, bordaInterna));
    }

    private void autenticar() {
        Optional<Administrador> adminOpt = controller.solicitarAutenticacaoAdmin(
            txtLogin.getText(), new String(txtSenha.getPassword()));

        if (adminOpt.isPresent()) {
            loginSucedido = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login ou Senha inválidos.",
                "Falha na Autenticação", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    public boolean isLoginSucedido() {
        return loginSucedido;
    }
}