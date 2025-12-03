    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package betterware.controllers;

import betterware.dao.ConexionDB;
import betterware.dao.ProductosDao;
import betterware.models.Producto;
import java.awt.Checkbox;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author manac
 */
public class AccionAgregarControllerController implements Initializable {
    @FXML
    private Button CancelarBoton;
    
    private TablaPrincipalController tablaPrincipalController;
    
    @FXML
    private ToggleGroup groupCat;
    @FXML
    private RadioMenuItem PromEstandar;
    @FXML
    private RadioMenuItem PromCasitaPlus;
    @FXML
    private RadioMenuItem PromPrecioRojo;
    @FXML
    private RadioMenuItem PromMega;
    @FXML
    private RadioMenuItem PromHiper;
    @FXML
    private RadioMenuItem PromBetterLow;
    @FXML
    private RadioMenuItem NoProm;
    @FXML 
    private RadioMenuItem PromSegundo;
    @FXML
    private RadioMenuItem PromCasita;
    @FXML
    private ToggleGroup groupPromocion;
    @FXML
    private TextField TxtNombreProducto;
    @FXML
    private TextArea TxtDescripcion;
    @FXML
    private TextField TxtCod;
    @FXML
    private TextField TxtBolsa;
    
    @FXML
    private TextField TxtEstandar;
    @FXML
    private TextField TxtCantidad;
    @FXML
    private TextArea TxtEspcs;
    
    @FXML
    private CheckBox editNuevo;
    
    @FXML
    private CheckBox checkPermitir;
    @FXML
    private MenuButton TipoPromos;
    @FXML
    private TextField PrecioPromo;
    
    
    @FXML
    private ImageView imgPreview;
    @FXML
    private Button BtonCargarImg;
    private File archivoImagen;
    private String imagen;
    
    
    //llamar a los botones con @FXML y el fx:id    este se puede poner en el scene builder si es que aún
    //no tiene en el .fxml
    
    public void setTablaPrincipalController(TablaPrincipalController controller) {
        this.tablaPrincipalController = controller;
    }
    
    
    @FXML 
    private void CancelarBton() {
        Stage stage = (Stage) CancelarBoton.getScene().getWindow();
        stage.close();
    }
    
    //sin fx:id ni OnAction por parte del botón
    //Falta el de Lista De Promociones por configurar xdxdxdxdx
    @FXML
    private void GuardarBton() {
        try {
            // 1️⃣ Leer los valores de los campos
            BigDecimal precioPromocion = parseDecimal(PrecioPromo.getText());
            Boolean nuevo1 = editNuevo.isSelected();
            String especificaciones = TxtEspcs.getText();
            String codigo = TxtCod.getText();
            String nombre = TxtNombreProducto.getText();
            String descripcion = TxtDescripcion.getText();
            String bolsa = TxtBolsa.getText();
            int cantidad = Integer.parseInt(TxtCantidad.getText().isEmpty() ? "0" : TxtCantidad.getText());
            BigDecimal precioEstandar = new BigDecimal(TxtEstandar.getText().isEmpty() ? "0" : TxtEstandar.getText());

            // 2️⃣ Determinar promoción seleccionada
            String promocion = null;
            RadioMenuItem seleccionado = (RadioMenuItem) groupPromocion.getSelectedToggle();
            if (seleccionado != null) {
                promocion = seleccionado.getText();
            }
            
            String categoria = null;
            RadioMenuItem categoriaSeleccionada = (RadioMenuItem) groupCat.getSelectedToggle();
            if (categoriaSeleccionada != null) {
                categoria = categoriaSeleccionada.getText();
            }

            // 3️⃣ Crear el producto
            System.out.println("Valor de PrecioPromo: " + PrecioPromo.getText());
            Producto nuevo = new Producto(0, codigo, nombre, descripcion, especificaciones,null,precioEstandar, precioPromocion, categoria,
                    cantidad, bolsa, nuevo1, promocion, imagen);

            // 4️⃣ Guardar en la BD
            ProductosDao dao = new ProductosDao();
            boolean exito = dao.insertarProducto(nuevo);

            if (exito && tablaPrincipalController != null) {
                // Cerrar la ventana si se guardó correctamente
                tablaPrincipalController.actualizarTabla();
                Stage stage = (Stage) CancelarBoton.getScene().getWindow();
                stage.close();
                System.out.println("✅ Producto agregado correctamente.");
            } else {
                System.out.println("⚠️ No se pudo agregar el producto.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error al guardar: " + e.getMessage());
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

            // Guardar la ruta en formato URI (válida para ImageView)
            imagen = file.toURI().toString();

            // Mostrar en el ImageView
            imgPreview.setImage(new Image(imagen));

            System.out.println("Ruta guardada: " + imagen);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PromEstandar.setToggleGroup(groupPromocion);
        PromSegundo.setToggleGroup(groupPromocion);
        PromCasitaPlus.setToggleGroup(groupPromocion);
        PromHiper.setToggleGroup(groupPromocion);
        PromBetterLow.setToggleGroup(groupPromocion);
        NoProm.setToggleGroup(groupPromocion);
        PromCasita.setToggleGroup(groupPromocion);
        PromPrecioRojo.setToggleGroup(groupPromocion);
        PromMega.setToggleGroup(groupPromocion);
               
        PrecioPromo.disableProperty().bind(checkPermitir.selectedProperty().not());
        TipoPromos.disableProperty().bind(checkPermitir.selectedProperty().not());
    }    
    private BigDecimal parseDecimal(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO; // o null si tu campo permite
            try {
                return new BigDecimal(text.trim());
            } catch (NumberFormatException e) {
            return BigDecimal.ZERO; // o lanzar alerta
        }
    }
}
