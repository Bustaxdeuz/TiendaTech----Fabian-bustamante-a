package tienda.services;

import tienda.domain.Producto;
import tienda.domain.ProductoDetalleAmpliada;
import tienda.repository.ProductoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto);
        if (!imagenFile.isEmpty()) { //Si no está vacío... pasaron una imagen...            
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile, "producto",
                        producto.getIdProducto());
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
            } catch (IOException e) {

            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {
        // Verifica si el producto existe antes de intentar eliminarlo
        if (!productoRepository.existsById(idProducto)) {
            // Lanza una excepción para indicar que el usuario no fue encontrado
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
        }
        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepción para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar el producto. Tiene datos asociados.", e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Producto> consultaDerivada(double precioInf, double precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaJPQL(double precioInf, double precioSup) {
        return productoRepository.consultaJPQL(precioInf, precioSup);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultaSQL(double precioInf, double precioSup) {
        return productoRepository.consultaSQL(precioInf, precioSup);
    }
    
    @Transactional(readOnly = true)
    public List<ProductoDetalleAmpliada> consultaAmpliadaProductos() {
        List<Object[]> resultados = productoRepository.consultaAmpliadaProductos();
        List<ProductoDetalleAmpliada> productosAmpliados = new ArrayList<>();
        
        for (Object[] fila : resultados) {
            ProductoDetalleAmpliada producto = new ProductoDetalleAmpliada();
            
            producto.setIdProducto(fila[0] != null ? ((Number) fila[0]).intValue() : null);
            producto.setDescripcion(fila[1] != null ? (String) fila[1] : "");
            producto.setDetalle(fila[2] != null ? (String) fila[2] : null);
            producto.setPrecio(fila[3] != null ? (BigDecimal) fila[3] : BigDecimal.ZERO);
            producto.setExistencias(fila[4] != null ? ((Number) fila[4]).intValue() : 0);
            producto.setRutaImagen(fila[5] != null ? (String) fila[5] : null);
            producto.setActivo(fila[6] != null ? (Boolean) fila[6] : false);
            producto.setIdCategoria(fila[7] != null ? ((Number) fila[7]).intValue() : 0);
            producto.setNombreCategoria(fila[8] != null ? (String) fila[8] : "Sin categoría");
            producto.setValorTotalInventario(fila[9] != null ? (BigDecimal) fila[9] : BigDecimal.ZERO);
            producto.setEstadoStock(fila[10] != null ? (String) fila[10] : "Desconocido");
            producto.setProductosEnCategoria(fila[11] != null ? ((Number) fila[11]).intValue() : 0);
            producto.setPrecioPromedioCategoria(fila[12] != null ? (BigDecimal) fila[12] : BigDecimal.ZERO);
            
            productosAmpliados.add(producto);
        }
        
        return productosAmpliados;
    }
}