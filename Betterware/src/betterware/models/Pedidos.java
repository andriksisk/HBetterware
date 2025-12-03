/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.models;

public class Pedidos {

    // Campos que realmente tienes en la tabla `pedidos`
    private int idPedido;
    private String cliente;
    private String telefonoCliente;
    private String fechaEntrega;
    private String fechaSolicitud;

    public Pedidos() {
    }

    public Pedidos(int idPedido, String cliente, String telefonoCliente,
                   String fechaEntrega, String fechaSolicitud) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.telefonoCliente = telefonoCliente;
        this.fechaEntrega = fechaEntrega;
        this.fechaSolicitud = fechaSolicitud;
    }

    // ===== nuevos getters / setters =====
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    // ===== compatibilidad con código viejo (si en algún lado usas getId/setId) =====
    public int getId() {
        return idPedido;
    }

    public void setId(int id) {
        this.idPedido = id;
    }
}






