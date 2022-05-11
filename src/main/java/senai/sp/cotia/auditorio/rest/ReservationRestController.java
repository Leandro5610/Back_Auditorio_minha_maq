package senai.sp.cotia.auditorio.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.repository.ReservationRepository;



	@RestController
	@RequestMapping("api/reservation")
	public class ReservationRestController {
	@Autowired
	private ReservationRepository repository;
	
	@RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object saveReservation(@RequestBody Reservation reservation) {
	return repository.save(reservation);
	}

	@RequestMapping(value = "", method = RequestMethod.GET) 
	public Iterable<Reservation> getReservations(){
	return repository.findAll();
}

	@RequestMapping(value = "/{id}")
	public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
	repository.deleteById(id);
	return ResponseEntity.noContent().build();
	}
}
