package view;

import controller.TerminalController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import model.Usuario;

public class TelaEdicaoUsuario extends JDialog {

    private static final long serialVersionUID = 1L;

    // As variáveis originais permanecem as mesmas
    private final TerminalController controller;
    private final Usuario usuario;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtEmail;
    private JCheckBox chkAtivo;

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
        setSize(450, 350);
        // CORREÇÃO: Usar getOwner() para buscar a janela "mãe" que foi definida no construtor.
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // Define a cor de fundo principal
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Cria e organiza os componentes visuais na tela.
     */
    private void inicializarComponentes() {
        // --- PAINEL PRINCIPAL COM CAMPOS DE EDIÇÃO ---
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBackground(COR_PAINEL);
        // Adiciona uma borda com espaçamento e uma linha sutil
        Border bordaExterna = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        Border bordaInterna = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        painelCampos.setBorder(BorderFactory.createCompoundBorder(bordaInterna, bordaExterna));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaçamento entre os componentes
        gbc.anchor = GridBagConstraints.WEST; // Alinha componentes à esquerda

        // Campo Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(FONTE_LABEL);
        painelCampos.add(lblNome, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Faz o campo de texto ocupar o espaço horizontal disponível
        txtNome = new JTextField(usuario.getNome());
        txtNome.setFont(FONTE_CAMPO);
        painelCampos.add(txtNome, gbc);

        // Campo CPF (não editável)
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
        txtCpf.setBackground(new Color(230, 230, 230)); // Cor para indicar que não é editável
        painelCampos.add(txtCpf, gbc);

        // Campo Email
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

        // Checkbox Ativo
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
        chkAtivo.setBackground(COR_PAINEL); // Garante que o fundo do checkbox seja o mesmo do painel
        painelCampos.add(chkAtivo, gbc);

        // Adiciona o painel de campos ao centro da janela
        add(painelCampos, BorderLayout.CENTER);

        // --- PAINEL DE BOTÕES ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoes.setBackground(COR_FUNDO); // Mesma cor de fundo da janela

        JButton btnSalvar = new JButton("Salvar");
        configurarBotao(btnSalvar, COR_BOTAO_SALVAR);
        btnSalvar.addActionListener(e -> salvar());

        JButton btnCancelar = new JButton("Cancelar");
        configurarBotao(btnCancelar, Color.GRAY);
        btnCancelar.addActionListener(e -> dispose());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        // Adiciona o painel de botões na parte inferior da janela
        add(painelBotoes, BorderLayout.SOUTH);

        // Define o botão "Salvar" como padrão (acionado com Enter)
        getRootPane().setDefaultButton(btnSalvar);
    }

    /**
     * Aplica um estilo padrão aos botões.
     */
    private void configurarBotao(JButton botao, Color corFundo) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setBackground(corFundo);
        botao.setForeground(COR_TEXTO_BOTAO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    /**
     * Ação de salvar os dados. A lógica interna permanece a mesma.
     */
    private void salvar() {
        usuario.setNome(txtNome.getText());
        usuario.setEmail(txtEmail.getText());
        usuario.setAtivo(chkAtivo.isSelected());

        try {
            controller.editarUsuario(usuario);
            JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar usuário: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}