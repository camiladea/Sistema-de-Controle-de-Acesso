package view;

import controller.TerminalController;
import java.awt.*;
import java.awt.event.ItemEvent; // NOVO IMPORT NECESSÁRIO
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
    private JCheckBox chkAtivo;

    // --- NOVOS CAMPOS ADICIONADOS ---
    private JCheckBox chkAdmin;
    private JLabel lblLogin;
    private JTextField txtLogin;
    private JLabel lblSenha;
    private JPasswordField txtSenha;
    // --------------------------------

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
        setSize(450, 500); // TAMANHO AUMENTADO para acomodar os novos campos
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // Define a cor de fundo principal
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // PAINEL PRINCIPAL
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBackground(COR_PAINEL);
        // borda
        Border bordaExterna = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        Border bordaInterna = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        painelCampos.setBorder(BorderFactory.createCompoundBorder(bordaInterna, bordaExterna));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // --- CAMPO NOME (GridY 0) ---
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

        // --- CAMPO CPF (GridY 1 - não editável) ---
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

        // --- CAMPO EMAIL (GridY 2) ---
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

        // --- CHECKBOX ATIVO (GridY 3) ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblAtivo = new JLabel("Ativo:");
        lblAtivo.setFont(FONTE_LABEL);
        painelCampos.add(lblAtivo, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        chkAtivo = new JCheckBox();
        chkAtivo.setSelected(usuario.isAtivo());
        chkAtivo.setBackground(COR_PAINEL);
        painelCampos.add(chkAtivo, gbc);

        // --- INÍCIO DOS CAMPOS ADM ---
        boolean isAdm = "ADMINISTRADOR".equals(usuario.getTipoUsuario());

        // CHECKBOX ADMIN (GridY 4)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Ocupa duas colunas
        gbc.anchor = GridBagConstraints.WEST;
        chkAdmin = new JCheckBox("Definir como Administrador");
        chkAdmin.setFont(FONTE_LABEL);
        chkAdmin.setBackground(COR_PAINEL);
        chkAdmin.setSelected(isAdm); // Carrega o estado atual
        painelCampos.add(chkAdmin, gbc);

        // CAMPO LOGIN (GridY 5)
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        lblLogin = new JLabel("Login:");
        lblLogin.setFont(FONTE_LABEL);
        painelCampos.add(lblLogin, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        // Carrega o login atual, se houver
        txtLogin = new JTextField(usuario.getLogin() != null ? usuario.getLogin() : "");
        txtLogin.setFont(FONTE_CAMPO);
        painelCampos.add(txtLogin, gbc);

        // CAMPO SENHA (GridY 6)
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        lblSenha = new JLabel("Nova Senha:");
        lblSenha.setFont(FONTE_LABEL);
        painelCampos.add(lblSenha, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtSenha = new JPasswordField(20);
        txtSenha.setFont(FONTE_CAMPO);
        painelCampos.add(txtSenha, gbc);

        // LÓGICA DE VISIBILIDADE (Inicializa e adiciona Listener)
        lblLogin.setVisible(isAdm);
        txtLogin.setVisible(isAdm);
        lblSenha.setVisible(isAdm);
        txtSenha.setVisible(isAdm);

        chkAdmin.addItemListener(e -> {
            boolean selecionado = (e.getStateChange() == ItemEvent.SELECTED);
            lblLogin.setVisible(selecionado);
            txtLogin.setVisible(selecionado);
            lblSenha.setVisible(selecionado);
            txtSenha.setVisible(selecionado);

            // Força o re-layout para ajustar o tamanho da tela
            painelCampos.revalidate();
            painelCampos.repaint();
            revalidate();
            repaint();
        });
        // --- FIM DOS CAMPOS ADM ---

        // Adiciona o painel de campos ao centro da janela
        add(painelCampos, BorderLayout.CENTER);

        // BOTÕES
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoes.setBackground(COR_FUNDO);

        JButton btnSalvar = new JButton("Salvar");
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
            JOptionPane.showMessageDialog(this, "O Nome é obrigatório.", "Erro de Validação",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "O email informado parece ser inválido.", "Erro de Validação",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- LÓGICA DE ADMIN ADICIONADA ---
        final boolean isAdmin = chkAdmin.isSelected();
        String login = null;
        String senha = null;

        if (isAdmin) {
            login = txtLogin.getText().trim();
            senha = new String(txtSenha.getPassword());

            if (login.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Para Administradores, o Login é obrigatório.", "Erro de Validação",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Se a senha foi preenchida, usa o valor. Se for vazia/null, passamos null para
        // o service
        // saber que deve manter a senha atual.
        final String finalSenha = senha != null && !senha.isEmpty() ? senha : null;
        final String finalLogin = login;
        // ---------------------------------

        // Atualiza os campos do objeto usuário antes de passar para o controller.
        usuario.setNome(txtNome.getText());
        usuario.setEmail(email);
        usuario.setAtivo(chkAtivo.isSelected());

        // Supondo que você tenha um campo 'cargo', que não estava na sua versão, vou
        // adicioná-lo ao objeto
        // para evitar NullPointer se o Service o usar.
        // usuario.setCargo(txtCargo.getText()); // Removido, pois o campo não existia
        // no seu Grid.

        try {
            // --- CHAMADA DO CONTROLLER MODIFICADA ---
            controller.editarUsuario(usuario, isAdmin, finalLogin, finalSenha);
            JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar usuário: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}