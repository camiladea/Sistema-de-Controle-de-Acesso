package view;

import controller.TerminalController;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener; // Importe o FocusListener
import javax.swing.text.MaskFormatter;

public class TelaCadastroUsuario extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient TerminalController controller;
    private JTextField txtNome, txtEmail, txtCargo, txtMatricula;
    private JFormattedTextField txtCpf; 
    private JButton btnSalvar;

    private static final Color COR_FUNDO = new Color(240, 242, 245);
    private static final Color COR_PAINEL_FORMULARIO = Color.WHITE;
    private static final Color COR_BOTAO_PRINCIPAL = new Color(24, 119, 242);
    // --- NOVO CÓDIGO ---
    private static final Color COR_BOTAO_SECUNDARIO = new Color(230, 232, 235);
    private static final Color COR_TEXTO_BOTAO_SECUNDARIO = new Color(40, 40, 40);
    // --- FIM DO NOVO CÓDIGO ---
    private static final Color COR_TEXTO_BOTAO = Color.WHITE;
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

    public TelaCadastroUsuario(Window owner, TerminalController controller) {
        super(owner, "Cadastro de Novo Usuário", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        
        configurarJanela();
        inicializarComponentes();

        setSize(1200, 800);
        setLocationRelativeTo(owner);
    }
    
    private void configurarJanela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 15));
        getContentPane().setBackground(COR_FUNDO);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void inicializarComponentes() {
        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painel.setBackground(COR_FUNDO);
        JLabel lblTitulo = new JLabel("Dados do Novo Funcionário");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(new Color(60, 60, 60));
        painel.add(lblTitulo);
        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_PAINEL_FORMULARIO);
        painel.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(30);
        TextPrompt placeholderNome = new TextPrompt("Nome Sobrenome", txtNome);
        placeholderNome.setForeground(new Color(150, 150, 150));
        
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            txtCpf = new JFormattedTextField(cpfFormatter);
        } catch (ParseException e) {
            txtCpf = new JFormattedTextField();
            JOptionPane.showMessageDialog(this, "Erro ao criar máscara de CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        txtEmail = new JTextField();
        txtCargo = new JTextField();
        txtMatricula = new JTextField();

        adicionarCampo(painel, gbc, "Nome Completo:", txtNome, 0);
        adicionarCampo(painel, gbc, "CPF:", txtCpf, 1);
        adicionarCampo(painel, gbc, "Email:", txtEmail, 2);
        adicionarCampo(painel, gbc, "Cargo:", txtCargo, 3);
        adicionarCampo(painel, gbc, "Matrícula:", txtMatricula, 4);
        return painel;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String rotulo, JComponent campo, int yPos) {
        JLabel label = new JLabel(rotulo);
        label.setFont(FONTE_LABEL);
        gbc.gridx = 0; gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.2;
        painel.add(label, gbc);

        campo.setFont(FONTE_CAMPO);
        if (campo instanceof JTextField || campo instanceof JFormattedTextField) {
             // Esta é uma forma mais moderna e recomendada de adicionar margens
            campo.putClientProperty("JTextField.margin", new Insets(5, 5, 5, 5));
        }

        gbc.gridx = 1; gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.8;
        painel.add(campo, gbc);
    }
    
    // --- MÉTODO MODIFICADO ---
    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // MODIFICADO para alinhar à direita
        painel.setBorder(new EmptyBorder(15, 0, 0, 0));
        painel.setBackground(COR_FUNDO);

        // --- Botão de Cancelar (NOVO) ---
        JButton btnCancelar = new JButton("CANCELAR");
        configurarBotao(btnCancelar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO, new Font("Segoe UI", Font.BOLD, 14), new EmptyBorder(12, 30, 12, 30));
        btnCancelar.addActionListener(e -> dispose()); // Ação para fechar a janela
        
        // --- Botão Salvar (Existente) ---
        btnSalvar = new JButton("CAPTURAR DIGITAL E SALVAR");
        configurarBotao(btnSalvar, COR_BOTAO_PRINCIPAL, COR_TEXTO_BOTAO, new Font("Segoe UI", Font.BOLD, 14), new EmptyBorder(12, 30, 12, 30));
        btnSalvar.addActionListener(e -> executarCadastro());

        painel.add(btnCancelar); // Adiciona o botão de cancelar primeiro
        painel.add(btnSalvar);
        return painel;
    }

    // --- NOVO MÉTODO AUXILIAR ---
    private void configurarBotao(JButton botao, Color corFundo, Color corTexto, Font fonte, EmptyBorder borda) {
        botao.setFont(fonte);
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFocusPainted(false);
        botao.setBorder(borda);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void executarCadastro() {
        String cpfSemMascara = txtCpf.getText().replaceAll("[^0-9]", "");
        if (txtNome.getText().trim().isEmpty() || cpfSemMascara.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "O email informado é inválido. Certifique-se de que ele contém o caractere '@'.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "A seguir, será solicitada a captura da digital.", "Próximo Passo", JOptionPane.INFORMATION_MESSAGE);
        
        btnSalvar.setEnabled(false);
        btnSalvar.setText("PROCESSANDO...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controller.solicitarCadastroNovoFuncionario(txtNome.getText(), cpfSemMascara, email, txtCargo.getText(), txtMatricula.getText());
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

/**
 * Classe utilitária para criar um texto de placeholder (prompt) em campos de texto.
 */
// --- CORREÇÃO APLICADA AQUI ---
class TextPrompt extends JLabel implements DocumentListener, FocusListener {
    private static final long serialVersionUID = 1L;
    private final JTextField textField;

    public TextPrompt(String text, JTextField textField) {
        this.textField = textField;
        setText(text);
        setFont(textField.getFont().deriveFont(Font.ITALIC));
        setForeground(Color.GRAY);
        setBorder(new EmptyBorder(textField.getInsets()));
        
        textField.add(this, 0);
        textField.getDocument().addDocumentListener(this);
        // Agora this é um FocusListener válido
        textField.addFocusListener(this);
        
        checkForPrompt();
    }

    private void checkForPrompt() {
        setVisible(textField.getText().isEmpty());
    }

    @Override
    public void focusGained(FocusEvent e) {
        setVisible(false);
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
        // Não usado para texto plano
    }
}