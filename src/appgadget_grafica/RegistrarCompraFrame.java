package appgadget_grafica;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrarCompraFrame extends JFrame {

    private MainFrame parent;
    private LM_CircularProducto lmProductos;
    private LM_CircularPedido lmPedidos;
    private RegistroFacturas rf;

    private DefaultTableModel modelCarrito;
    private JTable tablaCarrito;
    private JTextField txtCliente, txtCI, txtCorreo, txtFecha, txtProductoID;
    private JSpinner spnCantidad;
    private JButton btnAgregarCarrito, btnProcesar, btnCancelar;
    private JLabel lblTotalCarrito;
    private JTable tablaProductosDisponibles;
    private DefaultTableModel modelProductosDisponibles;

    public RegistrarCompraFrame(MainFrame parent, LM_CircularProducto lmProductos,
            LM_CircularPedido lmPedidos, RegistroFacturas rf) {
        super("Registrar Nueva Compra");
        this.parent = parent;
        this.lmProductos = lmProductos;
        this.lmPedidos = lmPedidos;
        this.rf = rf;

        initComponents();
        setupLayout();
        setupListeners();

        setSize(900, 700);
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
        txtCliente = new JTextField(20);
        txtCI = new JTextField(15);
        txtCorreo = new JTextField(20);
        txtFecha = new JTextField(10);
        txtFecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtFecha.setEditable(false);

        txtProductoID = new JTextField(10);
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        String[] columnasCarrito = {"ID", "Producto", "Precio", "Cantidad", "Subtotal"};
        modelCarrito = new DefaultTableModel(columnasCarrito, 0) {
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
                        return Double.class;
                    case 3:
                        return Integer.class;
                    case 4:
                        return Double.class;
                    default:
                        return Object.class;
                }
            }
        };
        tablaCarrito = new JTable(modelCarrito);
        tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAgregarCarrito = createStyledButton("Agregar al Carrito", new Color(52, 152, 219));
        btnProcesar = createStyledButton("Procesar Compra", new Color(46, 204, 113));
        btnCancelar = createStyledButton("Cancelar", new Color(231, 76, 60));
    }

    private void setupLayout() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Datos del Cliente", crearPanelCliente());

        tabbedPane.addTab("Agregar Productos", crearPanelProductos());

        tabbedPane.addTab("Carrito de Compra", crearPanelCarrito());

        JPanel mainButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        mainButtonPanel.add(btnProcesar);
        mainButtonPanel.add(btnCancelar);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(mainButtonPanel, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCliente() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre del Cliente:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCliente, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("CI:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCI, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Fecha de Compra:"), gbc);
        gbc.gridx = 1;
        panel.add(txtFecha, gbc);

        return panel;
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("ID Producto:"));
        searchPanel.add(txtProductoID);

        searchPanel.add(new JLabel("Cantidad:"));
        searchPanel.add(spnCantidad);

        JButton btnBuscar = createStyledButton("Buscar", new Color(155, 89, 182));
        btnBuscar.addActionListener(e -> buscarProducto());
        searchPanel.add(btnBuscar);

        JButton btnSeleccionar = createStyledButton("Seleccionar Producto", new Color(52, 152, 219));
        btnSeleccionar.addActionListener(e -> seleccionarProductoDeTabla());
        searchPanel.add(btnSeleccionar);

        searchPanel.add(btnAgregarCarrito);

        String[] columnasProductos = {"ID", "Producto", "Categoría", "Precio", "Stock"};
        modelProductosDisponibles = new DefaultTableModel(columnasProductos, 0) {
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
                        return Double.class;
                    case 4:
                        return Integer.class;
                    default:
                        return Object.class;
                }
            }
        };

        tablaProductosDisponibles = new JTable(modelProductosDisponibles);
        tablaProductosDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaProductosDisponibles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarProductoDeTabla();
                }
            }
        });

        cargarProductosDisponibles();

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaProductosDisponibles), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton btnEliminar = createStyledButton("Eliminar Seleccionado", new Color(230, 126, 34));
        btnEliminar.addActionListener(e -> {
            int row = tablaCarrito.getSelectedRow();
            if (row >= 0) {
                modelCarrito.removeRow(row);
                actualizarTotalCarrito();
            }
        });

        lblTotalCarrito = new JLabel("Total: Bs 0.00");
        lblTotalCarrito.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalCarrito.setForeground(new Color(231, 76, 60));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(lblTotalCarrito);

        panel.add(btnEliminar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);
        panel.add(totalPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setupListeners() {
        btnAgregarCarrito.addActionListener(e -> agregarAlCarrito());
        btnProcesar.addActionListener(e -> procesarCompra());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarProductosDisponibles() {
        modelProductosDisponibles.setRowCount(0); 

        for (int i = 0; i < lmProductos.getN(); i++) {
            String categoria = lmProductos.getCategoria(i);
            LD_CircularProducto lista = lmProductos.getLista(i);

            if (!lista.esVacia()) {
                NodoProducto R = lista.getP();
                NodoProducto inicio = R;

                do {
                    Producto p = R.getProducto();
                    modelProductosDisponibles.addRow(new Object[]{
                        p.getId(),
                        p.getNombre(),
                        categoria,
                        p.getPrecio(),
                        p.getStock()
                    });
                    R = R.getSig();
                } while (R != inicio);
            }
        }
    }

    private void buscarProducto() {
        try {
            int id = Integer.parseInt(txtProductoID.getText().trim());
            Producto producto = lmProductos.buscarProductoGlobal(id);

            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String mensaje = "Producto encontrado:\n\n"
                    + "ID: " + producto.getId() + "\n"
                    + "Nombre: " + producto.getNombre() + "\n"
                    + "Precio: Bs " + producto.getPrecio() + "\n"
                    + "Stock disponible: " + producto.getStock();

            JOptionPane.showMessageDialog(this, mensaje, "Producto Encontrado", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarProductoDeTabla() {
        int row = tablaProductosDisponibles.getSelectedRow();
        if (row >= 0) {
            int id = (int) modelProductosDisponibles.getValueAt(row, 0);
            txtProductoID.setText(String.valueOf(id));

            String nombre = (String) modelProductosDisponibles.getValueAt(row, 1);
            double precio = (double) modelProductosDisponibles.getValueAt(row, 3);
            int stock = (int) modelProductosDisponibles.getValueAt(row, 4);

            JOptionPane.showMessageDialog(this,
                    "Producto seleccionado:\n"
                    + "ID: " + id + "\n"
                    + "Nombre: " + nombre + "\n"
                    + "Precio: Bs " + precio + "\n"
                    + "Stock: " + stock,
                    "Producto Seleccionado",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto de la tabla",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void agregarAlCarrito() {
        try {
            int id = Integer.parseInt(txtProductoID.getText().trim());
            int cantidad = (Integer) spnCantidad.getValue();

            Producto producto = lmProductos.buscarProductoGlobal(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cantidad > producto.getStock()) {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente. Disponible: " + producto.getStock(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < modelCarrito.getRowCount(); i++) {
                int idEnCarrito = (int) modelCarrito.getValueAt(i, 0);
                if (idEnCarrito == id) {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "Este producto ya está en el carrito.\n¿Desea actualizar la cantidad?",
                            "Producto duplicado",
                            JOptionPane.YES_NO_OPTION);

                    if (respuesta == JOptionPane.YES_OPTION) {
                        int cantidadAnterior = (int) modelCarrito.getValueAt(i, 3);
                        int nuevaCantidadTotal = cantidadAnterior + cantidad;

                        if (nuevaCantidadTotal > producto.getStock()) {
                            JOptionPane.showMessageDialog(this,
                                    "Stock insuficiente. Disponible: " + producto.getStock()
                                    + "\nYa tienes " + cantidadAnterior + " unidades en el carrito.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        modelCarrito.setValueAt(nuevaCantidadTotal, i, 3);
                        double nuevoSubtotal = producto.getPrecio() * nuevaCantidadTotal;
                        modelCarrito.setValueAt(nuevoSubtotal, i, 4);

                        txtProductoID.setText("");
                        spnCantidad.setValue(1);
                        actualizarTotalCarrito();
                        return;
                    } else {
                        return;
                    }
                }
            }

            double subtotal = producto.getPrecio() * cantidad;
            modelCarrito.addRow(new Object[]{
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                cantidad,
                subtotal
            });

            txtProductoID.setText("");
            spnCantidad.setValue(1);
            actualizarTotalCarrito();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTotalCarrito() {
        double total = 0.0;
        for (int i = 0; i < modelCarrito.getRowCount(); i++) {
            double subtotal = (double) modelCarrito.getValueAt(i, 4);
            total += subtotal;
        }
        lblTotalCarrito.setText(String.format("Total: Bs %.2f", total));
    }

    private void procesarCompra() {
        if (modelCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtCliente.getText().trim().isEmpty() || txtCI.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los datos del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double subtotalAcumulado = 0;
        for (int i = 0; i < modelCarrito.getRowCount(); i++) {
            subtotalAcumulado += (double) modelCarrito.getValueAt(i, 4);
        }

        Cliente cliente = new Cliente(txtCliente.getText().trim(), txtCI.getText().trim(), txtCorreo.getText().trim());
        Factura factura = new Factura(cliente, txtFecha.getText());

        for (int i = 0; i < modelCarrito.getRowCount(); i++) {
            int id = (int) modelCarrito.getValueAt(i, 0);
            String nombreProducto = (String) modelCarrito.getValueAt(i, 1);
            double precio = (double) modelCarrito.getValueAt(i, 2);
            int cantidad = (int) modelCarrito.getValueAt(i, 3);
            double subtotal = (double) modelCarrito.getValueAt(i, 4);

            Producto producto = lmProductos.buscarProductoGlobal(id);
            if (producto != null) {
                producto.setStock(producto.getStock() - cantidad);
            }

            Pedido pedido = new Pedido(cliente.getNombre(), nombreProducto, cantidad, subtotal);
            factura.agregarPedido(pedido);

            lmPedidos.adiPedido(0, pedido);
        }
        if (subtotalAcumulado > 10000) {
            factura.setDescuento(10.0);
        }

        factura.calcularTotal();
        rf.agregarFactura(factura);

        System.out.println("Compra registrada - Factura #" + factura.getNumero()
                + " - Cliente: " + cliente.getNombre()
                + " - Total: Bs " + String.format("%.2f", factura.getTotalConDescuento()));
        mostrarFactura(factura);
        parent.actualizarCatalogo();
        dispose();
    }

    private void mostrarFactura(Factura factura) {
        JFrame facturaFrame = new JFrame("Factura #" + factura.getNumero());
        facturaFrame.setSize(500, 600);
        facturaFrame.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("             FACTURA ELECTRÓNICA           \n");
        sb.append("============================================\n");
        sb.append("Nro Factura: ").append(factura.getNumero()).append("\n");
        sb.append("Fecha: ").append(factura.getFecha()).append("\n");
        sb.append("Cliente: ").append(factura.getCliente().getNombre()).append("\n");
        sb.append("CI: ").append(factura.getCliente().getCi()).append("\n");

        if (factura.getDescuento() > 0) {
            sb.append("DESCUENTO APLICADO: ").append(factura.getDescuento()).append("%\n");
        }

        sb.append("--------------------------------------------\n");
        sb.append("Producto              Cant.   Subtotal\n");

        for (int i = 0; i < factura.getCantidadPedidos(); i++) {
            Pedido pedido = factura.getPedidos()[i];
            sb.append(String.format("%-20s %-6d Bs %-8.2f\n",
                    pedido.getProducto(),
                    pedido.getCantidad(),
                    pedido.getTotal()));
        }

        sb.append("--------------------------------------------\n");
        sb.append(String.format("SUBTOTAL: Bs %10.2f\n", factura.getTotal()));

        if (factura.getDescuento() > 0) {
            double montoDescuento = factura.getTotal() * (factura.getDescuento() / 100);
            sb.append(String.format("DESCUENTO: -Bs %8.2f\n", montoDescuento));
            sb.append("--------------------------------------------\n");
        }

        sb.append(String.format("TOTAL: Bs %10.2f\n", factura.getTotalConDescuento()));
        sb.append("\n¡Gracias por su compra!\n");
        sb.append("============================================\n");

        textArea.setText(sb.toString());

        JButton btnCerrar = createStyledButton("Cerrar", new Color(231, 76, 60));
        btnCerrar.addActionListener(e -> facturaFrame.dispose());

        facturaFrame.setLayout(new BorderLayout());
        facturaFrame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        facturaFrame.add(btnCerrar, BorderLayout.SOUTH);
        facturaFrame.setVisible(true);
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

