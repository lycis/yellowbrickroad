package at.deder.ybr.commands;

import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ProtocolViolationException;
import at.deder.ybr.server.ServerFactory;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Fetches all packages according to the config file.
 *
 * @author lycis
 */
public class Update implements ICliCommand {

    @Override
    public void setOption(String name, String value) {
        // TODO implement
        // TODO --create-target
    }

    @Override
    public void setData(List<String> cliData) {
        // TODO implement
    }

    @Override
    public void execute() {
        final IFileSystemAccessor filesystem = FileSystem.getAccess();
        final AbstractOutputChannel output = OutputChannelFactory.getOutputChannel();

        File config = filesystem.getClientConfigFile(filesystem.getWorkingDirectory());
        if (config == null || !config.exists()) {
            output.printErrLn("error: no configuration available");
            return;
        }

        final ClientConfiguration clientConf;
        try {
            clientConf = ClientConfiguration.readYaml(new FileReader(config));
        } catch (FileNotFoundException ex) {
            output.printErrLn("error: client configuration could not be loaded (" + ex.getMessage() + ")");
            return;
        }
        
        // get target directory
        File targetDir = filesystem.getFile(clientConf.getTargetPath());
        if(targetDir == null) {
            output.printErrLn("error: target directory does not exist (use --create-target for automatic creation)");
            return;
        }

        IServerGateway server = ServerFactory.createServer(clientConf);
        // TODO option "--parallel" for multithreaded processing?
        clientConf.getPackages().stream().forEach(pkgName -> {
            output.print("Fetching content for '" + pkgName + "'... ");

            // download files
            Map<String, byte[]> files = null;
            try {
                files = server.getFilesOfPackage(pkgName);
            } catch (ProtocolViolationException ex) {
                output.println("error");
                output.printErrLn("reason: " + ex.getMessage());
                return;
            }

            output.println("ok");
            output.println("Writing package files:");

            // write files to target
            if (files != null) {                    
                for (String filename : files.keySet()) {
                    byte[] content = files.get(filename);
                    output.print(filename + " [" + humanReadableByteCount(content.length, false)+"] ... ");
                    try {
                        File f = filesystem.createFile(targetDir, filename, false);
                        DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
                        out.write(content);
                    } catch (IOException ex) {
                        output.println("nok");
                        output.printErrLn("reason: "+ex.getMessage());
                    }
                    output.println("ok");
                }
            }
        });

        FileSystem.releaseAccess(filesystem);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
