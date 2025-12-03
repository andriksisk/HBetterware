/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.controllers;

import betterware.aux.FiltroProducto;
import betterware.dao.ProductosDao;
import betterware.models.Producto;
import betterware.controllers.TablaPrincipalController;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 *
 * @author Ainz Oal Gown
 */
public class FiltroController implements Initializable {
    private TablaPrincipalController tablaPrincipalController;

    @FXML
    private Button CancelarBoton;
    @FXML
    private Slider SliderPrecio;
    @FXML
    private Label lblValor;
    @FXML
    private Spinner<String> SpinnerBolsa;
    
    @FXML
    private ToggleGroup groupEstado;
    @FXML
    private ToggleGroup groupDisp;
    @FXML
    private ToggleGroup groupPromocion;
    @FXML
    private ToggleGroup groupCat;
    @FXML
    private RadioButton rbtnNuevo;
    @FXML
    private RadioButton rbtnUsado;
    @FXML
    private RadioMenuItem DispAlta;
    @FXML
    private RadioMenuItem DispMedia;
    @FXML
    private RadioMenuItem DispBaja;
    @FXML 
    private RadioMenuItem DispNada;
    @FXML
    private Button buscarBton;
    
    @FXML
    private TableView<Producto> ProductosView;
    private ObservableList<Producto> listaProductos;
    
    @FXML 
    private void CancelarBton() {
        Stage stage = (Stage) CancelarBoton.getScene().getWindow();
        stage.close();
    }
    
    public void setTablaPrincipalController(TablaPrincipalController controller) {
        this.tablaPrincipalController = controller;
    }   
    
    @FXML
    private void guardarBuscarBton() {
        try {

            double precioMax = SliderPrecio.getValue();
            String valorSpinner = SpinnerBolsa.getValue();

            int cantidadMin = 1;

            // --- Estado, categoría, promoción, disponibilidad ---
            Boolean estado = null;
            if (groupEstado != null && groupEstado.getSelectedToggle() != null) {
                RadioButton seleccionado = (RadioButton) groupEstado.getSelectedToggle();
                estado = seleccionado.getText().equalsIgnoreCase("Si");
            }

            String categoria = null;
            if (groupCat != null && groupCat.getSelectedToggle() != null) {
                categoria = ((RadioMenuItem) groupCat.getSelectedToggle()).getText();
            }

            String promocion = null;
            if (groupPromocion != null && groupPromocion.getSelectedToggle() != null) {
                promocion = ((RadioMenuItem) groupPromocion.getSelectedToggle()).getText();
            }

            // Disponibilidad
            Integer minRango = null;
            Integer maxRango = null;
            String disponibilidadSeleccionada = null;

            if (groupDisp != null && groupDisp.getSelectedToggle() != null) {
                RadioMenuItem seleccionado = (RadioMenuItem) groupDisp.getSelectedToggle();
                disponibilidadSeleccionada = seleccionado.getText();

                switch (disponibilidadSeleccionada) {
                    case "Alta": minRango = 20; break;
                    case "Media": minRango = 10; maxRango = 19; break;
                    case "Baja": minRango = 1; maxRango = 9; break;
                    case "Sin stock":
                    case "Nada": minRango = 0; maxRango = 0; break;
                }
            }

            // 🔹 Crear filtro *ANTES* de asignar la bolsa
            FiltroProducto filtro = new FiltroProducto(
                precioMax, 
                cantidadMin, 
                estado, 
                promocion,
                categoria,
                disponibilidadSeleccionada
            );

            // --- Bolsa ---
            if (valorSpinner.equals("Todas")) {
                filtro.setBolsa(null);
            } else {
                filtro.setBolsa(valorSpinner);
            }

            filtro.setCantidadMinRango(minRango);
            filtro.setCantidadMaxRango(maxRango);

            // Buscar con el DAO
            ProductosDao dao = new ProductosDao();
            List<Producto> resultados = dao.buscarProductos(filtro);

            if (tablaPrincipalController != null) {
                tablaPrincipalController.actualizarTablaFiltrada(resultados);
            }

            // cerrar ventana
            Stage stage = (Stage) buscarBton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error al aplicar filtros: " + e.getMessage());
        }
    }
    
