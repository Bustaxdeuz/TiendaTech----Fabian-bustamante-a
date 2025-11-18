package tienda.services;

import java.util.List;
import org.springframework.stereotype.Service;
import tienda.domain.Ruta;
import tienda.repository.RutaRepository;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    public List<Ruta> getRutas() {
        return rutaRepository.findAllByOrderByRequiereRolAsc();
    }
}

