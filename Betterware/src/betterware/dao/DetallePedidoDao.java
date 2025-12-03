package betterware.dao;

import betterware.models.DetallePedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDao {

    // =========================
    //  OBTENER DETALLES DE UN PEDIDO
    // =========================
    public List<DetallePedido> obtenerDetallesPorPedido(int idPedido) {
        List<DetallePedido> detalles = new ArrayList<>();

        String sql = """
                SELECT id_producto,
                       nombre_producto,
                       cantidad,
                       precio
                FROM detalle_pedido
                WHERE id_pedido = ?
                """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetallePedido d = new DetallePedido(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio")
                );
                detalles.add(d);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error cargando detalles: " + e.getMessage());
        }

        return detalles;
    }

    // =========================
    //  ELIMINAR DETALLES DE UN PEDIDO
    // =========================
    // betterware.dao.DetallePedidoDao
public boolean eliminarDetallesPorPedido(int idPedido) {
    String sql = "DELETE FROM detalle_pedido WHERE id_pedido = ?";

    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idPedido);
        stmt.executeUpdate();   // no importa cuántas filas elimine
        return true;

    } catch (SQLException e) {
        System.out.println("❌ Error al eliminar detalles del pedido: " + e.getMessage());
        return false;
    }
}


    // =========================
    //  INSERTAR UN DETALLE
    // =========================
    public void insertarDetalle(int idPedido, DetallePedido d) {
        String sql = """
                INSERT INTO detalle_pedido
                (id_pedido, id_producto, nombre_producto, cantidad, precio)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);
            stmt.setInt(2, d.getIdProducto());
            stmt.setString(3, d.getNombreProducto());
            stmt.setInt(4, d.getCantidad());
            stmt.setDouble(5, d.getPrecio());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error insertando detalle: " + e.getMessage());
        }
    }
}









