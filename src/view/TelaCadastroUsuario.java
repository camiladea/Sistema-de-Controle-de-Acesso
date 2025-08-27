package view;

import controller.TerminalController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaCadastroUsuario extends JDialog {

    private static final long serialVersionUID = 1L;

    // As variáveis de controle e componentes permanecem as mesmas
    private final transient TerminalController controller;
    private JTextField txtNome, txtCpf, txtEmail, txtCargo, txtMatricula;
    private JButton btnSalvar;

    // --- paleta de cores E FONTES PARA UM DESIGN MODERNO ---
    private static final Color COR_FUNDO = new Color(240, 242, 245);
    private static final Color COR_PAINEL_FORMULARIO = Color.WHITE;
    private static final Color COR_BOTAO_PRINCIPAL = new Color(24, 119, 242);
    private static final Color COR_TEXTO_BOTAO = Color.WHITE;
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

    public TelaCadastroUsuario(Window owner, TerminalController controller) {
        super(owner, "Cadastro de Novo Usuário", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        
        configurarJanela();
        inicializarComponentes();

        // --- ALTERAÇÃO 1: DEFINIR TAMANHO GRANDE E CENTRALIZAR NO FINAL ---
        // Removemos o pack() e definimos um tamanho baseado na tela do computador.

        // Pega as dimensões do monitor
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Define o tamanho da janela como 85% da largura e 80% da altura do monitor
        int largura = (int) (screenSize.width * 0.85);
        int altura = (int) (screenSize.height * 0.80);
        setSize(largura, altura);

        // AGORA SIM: Centraliza a janela APÓS ela ter seu tamanho definido.
        setLocationRelativeTo(owner);
    }
    
    /**
     * Define as propriedades principais da janela.
     */
    private void configurarJanela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // A linha setLocationRelativeTo foi MOVIDA para o final do construtor
        setLayout(new BorderLayout(0, 15));
        getContentPane().setBackground(COR_FUNDO);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    /**
     * Cria e organiza todos os componentes visuais na tela. (Sem alterações aqui)
     */
    private void inicializarComponentes() {
        // 1. Painel do Título
        JPanel painelTitulo = criarPainelTitulo();
        add(painelTitulo, BorderLayout.NORTH);

        // 2. Painel do Formulário
        JPanel painelFormulario = criarPainelFormulario();
        add(painelFormulario, BorderLayout.CENTER);

        // 3. Painel de Botões
        JPanel painelBotoes = criarPainelBotoes();
        add(painelBotoes, BorderLayout.SOUTH);
    }

    /**
     * Cria o painel superior com o título da tela. (Sem alterações aqui)
     */
    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painel.setBackground(COR_FUNDO);
        
        JLabel lblTitulo = new JLabel("Dados do Novo Funcionário");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(new Color(60, 60, 60));
        painel.add(lblTitulo);
        
        return painel;
    }

    /**
     * Cria o painel central com os campos de entrada de dados. (Sem alterações aqui)
     */
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_PAINEL_FORMULARIO);
        painel.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8); // Aumentei o espaçamento vertical
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(30); // Aumentei o tamanho sugerido do campo
        txtCpf = new JTextField();
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
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.2; // Aumentei um pouco o peso do rótulo
        painel.add(label, gbc);

        campo.setFont(FONTE_CAMPO);
        // --- ALTERAÇÃO 2: MELHORAR O PREENCHIMENTO DO CAMPO ---
        // Adiciona um espaçamento interno nos campos de texto para o texto não ficar colado na borda.
        if (campo instanceof JTextField) {
            ((JTextField) campo).setMargin(new Insets(5, 5, 5, 5));
        }

        gbc.gridx = 1;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.8;
        painel.add(campo, gbc);
    }
    
    /**
     * Cria o painel inferior com o botão de salvar. (Sem alterações aqui)
     */
    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painel.setBorder(new EmptyBorder(15, 0, 0, 0)); // Adiciona espaço acima do botão
        painel.setBackground(COR_FUNDO);
        
        btnSalvar = new JButton("CAPTURAR DIGITAL E SALVAR");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Aumentei a fonte do botão
        btnSalvar.setBackground(COR_BOTAO_PRINCIPAL);
        btnSalvar.setForeground(COR_TEXTO_BOTAO);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorder(new EmptyBorder(15, 40, 15, 40)); // Aumentei o padding do botão
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSalvar.addActionListener(e -> executarCadastro());
        painel.add(btnSalvar);

        return painel;
    }

    /**
     * Ação de cadastro. A lógica original com SwingWorker foi mantida intacta.
     */
    private void executarCadastro() {
        //... (nenhuma alteração nesta parte)
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