/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.models;

/**
 * Modelo para la tabla `detalle_pedido`
 * Campos en BD:
 *  - id_detalle (INT, PK, AUTO_INCREMENT) [no lo usamos de momento]
 *  - id_pedido (INT, FK)
 *  - id_producto (INT)
 *  - nombre_producto (VARCHAR)
 *  - cantidad (INT)
 *  - precio (DECIMAL)   <-- precio total de esa línea
 */
public class DetallePedido {

    // si quieres, puedes agregar también id_detalle
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precio;

    public DetallePedido(int idProducto, String nombreProducto, int cantidad, double precio) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }
}





