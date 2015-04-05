package at.deder.ybr.server;

public class UnknownServerTypeException extends Exception {
	public UnknownServerTypeException(String type) {
		super(type);
	}
}
