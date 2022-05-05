package senai.sp.cotia.auditorio.type;

public enum Types {
	ADMINISTRATOR("Administrador"),
	COMMOM("Comum");
	
	String type;

	private Types(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
