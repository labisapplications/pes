package br.com.usp.labis.exception;


public class ApiErrorResponse {

    private int status;
    private String message;

    public ApiErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
	public String toString() {
    	StringBuilder status = new StringBuilder();
    	status.append(status);
    	status.append(message);
		return status.toString();
	}
}