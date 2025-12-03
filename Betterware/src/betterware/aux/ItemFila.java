/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.aux;

import betterware.models.Producto;
import java.math.BigDecimal;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Ainz Oal Gown
 */
public class ItemFila {
    private Producto producto; // referencia completa al modelo

    // propiedades para JavaFX TableView (observables)
    private IntegerProperty id = new SimpleIntegerProperty(0);
    private StringProperty nombre = new SimpleStringProperty("");
    private IntegerProperty cantidad = new SimpleIntegerProperty(0);
    private ObjectProperty<BigDecimal> precioTotal = new SimpleObjectProperty<>(BigDecimal.ZERO);

    // precio unitario (no observable — usado internamente)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    // límite de stock (para el spinner)
    private int maxCantidad = 0;

    public ItemFila() {}

    public ItemFila(Producto producto, int cantidadInicial) {
        setProducto(producto);
        setCantidad(cantidadInicial);
    }

    // --- getters / setters ---
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            id.set(producto.getId());
            nombre.set(producto.getProducto());
            precioUnitario = producto.getPrecio_estandar() == null ? BigDecimal.ZERO : producto.getPrecio_estandar();
            maxCantidad = producto.getCantidad();
            // definir cantidad por defecto (si aún no hay)
            if (getCantidad() <= 0) setCantidad(1);
            recalcularTotalSegunPromocion(); // inicializa precioTotal
        } else {
            id.set(0);
            nombre.set("");
            precioUnitario = BigDecimal.ZERO;
            maxCantidad = 0;
            precioTotal.set(BigDecimal.ZERO);
        }
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getNombre() { return nombre.get(); }
    public StringProperty nombreProperty() { return nombre; }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int value) { cantidad.set(value); }
    public IntegerProperty cantidadProperty() { return cantidad; }

    public BigDecimal getPrecioTotal() { return precioTotal.get(); }
    public ObjectProperty<BigDecimal> precioTotalProperty() { return precioTotal; }
    public void setPrecioTotal(BigDecimal v) { precioTotal.set(v); }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal p) { this.precioUnitario = p; }

    public int getMaxCantidad() { return maxCantidad; }
    public void setMaxCantidad(int max) { this.maxCantidad = max; }

    // recalcula el precioTotal aplicando la promoción (por defecto unitario * cantidad)
    public void recalcularTotalSegunPromocion() {
        if (producto == null) { precioTotal.set(BigDecimal.ZERO); return; }
        // Por defecto, sin promociones
        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(getCantidad()));
        precioTotal.set(total);
    }
}

