package appgadget_grafica;

import javax.swing.*;

public class AppGadget_Grafica {
    public static void main(String[] args) {
        // Establecer Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Ejecutar la aplicación
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}