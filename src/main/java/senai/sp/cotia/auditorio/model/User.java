package senai.sp.cotia.auditorio.model;

import lombok.Data;
import senai.sp.cotia.auditorio.type.Types;
@Data
public class User {
	
	private Long id;
	private String nif;
	private String senha;
	private Types type;
	
	
}
