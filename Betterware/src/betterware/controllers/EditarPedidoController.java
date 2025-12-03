/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.controllers;

import betterware.aux.ItemFila;
import betterware.dao.DetallePedidoDao;
import betterware.dao.PedidosDao;
import betterware.models.DetallePedido;
import betterware.models.Pedidos;
import betterware.models.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EditarPedidoController implements Initializable {

    @FXML private TableView<ItemFila> tablaPedido;
    @FXML private TableColumn<ItemFila, Integer> colId;
    @FXML private TableColumn<ItemFila, String> colNombre;
    @FXML private TableColumn<ItemFila, Integer> colCantidad;
    @FXML private TableColumn<ItemFila, BigDecimal> colPrecio;

    @FXML private TextField txtTotal;
    @FXML private TextField txtCliente;
    @FXML private TextField txtTelefono;
    @FXML private DatePicker fechaEntrega;
    @FXML private DatePicker fechaSolicitud;

    @FXML private Button btnAgregarFila;
    @FXML private Button btnEliminarFila;
    @FXML private Button CancelarBoton;
    @FXML private Button GuardarEdit;

    private ObservableList<ItemFila> filas;

    private TablaPrincipalController tablaPrincipalController;
    private Pedidos pedido;   // el pedido que estamos editando

    public void setTablaPrincipalController(TablaPrincipalController c) {
        this.tablaPrincipalController = c;
    }

    public void setPedido(Pedidos pedido) {
        this.pedido = pedido;
        cargarDatosPedido();
        cargarDetalles();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filas = FXCollections.observableArrayList();

        colId.setCellValueFactory(cd -> cd.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        colCantidad.setCellValueFactory(cd -> cd.getValue().cantidadProperty().asObject());
        colPrecio.setCellValueFactory(cd -> cd.getValue().precioTotalProperty());

        tablaPedido.setItems(filas);

        // primera fila vacía
        filas.add(new ItemFila());

        // ocultar ceros
        colId.setCellFactory(col -> new TableCell<ItemFila, Integer>() {
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null || value == 0) {
                    setText("");
                } else {
                    setText(String.valueOf(value));
                }
            }
        });

        colPrecio.setCellFactory(col -> new TableCell<ItemFila, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null || value.compareTo(BigDecimal.ZERO) == 0) {
                    setText("");
                } else {
                    setText(value.toString());
                }
            }
        });

        configurarSpinners();
    }

    // =========================
    //   CARGAR DATOS DEL PEDIDO
    // =========================
    private void cargarDatosPedido() {
        if (pedido == null) return;

        txtCliente.setText(pedido.getCliente());
        txtTelefono.setText(pedido.getTelefonoCliente());

        if (pedido.getFechaEntrega() != null)
            fechaEntrega.setValue(LocalDate.parse(pedido.getFechaEntrega()));
        if (pedido.getFechaSolicitud() != null)
            fechaSolicitud.setValue(LocalDate.parse(pedido.getFechaSolicitud()));
    }

    // =========================
    //   CARGAR DETALLES EN LA TABLA
    // =========================
    private void cargarDetalles() {
        filas.clear();

        DetallePedidoDao dao = new DetallePedidoDao();
        List<DetallePedido> detalles = dao.obtenerDetallesPorPedido(pedido.getId());

        for (DetallePedido d : detalles) {
            // precio unitario = precio total / cantidad
            BigDecimal total = BigDecimal.valueOf(d.getPrecio());
            BigDecimal qty = BigDecimal.valueOf(d.getCantidad());
            BigDecimal unit = qty.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : total.divide(qty, 2, RoundingMode.HALF_UP);

            // crear un Producto "dummy" para que ItemFila funcione
            Producto prod = new Producto();
            prod.setId(d.getIdProducto());
            prod.setProducto(d.getNombreProducto());
            prod.setPrecio_estandar(unit);
            prod.setCantidad(9999); // stock grande para no limitar el spinner

            ItemFila fila = new ItemFila();
            fila.setProducto(prod);
            fila.setCantidad(d.getCantidad());
            fila.setPrecioUnitario(unit);
            fila.setMaxCantidad(9999);
            fila.recalcularTotalSegunPromocion();

            filas.add(fila);
        }

        // aseguramos una fila vacía
        if (filas.stream().noneMatch(f -> f.getId() == 0)) {
            filas.add(new ItemFila());
        }

        tablaPedido.refresh();
        recalcularTotal();
    }

    // =========================
    //   SPINNER CANTIDAD
    // =========================
    private void configurarSpinners() {
        colCantidad.setCellFactory(col -> new TableCell<ItemFila, Integer>() {

            private final Spinner<Integer> spinner = new Spinner<>();

            {
                spinner.setEditable(false);
                spinner.valueProperty().addListener((o, oldVal, newVal) -> {
                    ItemFila fila = getTableRow() != null ? getTableRow().getItem() : null;
                    if (fila == null || fila.getId() == 0 || newVal == null) return;

                    fila.setCantidad(newVal);
                    fila.recalcularTotalSegunPromocion();
                    tablaPedido.refresh();
                    recalcularTotal();
                });
            }

            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                ItemFila f = getTableRow().getItem();

                if (f.getId() == 0 || f.getProducto() == null) {
                    setGraphic(null);
                    return;
                }

                int max = f.getMaxCantidad();
                if (max < 1) max = 1;

                int current = f.getCantidad();
                if (current < 1) current = 1;
                if (current > max) current = max;

                spinner.setValueFactory(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, max, current)
                );

                setGraphic(spinner);
            }
        });
    }

    private void recalcularTotal() {
        BigDecimal total = filas.stream()
                .filter(f -> f.getId() != 0)
                .map(ItemFila::getPrecioTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        txtTotal.setText(total.setScale(2, RoundingMode.HALF_UP).toString());
    }

    // =========================
    //   BOTONES AGREGAR / ELIMINAR PRODUCTO
    // =========================
    @FXML
    private void agregarProductoDesdeBoton() {
        ItemFila vacia = filas.stream().filter(f -> f.getId() == 0).findFirst().orElse(null);

        if (vacia == null) {
            vacia = new ItemFila();
            filas.add(vacia);
        }

        abrirSelector(vacia);
    }

    @FXML
    private void eliminarFilaSeleccionada() {
        ItemFila sel = tablaPedido.getSelectionModel().getSelectedItem();
        if (sel == null || sel.getId() == 0) return;

        filas.remove(sel);

        if (filas.stream().noneMatch(f -> f.getId() == 0)) {
            filas.add(new ItemFila());
        }

        tablaPedido.refresh();
        recalcularTotal();
    }

    private void abrirSelector(ItemFila fila) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/betterware/tablaPrincipal.fxml"));
            Parent root = loader.load();

            TablaPrincipalController ctrl = loader.getController();

            ctrl.setOnProductoSeleccionado(producto -> {
                fila.setProducto(producto);
                fila.setCantidad(1);
                fila.setPrecioUnitario(producto.getPrecio_estandar());
                fila.setMaxCantidad(producto.getCantidad());
                fila.recalcularTotalSegunPromocion();

                if (filas.stream().noneMatch(f -> f.getId() == 0))
                    filas.add(new ItemFila());

                tablaPedido.refresh();
                recalcularTotal();
                Platform.runLater(() -> ((Stage) root.getScene().getWindow()).close());
            });

            Stage st = new Stage();
            st.setScene(new Scene(root));
            st.setTitle("Seleccionar producto");
            st.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    //   GUARDAR CAMBIOS
    // =========================
    @FXML
    private void guardarCambios() {

        if (pedido == null) return;

        if (txtCliente.getText().isBlank()) {
            mostrar("Falta cliente");
            return;
        }

        if (fechaEntrega.getValue() == null || fechaSolicitud.getValue() == null) {
            mostrar("Debe seleccionar las fechas");
            return;
        }

        // actualizar objeto pedido
        pedido.setCliente(txtCliente.getText());
        pedido.setTelefonoCliente(txtTelefono.getText());
        pedido.setFechaEntrega(fechaEntrega.getValue().toString());
        pedido.setFechaSolicitud(fechaSolicitud.getValue().toString());

        PedidosDao pDao = new PedidosDao();
        boolean okPedido = pDao.actualizarPedido(pedido);

        if (!okPedido) {
            mostrar("Error al actualizar el pedido.");
            return;
        }

        // actualizar detalles: borro todos y los inserto de nuevo
        DetallePedidoDao dDao = new DetallePedidoDao();
        dDao.eliminarDetallesPorPedido(pedido.getId());

        for (ItemFila f : filas) {
            if (f.getId() == 0) continue;

            DetallePedido d = new DetallePedido(
                    f.getProducto().getId(),
                    f.getNombre(),
                    f.getCantidad(),
                    f.getPrecioTotal().doubleValue()
            );

            dDao.insertarDetalle(pedido.getId(), d);
        }

        mostrar("Pedido actualizado correctamente.");
        cerrarVentana();
        if (tablaPrincipalController != null) {
            tablaPrincipalController.recargarPedidos();
        }
    }

    // =========================
    //   UTILIDADES
    // =========================
    private void cerrarVentana() {
        Stage st = (Stage) CancelarBoton.getScene().getWindow();
        st.close();
    }

    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void CancelarBton() {
        cerrarVentana();
    }
}


