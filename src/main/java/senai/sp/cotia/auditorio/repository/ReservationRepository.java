package senai.sp.cotia.auditorio.repository;



import java.util.Calendar;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.type.StatusEvent;



public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {

	public Iterable<Reservation>findAllByStatus(StatusEvent status);
	
	@Query("SELECT res FROM Reservation res WHERE res.titulo LIKE %:p% OR res.dataInicio LIKE %:p% "
			+ "OR res.dataTermino LIKE %:p% OR usuario.nome LIKE %:p% OR status LIKE %:p%")
    public List<Reservation> procurarTudo(@Param("p") String param);
	
	@Query("SELECT res FROM Reservation res WHERE res.status = 'CONFIRMADO' "
			+ "OR res.status = 'ANALISE' AND :d between res.dataInicio AND res.dataTermino OR :d2 between res.dataInicio AND res.dataTermino")
	public List<Reservation> findAllReservadas(@Param("d") Calendar dataIn, @Param("d2") Calendar dataTerm);
	
	@Query("SELECT res from Reservation res Where :r between res.dataInicio and res.dataTermino")
    public Reservation between(@Param("r")Calendar dataInicio);
}
