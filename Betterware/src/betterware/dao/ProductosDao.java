/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.dao;

import betterware.aux.FiltroProducto;
import betterware.models.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductosDao {

    public List<Producto> obtenerProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("producto"),
                    rs.getString("descripcion"),
                    rs.getString("especificaciones"),
                    rs.getBigDecimal("precio_compra"),
                    rs.getBigDecimal("precio_estandar"),
                    rs.getBigDecimal("precio_promocion"),
                    rs.getString("categoria"),
                    rs.getInt("cantidad"),
                    rs.getString("bolsa"),
                    rs.getBoolean("nuevo"),
                    rs.getString("promocion"),
                    rs.getString("imagen")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al cargar productos: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO productos (codigo, producto, descripcion, especificaciones, precio_compra, precio_estandar, precio_promocion, categoria, cantidad, bolsa, nuevo, promocion, imagen) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getProducto());
            stmt.setString(3, producto.getDescripcion());
            stmt.setString(4, producto.getEspecificaciones());
            stmt.setBigDecimal(5, producto.getPrecio_compra());
            stmt.setBigDecimal(6, producto.getPrecio_estandar());
            stmt.setBigDecimal(7, producto.getPrecio_promocion());
            stmt.setString(8, producto.getCategoria());
            stmt.setInt(9, producto.getCantidad());
            stmt.setString(10, producto.getBolsa());
            stmt.setBoolean(11, producto.isNuevo());
            stmt.setString(12, producto.getPromocion());
            stmt.setString(13, producto.getImagen());

            
            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET codigo = ?, producto = ?, descripcion = ?, especificaciones = ?, "
               + "precio_compra = ?, precio_estandar = ?, precio_promocion = ?, categoria = ?, cantidad = ?, "
               + "bolsa = ?, nuevo = ?, promocion = ?, imagen = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getProducto());
            stmt.setString(3, producto.getDescripcion());
            stmt.setString(4, producto.getEspecificaciones());
            stmt.setBigDecimal(5, producto.getPrecio_compra());
            stmt.setBigDecimal(6, producto.getPrecio_estandar());
            stmt.setBigDecimal(7, producto.getPrecio_promocion());
            stmt.setString(8, producto.getCategoria());
            stmt.setInt(9, producto.getCantidad());
            stmt.setString(10, producto.getBolsa());
            stmt.setBoolean(11, producto.isNuevo());
            stmt.setString(12, producto.getPromocion());
            stmt.setString(13, producto.getImagen()); 
            stmt.setInt(14, producto.getId());
            
            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }
    
   public List<Producto> buscarProductos(FiltroProducto filtro) {
    List<Producto> lista = new ArrayList<>();
    StringBuilder sql = new StringBuilder("SELECT * FROM productos WHERE 1=1");

    // --- Agregar filtros opcionales dinámicos ---
    if (filtro.getPrecioMax() > 0) {
        sql.append(" AND precio_estandar <= ?");
    }
    if (filtro.getCantidadMin() > 0) {
        sql.append(" AND cantidad >= ?");
    }
    if (filtro.getEstado() != null) {
        sql.append(" AND nuevo = ?");
    }
    if (filtro.getPromocion() != null && !filtro.getPromocion().trim().isEmpty()) {
        sql.append(" AND LOWER(promocion) = LOWER(?)");
    }
    if (filtro.getCategoria() != null && !filtro.getCategoria().trim().isEmpty()) {
        sql.append(" AND LOWER(categoria) = LOWER(?)");
    }
    // --- Filtro por rango de cantidad (disponibilidad)
    if (filtro.getCantidadMinRango() != null && filtro.getCantidadMaxRango() != null) {
        sql.append(" AND cantidad BETWEEN ? AND ?");
    } else if (filtro.getCantidadMinRango() != null) {
        sql.append(" AND cantidad >= ?");
    }
    if (filtro.getBolsa() != null && !filtro.getBolsa().trim().isEmpty()) {
        sql.append(" AND bolsa = ?");
    }
   

    System.out.println("🧩 SQL generada: " + sql);

    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

        int index = 1;

        // --- Asignar parámetros ---
        if (filtro.getPrecioMax() > 0) {
            stmt.setDouble(index++, filtro.getPrecioMax());
        }

        if (filtro.getCantidadMin() > 0) {
            stmt.setInt(index++, filtro.getCantidadMin());
        }

        if (filtro.getEstado() != null) {
            // Si el campo en BD es TINYINT(1), mejor usar setInt
            int estadoValor = filtro.getEstado() ? 1 : 0;
            stmt.setInt(index++, estadoValor);
            System.out.println("➡ Estado (nuevo) enviado al SQL: " + estadoValor);
        }

        if (filtro.getPromocion() != null && !filtro.getPromocion().trim().isEmpty()) {
            stmt.setString(index++, filtro.getPromocion());
        }

        if (filtro.getCategoria() != null && !filtro.getCategoria().trim().isEmpty()) {
            stmt.setString(index++, filtro.getCategoria());
        }
        if (filtro.getCantidadMinRango() != null && filtro.getCantidadMaxRango() != null) {
            stmt.setInt(index++, filtro.getCantidadMinRango());
            stmt.setInt(index++, filtro.getCantidadMaxRango());
        } else if (filtro.getCantidadMinRango() != null) {
            stmt.setInt(index++, filtro.getCantidadMinRango());
        }
        if (filtro.getBolsa() != null && !filtro.getBolsa().trim().isEmpty()) {
            stmt.setString(index++, filtro.getBolsa());
        }

        // --- Ejecutar y mapear resultados ---
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Producto p = new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("producto"),
                rs.getString("descripcion"),
                rs.getString("especificaciones"),
                rs.getBigDecimal("precio_compra"),
                rs.getBigDecimal("precio_estandar"),
                rs.getBigDecimal("precio_promocion"),
                rs.getString("categoria"),
                rs.getInt("cantidad"),
                rs.getString("bolsa"),
                rs.getBoolean("nuevo"),
                rs.getString("promocion"),
                rs.getString("imagen")
            );
            lista.add(p);
        }

        System.out.println("✅ Productos filtrados encontrados: " + lista.size());

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("❌ Error al buscar productos: " + e.getMessage());
    }

    return lista;
}

}
