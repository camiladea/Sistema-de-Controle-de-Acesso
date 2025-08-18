package view;

import controller.TerminalController;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.List;

public class TelaGestao extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final transient TerminalController controller;
    private final DefaultTableModel modelUsuarios;
    private final DefaultTableModel modelRelatorio;
    private final JTextField txtDataInicio, txtDataFim;

    public TelaGestao(Window owner, TerminalController controller) {
        super(owner, "Painel de Gestão", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        setSize(900, 650);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // --- Aba Usuários ---
        JPanel painelUsuarios = new JPanel(new BorderLayout(10, 10));
        modelUsuarios = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Tipo", "Status"}, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaUsuarios = new JTable(modelUsuarios);
        tabelaUsuarios.setRowSorter(new TableRowSorter<>(modelUsuarios));
        painelUsuarios.add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);
        
        JPanel painelAcoesUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdicionarUsuario = new JButton("Adicionar Novo Usuário");
        btnAdicionarUsuario.addActionListener(e -> {
            TelaCadastroUsuario telaCadastro = new TelaCadastroUsuario(this, controller);
            telaCadastro.setVisible(true);
            carregarDadosUsuarios(); // Atualiza a lista após o cadastro
        });
        JButton btnAtualizarUsuarios = new JButton("Atualizar Lista");
        btnAtualizarUsuarios.addActionListener(e -> carregarDadosUsuarios());
        painelAcoesUsuario.add(btnAdicionarUsuario);
        painelAcoesUsuario.add(btnAtualizarUsuarios);
        painelUsuarios.add(painelAcoesUsuario, BorderLayout.SOUTH);

        // --- Aba Relatórios ---
        JPanel painelRelatorios = new JPanel(new BorderLayout(10, 10));
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        painelFiltro.add(new JLabel("Data Início:"));
        txtDataInicio = new JTextField(LocalDate.now().minusDays(7).format(formatter), 10);
        painelFiltro.add(txtDataInicio);
        painelFiltro.add(new JLabel("Data Fim:"));
        txtDataFim = new JTextField(LocalDate.now().format(formatter), 10);
        painelFiltro.add(txtDataFim);
        JButton btnGerarRelatorio = new JButton("Gerar Relatório");
        btnGerarRelatorio.addActionListener(e -> carregarRelatorio());
        painelFiltro.add(btnGerarRelatorio);
        painelRelatorios.add(painelFiltro, BorderLayout.NORTH);
        
        modelRelatorio = new DefaultTableModel(new String[]{"ID", "Data e Hora", "ID Usuário", "Status", "Origem"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaRelatorio = new JTable(modelRelatorio);
        painelRelatorios.add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);

        abas.addTab("Gerenciamento de Usuários", painelUsuarios);
        abas.addTab("Relatório de Acessos", painelRelatorios);
        add(abas, BorderLayout.CENTER);
        
        carregarDadosUsuarios();
    }

    private void carregarDadosUsuarios() {
        new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() { return controller.solicitarListaDeUsuarios(); }
            @Override
            protected void done() {
                try {
                    modelUsuarios.setRowCount(0);
                    get().forEach(u -> modelUsuarios.addRow(new Object[]{u.getId(), u.getNome(), u.getCpf(), (u instanceof Funcionario) ? "Funcionário" : "Admin", u.isAtivo() ? "Ativo" : "Inativo"}));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaGestao.this, "Erro ao carregar usuários.", "Erro", JOptionPane.ERROR_MESSAGE);
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
                protected List<RegistroAcesso> doInBackground() { return controller.solicitarRelatorioAcesso(inicio, fim); }
                @Override
                protected void done() {
                    try {
                        modelRelatorio.setRowCount(0);
                        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        get().forEach(r -> modelRelatorio.addRow(new Object[]{r.getId(), r.getDataHora().format(displayFormatter), r.getUsuarioId() == 0 ? "N/A" : r.getUsuarioId(), r.getStatus(), r.getOrigem()}));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TelaGestao.this, "Erro ao carregar relatório.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/mm/aaaa.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}