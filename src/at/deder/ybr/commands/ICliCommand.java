package at.deder.ybr.commands;

import java.util.List;

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
	 * Set further command line data (e.g. arguments)
	 * @param cliData
	 */
	public void setData(List<String> cliData);
	
	/**
	 * Executes the command.
	 */
	public void execute();
}
