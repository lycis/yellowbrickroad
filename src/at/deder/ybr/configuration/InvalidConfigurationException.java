package at.deder.ybr.configuration;

public class InvalidConfigurationException extends Exception {
	public InvalidConfigurationException(String reason) {
		super(reason);
	}
	
	public InvalidConfigurationException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
