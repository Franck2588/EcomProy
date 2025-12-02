package appgadget_grafica;

import java.util.Scanner;

public class LS_CircularPedido {

    protected NodoPedido P;

    public LS_CircularPedido() {
        P = null;
    }

    public boolean esVacia() {
        return P == null;
    }

    public int nroNodos() {
        if (esVacia()) {
            return 0;
        }

        int c = 0;
        NodoPedido R = P;
        while (R.getSig() != P) {
            c++;
            R = R.getSig();
        }
        c++;
        return c;
    }

    public void adiFinal(Pedido pedido) {
        NodoPedido nuevo = new NodoPedido();
        nuevo.setPedido(pedido);

        if (P == null) {
            P = nuevo;
            P.setSig(P);
        } else {
            NodoPedido R = P;
            while (R.getSig() != P) {
                R = R.getSig();
            }
            R.setSig(nuevo);
            nuevo.setSig(P);
        }
    }

    public void adiPrimero(Pedido pedido) {
        NodoPedido nuevo = new NodoPedido();
        nuevo.setPedido(pedido);

        if (P == null) {
            P = nuevo;
            P.setSig(P);
        } else {
            NodoPedido R = P;
            while (R.getSig() != P) {
                R = R.getSig();
            }
            R.setSig(nuevo);
            nuevo.setSig(P);
            P = nuevo;
        }
    }

    public Pedido eliPrimero() {
        if (esVacia()) {
            return null;
        }

        Pedido pedidoEliminado;
        if (P.getSig() == P) {
            pedidoEliminado = P.getPedido();
            P = null;
        } else {
            NodoPedido R = P;
            while (R.getSig() != P) {
                R = R.getSig();
            }
            pedidoEliminado = P.getPedido();
            P = P.getSig();
            R.setSig(P);
        }
        return pedidoEliminado;
    }

    public void mostrar() {
        if (esVacia()) {
            System.out.println("No hay pedidos");
            return;
        }

        NodoPedido R = P;
        while (R.getSig() != P) {
            R.getPedido().mostrar();
            R = R.getSig();
        }
        R.getPedido().mostrar();
    }

    public int cantidadTotalVendida(String nombreProducto) {
        if (esVacia()) {
            return 0;
        }

        int total = 0;
        NodoPedido R = P;
        while (R.getSig() != P) {
            if (R.getPedido().getProducto().equals(nombreProducto)) {
                total += R.getPedido().getCantidad();
            }
            R = R.getSig();
        }
        if (R.getPedido().getProducto().equals(nombreProducto)) {
            total += R.getPedido().getCantidad();
        }
        return total;
    }

    public void llenar(int n) {
        Scanner sc = new Scanner(System.in);
        for (int i = 1; i <= n; i++) {
            System.out.println("Pedido " + i + ":");
            Pedido p = new Pedido();
            p.leer();
            adiFinal(p);
        }
    }

    public NodoPedido getP() {
        return P;
    }

    public void setP(NodoPedido p) {
        P = p;
    }
}
