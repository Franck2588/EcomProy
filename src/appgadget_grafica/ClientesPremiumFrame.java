package appgadget_grafica;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ClientesPremiumFrame extends JFrame {

    public ClientesPremiumFrame(MainFrame parent, RegistroFacturas rf) {
        super("Clientes con Compras Superiores");

        String input = JOptionPane.showInputDialog(this,
                "Ingrese el monto mínimo (Bs):",
                "Monto Mínimo",
                JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                double montoMinimo = Double.parseDouble(input.trim());
                initComponents(parent, rf, montoMinimo);
                setSize(700, 500);
                setLocationRelativeTo(parent);
                setVisible(true);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un monto válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            dispose();
        }
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

    private void initComponents(MainFrame parent, RegistroFacturas rf, double montoMinimo) {
        Factura[] facturas = rf.getFacturas();

        ArrayList<String> nombresClientes = new ArrayList<>();
        ArrayList<Double> totalesClientes = new ArrayList<>();
        ArrayList<Integer> cantidadFacturas = new ArrayList<>();
        ArrayList<ArrayList<Factura>> facturasPorCliente = new ArrayList<>();

        for (Factura factura : facturas) {
            if (factura != null) {
                String nombre = factura.getCliente().getNombre();
                double total = factura.getTotalConDescuento();

                int index = nombresClientes.indexOf(nombre);
                if (index == -1) {
                    nombresClientes.add(nombre);
                    totalesClientes.add(total);
                    cantidadFacturas.add(1);

                    ArrayList<Factura> listaFacturas = new ArrayList<>();
                    listaFacturas.add(factura);
                    facturasPorCliente.add(listaFacturas);
                } else {
                    totalesClientes.set(index, totalesClientes.get(index) + total);
                    cantidadFacturas.set(index, cantidadFacturas.get(index) + 1);
                    facturasPorCliente.get(index).add(factura);
                }
            }
        }

        ArrayList<String> clientesPremium = new ArrayList<>();
        ArrayList<Double> totalesPremium = new ArrayList<>();
        ArrayList<Integer> facturasPremium = new ArrayList<>();
        ArrayList<ArrayList<Factura>> detallePremium = new ArrayList<>();

        for (int i = 0; i < nombresClientes.size(); i++) {
            if (totalesClientes.get(i) > montoMinimo) {
                clientesPremium.add(nombresClientes.get(i));
                totalesPremium.add(totalesClientes.get(i));
                facturasPremium.add(cantidadFacturas.get(i));
                detallePremium.add(facturasPorCliente.get(i));
            }
        }

        if (clientesPremium.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay clientes con compras superiores a Bs " + String.format("%.2f", montoMinimo),
                    "Sin Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        for (int i = 0; i < totalesPremium.size() - 1; i++) {
            for (int j = i + 1; j < totalesPremium.size(); j++) {
                if (totalesPremium.get(i) < totalesPremium.get(j)) {
                    String tempNombre = clientesPremium.get(i);
                    clientesPremium.set(i, clientesPremium.get(j));
                    clientesPremium.set(j, tempNombre);

                    double tempTotal = totalesPremium.get(i);
                    totalesPremium.set(i, totalesPremium.get(j));
                    totalesPremium.set(j, tempTotal);

                    int tempFacturas = facturasPremium.get(i);
                    facturasPremium.set(i, facturasPremium.get(j));
                    facturasPremium.set(j, tempFacturas);

                    ArrayList<Factura> tempDetalle = detallePremium.get(i);
                    detallePremium.set(i, detallePremium.get(j));
                    detallePremium.set(j, tempDetalle);
                }
            }
        }

        String[] columnas = {"Cliente", "Total Gastado", "Facturas", "Promedio por Factura"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        for (int i = 0; i < clientesPremium.size(); i++) {
            String cliente = clientesPremium.get(i);
            double totalGastado = totalesPremium.get(i);
            int numFacturas = facturasPremium.get(i);
            double promedio = totalGastado / numFacturas;

            model.addRow(new Object[]{
                cliente,
                String.format("Bs %.2f", totalGastado),
                numFacturas,
                String.format("Bs %.2f", promedio)
            });
        }

        JTable tabla = new JTable(model);
        tabla.setRowHeight(30);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Monto mínimo: Bs " + String.format("%.2f", montoMinimo)));
        infoPanel.add(new JLabel("-  Clientes premium: " + clientesPremium.size()));

        JButton btnDetalle = createStyledButton("Ver Historial del Cliente", new Color(52, 152, 219));
        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                String cliente = (String) model.getValueAt(row, 0);
                int index = clientesPremium.indexOf(cliente);
                if (index != -1) {
                    mostrarHistorialCliente(cliente, detallePremium.get(index));
                }
            }
        });

        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnDetalle);
        buttonPanel.add(btnCerrar);

        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void mostrarHistorialCliente(String cliente, ArrayList<Factura> facturas) {
        JFrame historialFrame = new JFrame("Historial de " + cliente);
        historialFrame.setSize(600, 400);
        historialFrame.setLocationRelativeTo(this);

        for (int i = 0; i < facturas.size() - 1; i++) {
            for (int j = i + 1; j < facturas.size(); j++) {
                Factura f1 = facturas.get(i);
                Factura f2 = facturas.get(j);

                String[] p1 = f1.getFecha().split("/");
                String[] p2 = f2.getFecha().split("/");

                int año1 = Integer.parseInt(p1[2]);
                int año2 = Integer.parseInt(p2[2]);
                if (año1 < año2) {
                    Collections.swap(facturas, i, j);
                } else if (año1 == año2) {
                    int mes1 = Integer.parseInt(p1[1]);
                    int mes2 = Integer.parseInt(p2[1]);
                    if (mes1 < mes2) {
                        Collections.swap(facturas, i, j);
                    } else if (mes1 == mes2) {
                        int dia1 = Integer.parseInt(p1[0]);
                        int dia2 = Integer.parseInt(p2[0]);
                        if (dia1 < dia2) {
                            Collections.swap(facturas, i, j);
                        }
                    }
                }
            }
        }

        String[] columnas = {"Fecha", "Factura", "Productos", "Total"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        double totalCliente = 0;

        for (Factura factura : facturas) {
            StringBuilder productos = new StringBuilder();
            for (int i = 0; i < factura.getCantidadPedidos(); i++) {
                if (i > 0) {
                    productos.append("- ");
                }
                productos.append(factura.getPedidos()[i].getProducto());
            }

            model.addRow(new Object[]{
                factura.getFecha(),
                factura.getNumero(),
                productos.toString(),
                String.format("Bs %.2f", factura.getTotalConDescuento())
            });

            totalCliente += factura.getTotalConDescuento();
        }

        JTable tabla = new JTable(model);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.add(new JLabel("Total gastado: Bs " + String.format("%.2f", totalCliente)));
        summaryPanel.add(new JLabel("-  Facturas: " + facturas.size()));

        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> historialFrame.dispose());

        historialFrame.setLayout(new BorderLayout());
        historialFrame.add(summaryPanel, BorderLayout.NORTH);
        historialFrame.add(new JScrollPane(tabla), BorderLayout.CENTER);
        historialFrame.add(btnCerrar, BorderLayout.SOUTH);

        historialFrame.setVisible(true);
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
