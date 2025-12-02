package appgadget_grafica;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PedidosClienteFrame extends JFrame {

    public PedidosClienteFrame(MainFrame parent, RegistroFacturas rf) {
        super("Pedidos por Cliente");

        String cliente = JOptionPane.showInputDialog(this,
                "Ingrese el nombre del cliente:",
                "Buscar Cliente",
                JOptionPane.QUESTION_MESSAGE);

        if (cliente != null && !cliente.trim().isEmpty()) {
            initComponents(parent, rf, cliente.trim());
            setSize(800, 600);
            setLocationRelativeTo(parent);
            setVisible(true);
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

    private void initComponents(MainFrame parent, RegistroFacturas rf, String cliente) {
        Factura[] facturas = rf.getFacturasPorCliente(cliente);

        if (facturas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron facturas para el cliente: " + cliente,
                    "Sin Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        Arrays.sort(facturas, (f1, f2) -> {
            String[] p1 = f1.getFecha().split("/");
            String[] p2 = f2.getFecha().split("/");

            int año1 = Integer.parseInt(p1[2]);
            int año2 = Integer.parseInt(p2[2]);
            if (año1 != año2) {
                return Integer.compare(año2, año1);
            }

            int mes1 = Integer.parseInt(p1[1]);
            int mes2 = Integer.parseInt(p2[1]);
            if (mes1 != mes2) {
                return Integer.compare(mes2, mes1);
            }

            int dia1 = Integer.parseInt(p1[0]);
            int dia2 = Integer.parseInt(p2[0]);
            return Integer.compare(dia2, dia1);
        });

        String[] columnas = {"Fecha", "Factura", "Producto", "Cantidad", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        double totalCliente = 0;

        for (Factura factura : facturas) {
            for (int i = 0; i < factura.getCantidadPedidos(); i++) {
                Pedido pedido = factura.getPedidos()[i];
                model.addRow(new Object[]{
                    factura.getFecha(),
                    factura.getNumero(),
                    pedido.getProducto(),
                    pedido.getCantidad(),
                    pedido.getTotal()
                });
                totalCliente += pedido.getTotal();
            }
        }

        JTable tabla = new JTable(model);
        tabla.setRowHeight(25);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Cliente: " + cliente));
        infoPanel.add(new JLabel("-  Facturas: " + facturas.length));
        infoPanel.add(new JLabel("-  Total gastado: Bs " + String.format("%.2f", totalCliente)));

        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnCerrar);

        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

