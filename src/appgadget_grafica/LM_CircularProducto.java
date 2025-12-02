package appgadget_grafica;

import java.util.Scanner;

public class LM_CircularProducto {

    private int n;
    private String[] categorias;
    private LD_CircularProducto[] listas;

    public LM_CircularProducto(int n) {
        this.n = n;
        categorias = new String[n];
        listas = new LD_CircularProducto[n];
        for (int i = 0; i < n; i++) {
            listas[i] = new LD_CircularProducto();
        }
    }

    public void setCategoria(int i, String nombre) {
        categorias[i] = nombre;
    }

    public void adiProducto(int i, Producto p) {
        listas[i].adiFinal(p);
    }

    public void mostrar() {
        for (int i = 0; i < n; i++) {
            System.out.println("\n=== CATEGORÍA: " + categorias[i] + " ===");
            listas[i].mostrar();
        }
    }

    public LD_CircularProducto getLista(int i) {
        return listas[i];
    }

    public String getCategoria(int i) {
        return categorias[i];
    }

    public int getN() {
        return n;
    }

    public Producto buscarProductoGlobal(int id) {
        for (int i = 0; i < n; i++) {
            NodoProducto nodo = listas[i].buscarProducto(id);
            if (nodo != null) {
                return nodo.getProducto();
            }
        }
        return null;
    }

    public void llenarCategoria(int i, int cantidad) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Llenando categoría: " + categorias[i]);

        for (int j = 0; j < cantidad; j++) {
            System.out.println("Producto " + (j + 1) + ":");
            Producto p = new Producto();
            p.leer();
            listas[i].adiFinal(p);
        }
    }
}
