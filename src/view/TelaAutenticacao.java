package view;

import controller.TerminalController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowStateListener;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Usuario;

public class TelaAutenticacao extends JFrame {

    private static final long serialVersionUID = 1L;
    private final transient TerminalController controller;
    private final JLabel labelStatus;
    private final FingerprintPanel fingerprintPanel;

    private static final Color COR_FUNDO = new Color(30, 30, 30);
    private static final Color COR_LETRA_PRINCIPAL = new Color(200, 200, 200);
    private static final Color COR_DESTAQUE_IDLE = new Color(100, 100, 100);
    private static final Color COR_DESTAQUE_PROCESSANDO = new Color(0, 174, 239);
    private static final Color COR_SUCESSO = new Color(0, 200, 83);
    private static final Color COR_ERRO = new Color(213, 0, 0);
    private static final Font FONTE_STATUS = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 14);

    private Point initialClick;
    private JButton btnMaximizar;

    public TelaAutenticacao(TerminalController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("O controller não pode ser nulo.");
        }
        this.controller = controller;

        configurarJanela();

        JPanel barraDeTitulo = criarBarraDeTituloCustomizada();

        fingerprintPanel = new FingerprintPanel();
        labelStatus = new JLabel("Aproxime o dedo para autenticar", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_STATUS);
        labelStatus.setForeground(COR_LETRA_PRINCIPAL);

        JButton btnPainelAdmin = criarBotaoAdmin();
        JPanel painelCentral = criarPainelCentral();

        JPanel painelConteudo = new JPanel(new BorderLayout(20, 20));
        painelConteudo.setBackground(COR_FUNDO);
        painelConteudo.setBorder(new EmptyBorder(30, 30, 30, 30));
        painelConteudo.add(painelCentral, BorderLayout.CENTER);
        painelConteudo.add(btnPainelAdmin, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(barraDeTitulo, BorderLayout.NORTH);
        getContentPane().add(painelConteudo, BorderLayout.CENTER);

        painelCentral.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                executarAutenticacao();
            }
        });

        MouseAdapter draggableAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    initialClick = e.getPoint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;
                    int xMoved = thisX + (e.getX() - initialClick.x);
                    int yMoved = thisY + (e.getY() - initialClick.y);
                    setLocation(xMoved, yMoved);
                }
            }
        };

        painelConteudo.addMouseListener(draggableAdapter);
        painelConteudo.addMouseMotionListener(draggableAdapter);
        painelCentral.addMouseListener(draggableAdapter);
        painelCentral.addMouseMotionListener(draggableAdapter);

    }

    private void configurarJanela() {
        setUndecorated(true);
        setSize(1024, 768);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
    }
    // barra superior

    private JPanel criarBarraDeTituloCustomizada() {
        JPanel barraDeTitulo = new JPanel(new BorderLayout());
        barraDeTitulo.setBackground(COR_FUNDO);
        barraDeTitulo.setBorder(new EmptyBorder(5, 10, 5, 5));

        JPanel painelTituloIcone = new JPanel(new BorderLayout(10, 0));
        painelTituloIcone.setOpaque(false);

        FingerprintIconPanel iconPanel = new FingerprintIconPanel();
        iconPanel.setBorder(new EmptyBorder(2, 0, 0, 0));
        painelTituloIcone.add(iconPanel, BorderLayout.WEST);

        JLabel tituloLabel = new JLabel("Controle de Acesso Biométrico");
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelTituloIcone.add(tituloLabel, BorderLayout.CENTER);

        barraDeTitulo.add(painelTituloIcone, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelBotoes.setOpaque(false);

        JButton btnMinimizar = new JButton("\u2014");
        btnMinimizar.setForeground(Color.WHITE);
        btnMinimizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMinimizar.setFocusPainted(false);
        btnMinimizar.setBorderPainted(false);
        btnMinimizar.setContentAreaFilled(false);
        btnMinimizar.addActionListener(e -> setState(JFrame.ICONIFIED));

        btnMaximizar = new JButton("\u25A1");
        btnMaximizar.setForeground(Color.WHITE);
        btnMaximizar.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
        btnMaximizar.setFocusPainted(false);
        btnMaximizar.setBorderPainted(false);
        btnMaximizar.setContentAreaFilled(false);
        btnMaximizar.addActionListener(e -> toggleMaximize());

        JButton btnFechar = new JButton("\u00D7");
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 21));
        btnFechar.setFocusPainted(false);
        btnFechar.setBorderPainted(false);
        btnFechar.setContentAreaFilled(false);
        btnFechar.addActionListener(e -> dispose());

        // aplicar o efeito de hover em todos os botoes
        applyButtonHoverEffect(btnMinimizar, COR_DESTAQUE_PROCESSANDO, Color.WHITE);
        applyButtonHoverEffect(btnMaximizar, COR_DESTAQUE_PROCESSANDO, Color.WHITE);
        applyButtonHoverEffect(btnFechar, COR_DESTAQUE_PROCESSANDO, Color.WHITE);

        painelBotoes.add(btnMinimizar);
        painelBotoes.add(btnMaximizar);
        painelBotoes.add(btnFechar);
        barraDeTitulo.add(painelBotoes, BorderLayout.EAST);

        barraDeTitulo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    initialClick = e.getPoint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleMaximize();
                }
            }
        });

        barraDeTitulo.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;
                    int xMoved = thisX + (e.getX() - initialClick.x);
                    int yMoved = thisY + (e.getY() - initialClick.y);
                    setLocation(xMoved, yMoved);
                }
            }
        });

        this.addWindowStateListener(e -> {
            if ((e.getNewState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                btnMaximizar.setText("\u29C9");
            } else {
                btnMaximizar.setText("\u25A1");
            }
        });

        return barraDeTitulo;
    }

    // efeito botoes
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

    private void toggleMaximize() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
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

        applyButtonHoverEffect(botao, COR_DESTAQUE_PROCESSANDO, COR_DESTAQUE_IDLE);

        botao.addActionListener(e -> abrirPainelAdmin());
        return botao;
    }

    private void executarAutenticacao() {
        // Define a cor de processamento imediatamente
        fingerprintPanel.setStatusCor(COR_DESTAQUE_PROCESSANDO);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Optional<Usuario>, String>() {
            @Override
            protected Optional<Usuario> doInBackground() {
                // O Consumer atualiza o JLabel na Event Dispatch Thread para segurança de thread.
                java.util.function.Consumer<String> statusUpdater =
                        mensagem -> publish(mensagem); // Usa publish para enviar para process()

                return controller.solicitarAutenticacaoBiometrica(statusUpdater);
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                // Atualiza o labelStatus com a última mensagem recebida
                String lastMessage = chunks.get(chunks.size() - 1);
                labelStatus.setText("<html><div style='text-align: center;'>" + lastMessage + "</div></html>");
            }

            @Override
            protected void done() {
                try {
                    Optional<Usuario> usuarioOpt = get();
                    if (usuarioOpt.isPresent()) {
                        // A mensagem final já é definida pelo SistemaAutenticacao
                        fingerprintPanel.setStatusCor(COR_SUCESSO);
                    } else {
                        // A mensagem final já é definida pelo SistemaAutenticacao
                        fingerprintPanel.setStatusCor(COR_ERRO);
                    }
                } catch (Exception ex) {
                    labelStatus.setText("<html><div style='text-align: center;'>Erro de Comunicação com o Leitor</div></html>");
                    fingerprintPanel.setStatusCor(COR_ERRO);
                    ex.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    // Reseta o status após um pequeno atraso para o usuário ver o resultado final
                    Timer timer = new Timer(3000, evt -> {
                        labelStatus.setText("<html><div style='text-align: center;'>Aproxime o dedo para autenticar</div></html>");
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

        // icone de digital grande
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diametro = Math.min(getWidth(), getHeight()) - 160;
            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;

            g2d.setColor(statusCor);
            g2d.setStroke(new BasicStroke(diametro / 20f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < 6; i++) {
                int d = diametro - (i * (diametro / 6));
                int arcX = x + (i * (diametro / 12));
                int arcY = y + (i * (diametro / 12));
                g2d.drawArc(arcX, arcY, d, d, -45 - (i * 10), 270 + (i * 5));
            }
            g2d.dispose();
        }
    }

    // icone da digital pequeno
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