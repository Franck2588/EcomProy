package appgadget_grafica;

public class NodoProducto {

    private Producto producto;
    private NodoProducto sig;
    private NodoProducto ant;

    public NodoProducto() {
        sig = null;
        ant = null;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public NodoProducto getSig() {
        return sig;
    }

    public void setSig(NodoProducto sig) {
        this.sig = sig;
    }

    public NodoProducto getAnt() {
        return ant;
    }

    public void setAnt(NodoProducto ant) {
        this.ant = ant;
    }
}
