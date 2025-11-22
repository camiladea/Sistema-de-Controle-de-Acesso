package view;

import controller.TerminalController;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;

public class TelaCadastroUsuario extends JDialog {

    private static final long serialVersionUID = 1L;
    private final transient TerminalController controller;
    private JTextField txtNome, txtEmail, txtCargo;
    private JFormattedTextField txtCpf;
    private JCheckBox chkAdmin;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnSalvar;

    private static final Color COR_FUNDO = new Color(240, 242, 245);
    private static final Color COR_PAINEL_FORMULARIO = Color.WHITE;
    private static final Color COR_BORDA_PAINEL = new Color(220, 223, 228);
    private static final Color COR_TEXTO_PRINCIPAL = new Color(60, 60, 60);
    private static final Color COR_PLACEHOLDER = new Color(150, 150, 150);

    private static final Color COR_BOTAO_PRINCIPAL = new Color(24, 119, 242);
    private static final Color COR_BOTAO_SECUNDARIO = new Color(230, 232, 235);
    private static final Color COR_TEXTO_BOTAO_SECUNDARIO = new Color(40, 40, 40);
    private static final Color COR_TEXTO_BOTAO = Color.WHITE;
    
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 15);

    public TelaCadastroUsuario(Window owner, TerminalController controller) {
        super(owner, "Cadastro de Novo Usuário", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        configurarJanela();
        inicializarComponentes();
        setSize(850, 750);
        setLocationRelativeTo(owner);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 20));
        getContentPane().setBackground(COR_FUNDO);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(30, 30, 20, 30));
    }

    private void inicializarComponentes() {
        add(criarPainelTitulo(), BorderLayout.NORTH);

        JPanel painelCentralWrapper = new JPanel(new GridBagLayout());
        painelCentralWrapper.setBackground(COR_FUNDO);
        painelCentralWrapper.add(criarPainelFormulario());
        add(painelCentralWrapper, BorderLayout.CENTER);
        
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painel.setBackground(COR_FUNDO);
        JLabel lblTitulo = new JLabel("Dados do Novo Funcionário");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_TEXTO_PRINCIPAL);
        painel.add(lblTitulo);
        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_PAINEL_FORMULARIO);
        
        Border bordaLinha = BorderFactory.createLineBorder(COR_BORDA_PAINEL, 1);
        Border bordaPadding = new EmptyBorder(50, 60, 50, 60);
        painel.setBorder(BorderFactory.createCompoundBorder(bordaLinha, bordaPadding));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(25);
        new TextPrompt("Nome Sobrenome", txtNome, COR_PLACEHOLDER);

        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            txtCpf = new JFormattedTextField(cpfFormatter);
        } catch (ParseException e) {
            txtCpf = new JFormattedTextField();
            JOptionPane.showMessageDialog(this, "Erro ao criar máscara de CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        txtEmail = new JTextField();
        new TextPrompt("ex: seu.nome@empresa.com", txtEmail, COR_PLACEHOLDER);
        
        txtCargo = new JTextField();
        new TextPrompt("ex: Desenvolvedor", txtCargo, COR_PLACEHOLDER);
        
        adicionarCampo(painel, gbc, "Nome Completo:", txtNome, 0);
        adicionarCampo(painel, gbc, "CPF:", txtCpf, 1);
        adicionarCampo(painel, gbc, "Email:", txtEmail, 2);
        adicionarCampo(painel, gbc, "Cargo:", txtCargo, 3);

        chkAdmin = new JCheckBox("Administrador");
        chkAdmin.setFont(FONTE_LABEL);
        chkAdmin.setBackground(COR_PAINEL_FORMULARIO);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_START;
        painel.add(chkAdmin, gbc);

        txtLogin = new JTextField();
        new TextPrompt("Login de acesso", txtLogin, COR_PLACEHOLDE
        adicionarCampo(painel, gbc, "Login:", txtLogin, 5);

        txtSenha = new JPasswordField();
        adicionarCampo(painel, gbc, "Senha:", txtSenha, 6);

        // Lógica para habilitar/desabilitar campos de admin
        txtLogin.setEnabled(false);
        txtSenha.setEnabled(false);

        chkAdmin.addActionListener(e -> {
            boolean isSelecionado = chkAdmin.isSelected();
            txtLogin.setEnabled(isSelecionado);
            txtSenha.setEnabled(isSelecionado);
        });

        return painel;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String rotulo, JComponent campo, int yPos) {
       
        JLabel label = new JLabel(rotulo);
        label.setFont(FONTE_LABEL);
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 1; 
        gbc.weightx = 0.2; 
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(15, 10, 15, 10); 
        painel.add(label, gbc);

        campo.setFont(FONTE_CAMPO);
        campo.putClientProperty("JTextField.margin", new Insets(12, 10, 12, 10));
        
        gbc.gridx = 1;
        gbc.gridy = yPos;
        gbc.gridwidth = 1; 
        gbc.weightx = 0.8; 
        gbc.anchor = GridBagConstraints.LINE_START; 
        painel.add(campo, gbc);
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        painel.setBorder(new EmptyBorder(15, 0, 0, 0));
        painel.setBackground(COR_FUNDO);

        JButton btnCancelar = new JButton("CANCELAR");
        configurarBotao(btnCancelar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO, new EmptyBorder(12, 40, 12, 40));
        btnCancelar.addActionListener(e -> dispose());

        btnSalvar = new JButton("CAPTURAR DIGITAL E SALVAR");
        configurarBotao(btnSalvar, COR_BOTAO_PRINCIPAL, COR_TEXTO_BOTAO, new EmptyBorder(12, 40, 12, 40));
        btnSalvar.addActionListener(e -> executarCadastro());

        painel.add(btnCancelar);
        painel.add(btnSalvar);
        return painel;
    }

    private void configurarBotao(JButton botao, Color corFundo, Color corTexto, EmptyBorder borda) {
        botao.setFont(FONTE_BOTAO);
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFocusPainted(false);
        botao.setBorder(borda);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void executarCadastro() {
        String cpfSemMascara = txtCpf.getText().replaceAll("[^0-9]", "");

        if (txtNome.getText().trim().isEmpty() || cpfSemMascara.trim().isEmpty() || cpfSemMascara.length() < 11) {
            JOptionPane.showMessageDialog(this, "Nome e CPF são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "O email informado parece ser inválido.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isAdmin = chkAdmin.isSelected();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (isAdmin && (login.isEmpty() || senha.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Login e Senha são obrigatórios para administradores.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "A seguir, será solicitada a captura da digital.", "Próximo Passo", JOptionPane.INFORMATION_MESSAGE);

        btnSalvar.setEnabled(false);
        btnSalvar.setText("PROCESSANDO...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controller.solicitarCadastroNovoFuncionario(
                    txtNome.getText(), cpfSemMascara, email, txtCargo.getText(),
                    isAdmin, login, senha
                );
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(TelaCadastroUsuario.this, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(TelaCadastroUsuario.this, "Falha no cadastro. Verifique se o CPF ou Login já existe, ou se há um problema com o leitor biométrico.", "Erro", JOptionPane.ERROR_MESSAGE);
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


class TextPrompt extends JLabel implements DocumentListener, FocusListener {

    private static final long serialVersionUID = 1L;
    private final JTextField textField;

    public TextPrompt(String text, JTextField textField, Color foregroundColor) {
        this.textField = textField;
        setText(text);
        setFont(textField.getFont().deriveFont(Font.ITALIC));
        setForeground(foregroundColor);
        setBorder(new EmptyBorder(textField.getInsets()));
        
        textField.setLayout(new BorderLayout());
        textField.add(this);

        textField.getDocument().addDocumentListener(this);
        textField.addFocusListener(this);
        
        checkForPrompt();
    }

    private void checkForPrompt() {
        setVisible(textField.getText().isEmpty());
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (isVisible()) {
            setVisible(false);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        checkForPrompt();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        setVisible(false);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}