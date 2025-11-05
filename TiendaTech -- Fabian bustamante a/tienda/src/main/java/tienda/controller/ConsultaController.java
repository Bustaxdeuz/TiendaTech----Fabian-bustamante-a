package tienda.controller;

import tienda.services.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    
    private final ProductoService productoService;  

    public ConsultaController(ProductoService productoService) {
        this.productoService = productoService;
    }
    
    @GetMapping("/listado")
    public String listado(Model model) {
        var lista = productoService.getProductos(false);
        model.addAttribute("productos", lista);
        return "/consultas/listado";
    }
    
    @PostMapping("/consultaDerivada")
    public String consultaDerivada(@RequestParam() double precioInf,
            @RequestParam() double precioSup, Model model) {
        var lista = productoService.consultaDerivada(precioInf, precioSup);
        model.addAttribute("productos", lista);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    
    @PostMapping("/consultaJPQL")
    public String consultaJPQL(@RequestParam() double precioInf,
            @RequestParam() double precioSup, Model model) {
        var lista = productoService.consultaJPQL(precioInf, precioSup);
        model.addAttribute("productos", lista);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    
    @PostMapping("/consultaSQL")
    public String consultaSQL(@RequestParam() double precioInf,
            @RequestParam() double precioSup, Model model) {
        var lista = productoService.consultaSQL(precioInf, precioSup);
        model.addAttribute("productos", lista);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    
    @GetMapping("/ampliada/productos")
    public String consultaAmpliadaProductos(Model model) {
        var productosAmpliados = productoService.consultaAmpliadaProductos();
        model.addAttribute("productosAmpliados", productosAmpliados);
        
        // Calcular estadísticas generales
        int totalProductos = productosAmpliados.size();
        int productosActivos = (int) productosAmpliados.stream().filter(p -> p.isActivo()).count();
        int productosInactivos = totalProductos - productosActivos;
        int productosDisponibles = (int) productosAmpliados.stream()
            .filter(p -> "Disponible".equals(p.getEstadoStock())).count();
        int productosStockBajo = (int) productosAmpliados.stream()
            .filter(p -> "Stock Bajo".equals(p.getEstadoStock())).count();
        int productosAgotados = (int) productosAmpliados.stream()
            .filter(p -> "Agotado".equals(p.getEstadoStock())).count();
        
        java.math.BigDecimal valorTotalInventario = productosAmpliados.stream()
            .map(p -> p.getValorTotalInventario() != null ? p.getValorTotalInventario() : java.math.BigDecimal.ZERO)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("productosActivos", productosActivos);
        model.addAttribute("productosInactivos", productosInactivos);
        model.addAttribute("productosDisponibles", productosDisponibles);
        model.addAttribute("productosStockBajo", productosStockBajo);
        model.addAttribute("productosAgotados", productosAgotados);
        model.addAttribute("valorTotalInventario", valorTotalInventario);
        
        return "/consultas/ampliadaProductos";
    }
}