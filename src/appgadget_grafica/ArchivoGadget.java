package appgadget_grafica;


import java.io.*;
import java.util.Scanner;

public class ArchivoGadget {
    private String nom_arch;

    public ArchivoGadget() {
        nom_arch = "gadget_store.dat";
    }

    public ArchivoGadget(String nom_arch) {
        this.nom_arch = nom_arch;
    }

    public void crear() throws IOException {
        ObjectOutputStream arch = new ObjectOutputStream(new FileOutputStream(nom_arch));
        arch.close();
        System.out.println("Archivo creado: " + nom_arch);
    }

    public void guardarProductos(LM_CircularProducto productos) throws IOException {
        String arch_aux = "productos_temp.dat";
        ObjectOutputStream arch_temp = new ObjectOutputStream(new FileOutputStream(arch_aux));
        
        arch_temp.writeInt(productos.getN());
        
        for (int i = 0; i < productos.getN(); i++) {
            arch_temp.writeObject(productos.getCategoria(i));
            
            LD_CircularProducto listaCat = productos.getLista(i);
            guardarListaProductos(arch_temp, listaCat);
        }
        
        arch_temp.close();
        
        File ARCH = new File("productos.dat");
        File ARCH_TEMP = new File(arch_aux);
        ARCH.delete();
        ARCH_TEMP.renameTo(ARCH);
    }

    private void guardarListaProductos(ObjectOutputStream arch, LD_CircularProducto lista) throws IOException {
        if (lista.esVacia()) {
            arch.writeInt(0); 
            return;
        }
        
        int contador = 0;
        NodoProducto R = lista.getP();
        NodoProducto inicio = R;
        do {
            contador++;
            R = R.getSig();
        } while (R != inicio);
        
        arch.writeInt(contador); 
        
        R = lista.getP();
        do {
            arch.writeObject(R.getProducto());
            R = R.getSig();
        } while (R != inicio);
    }

    public LM_CircularProducto cargarProductos() throws IOException, ClassNotFoundException {
        LM_CircularProducto lm = new LM_CircularProducto(2);        
        try {
            ObjectInputStream arch = new ObjectInputStream(new FileInputStream("productos.dat"));
            
            int numCategorias = arch.readInt();
            lm = new LM_CircularProducto(numCategorias);
            
            for (int i = 0; i < numCategorias; i++) {
                String nombreCategoria = (String) arch.readObject();
                lm.setCategoria(i, nombreCategoria);
                
                LD_CircularProducto listaCat = cargarListaProductos(arch);
                lm.getLista(i).setP(listaCat.getP());
            }
            
            arch.close();
            //System.out.println("Productos cargados: " + numCategorias + " categorías");
            
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de productos no encontrado. Se usará catálogo por defecto.");
            return catalogo();
        }
        
        return lm;
    }

    private LD_CircularProducto cargarListaProductos(ObjectInputStream arch) throws IOException, ClassNotFoundException {
        LD_CircularProducto lista = new LD_CircularProducto();
        
        int cantidad = arch.readInt();
        for (int i = 0; i < cantidad; i++) {
            Producto producto = (Producto) arch.readObject();
            lista.adiFinal(producto);
        }
        
        return lista;
    }

    public void guardarFacturas(RegistroFacturas facturas) throws IOException {
        String arch_aux = "facturas_temp.dat";
        ObjectOutputStream arch_temp = new ObjectOutputStream(new FileOutputStream(arch_aux));
        
        arch_temp.writeInt(facturas.getCantidad());
        
        Factura[] facturasArray = facturas.getFacturas();
        for (int i = 0; i < facturas.getCantidad(); i++) {
            arch_temp.writeObject(facturasArray[i]);
        }
        
        arch_temp.close();
        
        File ARCH = new File("facturas.dat");
        File ARCH_TEMP = new File(arch_aux);
        ARCH.delete();
        ARCH_TEMP.renameTo(ARCH);
    }

    public RegistroFacturas cargarFacturas() throws IOException, ClassNotFoundException {
        RegistroFacturas rf = new RegistroFacturas();
        
        try {
            ObjectInputStream arch = new ObjectInputStream(new FileInputStream("facturas.dat"));
            
            int cantidad = arch.readInt();
            
            for (int i = 0; i < cantidad; i++) {
                Factura factura = (Factura) arch.readObject();
                rf.agregarFactura(factura);
            }
            
            arch.close();
            //System.out.println("Facturas cargadas: " + cantidad);
            
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de facturas no encontrado. Se iniciará vacío.");
        }
        
        return rf;
    }

