package appgadget_grafica;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String ci;
    private String correo;

    public Cliente(String nombre, String ci, String correo) {
        this.nombre = nombre;
        this.ci = ci;
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCi() {
        return ci;
    }

    public String getCorreo() {
        return correo;
    }

    public void mostrar() {
        System.out.println("Cliente: " + nombre + " | CI: " + ci + " | Correo: " + correo);
    }

    @Override
    public String toString() {
        return nombre + " (CI: " + ci + ")";
    }
}
