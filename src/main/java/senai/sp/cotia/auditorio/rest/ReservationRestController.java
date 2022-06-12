package senai.sp.cotia.auditorio.rest;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import senai.sp.cotia.auditorio.annotation.Privado;
import senai.sp.cotia.auditorio.model.Erro;
import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.model.Usuario;
import senai.sp.cotia.auditorio.repository.ReservationRepository;
import senai.sp.cotia.auditorio.repository.UserRepository;
import senai.sp.cotia.auditorio.type.StatusEvent;

@CrossOrigin
@RestController
@RequestMapping("api/reservation")
public class ReservationRestController {
	@Autowired
	private ReservationRepository repository;

	@RequestMapping(value = "save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> saveReservation(@RequestBody Reservation reservation, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws IOException {
		Erro erro = new Erro();
		erro.setStatusCode(406);

		Calendar dataAtual = Calendar.getInstance();
		int horaInicioMin = 7, horaInicMax = 21, horaTerminoMin = 8, horaTermMax = 22, minuto = 31;

		System.out.println(dataAtual.getTime());
		if (reservation.getDataInicio().get(Calendar.HOUR_OF_DAY) < horaInicioMin) {
			System.out.println("Aqui" + reservation.getDataInicio());
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);

		} else if (reservation.getDataInicio().get(Calendar.HOUR_OF_DAY) >= horaInicMax
				&& reservation.getDataInicio().get(Calendar.MINUTE) >= minuto) {
			System.out.println("Aqui2" + reservation.getDataInicio());
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
		} else if (reservation.getDataTermino().get(Calendar.HOUR_OF_DAY) < horaTerminoMin) {
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
		} else if (reservation.getDataTermino().get(Calendar.HOUR_OF_DAY) >= horaTermMax
				&& reservation.getDataTermino().get(Calendar.MINUTE) >= minuto) {
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
		} else if (reservation.getDataInicio().get(Calendar.DAY_OF_WEEK) == 1) {
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
		} else if (reservation.getDataInicio().before(dataAtual.getTime())) {
			return ResponseEntity.badRequest().build();
		} else if (reservation.getDataTermino().before(reservation.getDataInicio())) {
			return ResponseEntity.badRequest().build();
		} else if (repository.between(reservation.getDataInicio()) != null) {
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
		} else {
			reservation.setStatus(StatusEvent.ANALISE);
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
				repository.between(reservation.getDataInicio());
			} catch (Exception e) {
				e.printStackTrace();
				if (token == null) {
					response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
				} else {
					response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());

				}
			}
			repository.save(reservation);

		}
		return ResponseEntity.ok().build();
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

	@RequestMapping(value = "deleta/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
		repository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(value = "confirmada/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> statusConfirmada(@RequestBody Reservation reserva, @PathVariable("id") Long id) {
		// valida o ID
		if (id != reserva.getId()) {
			throw new RuntimeException("ID Inválido");
		}
		// salva o usuario no BD
		reserva.setStatus(StatusEvent.CONFIRMADO);
		repository.save(reserva);
		// criar um cabeçalho HTTP
		HttpHeaders header = new HttpHeaders();
		header.setLocation(URI.create("/api/reservation"));
		return new ResponseEntity<Void>(header, HttpStatus.OK);
	}

	@RequestMapping(value = "historico", method = RequestMethod.GET)
	public Iterable<Reservation> getAllHistorico() {
		return repository.findAllByStatus(StatusEvent.FINALIZADO);
	}

	@Privado
	@RequestMapping(value = "pega/{id}", method = RequestMethod.GET)
	public ResponseEntity<Reservation> findUsuario(@PathVariable("id") Long idReserva) {
		// busca o usuario
		Optional<Reservation> reserva = repository.findById(idReserva);
		if (reserva.isPresent()) {
			return ResponseEntity.ok(reserva.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Privado
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> atualizarReserva(@RequestBody Reservation reservation, @PathVariable("id") Long id) {
		Erro erro = new Erro();
		erro.setStatusCode(406);

		Calendar dataAtual = Calendar.getInstance();
		int horaInicioMin = 7, horaInicMax = 21, horaTerminoMin = 8, horaTermMax = 22, minuto = 31;
		// valida o ID
		if (id != reservation.getId()) {
			throw new RuntimeException("ID Inválido");
		} else if (reservation.getDataTermino().before(reservation.getDataInicio())) {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIII 1");

			return new ResponseEntity<Object>(HttpStatus.NOT_ACCEPTABLE);
			
		}else if (reservation.getDataInicio().get(Calendar.HOUR_OF_DAY) < horaInicioMin) {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIII 2");
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);

		} else if (reservation.getDataInicio().get(Calendar.HOUR_OF_DAY) >= horaInicMax
				&& reservation.getDataInicio().get(Calendar.MINUTE) >= minuto) {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIII 3");
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
			
		} else if (reservation.getDataTermino().get(Calendar.HOUR_OF_DAY) < horaTerminoMin) {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIII 4");
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
			
		} else if (reservation.getDataTermino().get(Calendar.HOUR_OF_DAY) >= horaTermMax
				&& reservation.getDataTermino().get(Calendar.MINUTE) >= minuto) {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIII 5");
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
			
		} else if (reservation.getDataInicio().get(Calendar.DAY_OF_WEEK) == 1) {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIII 6");
			return new ResponseEntity<Object>(erro, HttpStatus.NOT_ACCEPTABLE);
			
		} else if (reservation.getDataInicio().before(dataAtual.getTime())) {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIII 7");
			return ResponseEntity.badRequest().build();
			
		}  
		
		reservation.setStatus(StatusEvent.ANALISE);
		// salva o usuario no BD
		repository.save(reservation);
		// criar um cabeçalho HTTP
		HttpHeaders header = new HttpHeaders();
		header.setLocation(URI.create("/api/reservation"));
		return new ResponseEntity<Object>(header, HttpStatus.OK);

	}

	@RequestMapping(value = "/findbyall/{p}")
	public Iterable<Reservation> findByAll(@PathVariable("p") String param) {
		return repository.procurarTudo(param);
	}

	@RequestMapping(value = "minhas", method = RequestMethod.GET)
	public Iterable<Reservation> minhasReservas(Long id, HttpServletRequest request, HttpServletResponse response) {
		String token = null;
		// obtem o token da request
		token = request.getHeader("Authorization");
		// algoritimo para descriptografar
		Algorithm algoritimo = Algorithm.HMAC256(UserRestController.SECRET);
		// objeto para verificar o token
		JWTVerifier verifier = JWT.require(algoritimo).withIssuer(UserRestController.EMISSOR).build();
		// validar o token
		DecodedJWT decoded = verifier.verify(token);
		// extrair os dados do payload
		Map<String, Claim> payload = decoded.getClaims();
		id = Long.parseLong(payload.get("usuario_id").toString());
		return repository.findByUsuarioId(id);
	}

}
