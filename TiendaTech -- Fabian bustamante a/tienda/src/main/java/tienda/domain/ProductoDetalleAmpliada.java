package tienda.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la consulta ampliada de productos con información detallada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDetalleAmpliada implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idProducto;
    private String descripcion;
    private String detalle;
    private BigDecimal precio;
    private Integer existencias;
    private String rutaImagen;
    private boolean activo;
    private Integer idCategoria;
    private String nombreCategoria;
    private BigDecimal valorTotalInventario; // precio * existencias
    private String estadoStock; // "Disponible", "Stock Bajo", "Agotado"
    private Integer productosEnCategoria; // Total de productos en la misma categoría
    private BigDecimal precioPromedioCategoria; // Precio promedio de la categoría

}

