/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.aux;

/**
 *
 * @author Ainz Oal Gown
 */
public class FiltroProducto {
    private double precioMax;
    private int cantidadMin;
    private Boolean estado;
    private String promocion;
    private String categoria;
    private String disponibilidad;
    private Integer cantidadMinRango;
    private Integer cantidadMaxRango;
    private String bolsa;

    public FiltroProducto(double precioMax, int cantidadMin, Boolean estado, String promocion, String categoria, String disponibilidad) {
        this.precioMax = precioMax;
        this.cantidadMin = cantidadMin;
        this.estado = estado;
        this.promocion = promocion;
         this.categoria = categoria;
         this.disponibilidad = disponibilidad;
    }

    public double getPrecioMax(){
        return precioMax; }
    public int getCantidadMin(){
        return cantidadMin; }
    public Boolean getEstado(){
        return estado; }
    public String getPromocion(){
        return promocion; }
    public String getCategoria(){
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getDisponibilidad(){
        return disponibilidad;
    } // 👈 nuevo getter
    
    public Integer getCantidadMinRango() { return cantidadMinRango; }
    public void setCantidadMinRango(Integer cantidadMinRango) { this.cantidadMinRango = cantidadMinRango; }

    public Integer getCantidadMaxRango() { return cantidadMaxRango; }
    public void setCantidadMaxRango(Integer cantidadMaxRango) { this.cantidadMaxRango = cantidadMaxRango; }
    public String getBolsa() { return bolsa; }
    public void setBolsa(String bolsa) { this.bolsa = bolsa; }
}
