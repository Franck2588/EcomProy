package appgadget_grafica;

import java.io.Serializable;
import java.util.Scanner;

public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cliente;
    private String producto;
    private int cantidad;
    private double total;

    public Pedido() {
    }

    public Pedido(String cliente, String producto, int cantidad, double total) {
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.total = total;
    }

    public void leer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nombre del cliente: ");
        cliente = sc.nextLine();
        System.out.print("Producto: ");
        producto = sc.nextLine();
        System.out.print("Cantidad: ");
        cantidad = sc.nextInt();
        System.out.print("Total Bs: ");
        total = sc.nextDouble();
        sc.nextLine();
    }

    public void mostrar() {
        System.out.println("Cliente: " + cliente + " | Producto: " + producto
                + " | Cant: " + cantidad + " | Total: Bs " + total);
    }

    public String getCliente() {
        return cliente;
    }

    public String getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getTotal() {
        return total;
    }
}
