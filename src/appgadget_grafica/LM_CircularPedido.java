package appgadget_grafica;

import java.util.Scanner;

public class LM_CircularPedido {

    private int n;
    private String[] tipos;
    private LS_CircularPedido[] listas;

    public LM_CircularPedido(int n) {
        this.n = n;
        tipos = new String[n];
        listas = new LS_CircularPedido[n];
        for (int i = 0; i < n; i++) {
            listas[i] = new LS_CircularPedido();
        }
    }

    public void setTipo(int i, String tipo) {
        tipos[i] = tipo;
    }

    public void adiPedido(int i, Pedido pedido) {
        listas[i].adiFinal(pedido);
    }

    public void mostrar() {
        for (int i = 0; i < n; i++) {
            System.out.println("\n=== TIPO DE PEDIDO: " + tipos[i] + " ===");
            listas[i].mostrar();
        }
    }

    public LS_CircularPedido getLista(int i) {
        return listas[i];
    }

    public String getTipo(int i) {
        return tipos[i];
    }

    public int getN() {
        return n;
    }

    public void llenarTipo(int i, int cantidad) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Llenando pedidos tipo: " + tipos[i]);
        listas[i].llenar(cantidad);
    }

    public int getTotalPedidos() {
        int total = 0;
        for (int i = 0; i < n; i++) {
            total += listas[i].nroNodos();
        }
        return total;
    }
}
