package tienda.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsernameAndActivoTrue(String username);
}

