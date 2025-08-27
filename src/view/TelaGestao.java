package view;

import controller.TerminalController;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import model.*;

public class TelaGestao extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient TerminalController controller;
    private DefaultTableModel modelUsuarios;
    private DefaultTableModel modelRelatorio;
    private JTextField txtDataInicio, txtDataFim;
    private JTable tabelaUsuarios;

    private static final Color COR_FUNDO = new Color(240, 242, 245);
    private static final Color COR_PAINEL_CONTEUDO = Color.WHITE;
    private static final Color COR_CABECALHO_TABELA = new Color(220, 223, 228);
    private static final Color COR_BOTAO_PRIMARIO = new Color(24, 119, 242);
    private static final Color COR_BOTAO_SECUNDARIO = new Color(230, 232, 235);
    private static final Color COR_TEXTO_BOTAO_PRIMARIO = Color.WHITE;
    private static final Color COR_TEXTO_BOTAO_SECUNDARIO = new Color(40, 40, 40);
    private static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 12);

    public TelaGestao(Window owner, TerminalController controller) {
        super(owner, "Painel de Gestão", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        configurarJanela();
        inicializarComponentes();

        // Carrega os dados iniciais após a interface estar pronta
        carregarDadosUsuarios();
    }

    private void configurarJanela() {
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);
    }

    private void inicializarComponentes() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        abas.setBackground(COR_FUNDO);

        // Cria e adiciona as abas
        JPanel painelUsuarios = criarAbaUsuarios();
        JPanel painelRelatorios = criarAbaRelatorios();

        abas.addTab("Gerenciamento de Usuários", painelUsuarios);
        abas.addTab("Relatório de Acessos", painelRelatorios);

        add(abas, BorderLayout.CENTER);
    }

    private JPanel criarAbaUsuarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        painel.setBackground(COR_FUNDO);

        // --- Tabela de Usuários ---
        modelUsuarios = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Tipo", "Status"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
        };
        tabelaUsuarios = new JTable(modelUsuarios);
        configurarTabela(tabelaUsuarios, new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        painel.add(scrollPane, BorderLayout.CENTER);

        // --- Painel de Ações do Usuário ---
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        painelAcoes.setBackground(COR_FUNDO);

        JButton btnAdicionar = new JButton("ADICIONAR NOVO");
        // new ImageIcon(getClass().getResource("/icons/add.png")) -> Exemplo de como adicionar ícone
        configurarBotao(btnAdicionar, COR_BOTAO_PRIMARIO, COR_TEXTO_BOTAO_PRIMARIO);
        btnAdicionar.addActionListener(e -> {
            new TelaCadastroUsuario(this, controller).setVisible(true);
            carregarDadosUsuarios();
        });

        JButton btnEditar = new JButton("EDITAR");
        configurarBotao(btnEditar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO);
        btnEditar.addActionListener(e -> editarSelecionado());

        JButton btnRemover = new JButton("REMOVER");
        configurarBotao(btnRemover, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO);
        btnRemover.addActionListener(e -> removerSelecionado());
        
        JButton btnAtualizar = new JButton("ATUALIZAR");
        configurarBotao(btnAtualizar, COR_BOTAO_SECUNDARIO, COR_TEXTO_BOTAO_SECUNDARIO);
        btnAtualizar.addActionListener(e -> carregarDadosUsuarios());

        painelAcoes.add(btnAtualizar);
        painelAcoes.add(btnEditar);
        painelAcoes.add(btnRemover);
        painelAcoes.add(btnAdicionar);

        painel.add(painelAcoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarAbaRelatorios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        painel.setBackground(COR_FUNDO);

        // --- Painel de Filtro ---
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelFiltro.setBackground(COR_PAINEL_CONTEUDO);
        painelFiltro.setBorder(new EmptyBorder(10,10,10,10));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        painelFiltro.add(new JLabel("Data Início:"));
        txtDataInicio = new JTextField(LocalDate.now().minusDays(7).format(formatter), 10);
        txtDataInicio.setFont(FONTE_PADRAO);
        painelFiltro.add(txtDataInicio);

        painelFiltro.add(new JLabel("Data Fim:"));
        txtDataFim = new JTextField(LocalDate.now().format(formatter), 10);
        txtDataFim.setFont(FONTE_PADRAO);
        painelFiltro.add(txtDataFim);

        JButton btnGerar = new JButton("GERAR RELATÓRIO");
        configurarBotao(btnGerar, COR_BOTAO_PRIMARIO, COR_TEXTO_BOTAO_PRIMARIO);
        btnGerar.addActionListener(e -> carregarRelatorio());
        painelFiltro.add(btnGerar);
        
        painel.add(painelFiltro, BorderLayout.NORTH);

        // --- Tabela de Relatório ---
        modelRelatorio = new DefaultTableModel(new String[]{"ID", "Data e Hora", "ID Usuário", "Status", "Origem"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaRelatorio = new JTable(modelRelatorio);
        configurarTabela(tabelaRelatorio, new ZebraTableCellRenderer()); // Usa o renderer padrão de zebra
        
        JScrollPane scrollPane = new JScrollPane(tabelaRelatorio);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        painel.add(scrollPane, BorderLayout.CENTER);
        
        return painel;
    }

    // ----- MÉTODOS AUXILIARES DE ESTILO -----

    private void configurarTabela(JTable tabela, DefaultTableCellRenderer renderer) {
        tabela.setRowSorter(new TableRowSorter<>(tabela.getModel()));
        tabela.setRowHeight(28);
        tabela.setFont(FONTE_PADRAO);
        tabela.setGridColor(new Color(230, 230, 230));
        tabela.setSelectionBackground(COR_BOTAO_PRIMARIO);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setDefaultRenderer(Object.class, renderer);
        
        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COR_CABECALHO_TABELA);
        header.setForeground(new Color(60, 60, 60));
        header.setReorderingAllowed(false);
    }
    
    private void configurarBotao(JButton botao, Color corFundo, Color corTexto) {
        botao.setFont(FONTE_BOTAO);
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void editarSelecionado() {
        int viewIndex = tabelaUsuarios.getSelectedRow();
        if (viewIndex == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelIndex = tabelaUsuarios.convertRowIndexToModel(viewIndex);
        int id = (Integer) modelUsuarios.getValueAt(modelIndex, 0);

        Usuario u = controller.buscarUsuarioPorId(id);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new TelaEdicaoUsuario(this, controller, u).setVisible(true);
        carregarDadosUsuarios();
    }

    private void removerSelecionado() {
        int viewIndex = tabelaUsuarios.getSelectedRow();
        if (viewIndex == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelIndex = tabelaUsuarios.convertRowIndexToModel(viewIndex);
        int id = (Integer) modelUsuarios.getValueAt(modelIndex, 0);

        int confirmar = JOptionPane.showConfirmDialog(this,
                "Confirma remover o usuário selecionado?", "Remover Usuário",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirmar != JOptionPane.YES_OPTION) return;

        if (controller.removerUsuario(id)) {
            JOptionPane.showMessageDialog(this, "Usuário removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarDadosUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível remover o usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void carregarDadosUsuarios() {
        new SwingWorker<List<Usuario>, Void>() {
            @Override protected List<Usuario> doInBackground() { return controller.solicitarListaDeUsuarios(); }
            @Override protected void done() {
                try {
                    modelUsuarios.setRowCount(0);
                    get().forEach(u -> modelUsuarios.addRow(new Object[]{
                            u.getId(), u.getNome(), u.getCpf(),
                            (u instanceof Funcionario) ? "Funcionário" : "Admin",
                            u.isAtivo() ? "Ativo" : "Inativo"
                    }));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaGestao.this, "Erro ao carregar usuários: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
                @Override protected List<RegistroAcesso> doInBackground() { return controller.solicitarRelatorioAcesso(inicio, fim); }
                @Override protected void done() {
                    try {
                        modelRelatorio.setRowCount(0);
                        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        get().forEach(r -> modelRelatorio.addRow(new Object[]{
                                r.getId(), r.getDataHora().format(displayFormatter),
                                r.getUsuarioId() == 0 ? "N/A" : r.getUsuarioId(),
                                r.getStatus(), r.getOrigem()
                        }));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TelaGestao.this, "Erro ao carregar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/mm/aaaa.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ----- CLASSES INTERNAS DE RENDERIZAÇÃO -----
    
    static class StatusCellRenderer extends ZebraTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                // table.convertColumnIndexToModel(column) é crucial se as colunas forem reordenáveis
                if (table.getColumnName(column).equals("Status")) {
                    String status = (String) value;
                    if ("Ativo".equals(status)) {
                        c.setForeground(new Color(0, 128, 0)); // Verde
                    } else if ("Inativo".equals(status)) {
                        c.setForeground(Color.RED);
                    }
                } else {
                     c.setForeground(table.getForeground());
                }
            }
            return c;
        }
    }
    
    /**
     * Renderizador base que apenas pinta as linhas com cores alternadas.
     */
    static class ZebraTableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final Color COR_LINHA_PAR = new Color(248, 249, 250);
        private static final Color COR_LINHA_IMPAR = Color.WHITE;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? COR_LINHA_PAR : COR_LINHA_IMPAR);
            }
            return c;
        }
    }
}