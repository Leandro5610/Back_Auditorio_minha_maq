package senai.sp.cotia.auditorio.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import senai.sp.cotia.auditorio.model.Usuario;


public interface UserRepository extends PagingAndSortingRepository<Usuario, Long>{
	
	public Usuario findByNifAndSenha(String email, String senha);
}
