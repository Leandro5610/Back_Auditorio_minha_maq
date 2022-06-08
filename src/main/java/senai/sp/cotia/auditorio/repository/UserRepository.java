package senai.sp.cotia.auditorio.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.auth0.jwt.interfaces.Claim;

import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.model.Usuario;
import senai.sp.cotia.auditorio.type.Types;


public interface UserRepository extends PagingAndSortingRepository<Usuario, Long>{
	
	public Usuario findByNifAndSenha(String email, String senha);

	@Query("SELECT u FROM Usuario u WHERE u.type = 'comum'")
	public List<Usuario> findAllByCommuns();
	
	@Query("SELECT u FROM Usuario u WHERE u.type = 'administrador'")
    public List<Usuario> findAllByAdministrador();
	
	@Query("SELECT u FROM Usuario u WHERE u.nome LIKE %:p% OR u.nif LIKE %:p% "
			+ "OR u.email LIKE %:p%")
    public List<Usuario> procurarUsuario(@Param("p") String param);
	
	
	
}
