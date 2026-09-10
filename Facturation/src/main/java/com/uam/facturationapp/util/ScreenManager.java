package com.uam.facturationapp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;

public final class ScreenManager {

    // Todas las vistas se muestran en el centro del menú principal: una sola ventana.
    private static BorderPane contenedor;
    private static Node inicio;

    private ScreenManager() {
    }

    public static void setContenedor(BorderPane contenedorPrincipal) {
        contenedor = contenedorPrincipal;
        inicio = contenedorPrincipal.getCenter();
    }

    public static void mostrarVista(String recurso) throws IOException {
        URL url = ScreenManager.class.getResource(recurso);
        if (url == null) {
            throw new IOException("FXML no encontrado: " + recurso);
        }

        Parent vista = new FXMLLoader(url).load();
        contenedor.setCenter(vista);
    }

    public static void mostrarInicio() {
        contenedor.setCenter(inicio);
    }
}
