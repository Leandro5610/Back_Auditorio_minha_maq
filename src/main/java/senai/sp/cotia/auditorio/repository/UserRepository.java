package senai.sp.cotia.auditorio.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import senai.sp.cotia.auditorio.model.User;


public interface UserRepository extends PagingAndSortingRepository<User, Long>{
	
	public User findByNifAndSenha(String email, String senha);
}
