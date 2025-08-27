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

    private static final long serialVersionUID = 1L;

    private final transient TerminalController controller;
    private final DefaultTableModel modelUsuarios;
    private final DefaultTableModel modelRelatorio;
    private final JTextField txtDataInicio, txtDataFim;

    // Agora a tabela é atributo para ser usada pelos botões
    private JTable tabelaUsuarios;

    public TelaGestao(Window owner, TerminalController controller) {
        super(owner, "Painel de Gestão", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        setSize(1116, 762);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // --- Aba Usuários ---
        JPanel painelUsuarios = new JPanel(new BorderLayout(10, 10));

        modelUsuarios = new DefaultTableModel(new String[] { "ID", "Nome", "CPF", "Tipo", "Status" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class;
                    default -> String.class;
                };
            }
        };

        tabelaUsuarios = new JTable(modelUsuarios);
        tabelaUsuarios.setRowSorter(new TableRowSorter<>(modelUsuarios));
        painelUsuarios.add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);

        JPanel painelAcoesUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));

        JButton btnAdicionarUsuario = new JButton("Adicionar Novo Usuário");
        btnAdicionarUsuario.addActionListener(e -> {
            TelaCadastroUsuario telaCadastro = new TelaCadastroUsuario(this, controller);
            telaCadastro.setVisible(true);
            carregarDadosUsuarios(); // Atualiza a lista após o cadastro
        });

        JButton btnEditarUsuario = new JButton("Editar");
        btnEditarUsuario.addActionListener(e -> editarSelecionado());

        JButton btnRemoverUsuario = new JButton("Remover");
        btnRemoverUsuario.addActionListener(e -> removerSelecionado());

        JButton btnAtualizarUsuarios = new JButton("Atualizar Lista");
        btnAtualizarUsuarios.addActionListener(e -> carregarDadosUsuarios());

        // ADICIONA TODOS NO MESMO PAINEL DAS AÇÕES
        painelAcoesUsuario.add(btnAdicionarUsuario);
        painelAcoesUsuario.add(btnEditarUsuario);
        painelAcoesUsuario.add(btnRemoverUsuario);
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

        modelRelatorio = new DefaultTableModel(new String[] { "ID", "Data e Hora", "ID Usuário", "Status", "Origem" },
                0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabelaRelatorio = new JTable(modelRelatorio);
        painelRelatorios.add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);

        abas.addTab("Gerenciamento de Usuários", painelUsuarios);
        abas.addTab("Relatório de Acessos", painelRelatorios);
        getContentPane().add(abas, BorderLayout.CENTER);

        carregarDadosUsuarios();
    }

    // ----- AÇÕES DOS BOTÕES -----

    private void editarSelecionado() {
        int viewIndex = tabelaUsuarios.getSelectedRow();
        if (viewIndex == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.");
            return;
        }
        int modelIndex = tabelaUsuarios.convertRowIndexToModel(viewIndex);
        int id = (Integer) modelUsuarios.getValueAt(modelIndex, 0);

        Usuario u = controller.buscarUsuarioPorId(id);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TelaEdicaoUsuario tela = new TelaEdicaoUsuario(this, controller, u);
        tela.setVisible(true);
        carregarDadosUsuarios();
    }

    private void removerSelecionado() {
        int viewIndex = tabelaUsuarios.getSelectedRow();
        if (viewIndex == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para remover.");
            return;
        }
        int modelIndex = tabelaUsuarios.convertRowIndexToModel(viewIndex);
        int id = (Integer) modelUsuarios.getValueAt(modelIndex, 0);

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "Confirma remover o usuário selecionado?",
                "Remover Usuário",
                JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION)
            return;

        boolean ok = controller.removerUsuario(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Usuário removido com sucesso!");
            carregarDadosUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível remover o usuário.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----- CARREGAMENTO DE DADOS -----

    private void carregarDadosUsuarios() {
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
                            u.getId(),
                            u.getNome(),
                            u.getCpf(),
                            (u instanceof Funcionario) ? "Funcionário" : "Admin",
                            u.isAtivo() ? "Ativo" : "Inativo"
                    }));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaGestao.this,
                            "Erro ao carregar usuários.", "Erro", JOptionPane.ERROR_MESSAGE);
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
                                r.getStatus(),
                                r.getOrigem()
                        }));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TelaGestao.this,
                                "Erro ao carregar relatório.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de data inválido. Use dd/mm/aaaa.",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}
