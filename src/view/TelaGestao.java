package view;

import controller.TerminalController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener; // <--- CORRIGIDO: Adicionado o MouseListener
import java.time.*;
import java.time.format.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import model.*;
import view.TelaCadastroUsuario; 
import view.TelaEdicaoUsuario;
// Importações de UI Look and Feel foram removidas para evitar erros complexos
// import javax.swing.plaf.basic.BasicTabbedPaneUI; 

public class TelaGestao extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient TerminalController controller;
    private DefaultTableModel modelUsuarios;
    private DefaultTableModel modelRelatorio;
    private JTextField txtDataInicio, txtDataFim;
    private JTable tabelaUsuarios;

    private Point initialClick;
    private JButton btnMaximizar;

    // --- CONSTANTES DE DESIGN (DARK MODE) ---
    private static final Color COR_FUNDO = new Color(30, 30, 30); // Fundo Principal Escuro (Geral)
    private static final Color COR_PAINEL_CONTEUDO = new Color(45, 45, 45); // Fundo dos Painéis/Abas
    private static final Color COR_CABECALHO_TABELA = new Color(60, 60, 60); // Cabeçalho Escuro
    private static final Color COR_TEXTO = new Color(200, 200, 200); // Texto Claro
    private static final Color COR_BOTAO_PRIMARIO = new Color(0, 174, 239); // Azul de Destaque (Adicionar/Gerar)
    private static final Color COR_BOTAO_SECUNDARIO = new Color(70, 70, 70); // Cinza Escuro (Outras Ações)
    private static final Color COR_TEXTO_BOTAO_PRIMARIO = Color.WHITE;
    private static final Color COR_TEXTO_BOTAO_SECUNDARIO = new Color(220, 220, 220);
    private static final Color COR_DESTAQUE_BOTAO = new Color(0, 174, 239).brighter(); // Azul Claro para Hover
    private static final Color COR_LINHA_GRADE = new Color(60, 60, 60);
    private static final Color COR_SELECAO = new Color(0, 130, 180); // Azul Escuro de Seleção

    private static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CABECALHO = new Font("Segoe UI", Font.BOLD, 14);

    public TelaGestao(Window owner, TerminalController controller) {
        super();
        this.controller = controller;

        // Configurações UI Manager para o Dark Mode (ajustadas para estabilidade)
        // Setando cores no UIManager é menos arriscado do que herdar BasicTabbedPaneUI
        UIManager.put("TabbedPane.contentAreaColor", COR_PAINEL_CONTEUDO);
        UIManager.put("TabbedPane.selected", COR_PAINEL_CONTEUDO.brighter());
        UIManager.put("TabbedPane.background", COR_FUNDO);
        UIManager.put("TabbedPane.foreground", COR_TEXTO);
        UIManager.put("TabbedPane.border", BorderFactory.createEmptyBorder());

        configurarJanela();
        inicializarComponentes();

        carregarDadosUsuarios();
    }

    private void configurarJanela() {
        setUndecorated(true);
        setTitle("Painel de Gestão");
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO);
    }

    private void inicializarComponentes() {
        JPanel barraDeTitulo = criarBarraDeTituloCustomizada();
        getContentPane().add(barraDeTitulo, BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        abas.setBackground(COR_FUNDO);
        abas.setForeground(COR_TEXTO);
        abas.setOpaque(true);

        // Define a cor de fundo do JTabbedPane para evitar bordas brancas indesejadas
        // (Isso é uma alternativa mais segura ao BasicTabbedPaneUI)
        abas.setBorder(BorderFactory.createLineBorder(COR_FUNDO, 5));

        JPanel painelUsuarios = criarAbaUsuarios();
        JPanel painelRelatorios = criarAbaRelatorios();

        abas.addTab("Gerenciamento de Usuários", painelUsuarios);
        abas.addTab("Relatório de Acessos", painelRelatorios);

        getContentPane().add(abas, BorderLayout.CENTER);

        // Adapters de arrastar (draggable)
        MouseAdapter draggableAdapter = createDraggableMouseAdapter();
        // Não aplica draggable ao abas, pois pode interferir. Apenas a barra de título
        // deve ser draggable.
    }

    private JPanel criarBarraDeTituloCustomizada() {
        JPanel barraDeTitulo = new JPanel(new BorderLayout());
        barraDeTitulo.setBackground(COR_PAINEL_CONTEUDO);
        barraDeTitulo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_LINHA_GRADE));

        JPanel painelTituloIcone = new JPanel(new BorderLayout(10, 0));
        painelTituloIcone.setOpaque(false);
        painelTituloIcone.setBorder(new EmptyBorder(5, 10, 5, 5));

        FingerprintIconPanel iconPanel = new FingerprintIconPanel();
        iconPanel.setBorder(new EmptyBorder(2, 0, 0, 0));
        painelTituloIcone.add(iconPanel, BorderLayout.WEST);

        JLabel tituloLabel = new JLabel("Painel de Gestão");
        tituloLabel.setForeground(COR_TEXTO);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelTituloIcone.add(tituloLabel, BorderLayout.CENTER);

        barraDeTitulo.add(painelTituloIcone, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setOpaque(false);
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        JButton btnMinimizar = new JButton("—");
        configurarBotaoControle(btnMinimizar);
        btnMinimizar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMinimizar.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        painelBotoes.add(btnMinimizar);

        btnMaximizar = new JButton("\u25A1"); // Quadrado
        configurarBotaoControle(btnMaximizar);
        btnMaximizar.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnMaximizar.addActionListener(e -> toggleMaximize());

        JButton btnFechar = new JButton("\u00D7");
        configurarBotaoControle(btnFechar);
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnFechar.addActionListener(e -> dispose());

        applyButtonHoverEffect(btnMinimizar, COR_DESTAQUE_BOTAO, COR_TEXTO);
        applyButtonHoverEffect(btnMaximizar, COR_DESTAQUE_BOTAO, COR_TEXTO);
        applyButtonHoverEffect(btnFechar, new Color(232, 17, 35), COR_TEXTO);

        painelBotoes.add(btnMaximizar);
        painelBotoes.add(btnFechar);
        barraDeTitulo.add(painelBotoes, BorderLayout.EAST);

        MouseAdapter draggableAdapter = createDraggableMouseAdapter();
        barraDeTitulo.addMouseListener(draggableAdapter);
        barraDeTitulo.addMouseMotionListener(draggableAdapter);

        this.addWindowStateListener(e -> {
            if ((e.getNewState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                btnMaximizar.setText("\u29C9");
            } else {
                btnMaximizar.setText("\u25A1");
            }
        });

        return barraDeTitulo;
    }

    private void configurarBotaoControle(JButton button) {
        button.setForeground(COR_TEXTO);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(45, 30));
    }

    private JPanel criarAbaUsuarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        painel.setBackground(COR_PAINEL_CONTEUDO);
        painel.setForeground(COR_TEXTO);

        modelUsuarios = new DefaultTableModel(new String[] { "ID", "Nome", "CPF", "Tipo", "Status" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : String.class;
            }
        };
        tabelaUsuarios = new JTable(modelUsuarios);
        configurarTabela(tabelaUsuarios, new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(COR_LINHA_GRADE, 1));
        scrollPane.getViewport().setBackground(COR_PAINEL_CONTEUDO.brighter());
        painel.add(scrollPane, BorderLayout.CENTER);

        JPanel painelAcoes = new JPanel(new BorderLayout());
        painelAcoes.setBackground(COR_PAINEL_CONTEUDO);
        painelAcoes.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnVoltar = new JButton("VOLTAR / SAIR");
        configurarBotao(btnVoltar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO);
        btnVoltar.addActionListener(e -> dispose());
        painelAcoes.add(btnVoltar, BorderLayout.WEST);

        JPanel painelBotoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoesDireita.setBackground(COR_PAINEL_CONTEUDO);

        JButton btnAdicionar = new JButton("ADICIONAR NOVO");
        configurarBotao(btnAdicionar, COR_BOTAO_PRIMARIO, COR_TEXTO_BOTAO_PRIMARIO);
        btnAdicionar.addActionListener(e -> {
            // Supondo a existência de TelaCadastroUsuario
             new TelaCadastroUsuario(this, controller).setVisible(true);
            carregarDadosUsuarios();
        });

        JButton btnEditar = new JButton("EDITAR");
