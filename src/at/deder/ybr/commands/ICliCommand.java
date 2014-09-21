package at.deder.ybr.commands;

/**
 * This is the interface for all command line actions.
 * @author lycis
 *
 */
public interface ICliCommand {
	/**
	 * Specify the value for a specific runtime option of the command.
	 * @param name key that identifies the option
	 * @param value value for operation
	 */
	public void setOption(String name, String value);
	
	/**
	 * Executes the command.
	 */
	public void execute();
}
