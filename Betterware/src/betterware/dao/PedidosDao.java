/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package betterware.dao;

import betterware.models.Pedidos;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidosDao {

    // =========================
    //  OBTENER TODOS LOS PEDIDOS
    // =========================
    public List<Pedidos> obtenerPedidos() {
        List<Pedidos> lista = new ArrayList<>();

        String sql = """
                SELECT id_pedido,
                       cliente,
                       telefono_cliente,
                       fecha_entrega,
                       fecha_solicitud
                FROM pedidos
                """;

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pedidos p = new Pedidos();
                p.setIdPedido(rs.getInt("id_pedido"));
                p.setCliente(rs.getString("cliente"));
                p.setTelefonoCliente(rs.getString("telefono_cliente"));
                p.setFechaEntrega(rs.getString("fecha_entrega"));
                p.setFechaSolicitud(rs.getString("fecha_solicitud"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al cargar pedidos: " + e.getMessage());
        }

        return lista;
    }

    // =========================
    //  INSERTAR PEDIDO NUEVO
    // =========================
    public int insertarPedido(Pedidos p) {
        String sql = """
                INSERT INTO pedidos (cliente, telefono_cliente, fecha_entrega, fecha_solicitud)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getCliente());
            stmt.setString(2, p.getTelefonoCliente());
            stmt.setString(3, p.getFechaEntrega());
            stmt.setString(4, p.getFechaSolicitud());

            int filas = stmt.executeUpdate();
            if (filas == 0) return -1;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // id_pedido generado
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar pedido: " + e.getMessage());
        }

        return -1;
    }

    // =========================
    //  ACTUALIZAR PEDIDO EXISTENTE
    // =========================
    public boolean actualizarPedido(Pedidos p) {
        String sql = """
                UPDATE pedidos
                SET cliente = ?,
                    telefono_cliente = ?,
                    fecha_entrega = ?,
                    fecha_solicitud = ?
                WHERE id_pedido = ?
                """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setString(1, p.getCliente());
            stmt.setString(2, p.getTelefonoCliente());
            stmt.setString(3, p.getFechaEntrega());
            stmt.setString(4, p.getFechaSolicitud());
            stmt.setInt(5, p.getId());   // <-- aquí ya existe getId()

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar pedido: " + e.getMessage());
            return false;
        }
    }

    // =========================
    //  ELIMINAR PEDIDO
    // =========================
    public boolean eliminarPedido(int id) {
        String sql = "DELETE FROM pedidos WHERE id_pedido = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar pedido: " + e.getMessage());
            return false;
        }
    }
    
public List<Pedidos> buscarPedidosPorFecha(LocalDate fecha) {
    List<Pedidos> lista = new ArrayList<>();

    String sql = """
        SELECT id_pedido, cliente, telefono_cliente, fecha_entrega, fecha_solicitud
        FROM pedidos
        WHERE fecha_entrega = ? OR fecha_solicitud = ?
    """;

    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, fecha.toString());
        stmt.setString(2, fecha.toString());

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Pedidos p = new Pedidos();
            p.setId(rs.getInt("id_pedido"));
            p.setCliente(rs.getString("cliente"));
            p.setTelefonoCliente(rs.getString("telefono_cliente"));
            p.setFechaEntrega(rs.getString("fecha_entrega"));
            p.setFechaSolicitud(rs.getString("fecha_solicitud"));

            lista.add(p);
        }

    } catch (Exception e) {
        System.out.println("❌ Error buscando pedidos por fecha: " + e.getMessage());
    }

    return lista;
}

}






