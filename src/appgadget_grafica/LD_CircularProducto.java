package appgadget_grafica;

public class LD_CircularProducto {

    protected NodoProducto P;

    public LD_CircularProducto() {
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
        NodoProducto R = P;
        do {
            c++;
            R = R.getSig();
        } while (R != P);

        return c;
    }

    public void adiPrimero(Producto producto) {
        NodoProducto nuevo = new NodoProducto();
        nuevo.setProducto(producto);

        if (esVacia()) {
            P = nuevo;
            P.setSig(P);
            P.setAnt(P);
        } else {
            NodoProducto ultimo = P.getAnt();
            nuevo.setSig(P);
            nuevo.setAnt(ultimo);
            P.setAnt(nuevo);
            ultimo.setSig(nuevo);
            P = nuevo;
        }
    }

    public void adiFinal(Producto producto) {
        NodoProducto nuevo = new NodoProducto();
        nuevo.setProducto(producto);

        if (esVacia()) {
            P = nuevo;
            P.setSig(P);
            P.setAnt(P);
        } else {
            NodoProducto ultimo = P.getAnt();
            ultimo.setSig(nuevo);
            nuevo.setAnt(ultimo);
            nuevo.setSig(P);
            P.setAnt(nuevo);
        }
    }

    public NodoProducto eliPrimero() {
        NodoProducto x = null;
        if (!esVacia()) {
            if (nroNodos() == 1) {
                x = P;
                P = null;
            } else {
                x = P;
                NodoProducto ultimo = P.getAnt();
                P = P.getSig();
                P.setAnt(ultimo);
                ultimo.setSig(P);
            }
            x.setSig(null);
            x.setAnt(null);
        }
        return x;
    }

    public NodoProducto eliFinal() {
        NodoProducto x = null;
        if (!esVacia()) {
            if (nroNodos() == 1) {
                x = P;
                P = null;
            } else {
                x = P.getAnt();
                NodoProducto anterior = x.getAnt();
                anterior.setSig(P);
                P.setAnt(anterior);
            }
            x.setSig(null);
            x.setAnt(null);
        }
        return x;
    }

    public void mostrar() {
        if (esVacia()) {
            System.out.println("Lista vacía");
            return;
        }

        NodoProducto R = P;
        do {
            R.getProducto().mostrar();
            R = R.getSig();
        } while (R != P);
    }

    public NodoProducto buscarProducto(int id) {
        if (esVacia()) {
            return null;
        }

        NodoProducto R = P;
        do {
            if (R.getProducto().getId() == id) {
                return R;
            }
            R = R.getSig();
        } while (R != P);

        return null;
    }

    public void actualizarStock(int id, int nuevaCantidad) {
        NodoProducto nodo = buscarProducto(id);
        if (nodo != null) {
            nodo.getProducto().setStock(nuevaCantidad);
        }
    }

    public NodoProducto getP() {
        return P;
    }

    public void setP(NodoProducto p) {
        P = p;
    }
}
