package tienda.controller;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tienda.domain.Categoria;
import tienda.service.CategoriaService;
import tienda.service.FirebaseStorageService;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private MessageSource messageSource;

    /* ==================== LISTADO DE CATEGORÍAS ==================== */
    @GetMapping("/listado")
    public String inicio(Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        return "/categoria/listado";
    }

    /* ==================== GUARDAR / ACTUALIZAR CATEGORÍA ==================== */
    @PostMapping("/guardar")
    public String guardar(Categoria categoria,
                          @RequestParam MultipartFile imagenFile,
                          RedirectAttributes redirectAttributes) {
        try {
            if (!imagenFile.isEmpty()) {
                categoriaService.save(categoria); // guardar primero para generar ID

                String rutaImagen = firebaseStorageService.cargaImagen(
                        imagenFile,
                        "categoria",
                        categoria.getIdCategoria());

                categoria.setRutaImagen(rutaImagen);
            }

            categoriaService.save(categoria);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
            );

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar la categoría");
        }

        return "redirect:/categoria/listado";
    }

    /* ==================== ELIMINAR CATEGORÍA ==================== */
    @PostMapping("/eliminar")
    public String eliminar(Categoria categoria, RedirectAttributes redirectAttributes) {
        categoria = categoriaService.getCategoria(categoria);

        if (categoria == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("categoria.error01", null, Locale.getDefault())
            );

        } else if (false) { // reservado para futuras validaciones
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("categoria.error02", null, Locale.getDefault())
            );

        } else if (categoriaService.delete(categoria)) {
            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    messageSource.getMessage("mensaje.eliminado", null, Locale.getDefault())
            );

            // 🔥 opcional: eliminar la imagen del bucket
            // firebaseStorageService.eliminarImagen(categoria.getRutaImagen());

        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("categoria.error03", null, Locale.getDefault())
            );
        }

        return "redirect:/categoria/listado";
    }

    /* ==================== MODIFICAR CATEGORÍA ==================== */
    @PostMapping("/modificar")
    public String modificar(Categoria categoria, Model model) {
        categoria = categoriaService.getCategoria(categoria);
        model.addAttribute("categoria", categoria);
        return "/categoria/modifica";
    }
}
