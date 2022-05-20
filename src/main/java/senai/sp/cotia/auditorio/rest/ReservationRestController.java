package senai.sp.cotia.auditorio.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import senai.sp.cotia.auditorio.model.Erro;
import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.model.Usuario;
import senai.sp.cotia.auditorio.repository.ReservationRepository;
import senai.sp.cotia.auditorio.repository.UserRepository;
import senai.sp.cotia.auditorio.type.StatusEvent;

@RestController
@RequestMapping("api/reservation")
public class ReservationRestController {
	@Autowired
	private ReservationRepository repository;
	@Autowired
	private UserRepository userRepo;

	@RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> saveReservation(@RequestBody Reservation reservation, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws IOException {
		
		Calendar dataAtual = Calendar.getInstance();
		if (reservation.getDataInicio().before(dataAtual.getTimeInMillis())) {
			return ResponseEntity.badRequest().build();
		} else if(reservation.getDataTermino().before(reservation.getDataInicio())) {
			return ResponseEntity.badRequest().build();
		} else {			
			reservation.setStatus(StatusEvent.CONFIRMADO);
			String token = null;
			try {
				// obtem o token da request
				token = request.getHeader("Authorization");
				// algoritimo para descriptografar
				Algorithm algoritimo = Algorithm.HMAC256(UserRestController.SECRET);
				// objeto para verificar o token
				JWTVerifier verifier = JWT.require(algoritimo).withIssuer(UserRestController.EMISSOR).build();
				// validar o token
				System.out.println(token);
				DecodedJWT decoded = verifier.verify(token);
				// extrair os dados do payload
				Map<String, Claim> payload = decoded.getClaims();
				Long id = Long.parseLong(payload.get("usuario_id").toString());
				Usuario user = new Usuario();
				user.setId(id);
				reservation.setUsuario(user);
				
			} catch (Exception e) {
				e.printStackTrace();
				if (token == null) {
					response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
				} else {
					response.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
					
				}
			}
			repository.save(reservation);
			return ResponseEntity.ok().build();
		}
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public Iterable<Reservation> getReservations() {
		isFinalizada();
		return repository.findAll();
	}

	public void isFinalizada() {
		Calendar horaAtual = Calendar.getInstance();
		for (Reservation reserv : repository.findAll()) {
			if (reserv.getDataTermino().before(horaAtual)) {
				reserv.setStatus(StatusEvent.FINALIZADO);
				repository.save(reserv);

			}
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
		repository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(value = "confirmada{id}", method = RequestMethod.PUT)
	public Object statusConfirmada(Reservation reserva, @PathVariable("id") Long id) {
		Calendar horaAtual = Calendar.getInstance();
		if (id != null) {
			throw new RuntimeException("Id Inválido");

		} else if (reserva.getDataTermino().after(horaAtual)) {
			reserva.setStatus(StatusEvent.CONFIRMADO);
			repository.save(reserva);
		}
		// se o a reserva acabar enviar para o Histórico
		return repository.save(reserva);
	}

	@RequestMapping(value = "analise{id}", method = RequestMethod.PUT)
	public Object statusAnalise(Reservation reserva, @PathVariable("id") Long id) {
		Calendar horaAtual = Calendar.getInstance();
		if (id != null) {
			throw new RuntimeException("Id Inválido");

		} else if (reserva.getDataTermino().after(horaAtual)) {
			reserva.setStatus(StatusEvent.ANALISE);
			repository.save(reserva);
		}
		// se o a reserva acabar enviar para o Histórico
		return repository.save(reserva);
	}
	
	@RequestMapping(value = "historico", method = RequestMethod.GET)
    public Iterable<Reservation> getAllHistorico() {
        return repository.findAllByStatus(StatusEvent.FINALIZADO);
    }

}
