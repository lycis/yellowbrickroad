package at.deder.ybr.server;

/**
 * This exception is thrown whenever the yellow brick road protocol that
 * regulates server-client communication is violated.
 * @author ederda
 */
public class ProtocolViolationException extends Exception{
	private static final long serialVersionUID = 1083447117849279965L;

	public ProtocolViolationException(String message) {
        super(message);
    }
    
    public ProtocolViolationException(Throwable t) {
        super(t);
    }
    
    public ProtocolViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
