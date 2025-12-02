package appgadget_grafica;

import java.io.Serializable;

public class PilaFactura implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int max = 100;
    private Factura[] v = new Factura[max];
    private int tope;

    public PilaFactura() {
        tope = 0;
    }

    public boolean esVacia() {
        return tope == 0;
    }

    public boolean esLlena() {
        return tope == max;
    }

    public void adi(Factura f) {
        if (!esLlena()) {
            tope++;
            v[tope] = f;
        }
    }

    public Factura eli() {
        if (!esVacia()) {
            return v[tope--];
        }
        return null;
    }

    public void mostrar() {
        PilaFactura aux = new PilaFactura();
        System.out.println("\n=== PILA DE FACTURAS ===");
        System.out.println("(Ultima factura primero)");
        System.out.println("------------------------");
        
        int contador = 0;
        while (!esVacia()) {
            Factura f = eli();
            System.out.println("[" + (++contador) + "] Factura #" + f.getNumero() + 
                             " - " + f.getCliente().getNombre() + 
                             " - Bs " + f.getTotalConDescuento());
            aux.adi(f);
        }
        vaciar(aux);
        
        if (contador == 0) {
            System.out.println("No hay facturas");
        }
        System.out.println("------------------------");
    }

    public void vaciar(PilaFactura a) {
        while (!a.esVacia()) {
            adi(a.eli());
        }
    }

    public int nroFacturas() {
        return tope;
    }

    public Factura[] getFacturasPorCliente(String nombreCliente) {
        PilaFactura aux = new PilaFactura();
        Factura[] resultado = new Factura[max];
        int contador = 0;
        
        while (!esVacia()) {
            Factura f = eli();
            if (f.getCliente().getNombre().equalsIgnoreCase(nombreCliente)) {
                resultado[contador++] = f;
            }
            aux.adi(f);
        }
        vaciar(aux);
        
        Factura[] exacto = new Factura[contador];
        for (int i = 0; i < contador; i++) {
            exacto[i] = resultado[i];
        }
        return exacto;
    }

    public Factura[] getFacturas() {
        Factura[] resultado = new Factura[tope];
        for (int i = 1; i <= tope; i++) {
            resultado[i-1] = v[i];
        }
        return resultado;
    }
}