package view;

import controller.TerminalController;
import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Tela principal de autenticação, recriada sem dependências de imagens externas.
 * O design foi ajustado para manter a clareza e a boa experiência do usuário.
 */
public class TelaAutenticacao extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final transient TerminalController controller;
    private final JLabel labelStatus;

    // Constantes de Design (mantidas para consistência)
    private static final Color COR_FUNDO = new Color(43, 43, 43);
    private static final Color COR_LETRA_PRINCIPAL = new Color(187, 187, 187);
    private static final Color COR_DESTAQUE = new Color(0, 174, 239);
    private static final Font FONTE_STATUS = new Font("Segoe UI", Font.BOLD, 24); // Fonte aumentada
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 16);

    public TelaAutenticacao(TerminalController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("O controller não pode ser nulo.");
        }
        this.controller = controller;

        configurarJanela();

        // --- Componentes ---
        labelStatus = new JLabel("Clique para Autenticar", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_STATUS);
        labelStatus.setForeground(COR_LETRA_PRINCIPAL);
        
        JButton btnPainelAdmin = criarBotaoAdmin();
        JPanel painelCentral = criarPainelCentral();

        // --- Layout ---
        add(painelCentral, BorderLayout.CENTER);
        add(btnPainelAdmin, BorderLayout.SOUTH);

        // --- Ações ---
        painelCentral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                executarAutenticacao();
            }
        });
        btnPainelAdmin.addActionListener(e -> abrirPainelAdmin());
    }

    private void configurarJanela() {
        setTitle("Controle de Acesso Biométrico");
        setSize(500, 400); // Altura pode ser reduzida pois não há imagem
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(20,20));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private JPanel criarPainelCentral() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_FUNDO);
        painel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painel.add(labelStatus); // Adiciona apenas o label de status
        return painel;
    }

    private JButton criarBotaoAdmin() {
        JButton botao = new JButton("Painel do Administrador");
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(COR_DESTAQUE);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Estilo "flat"
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        return botao;
    }

    private void executarAutenticacao() {
        labelStatus.setForeground(COR_DESTAQUE);
        labelStatus.setText("Processando...");
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
                        labelStatus.setText("Acesso Permitido!");
                        JOptionPane.showMessageDialog(TelaAutenticacao.this, "Bem-vindo(a), " + usuarioOpt.get().getNome() + "!", "Acesso Permitido", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        labelStatus.setForeground(Color.RED);
                        labelStatus.setText("Acesso Negado");
                    }
                } catch (Exception ex) {
                    labelStatus.setForeground(Color.RED);
                    labelStatus.setText("Erro de Comunicação");
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    Timer timer = new Timer(3000, evt -> {
                        labelStatus.setForeground(COR_LETRA_PRINCIPAL);
                        labelStatus.setText("Clique para Autenticar");
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
}