    private void validarSpinnerBolsa() {
        String texto = SpinnerBolsa.getEditor().getText().trim().toUpperCase();

        if (texto.equals("TODAS")) {
            SpinnerBolsa.getValueFactory().setValue("Todas");
            return;
        }

        if (texto.matches("[1-9]|10")) {
            SpinnerBolsa.getValueFactory().setValue(texto);
            return;
        }

        if (texto.matches("[A-H]")) {
            SpinnerBolsa.getValueFactory().setValue(texto);
            return;
        }

        // Si es inválido → vuelve a "Todas"
        SpinnerBolsa.getValueFactory().setValue("Todas");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 🔹 1. Configurar el slider
        SliderPrecio.valueProperty().addListener((obs, oldValue, newValue) -> {
            lblValor.setText(String.format("%.0f", newValue));
        });

        // 🔹 2. Configurar el Spinner personalizado
        SpinnerValueFactory<String> customValueFactory = new SpinnerValueFactory<String>() {
            private boolean isNumericMode = true; // modo inicial

            {
                setValue("Todas"); // valor inicial aaaaaaaaaaaaaaaaaaaaaaaaaaaa
            }

            @Override
            public void decrement(int steps) {
                setValue(getPreviousValue(getValue(), steps));
            }

            @Override
            public void increment(int steps) {
                setValue(getNextValue(getValue(), steps));
            }

             private String getNextValue(String current, int steps) {

                // --- Caso especial: "Todas" ---
                if (current.equals("Todas")) {
                    return "1";
                }

                // --- Si es número ---
                if (current.matches("\\d+")) {
                    int num = Integer.parseInt(current);
                    if (num < 10) {
                        return String.valueOf(num + 1);
                    }
                    // Si ya está en 10 → sigue A
                    return "A";
                }

                // --- Si es letra A-H ---
                if (current.matches("[A-H]")) {
                    char c = current.charAt(0);
                    if (c < 'H') {
                        return String.valueOf((char)(c + 1));
                    }
                    // Si está en H → se queda en H
                    return "H";
                }

                return "Todas";
            }

            private String getPreviousValue(String current, int steps) {

                // --- Si está en "Todas" ---
                if (current.equals("Todas")) {
                    return "H";  // Antes de 'Todas' está H
                }

                // --- Si es número ---
                if (current.matches("\\d+")) {
                    int num = Integer.parseInt(current);
                    if (num > 1) {
                        return String.valueOf(num - 1);
                    }
                    // Si está en 1 → antes está "Todas"
                    return "Todas";
                }

                // --- Si es letra A-H ---
                if (current.matches("[A-H]")) {
                    char c = current.charAt(0);

                    if (c > 'A') {
                        return String.valueOf((char)(c - 1));
                    }

                    // Si está en A → sigue 10
                    return "10";
                }

                return "Todas";
            }
        };
       

        SpinnerBolsa.setValueFactory(customValueFactory);
        SpinnerBolsa.setEditable(true);

        // 🔹 3. Enlazar validación al editor del Spinner
        SpinnerBolsa.getEditor().setOnAction(e -> validarSpinnerBolsa());
        SpinnerBolsa.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validarSpinnerBolsa();
        });

        // 🔹 4. Configurar los grupos de botones
        rbtnNuevo.setToggleGroup(groupEstado);
        rbtnUsado.setToggleGroup(groupEstado);

        DispAlta.setToggleGroup(groupDisp);
        DispMedia.setToggleGroup(groupDisp);
        DispBaja.setToggleGroup(groupDisp);
        DispNada.setToggleGroup(groupDisp);

        // 🔹 5. Mensaje de prueba (opcional)
        if (groupEstado == null) {
            System.out.println("❌ groupEstado no se cargó desde el FXML");
        } else {
            System.out.println("✅ groupEstado cargado con toggles: " + groupEstado.getToggles().size());
        }
    }
       
}
