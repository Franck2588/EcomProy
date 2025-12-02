package appgadget_grafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AgregarProductoFrame extends JFrame {

    private MainFrame parent;
    private LM_CircularProducto lmProductos;

    private JComboBox<String> cmbCategoria;
    private JTextField txtID, txtNombre, txtPrecio, txtStock;
    private JButton btnGuardar, btnCancelar;

    public AgregarProductoFrame(MainFrame parent, LM_CircularProducto lmProductos) {
        super("Agregar Nuevo Producto");
        this.parent = parent;
        this.lmProductos = lmProductos;

        initComponents();
        setupLayout();
        setupListeners();

        setSize(400, 400);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initComponents() {

        String[] categorias = new String[lmProductos.getN()];
        for (int i = 0; i < lmProductos.getN(); i++) {
            categorias[i] = lmProductos.getCategoria(i);
        }

        cmbCategoria = new JComboBox<>(categorias);
        txtID = new JTextField(10);
        txtNombre = new JTextField(20);
        txtPrecio = new JTextField(10);
        txtStock = new JTextField(10);

        btnGuardar = createStyledButton("Guardar", new Color(46, 204, 113));
        btnCancelar = createStyledButton("Cancelar", new Color(231, 76, 60));
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
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Color hoverColor = baseColor.darker();
                button.setBackground(hoverColor);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Color pressedColor = baseColor.darker().darker();
                button.setBackground(pressedColor);
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

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(cmbCategoria, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtID, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Precio (Bs):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtPrecio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Stock Inicial:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtStock, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void setupListeners() {
        btnGuardar.addActionListener(e -> guardarProducto());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarProducto() {
        try {
            int id = Integer.parseInt(txtID.getText().trim());
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            int categoriaIndex = cmbCategoria.getSelectedIndex();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el ID ya existe
            if (lmProductos.buscarProductoGlobal(id) != null) {
                JOptionPane.showMessageDialog(this,
                        "El ID " + id + " ya existe en el sistema",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto nuevoProducto = new Producto(id, nombre, precio, stock);
            lmProductos.adiProducto(categoriaIndex, nuevoProducto);

            System.out.println("Producto agregado: " + nombre + " (ID: " + id + ")");
            parent.actualizarCatalogo();
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Datos inválidos. Verifique que ID, Precio y Stock sean números válidos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

