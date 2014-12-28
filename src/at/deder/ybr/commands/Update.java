package at.deder.ybr.commands;

import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import at.deder.ybr.repository.PackageHash;
import at.deder.ybr.repository.PackageIndex;
import at.deder.ybr.repository.RepositoryEntry;
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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Fetches all packages according to the config file.
 *
 * @author lycis
 */
public class Update implements ICliCommand {

    private boolean validExecution = true;
    private boolean createTargetIfNotExists = false;

    @Override
    public void setOption(String name, String value) {
    }

    @Override
    public void setData(List<String> cliData) {
        Option file = OptionBuilder.withLongOpt("create-target").withDescription("automatically create target directory").create("ct");
        Options opt = new Options();
        opt.addOption(file);

        CommandLineParser clParser = new PosixParser();
        CommandLine cLine = null;
        try {
            cLine = clParser.parse(opt, cliData.toArray(new String[cliData.size()]));
        } catch (ParseException ex) {
            AbstractOutputChannel output = OutputChannelFactory.getOutputChannel();
            output.printErrLn("error: " + ex.getMessage());
            validExecution = false;
            return;
        }

        createTargetIfNotExists = cLine.hasOption("ct");
    }

    @Override
    public void execute() {
        if (!validExecution) {
            return;
        }

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
        
        // check if any packages are given
        if(clientConf.getPackages() == null) {
            output.println("no packages to update");
            return;
        }

        IServerGateway server = ServerFactory.createServer(clientConf);
        // TODO option "--parallel" for multithreaded processing?
        for(String pkgName: clientConf.getPackages()) {
            output.println(pkgName + ": ");
            
            // get target directory
            // TODO move outside of lambda... maybe
            File targetDir = filesystem.getFile(clientConf.getTargetPath());
            if (targetDir == null) {
                if (createTargetIfNotExists) {
                    try {
                        targetDir = filesystem.createFile(filesystem.getWorkingDirectory(), clientConf.getTargetPath(), true);
                    } catch (IOException ex) {
                        output.printErrLn("error: creation of target directory failed (" + ex.getMessage() + ")");
                    }
                } else {
                    output.printErrLn("error: target directory does not exist (use --create-target for automatic creation)");
                    return;
                }
            }
            
            // check if update is necessary
            try {
                output.printDetail("Check if package is up to date: ");
                PackageIndex index = server.getPackageIndex(pkgName);
                PackageHash lokalPkgHash = new PackageHash(targetDir, index);
                RepositoryEntry remotePkg = server.getPackage(pkgName);
                if(lokalPkgHash.equals(remotePkg.getPackageHash())) {
                    output.println("up to date");
                    continue;
                }
            } catch (ProtocolViolationException ex) {
               output.printErrLn("error: package index could not be checked - "+ex.getMessage());
               if(ex.getCause() != null) {
                   output.printErrLn("cause: "+ex.getMessage());
               }
            }

            // download files
            output.printDetail("\nDownloading files... ");
            Map<String, byte[]> files;
            try {
                files = server.getFilesOfPackage(pkgName);
            } catch (ProtocolViolationException ex) {
                output.println("error");
                output.printErrLn("reason: " + ex.getMessage());
                return;
            }

            output.printDetailLn("ok");
            output.printDetailLn("Writing package files:");

            // write files to target
            if (files != null) {
                for (String filename : files.keySet()) {
                    byte[] content = files.get(filename);
                    output.print(filename + " [" + humanReadableByteCount(content.length, false) + "] ... ");
                    try {
                        File f = filesystem.createFile(targetDir, filename, false);
                        DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
                        out.write(content);
                    } catch (IOException ex) {
                        output.println("nok");
                        output.printErrLn("reason: " + ex.getMessage());
                    }
                    output.println("ok");
                }
            }
            
            output.println("");
        }

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
