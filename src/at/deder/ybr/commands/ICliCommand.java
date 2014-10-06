package at.deder.ybr.commands;

import java.util.List;

import at.deder.ybr.access.IFileSystemAccessor;
import at.deder.ybr.access.IOutputChannel;

/**
 * This is the interface for all command line actions.
 *
 * @author lycis
 *
 */
public interface ICliCommand {

    /**
     * Specify the value for a specific runtime option of the command.
     *
     * @param name key that identifies the option
     * @param value value for operation
     */
    public void setOption(String name, String value);

    /**
     * Set further command line data (e.g. arguments)
     *
     * @param cliData
     */
    public void setData(List<String> cliData);

    /**
     * Executes the command.
     */
    public void execute();

    /**
     * Provides an accessor for the file system to the command. This will be
     * needed if the command executes any interaction with the local file system
     * (e.g. file input/output).
     *
     * @param f accessor implementation
     */
    public void setFileSystemAccessor(IFileSystemAccessor f);
    
    /**
     * Configure the output accessor that has to be used. It should be used for
     * displaying any kind of information to the user (e.g. error messages,
     * hints, ...)
     * @param o accessor implementation
     */
    public void setOutputAccessor(IOutputChannel o);
}
