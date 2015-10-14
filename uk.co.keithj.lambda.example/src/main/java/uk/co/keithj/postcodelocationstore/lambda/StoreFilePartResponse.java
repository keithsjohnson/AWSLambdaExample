package uk.co.keithj.postcodelocationstore.lambda;

public class StoreFilePartResponse {

	private String status;

	private String nextStartPosition;

	private String finished;

	public StoreFilePartResponse() {
		super();
	}

	public StoreFilePartResponse(String status, String nextStartPosition, String finished) {
		super();
		this.status = status;
		this.nextStartPosition = nextStartPosition;
		this.finished = finished;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNextStartPosition() {
		return nextStartPosition;
	}

	public void setNextStartPosition(String nextStartPosition) {
		this.nextStartPosition = nextStartPosition;
	}

	public String getFinished() {
		return finished;
	}

	public void setFinished(String finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "StoreFilePartResponse [status=" + status + ", nextStartPosition=" + nextStartPosition + ", finished="
				+ finished + "]";
	}
}
