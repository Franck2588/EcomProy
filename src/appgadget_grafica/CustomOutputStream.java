package appgadget_grafica;

import javax.swing.*;
import java.io.*;

public class CustomOutputStream extends OutputStream {

    private JTextArea textArea;
    private StringBuilder buffer;
    private static final int BUFFER_SIZE = 1000;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
        this.buffer = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {
        // Convertir el byte a char
        char c = (char) b;

        // Agregar al buffer
        buffer.append(c);

        // Si es salto de línea o buffer lleno, actualizar el JTextArea
        if (c == '\n' || buffer.length() >= BUFFER_SIZE) {
            flushBuffer();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String text = new String(b, off, len);
        buffer.append(text);

        // Si hay salto de línea o buffer lleno, actualizar
        if (text.contains("\n") || buffer.length() >= BUFFER_SIZE) {
            flushBuffer();
        }
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
        super.flush();
    }

    private void flushBuffer() {
        if (buffer.length() > 0) {
            final String text = buffer.toString();

            // Actualizar el JTextArea en el hilo de eventos de Swing
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);

                // Auto-scroll al final
                textArea.setCaretPosition(textArea.getDocument().getLength());

                // Limitar el tamaño del texto para evitar problemas de memoria
                int maxLength = 10000;
                if (textArea.getDocument().getLength() > maxLength) {
                    try {
                        textArea.getDocument().remove(0, textArea.getDocument().getLength() - maxLength);
                    } catch (Exception e) {
                        // Ignorar excepciones al truncar
                    }
                }
            });

            // Limpiar el buffer
            buffer.setLength(0);
        }
    }

    @Override
    public void close() throws IOException {
        flushBuffer();
        super.close();
    }
}
