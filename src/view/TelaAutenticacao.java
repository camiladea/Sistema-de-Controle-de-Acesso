package view;

import controller.TerminalController;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * Tela principal de autenticação com um design visual aprimorado e interativo.
 */
public class TelaAutenticacao extends JFrame {

    private static final long serialVersionUID = 1L;
    private final transient TerminalController controller;
    private final JLabel labelStatus;
    private final FingerprintPanel fingerprintPanel; // Painel customizado para o ícone de biometria

    // --- Constantes de Design ---
    private static final Color COR_FUNDO = new Color(30, 30, 30);
    private static final Color COR_LETRA_PRINCIPAL = new Color(200, 200, 200);
    private static final Color COR_DESTAQUE_IDLE = new Color(100, 100, 100);
    private static final Color COR_DESTAQUE_PROCESSANDO = new Color(0, 174, 239);
    private static final Color COR_SUCESSO = new Color(0, 200, 83);
    private static final Color COR_ERRO = new Color(213, 0, 0);
    private static final Font FONTE_STATUS = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 14);

    public TelaAutenticacao(TerminalController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("O controller não pode ser nulo.");
        }
        this.controller = controller;

        configurarJanela();

        // --- Componentes ---
        fingerprintPanel = new FingerprintPanel();
        labelStatus = new JLabel("Aproxime o dedo para autenticar", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_STATUS);
        labelStatus.setForeground(COR_LETRA_PRINCIPAL);

        JButton btnPainelAdmin = criarBotaoAdmin();
        JPanel painelCentral = criarPainelCentral();

        // --- Layout ---
        getContentPane().add(painelCentral, BorderLayout.CENTER);
        getContentPane().add(btnPainelAdmin, BorderLayout.SOUTH);

        // --- Ações ---
        painelCentral.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                executarAutenticacao();
            }
        });
    }

    private void configurarJanela() {
        setTitle("Controle de Acesso Biométrico");
        setSize(1024, 768);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        getContentPane().setLayout(new BorderLayout(20, 20));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    }

    private JPanel criarPainelCentral() {
        JPanel painel = new JPanel(new BorderLayout(0, 20));
        painel.setBackground(COR_FUNDO);
        painel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painel.add(fingerprintPanel, BorderLayout.CENTER);
        painel.add(labelStatus, BorderLayout.SOUTH);
        
        return painel;
    }

    private JButton criarBotaoAdmin() {
        JButton botao = new JButton("Painel do Administrador");
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(COR_DESTAQUE_IDLE);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Efeito Hover
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setForeground(COR_DESTAQUE_PROCESSANDO);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setForeground(COR_DESTAQUE_IDLE);
            }
        });

        botao.addActionListener(e -> abrirPainelAdmin());
        return botao;
    }

    private void executarAutenticacao() {
        labelStatus.setText("Aguarde, lendo biometria...");
        fingerprintPanel.setStatusCor(COR_DESTAQUE_PROCESSANDO);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Optional<Usuario>, Void>() {
            @Override
            protected Optional<Usuario> doInBackground() {
                return controller.solicitarAutenticacaoBiometrica();
            }
            @Override
            protected void done() {
                try {
                    Optional<Usuario> usuarioOpt = get();
                    if (usuarioOpt.isPresent()) {
                        labelStatus.setText("Acesso Permitido: " + usuarioOpt.get().getNome());
                        fingerprintPanel.setStatusCor(COR_SUCESSO);
                    } else {
                        labelStatus.setText("Acesso Negado. Tente novamente.");
                        fingerprintPanel.setStatusCor(COR_ERRO);
                    }
                } catch (Exception ex) {
                    labelStatus.setText("Erro de Comunicação com o Leitor");
                    fingerprintPanel.setStatusCor(COR_ERRO);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    Timer timer = new Timer(3000, evt -> {
                        labelStatus.setText("Aproxime o dedo para autenticar");
                        fingerprintPanel.setStatusCor(COR_DESTAQUE_IDLE);
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }.execute();
    }

    private void abrirPainelAdmin() {
        TelaLoginAdmin telaLogin = new TelaLoginAdmin(this, controller);
        telaLogin.setVisible(true);
        if (telaLogin.isLoginSucedido()) {
            TelaGestao telaGestao = new TelaGestao(this, controller);
            telaGestao.setVisible(true);
        }
    }

    /**
     * Painel customizado que desenha uma impressão digital estilizada.
     */
    private static class FingerprintPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Color statusCor = COR_DESTAQUE_IDLE;

        public FingerprintPanel() {
            setOpaque(false);
        }

        public void setStatusCor(Color cor) {
            this.statusCor = cor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // --- ALTERAÇÃO AQUI ---
            // O valor subtraído define a "margem" em volta do desenho.
            // Um número maior resulta em um desenho menor.
            int diametro = Math.min(getWidth(), getHeight()) - 160;

            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;

            g2d.setColor(statusCor);
            g2d.setStroke(new BasicStroke(diametro / 20f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Desenha arcos concêntricos para simular uma impressão digital
            for (int i = 0; i < 6; i++) {
                int d = diametro - (i * (diametro / 6));
                int arcX = x + (i * (diametro / 12));
                int arcY = y + (i * (diametro / 12));
                g2d.drawArc(arcX, arcY, d, d, -45 - (i * 10), 270 + (i * 5));
            }
            g2d.dispose();
        }
    }
}