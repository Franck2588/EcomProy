package appgadget_grafica;

public class NodoPedido {
    private Pedido pedido;
    private NodoPedido sig;

    public NodoPedido() {
        sig = null;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public NodoPedido getSig() {
        return sig;
    }

    public void setSig(NodoPedido sig) {
        this.sig = sig;
    }
}