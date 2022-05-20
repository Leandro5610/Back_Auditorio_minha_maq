package senai.sp.cotia.auditorio.repository;



import org.springframework.data.repository.PagingAndSortingRepository;



import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.type.StatusEvent;



public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {

	public Iterable<Reservation>findAllByStatus(StatusEvent status);
	
}
