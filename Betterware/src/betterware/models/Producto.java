/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.models;

import java.math.BigDecimal;

/**
 *
 * @author Ainz Oal Gown
 */

public class Producto {
    private int id;
    private String codigo;                // varchar(100)
    private String producto;              // varchar(255)
    private String descripcion;           // text (nullable)
    private String especificaciones;      // text (nullable)
    private BigDecimal precio_compra;     // decimal(10,2)
    private BigDecimal precio_estandar;   // decimal(10,2)
    private BigDecimal precio_promocion;  // decimal(10,2)
    private String categoria;             // varchar(150)
    private int cantidad;                 // int(11) NOT NULL DEFAULT 0
    private String bolsa;                 // varchar(100)
    private boolean nuevo;                // tinyint(1)
    private String promocion;             // varchar(100)
    private String imagen;

    public Producto(int id, String codigo, String producto, String descripcion, String especificaciones,
                    BigDecimal precio_compra, BigDecimal precio_estandar, BigDecimal precio_promocion,
                    String categoria, int cantidad, String bolsa, boolean nuevo, String promocion, String imagen) {
        this.id = id;
        this.codigo = codigo;
        this.producto = producto;
        this.descripcion = descripcion;
        this.especificaciones = especificaciones;
        this.precio_compra = precio_compra;
        this.precio_estandar = precio_estandar;
        this.precio_promocion = precio_promocion;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.bolsa = bolsa;
        this.nuevo = nuevo;
        this.promocion = promocion;
        this.imagen = imagen;
    }
    
    public Producto() {
    // constructor vacío
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEspecificaciones() { return especificaciones; }
    public void setEspecificaciones(String especificaciones) { this.especificaciones = especificaciones; }

    public BigDecimal getPrecio_compra() { return precio_compra; }
    public void setPrecio_compra(BigDecimal precio_compra) { this.precio_compra = precio_compra; }

    public BigDecimal getPrecio_estandar() { return precio_estandar; }
    public void setPrecio_estandar(BigDecimal precio_estandar) { this.precio_estandar = precio_estandar; }

    public BigDecimal getPrecio_promocion() { return precio_promocion; }
    public void setPrecio_promocion(BigDecimal precio_promocion) { this.precio_promocion = precio_promocion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getBolsa() { return bolsa; }
    public void setBolsa(String bolsa) { this.bolsa = bolsa; }

    public boolean isNuevo() { return nuevo; }
    public void setNuevo(boolean nuevo) { this.nuevo = nuevo; }

    public String getPromocion() { return promocion; }
    public void setPromocion(String promocion) { this.promocion = promocion; }
    
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

}