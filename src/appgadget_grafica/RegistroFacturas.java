package appgadget_grafica;

import java.io.Serializable;

public class RegistroFacturas implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private PilaFactura pilaFacturas;

    public RegistroFacturas() {
        pilaFacturas = new PilaFactura();
    }

    public void agregarFactura(Factura factura) {
        pilaFacturas.adi(factura);
    }

    public void mostrarFacturas() {
        pilaFacturas.mostrar();
    }

    public Factura[] getFacturasPorCliente(String nombreCliente) {
        return pilaFacturas.getFacturasPorCliente(nombreCliente);
    }

    public Factura[] getFacturas() {
        return pilaFacturas.getFacturas();
    }

    public int getCantidad() {
        return pilaFacturas.nroFacturas();
    }
}