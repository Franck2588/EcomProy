package appgadget_grafica;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainFrame extends JFrame {

    private ArchivoGadget archivo;
    private LM_CircularProducto lmProductos;
    private LM_CircularPedido lmPedidos;
    private RegistroFacturas rf;

    private JTable tablaCatalogo;
    private DefaultTableModel modelCatalogo;

    private Color colorCompra = new Color(46, 204, 113);
    private Color colorReportes = new Color(155, 89, 182);
    private Color colorFacturas = new Color(52, 152, 219);
    private Color colorClientes = new Color(241, 196, 15);
    private Color colorGuardar = new Color(39, 174, 96);
    private Color colorConfig = new Color(149, 165, 166);
    private Color colorSalir = new Color(231, 76, 60);
    private Color colorCatalogo = new Color(41, 128, 185);
    private Color colorStock = new Color(230, 126, 34);

    public MainFrame() {
        super("Gadget Store - Sistema de Venta");

        try {
            archivo = new ArchivoGadget();
            String[] columnas = {"ID", "Categoría", "Producto", "Precio (Bs)", "Stock", "Estado"};
            modelCatalogo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0 || columnIndex == 4) {
                        return Integer.class;
                    }
                    if (columnIndex == 3) {
                        return Double.class;
                    }
                    return String.class;
                }
            };

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1200, 700);
            setLocationRelativeTo(null);

            crearMenuPrincipal();
            crearPanelPrincipal();

            cargarDatos();

            setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error crítico al iniciar la aplicación:\n" + e.getMessage(),
                    "Error de Inicio",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void crearMenuPrincipal() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(41, 128, 185));

        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setForeground(Color.WHITE);

        JMenuItem itemGuardar = new JMenuItem("Guardar Sistema");
        itemGuardar.addActionListener(e -> guardarSistema());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> salir());

        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuReportes = new JMenu("Reportes");
        menuReportes.setForeground(Color.WHITE);

        JMenuItem itemReporte1 = new JMenuItem("Producto Más Vendido");
        itemReporte1.addActionListener(e -> new ProductoMasVendidoFrame(this, lmProductos, lmPedidos));

        JMenuItem itemReporte2 = new JMenuItem("Clientes Premium");
        itemReporte2.addActionListener(e -> new ClientesPremiumFrame(this, rf));

        JMenuItem itemReporte3 = new JMenuItem("Ranking de Clientes");
        itemReporte3.addActionListener(e -> new RankingClientesFrame(this, rf));

        menuReportes.add(itemReporte1);
        menuReportes.add(itemReporte2);
        menuReportes.add(itemReporte3);

        menuBar.add(menuArchivo);
        menuBar.add(menuReportes);
        setJMenuBar(menuBar);
    }

    private void crearPanelPrincipal() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("SISTEMA DE VENTAS - GADGET STORE");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(41, 128, 185));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel catalogoPanel = crearPanelCatalogo();

        JPanel botonesPanel = crearPanelBotones();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titulo, BorderLayout.NORTH);

        JPanel statsPanel = crearPanelEstadisticas();
        topPanel.add(statsPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(catalogoPanel, BorderLayout.CENTER);
        mainPanel.add(botonesPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                " CATÁLOGO DE PRODUCTOS ",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(41, 128, 185)));
        panel.setBackground(Color.WHITE);

        tablaCatalogo = new JTable(modelCatalogo);
        tablaCatalogo.setRowHeight(35);
        tablaCatalogo.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaCatalogo.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tablaCatalogo.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaCatalogo.getTableHeader().setForeground(Color.BLACK);
        tablaCatalogo.setGridColor(Color.LIGHT_GRAY);
        tablaCatalogo.setShowGrid(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tablaCatalogo.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaCatalogo.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        tablaCatalogo.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        tablaCatalogo.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                try {
                    Object stockObj = table.getValueAt(row, 4);
                    if (stockObj instanceof Integer) {
                        int stock = (Integer) stockObj;

                        if (stock == 0) {
                            setText("AGOTADO");
                            setForeground(Color.RED);
                            setFont(new Font("Arial", Font.BOLD, 12));
                        } else if (stock <= 3) {
                            setText("BAJO STOCK");
                            setForeground(new Color(230, 126, 34));
                            setFont(new Font("Arial", Font.BOLD, 12));
                        } else if (stock <= 10) {
                            setText("MEDIO STOCK");
                            setForeground(Color.ORANGE);
                            setFont(new Font("Arial", Font.PLAIN, 12));
                        } else {
                            setText("DISPONIBLE");
                            setForeground(new Color(39, 174, 96));
                            setFont(new Font("Arial", Font.PLAIN, 12));
                        }
                    }
                } catch (Exception e) {
                    setText("N/A");
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaCatalogo);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel catalogButtonsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        catalogButtonsPanel.setBackground(Color.WHITE);

  
        JButton btnAgregar = crearBotonCatalogo("Nuevo", new Color(46, 204, 113));
        JButton btnAumentarStock = crearBotonCatalogo("Actualizar Stock", Color.BLACK);
        JButton btnStockBajo = crearBotonCatalogo("Stock Bajo", colorStock);

        btnAgregar.addActionListener(e -> {
            if (lmProductos != null) {
                new AgregarProductoFrame(this, lmProductos);
            } else {
                JOptionPane.showMessageDialog(this, "Sistema no inicializado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAumentarStock.addActionListener(e -> {
            if (lmProductos != null) {
                mostrarOpcionesStock();
            } else {
                JOptionPane.showMessageDialog(this, "Sistema no inicializado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnStockBajo.addActionListener(e -> {
            if (lmProductos != null) {
                new StockBajoFrame(this, lmProductos);
            } else {
                JOptionPane.showMessageDialog(this, "Sistema no inicializado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        catalogButtonsPanel.add(btnAgregar);
        catalogButtonsPanel.add(btnAumentarStock);
        catalogButtonsPanel.add(btnStockBajo);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(catalogButtonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(240, 248, 255));

        JLabel titulo = new JLabel("MENÚ");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(41, 128, 185));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnRegistrarCompra = crearBotonMenu("Registrar Nueva Compra", colorCompra);
        JButton btnProductoMasVendido = crearBotonMenu("Producto Más Vendido", colorReportes);
        JButton btnPedidosCliente = crearBotonMenu("Pedidos por Cliente", colorReportes);
        JButton btnVerFacturas = crearBotonMenu("Ver Facturas", colorFacturas);
        JButton btnClientesPremium = crearBotonMenu("Clientes Premium", colorClientes);
        JButton btnRankingClientes = crearBotonMenu("Ranking Clientes", colorClientes);
        JButton btnGuardar = crearBotonMenu("Guardar Sistema", colorGuardar);
        JButton btnConfiguracion = crearBotonMenu("Configuración", colorConfig);
        JButton btnSalir = crearBotonMenu("Salir", colorSalir);

        btnRegistrarCompra.addActionListener(e -> {
            if (lmProductos != null && lmPedidos != null && rf != null) {
                new RegistrarCompraFrame(this, lmProductos, lmPedidos, rf);
            } else {
                JOptionPane.showMessageDialog(this, "Datos no cargados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnProductoMasVendido.addActionListener(e -> {
            if (lmProductos != null && lmPedidos != null) {
                new ProductoMasVendidoFrame(this, lmProductos, lmPedidos);
            } else {
                JOptionPane.showMessageDialog(this, "Datos no cargados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPedidosCliente.addActionListener(e -> {
            if (rf != null) {
                new PedidosClienteFrame(this, rf);
            } else {
                JOptionPane.showMessageDialog(this, "Datos no cargados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVerFacturas.addActionListener(e -> {
            if (rf != null) {
                new VerFacturasFrame(this, rf);
            } else {
                JOptionPane.showMessageDialog(this, "Datos no cargados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnClientesPremium.addActionListener(e -> {
            if (rf != null) {
                new ClientesPremiumFrame(this, rf);
            } else {
                JOptionPane.showMessageDialog(this, "Datos no cargados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRankingClientes.addActionListener(e -> {
            if (rf != null) {
                new RankingClientesFrame(this, rf);
            } else {
                JOptionPane.showMessageDialog(this, "Datos no cargados", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGuardar.addActionListener(e -> guardarSistema());
        btnConfiguracion.addActionListener(e -> {
            if (archivo != null && lmProductos != null && lmPedidos != null && rf != null) {
                new ConfiguracionFrame(this, archivo, lmProductos, lmPedidos, rf);
            } else {
                JOptionPane.showMessageDialog(this, "Sistema no inicializado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnSalir.addActionListener(e -> salir());

        panel.add(btnRegistrarCompra);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnProductoMasVendido);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnPedidosCliente);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnVerFacturas);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnClientesPremium);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnRankingClientes);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnGuardar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnConfiguracion);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnSalir);

        return panel;
    }

    private void mostrarOpcionesStock() {
        String[] opciones = {
            "Actualizar stock de producto existente",
            "Cancelar"
        };

        int seleccion = JOptionPane.showOptionDialog(this,
                "Seleccione una opción para gestionar el stock:",
                "Gestión de Stock",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        switch (seleccion) {
            case 0:
                aumentarStockProductoExistente();
                break;
            case 1:
                break;
        }
    }

    private void aumentarStockProductoExistente() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtID = new JTextField(10);
        JTextField txtCantidad = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar");

        panel.add(new JLabel("ID del Producto:"));
        panel.add(txtID);
        panel.add(new JLabel("Cantidad nuevo Stock:"));
        panel.add(txtCantidad);
        panel.add(new JLabel(""));
        panel.add(btnBuscar);

        JDialog dialog = new JDialog(this, "Actualizar Stock", true);
        dialog.setLayout(new BorderLayout());

        btnBuscar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtID.getText().trim());
                Producto producto = lmProductos.buscarProductoGlobal(id);

                if (producto == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Producto no encontrado",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String info = "Producto encontrado:\n\n"
                        + "ID: " + producto.getId() + "\n"
                        + "Nombre: " + producto.getNombre() + "\n"
                        + "Precio: Bs " + producto.getPrecio() + "\n"
                        + "Stock actual: " + producto.getStock();

                JOptionPane.showMessageDialog(dialog, info,
                        "Producto Encontrado", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "ID debe ser un número",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnConfirmar = crearBotonCatalogo("Actualizar Stock", new Color(46, 204, 113));
        btnConfirmar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtID.getText().trim());
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());

                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "La cantidad debe ser mayor a 0",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Producto producto = lmProductos.buscarProductoGlobal(id);
                if (producto == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Producto no encontrado",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int nuevoStock = producto.getStock() + cantidad;
                producto.setStock(nuevoStock);

                JOptionPane.showMessageDialog(dialog,
                        "Stock actualizado exitosamente\n\n"
                        + "Producto: " + producto.getNombre() + "\n"
                        + "Stock anterior: " + (nuevoStock - cantidad) + "\n"
                        + "Cantidad agregada: " + cantidad + "\n"
                        + "Nuevo stock: " + nuevoStock,
                        "Stock Actualizado",
                        JOptionPane.INFORMATION_MESSAGE);

                mostrarCatalogoEnTabla();
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Datos inválidos. Verifique que ID y Cantidad sean números válidos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = crearBotonCatalogo("Cancelar", new Color(231, 76, 60));
        btnCancelar.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnConfirmar);
        buttonPanel.add(btnCancelar);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 1));
        panel.setBackground(new Color(240, 248, 255));

        int totalProductos = 0;
        double valorInventario = 0;
        int facturasRegistradas = 0;
        int stockBajo = 0;

        if (lmProductos != null && rf != null) {
            totalProductos = contarProductos();
            valorInventario = calcularValorInventario();
            facturasRegistradas = rf.getCantidad();
            stockBajo = contarStockBajo();
        }

        JLabel lblProductos = crearEstadistica("Productos", String.valueOf(totalProductos), new Color(41, 128, 185));
        JLabel lblInventario = crearEstadistica("Inventario", "Bs " + String.format("%.2f", valorInventario), new Color(39, 174, 96));
        JLabel lblFacturas = crearEstadistica("Facturas", String.valueOf(facturasRegistradas), new Color(155, 89, 182));
        JLabel lblStockBajo = crearEstadistica("Stock Bajo", String.valueOf(stockBajo), new Color(230, 126, 34));

        panel.add(lblProductos);
        panel.add(lblInventario);
        panel.add(lblFacturas);
        panel.add(lblStockBajo);

        return panel;
    }

    private JLabel crearEstadistica(String titulo, String valor, Color colorValor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitulo.setForeground(Color.DARK_GRAY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 14));
        lblValor.setForeground(colorValor);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblValor);

        return new JLabel() {
            //@Override
            public Component getComponent() {
                return panel;
            }
        };
    }

    private JButton crearBotonMenu(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 14));

        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);

        boton.setContentAreaFilled(false);
        boton.setOpaque(true);

        boton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(250, 50));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                boton.setBackground(color.darker().darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boton.setBackground(color.darker());
            }
        });

        return boton;
    }

    private JButton crearBotonCatalogo(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 12));

        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);

        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (color.equals(Color.BLACK)) {
                    boton.setBackground(new Color(50, 50, 50));
                } else {
                    boton.setBackground(color.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (color.equals(Color.BLACK)) {
                    boton.setBackground(new Color(30, 30, 30));
                } else {
                    boton.setBackground(color.darker().darker());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (boton.getBounds().contains(e.getPoint())) {
                    if (color.equals(Color.BLACK)) {
                        boton.setBackground(new Color(50, 50, 50));
                    } else {
                        boton.setBackground(color.darker());
                    }
                } else {
                    boton.setBackground(color);
                }
            }
        });

        return boton;
    }

    private void cargarDatos() {
        try {
            lmProductos = archivo.cargarProductos();
            lmPedidos = archivo.cargarPedidos();
            rf = archivo.cargarFacturas();

            if (lmProductos != null && lmPedidos != null && rf != null) {
                System.out.println("Sistema cargado correctamente");
                System.out.println("Productos: " + contarProductos() + " en " + lmProductos.getN() + " categorías");

                mostrarCatalogoEnTabla();
            } else {
                throw new Exception("Algunos archivos no se cargaron correctamente");
            }

        } catch (Exception e) {
            System.out.println("Error al cargar datos: " + e.getMessage());
            System.out.println("Inicializando sistema nuevo...");

            lmProductos = catalogoConListas();
            lmPedidos = new LM_CircularPedido(3);
            lmPedidos.setTipo(0, "Normal");
            lmPedidos.setTipo(1, "Express");
            lmPedidos.setTipo(2, "Prioritario");
            rf = new RegistroFacturas();

            System.out.println("Sistema inicializado con catálogo predeterminado");

            mostrarCatalogoEnTabla();
        }
    }

    private void guardarSistema() {
        try {
            if (lmProductos != null && lmPedidos != null && rf != null) {
                archivo.guardarProductos(lmProductos);
                archivo.guardarPedidos(lmPedidos);
                archivo.guardarFacturas(rf);

                System.out.println("Sistema guardado correctamente");
                JOptionPane.showMessageDialog(this,
                        "Sistema guardado correctamente",
                        "Guardado Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new IOException("Datos no inicializados");
            }
        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Guardar datos antes de salir?",
                "Salir",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            guardarSistema();
            System.exit(0);
        } else if (opcion == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    private void mostrarCatalogoEnTabla() {
        if (modelCatalogo == null) {
            System.out.println("Error: Modelo de tabla no inicializado");
            return;
        }

        modelCatalogo.setRowCount(0);

        if (lmProductos == null) {
            System.out.println("No hay productos cargados en el sistema");
            modelCatalogo.addRow(new Object[]{"-", "Sistema no inicializado", "Vuelva a cargar", 0, 0, "ERROR"});
            return;
        }

        try {
            int totalProductos = 0;

            for (int i = 0; i < lmProductos.getN(); i++) {
                String categoria = lmProductos.getCategoria(i);
                LD_CircularProducto listaCategoria = lmProductos.getLista(i);

                if (listaCategoria != null && !listaCategoria.esVacia()) {
                    NodoProducto R = listaCategoria.getP();
                    NodoProducto inicio = R;

                    do {
                        Producto p = R.getProducto();
                        if (p != null) {
                            modelCatalogo.addRow(new Object[]{
                                p.getId(),
                                categoria != null ? categoria : "Sin categoría",
                                p.getNombre(),
                                p.getPrecio(),
                                p.getStock(),
                                ""
                            });
                            totalProductos++;
                        }
                        R = R.getSig();
                    } while (R != null && R != inicio);
                }
            }

            System.out.println("Catálogo actualizado: " + totalProductos + " productos");

            if (totalProductos == 0) {
                modelCatalogo.addRow(new Object[]{"-", "No hay productos", "Agregue productos primero", 0, 0, "VACÍO"});
            }

        } catch (Exception e) {
            System.out.println("Error al mostrar catálogo: " + e.getMessage());
            modelCatalogo.addRow(new Object[]{"ERROR", "Error al cargar", e.getMessage(), 0, 0, "ERROR"});
        }
    }

    private int contarProductos() {
        int total = 0;
        if (lmProductos != null) {
            for (int i = 0; i < lmProductos.getN(); i++) {
                LD_CircularProducto lista = lmProductos.getLista(i);
                if (lista != null) {
                    total += lista.nroNodos();
                }
            }
        }
        return total;
    }

    private double calcularValorInventario() {
        double total = 0;
        if (lmProductos != null) {
            for (int i = 0; i < lmProductos.getN(); i++) {
                LD_CircularProducto lista = lmProductos.getLista(i);
                if (lista != null && !lista.esVacia()) {
                    NodoProducto R = lista.getP();
                    NodoProducto inicio = R;
                    do {
                        Producto p = R.getProducto();
                        total += p.getPrecio() * p.getStock();
                        R = R.getSig();
                    } while (R != null && R != inicio);
                }
            }
        }
        return total;
    }

    private int contarStockBajo() {
        int contador = 0;
        if (lmProductos != null) {
            for (int i = 0; i < lmProductos.getN(); i++) {
                LD_CircularProducto lista = lmProductos.getLista(i);
                if (lista != null && !lista.esVacia()) {
                    NodoProducto R = lista.getP();
                    NodoProducto inicio = R;
                    do {
                        if (R.getProducto().getStock() <= 3) {
                            contador++;
                        }
                        R = R.getSig();
                    } while (R != null && R != inicio);
                }
            }
        }
        return contador;
    }

    private LM_CircularProducto catalogoConListas() {
        LM_CircularProducto lm = new LM_CircularProducto(2);
        lm.setCategoria(0, "Electrónica");
        lm.setCategoria(1, "Accesorios");

        // Electrónica
        lm.adiProducto(0, new Producto(1, "Laptop Lenovo", 5500, 5));
        lm.adiProducto(0, new Producto(2, "Smartphone Samsung", 3200, 10));
        lm.adiProducto(0, new Producto(3, "iPhone 15", 11000, 3));
        lm.adiProducto(0, new Producto(4, "Tablet Xiaomi", 2500, 7));
        lm.adiProducto(0, new Producto(5, "Smartwatch Apple", 3500, 4));

        // Accesorios
        lm.adiProducto(1, new Producto(6, "Mouse Logitech", 150, 20));
        lm.adiProducto(1, new Producto(7, "Teclado Mecánico", 300, 8));
        lm.adiProducto(1, new Producto(8, "Auriculares Bluetooth", 250, 15));
        lm.adiProducto(1, new Producto(9, "Cargador Rápido", 120, 30));
        lm.adiProducto(1, new Producto(10, "Memoria USB 64GB", 100, 25));

        return lm;
    }

    public LM_CircularProducto getProductos() {
        return lmProductos;
    }

    public LM_CircularPedido getPedidos() {
        return lmPedidos;
    }

    public RegistroFacturas getFacturas() {
        return rf;
    }

    public ArchivoGadget getArchivo() {
        return archivo;
    }

    public void actualizarCatalogo() {
        mostrarCatalogoEnTabla();
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