    public void guardarPedidos(LM_CircularPedido pedidos) throws IOException {
        String arch_aux = "pedidos_temp.dat";
        ObjectOutputStream arch_temp = new ObjectOutputStream(new FileOutputStream(arch_aux));
        
        arch_temp.writeInt(pedidos.getN());
        
        for (int i = 0; i < pedidos.getN(); i++) {
            arch_temp.writeObject(pedidos.getTipo(i));
            
            LS_CircularPedido listaTipo = pedidos.getLista(i);
            guardarListaPedidos(arch_temp, listaTipo);
        }
        
        arch_temp.close();
        
        File ARCH = new File("pedidos.dat");
        File ARCH_TEMP = new File(arch_aux);
        ARCH.delete();
        ARCH_TEMP.renameTo(ARCH);
    }

    private void guardarListaPedidos(ObjectOutputStream arch, LS_CircularPedido lista) throws IOException {
        if (lista.esVacia()) {
            arch.writeInt(0); 
            return;
        }
        
        int contador = 0;
        NodoPedido R = lista.getP();
        NodoPedido inicio = R;
        do {
            contador++;
            R = R.getSig();
        } while (R != inicio);
        
        arch.writeInt(contador); 
        
        R = lista.getP();
        do {
            arch.writeObject(R.getPedido());
            R = R.getSig();
        } while (R != inicio);
    }

    public LM_CircularPedido cargarPedidos() throws IOException, ClassNotFoundException {
        LM_CircularPedido lm = new LM_CircularPedido(3); // 3 tipos por defecto
        
        try {
            ObjectInputStream arch = new ObjectInputStream(new FileInputStream("pedidos.dat"));
            
            int numTipos = arch.readInt();
            lm = new LM_CircularPedido(numTipos);
            
            for (int i = 0; i < numTipos; i++) {
                String nombreTipo = (String) arch.readObject();
                lm.setTipo(i, nombreTipo);
                
                LS_CircularPedido listaTipo = cargarListaPedidos(arch);
                lm.getLista(i).setP(listaTipo.getP());
            }
            
            arch.close();
            //System.out.println("Pedidos cargados: " + numTipos + " tipos");
            
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de pedidos no encontrado. Se iniciará vacío.");
            lm.setTipo(0, "Normal");
            lm.setTipo(1, "Express");
            lm.setTipo(2, "Prioritario");
        }
        
        return lm;
    }
    private LS_CircularPedido cargarListaPedidos(ObjectInputStream arch) throws IOException, ClassNotFoundException {
        LS_CircularPedido lista = new LS_CircularPedido();
        
        int cantidad = arch.readInt();
        for (int i = 0; i < cantidad; i++) {
            Pedido pedido = (Pedido) arch.readObject();
            lista.adiFinal(pedido);
        }
        
        return lista;
    }

    private LM_CircularProducto catalogo() {
        LM_CircularProducto lm = new LM_CircularProducto(2);
        lm.setCategoria(0, "Electrónica");
        lm.setCategoria(1, "Accesorios");

        lm.adiProducto(0, new Producto(1, "Laptop Lenovo", 5500, 5));
        lm.adiProducto(0, new Producto(2, "Smartphone Samsung", 3200, 10));
        lm.adiProducto(0, new Producto(3, "iPhone 15", 11000, 3));
        lm.adiProducto(0, new Producto(4, "Tablet Xiaomi", 2500, 7));
        lm.adiProducto(0, new Producto(5, "Smartwatch Apple", 3500, 4));

        lm.adiProducto(1, new Producto(6, "Mouse Logitech", 150, 20));
        lm.adiProducto(1, new Producto(7, "Teclado Mecánico", 300, 8));
        lm.adiProducto(1, new Producto(8, "Auriculares Bluetooth", 250, 15));
        lm.adiProducto(1, new Producto(9, "Cargador Rápido", 120, 30));
        lm.adiProducto(1, new Producto(10, "Memoria USB 64GB", 100, 25));

        return lm;
    }
}