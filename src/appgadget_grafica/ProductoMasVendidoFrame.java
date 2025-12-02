package appgadget_grafica;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ProductoMasVendidoFrame extends JFrame {

    public ProductoMasVendidoFrame(MainFrame parent, LM_CircularProducto lmProductos, LM_CircularPedido lmPedidos) {
        super("Productos Más Vendidos por Categoría");

        initComponents(parent, lmProductos, lmPedidos);
        setSize(700, 500);
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

    private void initComponents(MainFrame parent, LM_CircularProducto lmProductos, LM_CircularPedido lmPedidos) {
        String[] columnas = {"Categoría", "Producto Más Vendido", "Cantidad Vendida"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        for (int cat = 0; cat < lmProductos.getN(); cat++) {
            String categoria = lmProductos.getCategoria(cat);

            ArrayList<String> productos = new ArrayList<>();
            ArrayList<Integer> cantidades = new ArrayList<>();

            for (int tipo = 0; tipo < lmPedidos.getN(); tipo++) {
                LS_CircularPedido listaPedidos = lmPedidos.getLista(tipo);
                if (!listaPedidos.esVacia()) {
                    NodoPedido nodoPedido = listaPedidos.getP();
                    if (nodoPedido != null) {
                        NodoPedido inicio = nodoPedido;
                        boolean primerNodo = true;

                        while (primerNodo || nodoPedido != inicio) {
                            primerNodo = false;
                            String nombreProducto = nodoPedido.getPedido().getProducto();
                            int cantidad = nodoPedido.getPedido().getCantidad();

                            boolean productoExiste = false;

                            for (int i = 0; i < lmProductos.getN(); i++) {
                                LD_CircularProducto listaCat = lmProductos.getLista(i);
                                if (!listaCat.esVacia()) {
                                    NodoProducto R = listaCat.getP();
                                    NodoProducto inicioCat = R;

                                    do {
                                        if (R.getProducto().getNombre().equalsIgnoreCase(nombreProducto)) {
                                            productoExiste = true;
                                            break;
                                        }
                                        R = R.getSig();
                                    } while (R != inicioCat);
                                }
                                if (productoExiste) {
                                    break;
                                }
                            }

                            if (productoExiste) {
                                boolean enEstaCategoria = false;
                                LD_CircularProducto listaCatActual = lmProductos.getLista(cat);

                                if (!listaCatActual.esVacia()) {
                                    NodoProducto R = listaCatActual.getP();
                                    NodoProducto inicioCat = R;

                                    do {
                                        if (R.getProducto().getNombre().equalsIgnoreCase(nombreProducto)) {
                                            enEstaCategoria = true;
                                            break;
                                        }
                                        R = R.getSig();
                                    } while (R != inicioCat);
                                }

                                if (enEstaCategoria) {
                                    int index = -1;
                                    for (int i = 0; i < productos.size(); i++) {
                                        if (productos.get(i).equals(nombreProducto)) {
                                            index = i;
                                            break;
                                        }
                                    }

                                    if (index == -1) {
                                        productos.add(nombreProducto);
                                        cantidades.add(cantidad);
                                    } else {
                                        cantidades.set(index, cantidades.get(index) + cantidad);
                                    }
                                }
                            }

                            nodoPedido = nodoPedido.getSig();
                        }
                    }
                }
            }

            String productoMasVendido = "Sin ventas";
            int maxVentas = 0;

            for (int i = 0; i < productos.size(); i++) {
                int cantidad = cantidades.get(i);
                if (cantidad > maxVentas) {
                    maxVentas = cantidad;
                    productoMasVendido = productos.get(i);
                } else if (cantidad == maxVentas && maxVentas > 0) {
                    productoMasVendido += ", " + productos.get(i);
                }
            }

            model.addRow(new Object[]{categoria, productoMasVendido, maxVentas});
        }
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"No hay datos", "No hay ventas registradas", 0});
        }

        JTable tabla = new JTable(model);
        tabla.setRowHeight(30);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Productos más vendidos por categoría"));

        JButton btnRefrescar = createStyledButton("Refrescar", new Color(52, 152, 219));
        btnRefrescar.addActionListener(e -> {
            dispose();
            new ProductoMasVendidoFrame(parent, lmProductos, lmPedidos);
        });

        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(btnRefrescar);
        buttonPanel.add(btnCerrar);

        setLayout(new BorderLayout(10, 10));
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.setShowGrid(true);
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