configurarBotao(btnEditar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO);
btnEditar.addActionListener(e -> editarSelecionado());

        JButton btnRemover = new JButton("REMOVER");
        configurarBotao(btnRemover, new Color(213, 0, 0), Color.WHITE);
        btnRemover.addActionListener(e -> removerSelecionado());

        JButton btnAtualizar = new JButton("ATUALIZAR");
        configurarBotao(btnAtualizar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO);
        btnAtualizar.addActionListener(e -> carregarDadosUsuarios());

        painelBotoesDireita.add(btnAtualizar);
        painelBotoesDireita.add(btnEditar);
        painelBotoesDireita.add(btnRemover);
        painelBotoesDireita.add(btnAdicionar);

        painelAcoes.add(painelBotoesDireita, BorderLayout.CENTER);

        painel.add(painelAcoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarAbaRelatorios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        painel.setBackground(COR_PAINEL_CONTEUDO);
        painel.setForeground(COR_TEXTO);

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelFiltro.setBackground(COR_PAINEL_CONTEUDO.darker());
        painelFiltro.setBorder(new EmptyBorder(10, 10, 10, 10));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        JLabel lblDataInicio = new JLabel("Data Início:");
        lblDataInicio.setForeground(COR_TEXTO);
        painelFiltro.add(lblDataInicio);

        txtDataInicio = new JTextField(LocalDate.now().minusDays(7).format(formatter), 10);
        configuraCampoFiltro(txtDataInicio);
        painelFiltro.add(txtDataInicio);

        JLabel lblDataFim = new JLabel("Data Fim:");
        lblDataFim.setForeground(COR_TEXTO);
        painelFiltro.add(lblDataFim);

        txtDataFim = new JTextField(LocalDate.now().format(formatter), 10);
        configuraCampoFiltro(txtDataFim);
        painelFiltro.add(txtDataFim);

        JButton btnGerar = new JButton("GERAR RELATÓRIO");
        configurarBotao(btnGerar, COR_BOTAO_PRIMARIO, COR_TEXTO_BOTAO_PRIMARIO);
        btnGerar.addActionListener(e -> carregarRelatorio());
        painelFiltro.add(btnGerar);

        JButton btnVoltar = new JButton("VOLTAR / SAIR");
        configurarBotao(btnVoltar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO);
        btnVoltar.addActionListener(e -> dispose());
        painelFiltro.add(btnVoltar);

        painel.add(painelFiltro, BorderLayout.NORTH);

        modelRelatorio = new DefaultTableModel(
                new String[] { "ID", "Data e Hora", "ID Usuário", "Nome Usuário", "Status", "Origem" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabelaRelatorio = new JTable(modelRelatorio);
        configurarTabela(tabelaRelatorio, new ZebraTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tabelaRelatorio);
        scrollPane.setBorder(BorderFactory.createLineBorder(COR_LINHA_GRADE, 1));
        scrollPane.getViewport().setBackground(COR_PAINEL_CONTEUDO.brighter());
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    private void configuraCampoFiltro(JTextField campo) {
        campo.setFont(FONTE_PADRAO);
        campo.setBackground(COR_CABECALHO_TABELA.darker());
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_DESTAQUE_BOTAO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_LINHA_GRADE.brighter(), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private void configurarTabela(JTable tabela, DefaultTableCellRenderer renderer) {
        tabela.setRowSorter(new TableRowSorter<>(tabela.getModel()));
        tabela.setRowHeight(28);
        tabela.setFont(FONTE_PADRAO);
        tabela.setGridColor(COR_LINHA_GRADE);
        tabela.setForeground(COR_TEXTO);
        tabela.setBackground(COR_PAINEL_CONTEUDO.brighter());

        tabela.setSelectionBackground(COR_SELECAO);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setDefaultRenderer(Object.class, renderer);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(FONTE_CABECALHO);
        header.setBackground(COR_CABECALHO_TABELA);
        header.setForeground(COR_TEXTO);
        header.setReorderingAllowed(false);
    }

    private void configurarBotao(JButton botao, Color corFundo, Color corTexto) {
        botao.setFont(FONTE_BOTAO);
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        applyButtonHoverEffect(botao, corFundo.brighter(), corTexto);
    }

   

   private void editarSelecionado() {
    int viewIndex = tabelaUsuarios.getSelectedRow();
    if (viewIndex == -1) {
        JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.", "Aviso",
                JOptionPane.WARNING_MESSAGE);
        return;
    }
    int modelIndex = tabelaUsuarios.convertRowIndexToModel(viewIndex);
    // Garante que a linha existe no modelo original
    if (modelIndex < 0 || modelIndex >= modelUsuarios.getRowCount()) return; 

    int id = (Integer) modelUsuarios.getValueAt(modelIndex, 0);

    
    Usuario u = controller.buscarUsuarioPorId(id);
    if (u == null) {
        JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro",
                JOptionPane.ERROR_MESSAGE);
        return;
    }
     
    // Ação Corrigida: Abre a TelaEdicaoUsuario com os dados do usuário
    new TelaEdicaoUsuario(this, controller, u).setVisible(true); 
    
    carregarDadosUsuarios(); 
}

    private void removerSelecionado() {
        int viewIndex = tabelaUsuarios.getSelectedRow();
        if (viewIndex == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para remover.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelIndex = tabelaUsuarios.convertRowIndexToModel(viewIndex);
        int id = (Integer) modelUsuarios.getValueAt(modelIndex, 0);

        int confirmar = JOptionPane.showConfirmDialog(this,
                "Confirma remover o usuário selecionado?", "Remover Usuário",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmar != JOptionPane.YES_OPTION)
            return;

        if (controller.removerUsuario(id)) {
            JOptionPane.showMessageDialog(this, "Usuário removido com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            carregarDadosUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível remover o usuário.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void carregarDadosUsuarios() {
        new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() {
                return controller.solicitarListaDeUsuarios();
            }

            @Override
            protected void done() {
                try {
                    modelUsuarios.setRowCount(0);
                    get().forEach(u -> modelUsuarios.addRow(new Object[] {
                            u.getId(), u.getNome(), u.getCpf(),
                            // Adaptação para classes model
                           // CÓDIGO CORRIGIDO:
                            (u instanceof Administrador) ? "Administrador" : "Funcionário",
                            u.isAtivo() ? "Ativo" : "Inativo"
                    }));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaGestao.this, "Erro ao carregar usuários: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void carregarRelatorio() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime inicio = LocalDate.parse(txtDataInicio.getText(), formatter).atStartOfDay();
            LocalDateTime fim = LocalDate.parse(txtDataFim.getText(), formatter).atTime(LocalTime.MAX);

            new SwingWorker<List<RegistroAcesso>, Void>() {
                @Override
                protected List<RegistroAcesso> doInBackground() {
                    return controller.solicitarRelatorioAcesso(inicio, fim);
                }

                @Override
                protected void done() {
                    try {
                        modelRelatorio.setRowCount(0);
                        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        get().forEach(r -> modelRelatorio.addRow(new Object[] {
                                r.getId(),
                                r.getDataHora().format(displayFormatter),
                                r.getUsuarioId() == 0 ? "N/A" : r.getUsuarioId(),
                                r.getNomeUsuario() == null ? "Usuário Removido" : r.getNomeUsuario(),
                                r.getStatus(),
                                r.getOrigem()
                        }));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TelaGestao.this, "Erro ao carregar relatório: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/mm/aaaa.", "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleMaximize() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    private MouseAdapter createDraggableMouseAdapter() {
        return new MouseAdapter() {
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
    }

    private void applyButtonHoverEffect(JButton button, Color hoverColor, Color defaultColor) {
        // Remove listeners para evitar duplicação (MouseListener agora está importado)
        for (MouseListener l : button.getMouseListeners()) {
            if (l instanceof MouseAdapter)
                button.removeMouseListener(l);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isContentAreaFilled()) {
                    button.setBackground(hoverColor);
                    // Garante que o texto fique branco para botões preenchidos
                    button.setForeground(Color.WHITE);
                } else {
                    // Botões de controle (sem preenchimento)
                    button.setForeground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isContentAreaFilled()) {
                    // Se for o botão REMOVER (vermelho)
                    if (button.getBackground().equals(new Color(213, 0, 0).brighter())) {
                        button.setBackground(new Color(213, 0, 0));
                        button.setForeground(Color.WHITE);
                    }
                    // Se for o botão PRIMÁRIO (azul)
                    else if (button.getBackground().equals(COR_BOTAO_PRIMARIO.brighter())) {
                        button.setBackground(COR_BOTAO_PRIMARIO);
                        button.setForeground(COR_TEXTO_BOTAO_PRIMARIO);
                    }
                    // Se for o botão SECUNDÁRIO (cinza)
                    else if (button.getBackground().equals(COR_BOTAO_SECUNDARIO.brighter())) {
                        button.setBackground(COR_BOTAO_SECUNDARIO);
                        button.setForeground(COR_TEXTO_BOTAO_SECUNDARIO);
                    }
                } else {
                    // Caso dos botões de controle (não preenchidos)
                    button.setForeground(defaultColor);
                }
            }
        });
    }

    // --- Renderizadores de Tabela Adaptados ao Dark Mode ---

    static class StatusCellRenderer extends ZebraTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                if (table.getColumnName(column).equals("Status")) {
                    String status = (String) value;
                    if ("Ativo".equals(status)) {
                        c.setForeground(new Color(0, 200, 83));
                    } else if ("Inativo".equals(status)) {
                        c.setForeground(new Color(213, 0, 0));
                    }
                } else {
                    c.setForeground(table.getForeground());
                }
            }
            return c;
        }
    }

    static class ZebraTableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final Color COR_LINHA_PAR = new Color(50, 50, 50);
        private static final Color COR_LINHA_IMPAR = new Color(60, 60, 60);
        private static final Color COR_TEXTO = new Color(200, 200, 200);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? COR_LINHA_PAR : COR_LINHA_IMPAR);
                c.setForeground(COR_TEXTO);
            }
            return c;
        }
    }

    private static class FingerprintIconPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final Color COR_TEXTO = new Color(200, 200, 200);

        public FingerprintIconPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int diametro = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;
            g2d.setColor(COR_TEXTO);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 4; i++) {
                int d = diametro - (i * (diametro / 4));
                int arcX = x + (i * (diametro / 8));
                int arcY = y + (i * (diametro / 8));
                g2d.drawArc(arcX, arcY, d, d, -45 - (i * 10), 270 + (i * 5));
            }
            g2d.dispose();
        }
    }
}