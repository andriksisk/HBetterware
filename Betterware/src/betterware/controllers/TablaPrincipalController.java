 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package betterware.controllers;

import betterware.dao.DetallePedidoDao;
import betterware.dao.PedidosDao;
import betterware.dao.ProductosDao;
import betterware.models.Pedidos;
import betterware.models.Producto;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;


public class TablaPrincipalController implements Initializable {

    @FXML
    private TableView<Object> ProductosView;//cambiar nombre a tabla principal
    
    @FXML
    private Button recargarBton;
    
    @FXML 
    private TextField txtBuscar;
  
    @FXML
    private Button FiltroBton;
    
    //parte para pedidos//
    @FXML
    private ToggleButton switchModo;
    @FXML
    private boolean modoPedidos=false;
    private ObservableList<Object> listaPedidos;
    //
    @FXML
    private Button buscarBton;
    @FXML
    private Button agregarBton;
    @FXML
    private Button EditarBoton;
    @FXML 
    private Button EliminarBoton;
    @FXML
    private Button agregarPedido;

    private ObservableList<Object> listaProductos;
    private Consumer<Producto> onProductoSeleccionado;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarProductos(); // se ejecuta al iniciar
        switchModo.setOnAction(e -> cambiarModo());
        ProductosView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Producto seleccionado = (Producto) ProductosView.getSelectionModel().getSelectedItem();
                if (seleccionado != null && onProductoSeleccionado != null) {
                    onProductoSeleccionado.accept(seleccionado);

                    // cerrar ventana del selector
                    ((Stage) ProductosView.getScene().getWindow()).close();
                }
            }
        });
    }
   
    @FXML
    private void cambiarModo(){
        modoPedidos = switchModo.isSelected();
        
        if(modoPedidos){
            switchModo.setText("Modo Pedidos");
            cargarPedidos();
            configurarBotonesParaPedidos();
        }else{
            switchModo.setText("Modo Productos");
            cargarProductos();
            configurarBotonesParaProductos();
        }
    }
    
    private void configurarBotonesParaProductos() {
        agregarBton.setOnAction(e -> accionAgregar());
        EditarBoton.setOnAction(e -> EditarBton());
        EliminarBoton.setOnAction(e -> EliminarBton());
        FiltroBton.setOnAction(e -> FiltroProductos());
        recargarBton.setOnAction(e -> recargarProductos());
    }
    private void configurarBotonesParaPedidos(){
        agregarBton.setOnAction( e -> accionPedido());
        EditarBoton.setOnAction(e -> editarPedidoSeleccionado());
        EliminarBoton.setOnAction(e -> EliminarBtonP());
        recargarBton.setOnAction(e -> recargarPedidos());
        FiltroBton.setOnAction(e -> FiltroPedidos());

    }
    
    

    private void cargarProductos() {
    ProductosView.getColumns().clear();

    TableColumn<Object, Integer> colId = new TableColumn<>("ID");
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));

    TableColumn<Object, String> colCod = new TableColumn<>("Código");
    colCod.setCellValueFactory(new PropertyValueFactory<>("codigo"));

    TableColumn<Object, String> colNombre = new TableColumn<>("Producto");
    colNombre.setCellValueFactory(new PropertyValueFactory<>("producto"));

    TableColumn<Object, String> colDescripcion = new TableColumn<>("Descripción");
    colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

    TableColumn<Object, String> colEspcs = new TableColumn<>("Especificaciones");
    colEspcs.setCellValueFactory(new PropertyValueFactory<>("especificaciones"));

    TableColumn<Object, BigDecimal> colPC = new TableColumn<>("Precio Compra");
    colPC.setCellValueFactory(new PropertyValueFactory<>("precio_compra"));

    TableColumn<Object, BigDecimal> colPE = new TableColumn<>("Precio Estándar");
    colPE.setCellValueFactory(new PropertyValueFactory<>("precio_estandar"));

    TableColumn<Object, BigDecimal> colPP = new TableColumn<>("Precio Promoción");
    colPP.setCellValueFactory(new PropertyValueFactory<>("precio_promocion"));

    TableColumn<Object, String> colCat = new TableColumn<>("Categoría");
    colCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));

    TableColumn<Object, Integer> colCant = new TableColumn<>("Cantidad");
    colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

    TableColumn<Object, String> colBolsa = new TableColumn<>("Bolsa");
    colBolsa.setCellValueFactory(new PropertyValueFactory<>("bolsa"));

    TableColumn<Object, Boolean> colNuevo = new TableColumn<>("Nuevo");
    colNuevo.setCellValueFactory(new PropertyValueFactory<>("nuevo"));

    TableColumn<Object, String> colProm = new TableColumn<>("Promoción");
    colProm.setCellValueFactory(new PropertyValueFactory<>("promocion"));

    ProductosView.getColumns().addAll(colId, colCod, colNombre, colDescripcion, colEspcs,colPC, colPE, colPP, colCat, colCant, colBolsa, colNuevo, colProm);

    listaProductos = FXCollections.observableArrayList(new ProductosDao().obtenerProductos());
    ProductosView.setItems(listaProductos);
    }
    
