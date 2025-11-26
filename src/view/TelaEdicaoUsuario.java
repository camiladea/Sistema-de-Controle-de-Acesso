package view;

import controller.TerminalController;
import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;
import javax.swing.border.Border;
import model.Usuario;

public class TelaEdicaoUsuario extends JDialog {

    private static final long serialVersionUID = 1L;

    private final TerminalController controller;
    private final Usuario usuario;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtEmail;
    private JTextField txtCargo; // Adicionado de volta se houver no seu objeto
    private JCheckBox chkAtivo;

    // --- NOVOS CAMPOS PARA ADMINISTRAÇÃO ---
    private JCheckBox chkAdmin;
    private JLabel lblLogin;
    private JTextField txtLogin;
    private JLabel lblSenha;
    private JPasswordField txtSenha;
    // ---------------------------------------

    private static final Color COR_FUNDO = new Color(245, 245, 245);
    private static final Color COR_PAINEL = new Color(255, 255, 255);
    private static final Color COR_BOTAO_SALVAR = new Color(50, 150, 255);
    private static final Color COR_TEXTO_BOTAO = Color.WHITE;
    private static final Font FONTE_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

    public TelaEdicaoUsuario(Window owner, TerminalController controller, Usuario usuario) {
        super(owner, "Editar Usuário", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.usuario = usuario;

        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setSize(500, 600); // Altura aumentada para caber os novos campos
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // PAINEL PRINCIPAL
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBackground(COR_PAINEL);

        Border bordaExterna = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        Border bordaInterna = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        painelCampos.setBorder(BorderFactory.createCompoundBorder(bordaInterna, bordaExterna));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // 1. NOME
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(FONTE_LABEL);
        painelCampos.add(lblNome, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNome = new JTextField(usuario.getNome());
        txtNome.setFont(FONTE_CAMPO);
        painelCampos.add(txtNome, gbc);

        // 2. CPF (Leitura)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblCpf = new JLabel("CPF:");
        lblCpf.setFont(FONTE_LABEL);
        painelCampos.add(lblCpf, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtCpf = new JTextField(usuario.getCpf());
        txtCpf.setEditable(false);
        txtCpf.setFont(FONTE_CAMPO);
        txtCpf.setBackground(new Color(230, 230, 230));
        painelCampos.add(txtCpf, gbc);

        // 3. EMAIL
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(FONTE_LABEL);
        painelCampos.add(lblEmail, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(usuario.getEmail());
        txtEmail.setFont(FONTE_CAMPO);
        painelCampos.add(txtEmail, gbc);

        // 4. CARGO
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblCargo = new JLabel("Cargo:");
        lblCargo.setFont(FONTE_LABEL);
        painelCampos.add(lblCargo, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtCargo = new JTextField(usuario.getCargo());
        txtCargo.setFont(FONTE_CAMPO);
        painelCampos.add(txtCargo, gbc);

        // 5. ATIVO
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblAtivo = new JLabel("Status:");
        lblAtivo.setFont(FONTE_LABEL);
        painelCampos.add(lblAtivo, gbc);

        gbc.gridx = 1;
        chkAtivo = new JCheckBox("Usuário Ativo");
        chkAtivo.setFont(FONTE_CAMPO);
        chkAtivo.setBackground(COR_PAINEL);
        chkAtivo.setSelected(usuario.isAtivo());
        painelCampos.add(chkAtivo, gbc);

        // --- SEÇÃO ADMINISTRADOR ---

        // Verifica se já é admin
        String tipo = usuario.getTipoUsuario();
        boolean isAdm = tipo != null && (tipo.equalsIgnoreCase("ADMIN") || tipo.equalsIgnoreCase("ADMINISTRADOR"));

        // 6. Checkbox Admin
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Ocupa toda a linha
        chkAdmin = new JCheckBox("Conceder Acesso de Administrador");
        chkAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkAdmin.setForeground(new Color(0, 102, 204));
        chkAdmin.setBackground(COR_PAINEL);
        chkAdmin.setSelected(isAdm);
        painelCampos.add(chkAdmin, gbc);

        // 7. Login
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        lblLogin = new JLabel("Login Admin:");
        lblLogin.setFont(FONTE_LABEL);
        painelCampos.add(lblLogin, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtLogin = new JTextField(usuario.getLogin() != null ? usuario.getLogin() : "");
        txtLogin.setFont(FONTE_CAMPO);
        painelCampos.add(txtLogin, gbc);

        // 8. Senha
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0;
        lblSenha = new JLabel("Nova Senha:");
        lblSenha.setFont(FONTE_LABEL);
        painelCampos.add(lblSenha, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtSenha = new JPasswordField(20);
        txtSenha.setFont(FONTE_CAMPO);
        painelCampos.add(txtSenha, gbc);

        // Lógica de Visibilidade dos campos Login/Senha
        configurarVisibilidadeAdmin(isAdm);

        chkAdmin.addItemListener(e -> {
            boolean selecionado = (e.getStateChange() == ItemEvent.SELECTED);
            configurarVisibilidadeAdmin(selecionado);
        });

        // Adiciona ScrollPane caso a tela fique pequena
        JScrollPane scrollPane = new JScrollPane(painelCampos);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // BOTÕES
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoes.setBackground(COR_FUNDO);

        JButton btnSalvar = new JButton("Salvar Alterações");
        configurarBotao(btnSalvar, COR_BOTAO_SALVAR);
        btnSalvar.addActionListener(e -> salvar());

        JButton btnCancelar = new JButton("Cancelar");
        configurarBotao(btnCancelar, Color.GRAY);
        btnCancelar.addActionListener(e -> dispose());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnSalvar);
    }

    private void configurarVisibilidadeAdmin(boolean visivel) {
        lblLogin.setVisible(visivel);
        txtLogin.setVisible(visivel);
        lblSenha.setVisible(visivel);
        txtSenha.setVisible(visivel);

        // Se desmarcar, limpa os campos para evitar envio acidental
        if (!visivel) {
            txtLogin.setText("");
            txtSenha.setText("");
        }

        revalidate();
        repaint();
    }

    private void configurarBotao(JButton botao, Color corFundo) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setBackground(corFundo);
        botao.setForeground(COR_TEXTO_BOTAO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void salvar() {
        // Validação básica
        if (txtNome.getText().trim().isEmpty()) {
            mostrarErro("O Nome é obrigatório.");
            return;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarErro("O email informado parece ser inválido.");
            return;
        }

        // --- LÓGICA DE ADMIN ---
        boolean isAdmin = chkAdmin.isSelected();
        String login = null;
        String senha = null;

        if (isAdmin) {
            login = txtLogin.getText().trim();
            senha = new String(txtSenha.getPassword());

            if (login.isEmpty()) {
                mostrarErro("Para definir um Administrador, o campo Login é obrigatório.");
                return;
            }

            // Valida se é um NOVO admin sem senha
            // (Verifica se o usuario atual ja era admin. Se não era, senha é obrigatória)
            boolean eraAdmin = usuario.getTipoUsuario() != null && usuario.getTipoUsuario().equalsIgnoreCase("ADMIN");
            if (!eraAdmin && senha.isEmpty()) {
                mostrarErro("Para um novo Administrador, é necessário definir uma senha inicial.");
                return;
            }
        }

        // Prepara dados para o Controller
        // Se a senha estiver vazia, passamos null ou string vazia, e o Service deve
        // decidir manter a antiga.
        String finalSenha = (senha != null && !senha.isEmpty()) ? senha : null;

        // Atualiza o objeto local
        usuario.setNome(txtNome.getText());
        usuario.setCpf(txtCpf.getText()); // Geralmente CPF não muda, mas está aqui
        usuario.setEmail(email);
        usuario.setCargo(txtCargo.getText());
        usuario.setAtivo(chkAtivo.isSelected());

        try {
            // Chama o controller passando os dados de segurança separados
            controller.editarUsuario(usuario, isAdmin, login, finalSenha);

            JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            dispose();

        } catch (Exception e) {
            mostrarErro("Erro ao atualizar: " + e.getMessage());
        }
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}