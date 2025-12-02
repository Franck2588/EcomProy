package appgadget_grafica;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class StockBajoFrame extends JFrame {

    public StockBajoFrame(MainFrame parent, LM_CircularProducto lmProductos) {
        super("Productos con Stock Bajo");

        initComponents(parent, lmProductos);
        setSize(600, 400);
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

    private void initComponents(MainFrame parent, LM_CircularProducto lmProductos) {
        String[] columnas = {"ID", "Categoría", "Producto", "Precio", "Stock", "Valor"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        int contador = 0;
        double valorTotal = 0;

        for (int i = 0; i < lmProductos.getN(); i++) {
            String categoria = lmProductos.getCategoria(i);
            LD_CircularProducto lista = lmProductos.getLista(i);

            if (!lista.esVacia()) {
                NodoProducto R = lista.getP();
                NodoProducto inicio = R;

                do {
                    Producto p = R.getProducto();
                    if (p.getStock() <= 3) {
                        double valor = p.getPrecio() * p.getStock();
                        model.addRow(new Object[]{
                            p.getId(),
                            categoria,
                            p.getNombre(),
                            p.getPrecio(),
                            p.getStock(),
                            valor
                        });
                        contador++;
                        valorTotal += valor;
                    }
                    R = R.getSig();
                } while (R != inicio);
            }
        }

        if (contador == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos con stock bajo",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        JTable tabla = new JTable(model);
        tabla.setRowHeight(25);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 12));

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    int stock = (int) table.getValueAt(row, 4);

                    if (stock == 0) {
                        c.setBackground(new Color(255, 204, 204)); 
                    } else if (stock == 1) {
                        c.setBackground(new Color(255, 229, 204)); 
                    } else if (stock == 2) {
                        c.setBackground(new Color(255, 255, 204)); 
                    } else if (stock == 3) {
                        c.setBackground(new Color(204, 229, 255)); 
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                if (column == 5) { 
                    setFont(new Font("Arial", Font.BOLD, 12));
                    setForeground(new Color(231, 76, 60)); 
                }

                return c;
            }
        });

        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);  
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100); 
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150); 
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);  
        tabla.getColumnModel().getColumn(4).setPreferredWidth(60);  
        tabla.getColumnModel().getColumn(5).setPreferredWidth(90);  

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(new Color(240, 240, 240));

        JLabel lblCantidad = new JLabel("Productos con stock <= 3:  " + contador);
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 12));
        lblCantidad.setForeground(new Color(44, 62, 80));

        JLabel lblValor = new JLabel("Valor total: Bs " + String.format("%.2f", valorTotal));
        lblValor.setFont(new Font("Arial", Font.BOLD, 12));
        lblValor.setForeground(new Color(44, 62, 80));

        infoPanel.add(lblCantidad);
        infoPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        infoPanel.add(lblValor);

        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
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
