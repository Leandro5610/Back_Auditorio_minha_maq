package senai.sp.cotia.auditorio.rest;

import java.io.IOException;
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
	public Object saveReservation(@RequestBody Reservation reservation, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		reservation.setStatusEvent(StatusEvent.ANALISE);
		String token = null;
		try {
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
            Long id = Long.parseLong(payload.get("usuario_id").toString());
            Usuario user = new Usuario();
            user.setId(id);
            System.out.println(decoded.getClaims());
            System.out.println(decoded);
            reservation.setUsuario(user);
        } catch (Exception e) {
        	e.printStackTrace();
            if(token == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            }else {
                response.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
            }
        }
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
	
	
	@RequestMapping(value = "final{id}", method = RequestMethod.PUT)
    public Object statusFinalizada(Reservation reserva, @PathVariable("id") Long id) {
        Calendar horaAtual = Calendar.getInstance();
        if (id != null) {
            throw new RuntimeException("Id Inválido");
            
        }else if (reserva.getDataTermino().after(horaAtual)) {
            reserva.setStatusEvent(StatusEvent.FINALIZADO);
            repository.save(reserva);
        }
        // se o a reserva acabar enviar para o Histórico
        return repository.save(reserva);
    }
	
	@RequestMapping(value = "confirmada{id}", method = RequestMethod.PUT)
    public Object statusConfirmada(Reservation reserva, @PathVariable("id") Long id) {
        Calendar horaAtual = Calendar.getInstance();
        if (id != null) {
            throw new RuntimeException("Id Inválido");
            
        }else if (reserva.getDataTermino().after(horaAtual)) {
            reserva.setStatusEvent(StatusEvent.CONFIRMADO);
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
            
        }else if (reserva.getDataTermino().after(horaAtual)) {
            reserva.setStatusEvent(StatusEvent.ANALISE);
            repository.save(reserva);
        }
        // se o a reserva acabar enviar para o Histórico
        return repository.save(reserva);
    }
	
}
