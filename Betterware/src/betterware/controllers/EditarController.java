/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.controllers;

import betterware.dao.ProductosDao;
import betterware.models.Producto;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Ainz Oal Gown
 */
public class EditarController implements Initializable {
    
    private Producto producto;
    private TablaPrincipalController tablaPrincipalController;
    
    @FXML
    private ToggleGroup groupPromocion;
    @FXML
    private Button GuardarEdit;
    @FXML
    private Button CancelarBoton;
    @FXML 
    private ToggleGroup groupCat;
    @FXML
    private RadioMenuItem Practimuebles;
    @FXML
    private RadioMenuItem Hogar;
    @FXML
    private RadioMenuItem Recamara;
    @FXML
    private RadioMenuItem Cocina;
    @FXML
    private RadioMenuItem Gurmy;
    @FXML
    private RadioMenuItem Baño;
    @FXML
    private RadioMenuItem HigieneLimp;
    @FXML
    private RadioMenuItem Bienestar;
    @FXML
    private RadioMenuItem Contigo;
    @FXML
    private RadioMenuItem Verano;
    @FXML
    private RadioMenuItem DiaMuertos;
    @FXML
    private RadioMenuItem Navidad;
    
    @FXML
    private TextField ProductoNombre;
    @FXML 
    private TextField EditCodigo;
    @FXML
    private TextField editBolsa;
    @FXML 
    private TextField EditCantidad;
    @FXML
    private TextField editPP;
    @FXML
    private TextField editPE;
    
    @FXML
    private TextArea editEspcs;
    @FXML
    private TextArea editDesc;
    @FXML
    private CheckBox editNuevo;
    @FXML
    private CheckBox checkPermitir;
    @FXML
    private MenuButton TipoPromos;
    @FXML
    private ImageView imgPreview;

    @FXML
    private Button btnCargarImg;
    private File archivoImagen;
    private String imagen; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Practimuebles.setToggleGroup(groupCat);
        Hogar.setToggleGroup(groupCat);
        Recamara.setToggleGroup(groupCat);
        Cocina.setToggleGroup(groupCat);
        Gurmy.setToggleGroup(groupCat);
        Baño.setToggleGroup(groupCat);        
        HigieneLimp.setToggleGroup(groupCat);
        Bienestar.setToggleGroup(groupCat);
        Contigo.setToggleGroup(groupCat);
        Verano.setToggleGroup(groupCat);
        DiaMuertos.setToggleGroup(groupCat);
        Navidad.setToggleGroup(groupCat);
        
        editPP.disableProperty().bind(checkPermitir.selectedProperty().not());
        TipoPromos.disableProperty().bind(checkPermitir.selectedProperty().not());
    }    
     public void setProducto(Producto producto) {
        this.producto = producto;
        cargarDatosEnFormulario();
    }

    public void setTablaPrincipalController(TablaPrincipalController controller) {
        this.tablaPrincipalController = controller;
    }
    
    private void cargarDatosEnFormulario() {
    if (producto == null) return;

    ProductoNombre.setText(producto.getProducto());
    EditCodigo.setText(producto.getCodigo());
    editBolsa.setText(producto.getBolsa());
    editPE.setText(String.valueOf(producto.getPrecio_estandar()));
   
    editPP.setText(String.valueOf(producto.getPrecio_promocion()));
    EditCantidad.setText(String.valueOf(producto.getCantidad()));
    editDesc.setText(producto.getDescripcion());
    editEspcs.setText(producto.getEspecificaciones());
    editNuevo.setSelected(producto.isNuevo());

    // Seleccionar la categoría correcta
    for (Toggle toggle : groupCat.getToggles()) {
        RadioMenuItem item = (RadioMenuItem) toggle;
        if (item.getText().equalsIgnoreCase(producto.getCategoria())) {
            groupCat.selectToggle(item);
            break;
        }
    }
   
    //ver si hay imgs
    // Mostrar imagen si existe
    if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
        try {
            Image img = new Image(producto.getImagen());
            imgPreview.setImage(img);
            imagen = producto.getImagen(); // Guardar la ruta actual
        } catch (Exception e) {
            System.out.println("Error cargando imagen: " + e.getMessage());
        }
    }
    
    }
    
    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            String ruta = URLDecoder.decode(producto.getImagen(), StandardCharsets.UTF_8);
            Image img = new Image(ruta);
            imgPreview.setImage(img);
        }
    }
    
    @FXML
    private void cargarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            archivoImagen = file;

            // Guardar la ruta en formato URI 
            imagen = file.toURI().toString();

            // Mostrar en el ImageView
            imgPreview.setImage(new Image(imagen));

            System.out.println("Ruta guardada: " + imagen);
        }
    }
    
    @FXML 
    private void CancelarBton() {
        Stage stage = (Stage) CancelarBoton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void guardarCambios() {
        try {
        // Actualizar el objeto producto con los nuevos valores
            producto.setProducto(ProductoNombre.getText());
            producto.setCodigo(EditCodigo.getText());
            producto.setBolsa(editBolsa.getText());
            producto.setPrecio_estandar(parseDecimal(editPE.getText()));
            producto.setImagen(imagen);
        
            producto.setPrecio_promocion(parseDecimal(editPP.getText()));
            producto.setCantidad(parseInt(EditCantidad.getText()));
            producto.setDescripcion(editDesc.getText());
            producto.setEspecificaciones(editEspcs.getText());
            producto.setNuevo(editNuevo.isSelected());

            RadioMenuItem categoriaSeleccionada = (RadioMenuItem) groupCat.getSelectedToggle();
            if (categoriaSeleccionada != null) {
                producto.setCategoria(categoriaSeleccionada.getText());
            }
            
            RadioMenuItem promoSeleccionada = (RadioMenuItem) groupPromocion.getSelectedToggle();
            if (promoSeleccionada != null) {
                producto.setPromocion(promoSeleccionada.getText());
            }

        // Guardar en la BD
            ProductosDao dao = new ProductosDao();
            boolean exito = dao.actualizarProducto(producto);
            
            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Producto actualizado correctamente.");
                if (tablaPrincipalController != null) {
                    tablaPrincipalController.actualizarTabla(); // refresca la tabla principal
                }

            // Cerrar ventana
                Stage stage = (Stage) ProductoNombre.getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el producto.");
            }

            } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al guardar los cambios: " + e.getMessage());
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    
    private BigDecimal parseDecimal(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO; // o null si tu campo permite
            try {
                return new BigDecimal(text.trim());
            } catch (NumberFormatException e) {
            return BigDecimal.ZERO; // o lanzar alerta
        }
    }

    private Integer parseInt(String text) {
        if (text == null || text.trim().isEmpty()) return 0; // o null
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0; // o lanzar alerta
        }
    }
}
