package view;

import controller.TerminalController;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import model.Administrador;

public class TelaLoginAdmin extends JDialog {

    private static final long serialVersionUID = 1L;
    private final transient TerminalController controller;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private boolean loginSucedido = false;

    // arrastar a janela
    private Point initialClick;

    private static final Color COR_FUNDO_ESCURO = new Color(30, 30, 30);
    private static final Color COR_INPUT = new Color(55, 55, 55);
    private static final Color COR_BORDA_INPUT = new Color(80, 80, 80);
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_TEXTO_PLACEHOLDER = new Color(170, 170, 170);
    private static final Color COR_BOTAO_ACAO = new Color(24, 119, 242);
    private static final Color COR_BOTAO_ACAO_HOVER = new Color(60, 140, 250);
    private static final Color COR_DESTAQUE_PROCESSANDO = new Color(0, 174, 239);


    private static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);

    private static final Border BORDA_PADRAO_CAMPO = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COR_BORDA_INPUT),
        new EmptyBorder(12, 12, 12, 12)
    );
    private static final Border BORDA_FOCO_CAMPO = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COR_BOTAO_ACAO, 2),
        new EmptyBorder(11, 11, 11, 11)
    );

    public TelaLoginAdmin(JFrame parent, TerminalController controller) {
        super(parent, true);
        this.controller = controller;
        configurarEInicializar();
    }

    public boolean isLoginSucedido() {
        return loginSucedido;
    }

    public void setLoginSucedido(boolean loginSucedido) {
        this.loginSucedido = loginSucedido;
    }


    private void configurarEInicializar() {
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO_ESCURO);

        JPanel barraDeTitulo = criarBarraDeTituloCustomizada();
        getContentPane().add(barraDeTitulo, BorderLayout.NORTH);

        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(COR_FUNDO_ESCURO);
        painelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int linhaAtual = 0;

        JLabel lblIcone = new JLabel("🔒");
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcone.setForeground(COR_BOTAO_ACAO);
        lblIcone.setPreferredSize(new Dimension(1, 70));
        gbc.gridy = linhaAtual++;
        gbc.insets = new Insets(0, 0, 15, 0);
        painelPrincipal.add(lblIcone, gbc);

        JLabel lblTitulo = new JLabel("Autorização Necessária");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = linhaAtual++;
        gbc.insets = new Insets(0, 0, 30, 0);
        painelPrincipal.add(lblTitulo, gbc);

        JLabel lblLogin = new JLabel("Login:");
        estilizarLabel(lblLogin);
        gbc.gridy = linhaAtual++;
        gbc.insets = new Insets(0, 0, 5, 0);
        painelPrincipal.add(lblLogin, gbc);

        txtLogin = new JTextField(20);
        estilizarCampoDeTexto(txtLogin);
        gbc.gridy = linhaAtual++;
        gbc.insets = new Insets(0, 0, 15, 0);
        painelPrincipal.add(txtLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        estilizarLabel(lblSenha);
        gbc.gridy = linhaAtual++;
        gbc.insets = new Insets(0, 0, 5, 0);
        painelPrincipal.add(lblSenha, gbc);

        txtSenha = new JPasswordField();
        estilizarCampoDeTexto(txtSenha);
        gbc.gridy = linhaAtual++;
        gbc.insets = new Insets(0, 0, 30, 0);
        painelPrincipal.add(txtSenha, gbc);

        JButton btnLogin = new JButton("AUTORIZAR");
        estilizarBotao(btnLogin);
        btnLogin.addActionListener(e -> {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword()); // plain password
        Optional<Administrador> admOpt = controller.solicitarAutenticacaoAdmin(login, senha);
            if (admOpt.isPresent()) {
        // success
                this.setLoginSucedido(true);
            this.dispose();
            } else {
        // failure
        JOptionPane.showMessageDialog(this, "Credenciais inválidas", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = linhaAtual++;
        gbc.insets = new Insets(0, 0, 0, 0);
        painelPrincipal.add(btnLogin, gbc);

        getRootPane().setDefaultButton(btnLogin);
        
        MouseAdapter draggableAdapter = createDraggableMouseAdapter();
        painelPrincipal.addMouseListener(draggableAdapter);
        painelPrincipal.addMouseMotionListener(draggableAdapter);

        getContentPane().add(painelPrincipal, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private JPanel criarBarraDeTituloCustomizada() {
        JPanel barraDeTitulo = new JPanel(new BorderLayout());
        barraDeTitulo.setBackground(COR_FUNDO_ESCURO);
        barraDeTitulo.setBorder(new EmptyBorder(5, 10, 5, 5));

        JPanel painelTituloIcone = new JPanel(new BorderLayout(10, 0));
        painelTituloIcone.setOpaque(false);

        FingerprintIconPanel iconPanel = new FingerprintIconPanel();
        iconPanel.setBorder(new EmptyBorder(2, 0, 0, 0));
        painelTituloIcone.add(iconPanel, BorderLayout.WEST);
        
        JLabel tituloLabel = new JLabel("Autenticação de Administrador");
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelTituloIcone.add(tituloLabel, BorderLayout.CENTER);

        barraDeTitulo.add(painelTituloIcone, BorderLayout.CENTER);
        
        JButton btnFechar = new JButton("\u00D7");
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnFechar.setFocusPainted(false);
        btnFechar.setBorderPainted(false);
        btnFechar.setContentAreaFilled(false);
        btnFechar.setPreferredSize(new Dimension(45, 30));
        btnFechar.addActionListener(e -> dispose());
        
        applyButtonHoverEffect(btnFechar, COR_DESTAQUE_PROCESSANDO, Color.WHITE);
        
        barraDeTitulo.add(btnFechar, BorderLayout.EAST);

        MouseAdapter draggableAdapter = createDraggableMouseAdapter();
        barraDeTitulo.addMouseListener(draggableAdapter);
        barraDeTitulo.addMouseMotionListener(draggableAdapter);
        
        return barraDeTitulo;
    }

    private MouseAdapter createDraggableMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = thisX + (e.getX() - initialClick.x);
                int yMoved = thisY + (e.getY() - initialClick.y);
                setLocation(xMoved, yMoved);
            }
        };
    }

    private void applyButtonHoverEffect(JButton button, Color hoverColor, Color defaultColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(defaultColor);
            }
        });
    }

    private void estilizarBotao(JButton botao) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botao.setBackground(COR_BOTAO_ACAO);
        botao.setForeground(COR_TEXTO);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(14, 0, 14, 0));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(COR_BOTAO_ACAO_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(COR_BOTAO_ACAO);
            }
        });
    }

    private void estilizarLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(COR_TEXTO_PLACEHOLDER);
        label.setBorder(new EmptyBorder(0, 2, 0, 0));
    }

    private void estilizarCampoDeTexto(JTextField campo) {
        campo.setFont(FONTE_PADRAO);
        campo.setBackground(COR_INPUT);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BORDA_PADRAO_CAMPO);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BORDA_FOCO_CAMPO);
            }
            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BORDA_PADRAO_CAMPO);
            }
        });
    }

    private void autenticar() {
        Optional<Administrador> adminOpt = controller.solicitarAutenticacaoAdmin(
            txtLogin.getText(), new String(txtSenha.getPassword()));

        if (adminOpt.isPresent()) {
            loginSucedido = true;
            dispose();
        } else {
            UIManager.put("OptionPane.background", COR_FUNDO_ESCURO);
            UIManager.put("Panel.background", COR_FUNDO_ESCURO);
            UIManager.put("OptionPane.messageForeground", COR_TEXTO);
            UIManager.put("Button.background", COR_BOTAO_ACAO);
            UIManager.put("Button.foreground", COR_TEXTO);

            JOptionPane.showMessageDialog(this, "Login ou Senha inválidos.",
                "Falha na Autenticação", JOptionPane.ERROR_MESSAGE);

            UIManager.put("OptionPane.background", null);
            UIManager.put("Panel.background", null);
            UIManager.put("OptionPane.messageForeground", null);
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", null);

            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    private static class FingerprintIconPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        
        public FingerprintIconPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(20, 20)); // tamanho
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diametro = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;

            g2d.setColor(Color.WHITE); // cor
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            for (int i = 0; i < 4; i++) { // tem menos arcos que o grande
                int d = diametro - (i * (diametro / 4));
                int arcX = x + (i * (diametro / 8));
                int arcY = y + (i * (diametro / 8));
                g2d.drawArc(arcX, arcY, d, d, -45 - (i * 10), 270 + (i * 5));
            }
            g2d.dispose();
        }
    }
}