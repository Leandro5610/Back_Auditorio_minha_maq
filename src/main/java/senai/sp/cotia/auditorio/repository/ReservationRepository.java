package senai.sp.cotia.auditorio.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.model.Usuario;


public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {
	//public Optional<Usuario> findUserById(Long id);
}
