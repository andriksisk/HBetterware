/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.controllers;

import betterware.dao.DetallePedidoDao;
import betterware.models.DetallePedido;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DetallesPedidoController implements Initializable {

    @FXML private Label lblIdPedido;
    @FXML private Label lblCliente;
    @FXML private Label lblTelefono;
    @FXML private Label lblFechaSolicitud;
    @FXML private Label lblFechaEntrega;
    @FXML private Label lblTotal;

    @FXML private TableView<DetallePedido> tablaDetalles;
    @FXML private TableColumn<DetallePedido, Integer> colId;
    @FXML private TableColumn<DetallePedido, String> colNombre;
    @FXML private TableColumn<DetallePedido, Integer> colCantidad;
    @FXML private TableColumn<DetallePedido, Double> colPrecio;

    private int idPedido;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    // Se llama desde TablaPrincipalController al abrir la ventana
    public void cargarPedido(int idPedido, String cliente, String telefono, String fechaSolicitud, String fechaEntrega) {
        this.idPedido = idPedido;

        lblIdPedido.setText(String.valueOf(idPedido));
        lblCliente.setText(cliente);
        lblTelefono.setText(telefono);
        lblFechaSolicitud.setText(fechaSolicitud);
        lblFechaEntrega.setText(fechaEntrega);

        cargarDetalles();
    }

    private void cargarDetalles() {
        DetallePedidoDao dao = new DetallePedidoDao();
        List<DetallePedido> detalles = dao.obtenerDetallesPorPedido(idPedido);

        ObservableList<DetallePedido> lista = FXCollections.observableArrayList(detalles);
        tablaDetalles.setItems(lista);

        double total = detalles.stream()
                .mapToDouble(d -> d.getPrecio() * d.getCantidad())
                .sum();

        lblTotal.setText(String.format("$ %.2f", total));
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) tablaDetalles.getScene().getWindow();
        stage.close();
    }
}




