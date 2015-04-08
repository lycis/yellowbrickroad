package at.deder.ybr.configuration;

public class InvalidConfigurationException extends Exception {
	private static final long serialVersionUID = -7004650795884041754L;

	public InvalidConfigurationException(String reason) {
		super(reason);
	}
	
	public InvalidConfigurationException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
