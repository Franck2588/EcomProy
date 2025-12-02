package appgadget_grafica;

import java.io.*;

public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int numero;
    private Cliente cliente;
    private Pedido[] pedidos;
    private int cantidadPedidos;
    private double total;
    private double descuento;
    private double totalConDescuento;
    private String fecha;

    public Factura(Cliente cliente, String fecha) {
        this.numero = obtenerProximoNumero();
        this.cliente = cliente;
        this.pedidos = new Pedido[50];
        this.cantidadPedidos = 0;
        this.total = 0;
        this.descuento = 0;
        this.totalConDescuento = 0;
        this.fecha = fecha;
    }

    // Método simple para número de factura
    private int obtenerProximoNumero() {
        try {
            File archivo = new File("contador_facturas.dat");
            int ultimo;
            
            if (archivo.exists()) {
                ObjectInputStream arch = new ObjectInputStream(new FileInputStream(archivo));
                ultimo = arch.readInt();
                arch.close();
            } else {
                ultimo = 1000;
            }
            
            int siguiente = ultimo + 1;
            
            ObjectOutputStream archSalida = new ObjectOutputStream(new FileOutputStream(archivo));
            archSalida.writeInt(siguiente);
            archSalida.close();
            
            return siguiente;
            
        } catch (IOException e) {
            return 1001;
        }
    }

    public void agregarPedido(Pedido pedido) {
        if (cantidadPedidos < 50) {
            pedidos[cantidadPedidos++] = pedido;
            total += pedido.getTotal();
        }
    }

    public void calcularTotal() {
        totalConDescuento = total - (total * (descuento / 100));
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public void mostrar() {
        calcularTotal();

        System.out.println("\n============================================");
        System.out.println("             FACTURA ELECTRÓNICA           ");
        System.out.println("============================================");
        System.out.println("Nro Factura: " + numero);
        System.out.println("Fecha: " + fecha);
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("CI: " + cliente.getCi());
        System.out.println("Correo: " + cliente.getCorreo());
        if (descuento > 0) {
            System.out.println("DESCUENTO AUTOMÁTICO APLICADO: " + descuento + "%");
        }

        System.out.println("--------------------------------------------");
        System.out.println("Producto              Cant.   Subtotal");

        for (int i = 0; i < cantidadPedidos; i++) {
            System.out.printf("%-20s %-6d %-10.2f%n",
                    pedidos[i].getProducto(),
                    pedidos[i].getCantidad(),
                    pedidos[i].getTotal());
        }

        System.out.println("--------------------------------------------");
        System.out.printf("SUBTOTAL: %.2f Bs%n", total);

        if (descuento > 0) {
            double montoDescuento = total * (descuento / 100);
            System.out.printf("DESCUENTO (%.0f%%): -%.2f Bs%n", descuento, montoDescuento);
            System.out.println("--------------------------------------------");
        }

        System.out.printf("TOTAL A PAGAR: %.2f Bs%n", totalConDescuento);
        System.out.println("\nGracias por su compra!");
        System.out.println("============================================\n");
    }

    // Getters
    public int getNumero() { return numero; }
    public double getTotal() { return total; }
    public double getDescuento() { return descuento; }
    public double getTotalConDescuento() { return totalConDescuento; }
    public String getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public int getCantidadPedidos() { return cantidadPedidos; }
    public Pedido[] getPedidos() { return pedidos; }
}