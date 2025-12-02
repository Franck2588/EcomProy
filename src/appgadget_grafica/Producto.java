package appgadget_grafica;

import java.io.Serializable;
import java.util.Scanner;

public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private double precio;
    private int stock;

    public Producto() {
    }

    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public void leer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID: ");
        id = sc.nextInt();
        sc.nextLine();
        System.out.print("Nombre: ");
        nombre = sc.nextLine();
        System.out.print("Precio: ");
        precio = sc.nextDouble();
        System.out.print("Stock: ");
        stock = sc.nextInt();
    }

    public void mostrar() {
        System.out.println(id + " - " + nombre + " - Bs " + precio + " - Stock: " + stock);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
