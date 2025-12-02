package appgadget_grafica;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RankingClientesFrame extends JFrame {

    public RankingClientesFrame(MainFrame parent, RegistroFacturas rf) {
        super("Ranking de Clientes por Gasto");

        initComponents(parent, rf);
        setSize(800, 600);
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

    private void initComponents(MainFrame parent, RegistroFacturas rf) {
        Factura[] facturas = rf.getFacturas();

        if (facturas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay facturas registradas en el sistema",
                    "Sin Datos",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        ArrayList<String> nombresClientes = new ArrayList<>();
        ArrayList<Double> totalesClientes = new ArrayList<>();
        ArrayList<Integer> facturasClientes = new ArrayList<>();

        for (Factura factura : facturas) {
            if (factura != null) {
                String nombre = factura.getCliente().getNombre();
                double total = factura.getTotalConDescuento();

                int index = -1;
                for (int i = 0; i < nombresClientes.size(); i++) {
                    if (nombresClientes.get(i).equals(nombre)) {
                        index = i;
                        break;
                    }
                }

                if (index == -1) {
                    nombresClientes.add(nombre);
                    totalesClientes.add(total);
                    facturasClientes.add(1);
                } else {
                    totalesClientes.set(index, totalesClientes.get(index) + total);
                    facturasClientes.set(index, facturasClientes.get(index) + 1);
                }
            }
        }

        for (int i = 0; i < totalesClientes.size() - 1; i++) {
            for (int j = i + 1; j < totalesClientes.size(); j++) {
                if (totalesClientes.get(i) < totalesClientes.get(j)) {
                    String tempNombre = nombresClientes.get(i);
                    nombresClientes.set(i, nombresClientes.get(j));
                    nombresClientes.set(j, tempNombre);

                    double tempTotal = totalesClientes.get(i);
                    totalesClientes.set(i, totalesClientes.get(j));
                    totalesClientes.set(j, tempTotal);

                    int tempFacturas = facturasClientes.get(i);
                    facturasClientes.set(i, facturasClientes.get(j));
                    facturasClientes.set(j, tempFacturas);
                }
            }
        }

        String[] columnas = {"Posición", "Cliente", "Total Gastado", "Facturas", "Promedio por Factura"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        double totalGeneral = 0;

        for (int i = 0; i < nombresClientes.size(); i++) {
            String cliente = nombresClientes.get(i);
            double totalGastado = totalesClientes.get(i);
            int numFacturas = facturasClientes.get(i);
            double promedio = totalGastado / numFacturas;

            model.addRow(new Object[]{
                i + 1,
                cliente,
                String.format("Bs %.2f", totalGastado),
                numFacturas,
                String.format("Bs %.2f", promedio)
            });

            totalGeneral += totalGastado;
        }

        JTable tabla = new JTable(model);
        tabla.setRowHeight(30);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statsPanel.add(crearStatCard("Total Clientes", String.valueOf(nombresClientes.size())));
        statsPanel.add(crearStatCard("Total General", String.format("Bs %.2f", totalGeneral)));

        if (!nombresClientes.isEmpty()) {
            statsPanel.add(crearStatCard("Mejor Cliente", nombresClientes.get(0)));
            statsPanel.add(crearStatCard("Mejor Gasto", String.format("Bs %.2f", totalesClientes.get(0))));
        }
        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnCerrar);

        setLayout(new BorderLayout());
        add(statsPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel crearStatCard(String titulo, String valor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(150, 80));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 14));
        lblValor.setForeground(new Color(41, 128, 185));
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblValor);
        card.add(Box.createVerticalGlue());

        return card;
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

