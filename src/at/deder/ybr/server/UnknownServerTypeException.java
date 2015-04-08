package at.deder.ybr.server;

public class UnknownServerTypeException extends Exception {
	private static final long serialVersionUID = -8430326367136043566L;

	public UnknownServerTypeException(String type) {
		super(type);
	}
}
