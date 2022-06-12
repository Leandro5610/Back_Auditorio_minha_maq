package senai.sp.cotia.auditorio.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;
@Data
@Entity
public class Files {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	private String nomeAquivo;
	 
	private byte[] data;
	
	private String type;
	@OneToOne
	private Reservation reservation;
	
	private Files() {
		
	}
}
