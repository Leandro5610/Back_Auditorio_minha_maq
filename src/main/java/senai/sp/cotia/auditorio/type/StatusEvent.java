package senai.sp.cotia.auditorio.type;



public enum StatusEvent {
	CONFIRMADO("Confirmado"),
	ABERTO("Comum"),
	FINALIZADO("Finalizado");
	
	String status;
	
	private StatusEvent(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
