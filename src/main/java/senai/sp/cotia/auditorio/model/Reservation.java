package senai.sp.cotia.auditorio.model;

import java.util.Calendar;

import lombok.Data;

@Data
public class Reservation {
	
	private Long id;
	private String titulo;
	private User usuario;
	private String descricao;
	private Calendar dataInicio;
	private Calendar dataTermino;
	private boolean repetir;
	private int participantes;

}
