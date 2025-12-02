package appgadget_grafica;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class VerFacturasFrame extends JFrame {

    private RegistroFacturas rf;
    private DefaultTableModel model;

    public VerFacturasFrame(MainFrame parent, RegistroFacturas rf) {
        super("Todas las Facturas");
        this.rf = rf;

        initComponents();
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(this.getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(baseColor.darker().darker());
                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.getBounds().contains(e.getPoint())) {
                    button.setBackground(baseColor.darker());
                } else {
                    button.setBackground(baseColor);
                }
                button.repaint();
            }
        });

        return button;
    }

    private void initComponents() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        filterPanel.setBackground(new Color(240, 240, 240));

        JTextField txtFiltro = new JTextField(20);
        JButton btnFiltrar = createStyledButton("Filtrar por Cliente", new Color(52, 152, 219));
        JButton btnVerTodas = createStyledButton("Ver Todas", new Color(46, 204, 113));

        filterPanel.add(new JLabel("Cliente:"));
        filterPanel.add(txtFiltro);
        filterPanel.add(btnFiltrar);
        filterPanel.add(btnVerTodas);

        String[] columnas = {"N° Factura", "Fecha", "Cliente", "CI", "Subtotal", "Descuento", "Total"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return String.class;
                    case 4:
                        return Double.class;
                    case 5:
                        return String.class;
                    case 6:
                        return Double.class;
                    default:
                        return Object.class;
                }
            }
        };

        JTable tabla = new JTable(model);
        tabla.setRowHeight(30);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 12));

        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                c.setForeground(Color.BLACK);

                if (column == 6) {
                    setFont(new Font("Arial", Font.BOLD, 12));
                } else if (column == 4) {
                    setFont(new Font("Arial", Font.PLAIN, 12));
                } else {
                    setFont(new Font("Arial", Font.PLAIN, 12));
                }

                return c;
            }
        });

        mostrarTodasFacturas();

        btnFiltrar.addActionListener(e -> {
            String cliente = txtFiltro.getText().trim();
            if (!cliente.isEmpty()) {
                filtrarFacturas(cliente);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ingrese un nombre de cliente para filtrar",
                        "Campo vacío",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnVerTodas.addActionListener(e -> {
            mostrarTodasFacturas();
            txtFiltro.setText("");
        });

        JButton btnDetalle = createStyledButton("Ver Detalle", new Color(155, 89, 182));
        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                int numFactura = (int) model.getValueAt(row, 0);
                mostrarDetalleFactura(numFactura);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Seleccione una factura de la tabla",
                        "Selección requerida",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(btnDetalle);
        buttonPanel.add(btnCerrar);

        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void mostrarTodasFacturas() {
        model.setRowCount(0);
        Factura[] facturas = rf.getFacturas();

        if (facturas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay facturas registradas en el sistema",
                    "Sin datos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Arrays.sort(facturas, (f1, f2) -> Integer.compare(f2.getNumero(), f1.getNumero()));

        double totalGeneral = 0;
        int facturasConDescuento = 0;

        for (Factura factura : facturas) {
            if (factura != null) {
                model.addRow(new Object[]{
                    factura.getNumero(),
                    factura.getFecha(),
                    factura.getCliente().getNombre(),
                    factura.getCliente().getCi(),
                    factura.getTotal(),
                    factura.getDescuento() > 0 ? factura.getDescuento() + "%" : "0%",
                    factura.getTotalConDescuento()
                });
                totalGeneral += factura.getTotalConDescuento();
                if (factura.getDescuento() > 0) {
                    facturasConDescuento++;
                }
            }
        }

        setTitle("Todas las Facturas (" + facturas.length + " facturas, Total: Bs "
                + String.format("%.2f", totalGeneral) + ")");

        System.out.println("Mostrando " + facturas.length + " facturas - Total general: Bs "
                + String.format("%.2f", totalGeneral) + " - Con descuento: " + facturasConDescuento);
    }

    private void filtrarFacturas(String cliente) {
        model.setRowCount(0);
        Factura[] facturas = rf.getFacturasPorCliente(cliente);

        if (facturas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron facturas para: " + cliente,
                    "Sin Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        double totalCliente = 0;

        for (Factura factura : facturas) {
            if (factura != null) {
                model.addRow(new Object[]{
                    factura.getNumero(),
                    factura.getFecha(),
                    factura.getCliente().getNombre(),
                    factura.getCliente().getCi(),
                    factura.getTotal(),
                    factura.getDescuento() > 0 ? factura.getDescuento() + "%" : "0%",
                    factura.getTotalConDescuento()
                });
                totalCliente += factura.getTotalConDescuento();
            }
        }

        setTitle("Facturas de " + cliente + " (" + facturas.length + " facturas, Total: Bs "
                + String.format("%.2f", totalCliente) + ")");

        System.out.println(facturas.length + " facturas para " + cliente
                + " - Total: Bs " + String.format("%.2f", totalCliente));
    }

    private void mostrarDetalleFactura(int numFactura) {
        Factura[] facturas = rf.getFacturas();

        for (Factura factura : facturas) {
            if (factura != null && factura.getNumero() == numFactura) {
                JFrame detalleFrame = new JFrame("Detalle Factura #" + numFactura);
                detalleFrame.setSize(500, 500);
                detalleFrame.setLocationRelativeTo(this);

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                StringBuilder sb = new StringBuilder();
                sb.append("====================================\n");
                sb.append("               FACTURA         \n");
                sb.append("====================================\n");
                sb.append("Factura #").append(factura.getNumero()).append("\n");
                sb.append("Fecha: ").append(factura.getFecha()).append("\n");
                sb.append("Cliente: ").append(factura.getCliente().getNombre()).append("\n");
                sb.append("CI: ").append(factura.getCliente().getCi()).append("\n");
                sb.append("Correo: ").append(factura.getCliente().getCorreo()).append("\n\n");
                sb.append("PRODUCTOS:\n");
                sb.append("------------------------------------\n");

                for (int i = 0; i < factura.getCantidadPedidos(); i++) {
                    Pedido pedido = factura.getPedidos()[i];
                    sb.append(String.format("%-20s %-3d Bs %-8.2f\n",
                            pedido.getProducto(),
                            pedido.getCantidad(),
                            pedido.getTotal()));
                }

                sb.append("------------------------------------\n");
                sb.append(String.format("Subtotal: Bs %10.2f\n", factura.getTotal()));

                if (factura.getDescuento() > 0) {
                    sb.append(String.format("Descuento (%d%%): -Bs %8.2f\n",
                            (int) factura.getDescuento(),
                            factura.getTotal() * (factura.getDescuento() / 100)));
                }

                sb.append("------------------------------------\n");
                sb.append(String.format("TOTAL: Bs %10.2f\n", factura.getTotalConDescuento()));
                sb.append("====================================\n");

                textArea.setText(sb.toString());

                JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
                btnCerrar.addActionListener(e -> detalleFrame.dispose());

                detalleFrame.setLayout(new BorderLayout());
                detalleFrame.add(new JScrollPane(textArea), BorderLayout.CENTER);
                detalleFrame.add(btnCerrar, BorderLayout.SOUTH);
                detalleFrame.setVisible(true);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Factura #" + numFactura + " no encontrada",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

