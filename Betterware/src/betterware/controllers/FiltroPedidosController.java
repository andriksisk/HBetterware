/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.controllers;

import betterware.dao.PedidosDao;
import betterware.models.Pedidos;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

public class FiltroPedidosController implements Initializable {

    private TablaPrincipalController tablaPrincipalController;

    @FXML
    private DatePicker dpFechaPedido;  // 🔴 ESTE NOMBRE debe coincidir con el fx:id del FXML

    public void setTablaPrincipalController(TablaPrincipalController controller) {
        this.tablaPrincipalController = controller;
    }

    @FXML
    private void CancelarBton() {
        Stage st = (Stage) dpFechaPedido.getScene().getWindow();
        st.close();
    }

    @FXML
    private void guardarBuscarBton() {
        if (dpFechaPedido == null || dpFechaPedido.getValue() == null) {
            mostrar("Selecciona una fecha.");
            return;
        }

        LocalDate fecha = dpFechaPedido.getValue();

        // Buscar en la BD (fechaEntrega O fechaSolicitud)
        PedidosDao dao = new PedidosDao();
        List<Pedidos> lista = dao.buscarPedidosPorFecha(fecha);

        if (tablaPrincipalController != null) {
            tablaPrincipalController.actualizarTablaPedidosFiltrados(lista);
        }

        // cerrar ventana de filtros
        Stage st = (Stage) dpFechaPedido.getScene().getWindow();
        st.close();
    }

    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // opcional: poner fecha de hoy por defecto
        // dpFechaPedido.setValue(LocalDate.now());
    }
}