private void cargarPedidos() {
    ProductosView.getColumns().clear();
    ProductosView.getItems().clear();

    TableColumn<Object, Integer> colId = new TableColumn<>("ID");
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));

    TableColumn<Object, String> colCliente = new TableColumn<>("Cliente");
    colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));

    TableColumn<Object, String> colTelefono = new TableColumn<>("Teléfono");
    colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoCliente"));

    TableColumn<Object, String> colFechaEnt = new TableColumn<>("Fecha Entrega");
    colFechaEnt.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));

    TableColumn<Object, String> colFechaSol = new TableColumn<>("Fecha Solicitud");
    colFechaSol.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));

    // ====== FORMATO MEXICANO EN LAS DOS FECHAS ======
    DateTimeFormatter dest = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    colFechaEnt.setCellFactory(col -> new TableCell<Object, String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || item.isBlank()) {
                setText(null);
            } else {
                try {
                    // viene como yyyy-MM-dd desde la BD
                    LocalDate d = LocalDate.parse(item);
                    setText(d.format(dest));          // se muestra dd/MM/yyyy
                } catch (Exception e) {
                    // por si acaso, deja el valor tal cual
                    setText(item);
                }
            }
        }
    });

    colFechaSol.setCellFactory(col -> new TableCell<Object, String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || item.isBlank()) {
                setText(null);
            } else {
                try {
                    LocalDate d = LocalDate.parse(item);
                    setText(d.format(dest));
                } catch (Exception e) {
                    setText(item);
                }
            }
        }
    });
    // ================================================

    ProductosView.getColumns().addAll(
            colId, colCliente, colTelefono, colFechaEnt, colFechaSol
    );

    listaPedidos = FXCollections.observableArrayList(new PedidosDao().obtenerPedidos());
    ProductosView.setItems(listaPedidos);
}



    public void actualizarTabla() {
        listaProductos.clear();
        listaProductos.addAll(new ProductosDao().obtenerProductos());
    }
    public void actualizarTablaP(){
        listaPedidos.clear();
        listaPedidos.addAll(new ProductosDao().obtenerProductos());
    }
   

    @FXML
    private void EditarBton() {
        Producto seleccionado = (Producto) ProductosView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un pedido", "Por favor, selecciona una fila antes de editar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/betterware/AccionEditarController.fxml"));
            Parent root = loader.load();
            
            EditarController controller = loader.getController();
            controller.setProducto(seleccionado); // 👈 Enviamos el producto seleccionado
            controller.setTablaPrincipalController(this); // Para poder refrescar después

            
            Stage newStage = new Stage();
            newStage.setTitle("Editar");
            newStage.setScene(new Scene(root));
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
private void editarPedidoSeleccionado() {
    Object item = ProductosView.getSelectionModel().getSelectedItem();

    if (item == null) {
        mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un pedido",
                "Por favor, selecciona una fila antes de editar.");
        return;
    }

    if (!(item instanceof Pedidos seleccionado)) {
        mostrarAlerta(Alert.AlertType.WARNING, "Modo incorrecto",
                "Estás en modo Productos. Cambia a modo Pedidos.");
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/betterware/accionEditarPedidoController.fxml"));
        Parent root = loader.load();

        EditarPedidoController controller = loader.getController();
        controller.setTablaPrincipalController(this);
        controller.setPedido(seleccionado);   // 👈 pasar el pedido seleccionado

        Stage newStage = new Stage();
        newStage.setTitle("Editar Pedido");
        newStage.setScene(new Scene(root));
        newStage.setResizable(true);
        newStage.centerOnScreen();
        newStage.show();

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    @FXML
    private void FiltroPedidos(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/betterware/accionFiltroPedidosController.fxml"));
            Parent root = loader.load();
     
            FiltroPedidosController controller = loader.getController();
            controller.setTablaPrincipalController(this); 
            
            Stage newStage = new Stage();
            newStage.setTitle("Filtro para Pedidos");
            newStage.setScene(new Scene(root));
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    @FXML
    private void FiltroProductos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/betterware/accionFiltroController.fxml"));
            Parent root = loader.load();
     
            FiltroController controller = loader.getController();
            controller.setTablaPrincipalController(this); // 👈 ahora sí pasas la referencia
            
            Stage newStage = new Stage();
            newStage.setTitle("Agregar");
            newStage.setScene(new Scene(root));
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

@FXML
private void BuscarBton() {
    if (txtBuscar == null) return; // por si acaso

    String filtro = txtBuscar.getText().trim().toLowerCase();

    // Si está vacío, recargamos la lista completa según el modo
    if (filtro.isEmpty()) {
        if (modoPedidos) {
            ProductosView.setItems(listaPedidos);
        } else {
            ProductosView.setItems(listaProductos);
        }
        return;
    }

    if (modoPedidos) {
        // ===== MODO PEDIDOS =====
        javafx.collections.transformation.FilteredList<Object> filtrados =
                new javafx.collections.transformation.FilteredList<>(listaPedidos, obj -> {
                    if (!(obj instanceof Pedidos)) return false;
                    Pedidos p = (Pedidos) obj;

                    String id      = String.valueOf(p.getIdPedido());
                    String cliente = p.getCliente()         == null ? "" : p.getCliente().toLowerCase();
                    String tel     = p.getTelefonoCliente() == null ? "" : p.getTelefonoCliente();
                    String fEnt    = p.getFechaEntrega()    == null ? "" : p.getFechaEntrega();
                    String fSol    = p.getFechaSolicitud()  == null ? "" : p.getFechaSolicitud();

                    return id.contains(filtro)
                            || cliente.contains(filtro)
                            || tel.contains(filtro)
                            || fEnt.contains(filtro)
                            || fSol.contains(filtro);
                });

        ProductosView.setItems(filtrados);

    } else {
        // ===== MODO PRODUCTOS =====
        javafx.collections.transformation.FilteredList<Object> filtrados =
                new javafx.collections.transformation.FilteredList<>(listaProductos, obj -> {
                    if (!(obj instanceof Producto)) return false;
                    Producto pr = (Producto) obj;

                    String id        = String.valueOf(pr.getId());
                    String codigo    = pr.getCodigo()      == null ? "" : pr.getCodigo().toLowerCase();
                    String nombre    = pr.getProducto()    == null ? "" : pr.getProducto().toLowerCase();
                    String categoria = pr.getCategoria()   == null ? "" : pr.getCategoria().toLowerCase();
                    String bolsa     = pr.getBolsa()       == null ? "" : pr.getBolsa().toLowerCase();
                    String desc      = pr.getDescripcion() == null ? "" : pr.getDescripcion().toLowerCase();

                    // puedes añadir más campos si quieres
                    return id.contains(filtro)
                            || codigo.contains(filtro)
                            || nombre.contains(filtro)
                            || categoria.contains(filtro)
                            || bolsa.contains(filtro)
                            || desc.contains(filtro);
                });

        ProductosView.setItems(filtrados);
    }
}


    @FXML
    private void accionAgregar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/betterware/AccionAgregarController.fxml"));
            Parent root = loader.load();
     
            AccionAgregarControllerController controller = loader.getController();
            controller.setTablaPrincipalController(this); // 👈 le pasas referencia
            
            Stage newStage = new Stage();
            newStage.setTitle("Agregar");
            newStage.setScene(new Scene(root));
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void accionPedido() { //metodo agregar pedidos para la sección pedidos, y no para productos
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/betterware/accionAgregarPedido.fxml"));
            Parent root = loader.load();
     
            PedidoController controller = loader.getController();
            controller.setTablaPrincipalController(this); 
            
            Stage newStage = new Stage();
            newStage.setTitle("Agregar Pedido");
            newStage.setScene(new Scene(root));
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void EliminarBton() {
        Producto seleccionado = (Producto) ProductosView.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
        mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un producto", "Por favor, selecciona una fila antes de eliminar.");
        return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar producto?");
        confirm.setContentText("Se eliminará el producto: " + seleccionado.getProducto());

        Optional<ButtonType> resultado = confirm.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            
            ProductosDao dao = new ProductosDao();
            boolean exito = dao.eliminarProducto(seleccionado.getId());

            if (exito) {
                listaProductos.remove(seleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "El producto fue eliminado correctamente.");
                actualizarTabla();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el producto.");
            }
        }
    }
    
    @FXML
private void EliminarBtonP() {
    Pedidos seleccionado = (Pedidos) ProductosView.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un pedido", "Por favor, selecciona una fila antes de eliminar.");
        return;
    }

    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Confirmar eliminación");
    confirm.setHeaderText("¿Eliminar pedido?");
    confirm.setContentText("Se eliminará el pedido ID: " + seleccionado.getIdPedido());

    Optional<ButtonType> resultado = confirm.showAndWait();
    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

        int idPedido = seleccionado.getIdPedido();

        // 1️⃣ Primero borrar detalles
        DetallePedidoDao dDao = new DetallePedidoDao();
        dDao.eliminarDetallesPorPedido(idPedido);

        // 2️⃣ Luego borrar el pedido
        PedidosDao pDao = new PedidosDao();
        boolean exito = pDao.eliminarPedido(idPedido);

        if (exito) {
            listaPedidos.remove(seleccionado);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "El pedido fue eliminado correctamente.");
            recargarPedidos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el pedido.");
        }
    }
}

    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    public void actualizarTablaFiltrada(List<Producto> productos) {
        if (ProductosView == null) {
            System.out.println("⚠️ ProductosView es null, no se puede actualizar la tabla.");
            return;
        }

        // Limpia la tabla actual
        ProductosView.getItems().clear();

        // Convierte la lista de productos (que es de tipo Producto) a ObservableList<Object>
        ObservableList<Object> data = FXCollections.observableArrayList(productos);

        // Asigna los nuevos datos a la tabla
        ProductosView.setItems(data);

        System.out.println("Tabla actualizada con " + productos.size() + " productos filtrados.");
    }
    
    public void recargarProductos() {
    listaProductos.clear();
    listaProductos.addAll(new ProductosDao().obtenerProductos());
    ProductosView.setItems(listaProductos);
    System.out.println("? Productos recargados correctamente.");
    }
    
    public void recargarPedidos(){
        listaPedidos.clear();
        listaPedidos.addAll(new PedidosDao().obtenerPedidos());
        ProductosView.setItems(listaPedidos);
        System.out.println("Productos recargados correctamente");
    }
    
    public void setOnProductoSeleccionado(Consumer<Producto> callback) {
        this.onProductoSeleccionado = callback;
    }
    
    public void actualizarTablaPedidosFiltrados(List<Pedidos> pedidos) {
    listaPedidos.clear();
    listaPedidos.addAll(pedidos);
    ProductosView.refresh();
}

}

