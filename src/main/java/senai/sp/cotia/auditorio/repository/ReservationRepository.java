package senai.sp.cotia.auditorio.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import senai.sp.cotia.auditorio.model.Reservation;


public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {
	
}
