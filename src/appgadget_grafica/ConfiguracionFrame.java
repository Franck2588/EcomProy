package appgadget_grafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ConfiguracionFrame extends JFrame {
    private MainFrame parent;
    private ArchivoGadget archivo;
    private LM_CircularProducto lmProductos;
    private LM_CircularPedido lmPedidos;
    private RegistroFacturas rf;
    
    public ConfiguracionFrame(MainFrame parent, ArchivoGadget archivo, 
                             LM_CircularProducto lmProductos, LM_CircularPedido lmPedidos,
                             RegistroFacturas rf) {
        super("Configuración del Sistema");
        this.parent = parent;
        this.archivo = archivo;
        this.lmProductos = lmProductos;
        this.lmPedidos = lmPedidos;
        this.rf = rf;
        
        initComponents();
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btnBackup = crearBotonOpcion("Realizar Backup Completo");
        JButton btnRestaurar = crearBotonOpcion("Restaurar Catálogo Predeterminado");
        JButton btnLimpiar = crearBotonOpcion("Limpiar Todos los Datos");
        JButton btnEstadisticas = crearBotonOpcion("Ver Estadísticas del Sistema");
        JButton btnAcerca = crearBotonOpcion("Acerca del Sistema");
        
        btnBackup.addActionListener(e -> realizarBackup());
        btnRestaurar.addActionListener(e -> restaurarCatalogo());
        btnLimpiar.addActionListener(e -> limpiarDatos());
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        btnAcerca.addActionListener(e -> mostrarAcercaDe());
        
        optionsPanel.add(btnBackup);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(btnRestaurar);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(btnLimpiar);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(btnEstadisticas);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(btnAcerca);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnCerrar);
        
        setLayout(new BorderLayout());
        add(optionsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton crearBotonOpcion(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(300, 40));
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        
        return boton;
    }
    
    private void realizarBackup() {
        try {
            archivo.guardarProductos(lmProductos);
            archivo.guardarPedidos(lmPedidos);
            archivo.guardarFacturas(rf);
            
            JOptionPane.showMessageDialog(this,
                "Backup realizado exitosamente",
                "Backup Completado",
                JOptionPane.INFORMATION_MESSAGE);
            
            System.out.println("Backup del sistema realizado");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error al realizar backup: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void restaurarCatalogo() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Restaurar catálogo predeterminado?\n\nEsta acción reemplazará el catálogo actual.",
            "Confirmar Restauración",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            lmProductos = catalogoConListas();
            parent.actualizarCatalogo();
            
            JOptionPane.showMessageDialog(this,
                "Catálogo restaurado exitosamente",
                "Restauración Completada",
                JOptionPane.INFORMATION_MESSAGE);
            
            System.out.println("Catálogo restaurado a valores predeterminados");
        }
    }
    
    private void limpiarDatos() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de limpiar todos los datos?\n\nEsta acción eliminará:\n" +
            "• Todas las facturas\n" +
            "• Todos los pedidos\n" +
            "• El catálogo volverá al predeterminado\n\n" +
            "Esta acción NO se puede deshacer.",
            "Confirmar Limpieza",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            lmProductos = catalogoConListas();
            
            lmPedidos = new LM_CircularPedido(3);
            lmPedidos.setTipo(0, "Normal");
            lmPedidos.setTipo(1, "Express");
            lmPedidos.setTipo(2, "Prioritario");
            
            rf = new RegistroFacturas();
            
            parent.actualizarCatalogo();
            
            JOptionPane.showMessageDialog(this,
                "Todos los datos han sido limpiados",
                "Limpieza Completada",
                JOptionPane.INFORMATION_MESSAGE);
            
            System.out.println("Todos los datos han sido limpiados");
        }
    }
    
    private void mostrarEstadisticas() {
        int totalProductos = 0;
        double valorInventario = 0;
        int stockBajo = 0;
        
        for (int i = 0; i < lmProductos.getN(); i++) {
            LD_CircularProducto lista = lmProductos.getLista(i);
            if (!lista.esVacia()) {
                NodoProducto R = lista.getP();
                NodoProducto inicio = R;
                do {
                    Producto p = R.getProducto();
                    totalProductos++;
                    valorInventario += p.getPrecio() * p.getStock();
                    if (p.getStock() <= 3) stockBajo++;
                    R = R.getSig();
                } while (R != inicio);
            }
        }
        
        String estadisticas = "ESTADÍSTICAS DEL SISTEMA\n\n" +
                            "Productos registrados: " + totalProductos + "\n" +
                            "Valor del inventario: Bs " + String.format("%.2f", valorInventario) + "\n" +
                            "Productos con stock bajo: " + stockBajo + "\n" +
                            "Facturas registradas: " + rf.getCantidad() + "\n" +
                            "Pedidos registrados: " + lmPedidos.getTotalPedidos() + "\n" +
                            "Categorías de productos: " + lmProductos.getN() + "\n" +
                            "Tipos de pedido: " + lmPedidos.getN();
        
        JTextArea textArea = new JTextArea(estadisticas);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this,
            new JScrollPane(textArea),
            "Estadísticas del Sistema",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAcercaDe() {
        String acercaDe = "GADGET STORE - Sistema de Ventas\n\n" +
                         "Versión: 3.0\n" +
                         "Desarrollado por: Miguel Angel Benito Peñaloza\n\n" +
                         "Funcionalidades:\n" +
                         "• Gestión de catálogo de productos\n" +
                         "• Registro de ventas y facturación\n" +
                         "• Control de inventario y stock\n" +
                         "• Reportes y estadísticas\n" +
                         "• Backup de datos\n\n" +
                         "© 2025 - Todos los derechos reservados";
        
        JOptionPane.showMessageDialog(this,
            acercaDe,
            "Acerca del Sistema",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private LM_CircularProducto catalogoConListas() {
        LM_CircularProducto lm = new LM_CircularProducto(2);
        lm.setCategoria(0, "Electrónica");
        lm.setCategoria(1, "Accesorios");
        
        lm.adiProducto(0, new Producto(1, "Laptop Lenovo", 5500, 5));
        lm.adiProducto(0, new Producto(2, "Smartphone Samsung", 3200, 10));
        lm.adiProducto(0, new Producto(3, "iPhone 15", 11000, 3));
        lm.adiProducto(0, new Producto(4, "Tablet Xiaomi", 2500, 7));
        lm.adiProducto(0, new Producto(5, "Smartwatch Apple", 3500, 4));
        
        lm.adiProducto(1, new Producto(6, "Mouse Logitech", 150, 20));
        lm.adiProducto(1, new Producto(7, "Teclado Mecánico", 300, 8));
        lm.adiProducto(1, new Producto(8, "Auriculares Bluetooth", 250, 15));
        lm.adiProducto(1, new Producto(9, "Cargador Rápido", 120, 30));
        lm.adiProducto(1, new Producto(10, "Memoria USB 64GB", 100, 25));
        
        return lm;
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

