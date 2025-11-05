package tienda.repository;

import tienda.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    public List<Producto> findByActivoTrue();
    
    //Ejemplo de método utilizando consultas derivadas
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(double precioInf, double precioSup);

    //Ejemplo de método utilizando consultas JPQL
    @Query(value = "SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(@Param("precioInf") double precioInf, @Param("precioSup") double precioSup);

    //Ejemplo de método utilizando consultas SQL nativas
    @Query(nativeQuery = true,
            value = "SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaSQL(@Param("precioInf") double precioInf, @Param("precioSup") double precioSup);

    // Consulta ampliada de productos con estadísticas detalladas
    @Query(nativeQuery = true, value = 
        "SELECT " +
        "    p.id_producto AS idProducto, " +
        "    p.descripcion AS descripcion, " +
        "    p.detalle AS detalle, " +
        "    p.precio AS precio, " +
        "    p.existencias AS existencias, " +
        "    p.ruta_imagen AS rutaImagen, " +
        "    p.activo AS activo, " +
        "    COALESCE(p.id_categoria, 0) AS idCategoria, " +
        "    COALESCE(c.descripcion, 'Sin categoría') AS nombreCategoria, " +
        "    (p.precio * p.existencias) AS valorTotalInventario, " +
        "    CASE " +
        "        WHEN p.existencias = 0 THEN 'Agotado' " +
        "        WHEN p.existencias < 10 THEN 'Stock Bajo' " +
        "        ELSE 'Disponible' " +
        "    END AS estadoStock, " +
        "    COALESCE((SELECT COUNT(*) FROM producto p2 WHERE (p.id_categoria IS NULL AND p2.id_categoria IS NULL) OR p2.id_categoria = p.id_categoria), 0) AS productosEnCategoria, " +
        "    COALESCE((SELECT AVG(p3.precio) FROM producto p3 WHERE (p.id_categoria IS NULL AND p3.id_categoria IS NULL) OR p3.id_categoria = p.id_categoria), 0) AS precioPromedioCategoria " +
        "FROM producto p " +
        "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
        "ORDER BY p.descripcion ASC")
    List<Object[]> consultaAmpliadaProductos();

}

