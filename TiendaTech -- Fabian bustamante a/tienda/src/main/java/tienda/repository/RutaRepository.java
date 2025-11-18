package tienda.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.domain.Ruta;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    List<Ruta> findAllByOrderByRequiereRolAsc();
}

