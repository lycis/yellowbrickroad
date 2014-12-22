package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This command generates an index based on an index_rules file
 *
 * @author lycis
 */
public class GenerateIndex implements ICliCommand {

    private String rulesFilePath;

    @Override
    public void setOption(String name, String value) {
    }

    @Override
    public void setData(List<String> cliData) {
        if (cliData != null && !cliData.isEmpty()) {
            rulesFilePath = cliData.get(0); // arguments are names of rules files
        } else {
            rulesFilePath = Constants.INDEX_RULES_FILE;
        }
    }

    @Override
    public void execute() {
        IFileSystemAccessor filesystem = FileSystem.getAccess();
        AbstractOutputChannel output = OutputChannelFactory.getOutputChannel();
        File workDir = filesystem.getWorkingDirectory();

        // check if rules file exists
        File rulesFile = filesystem.getFile(rulesFilePath);
        if (rulesFile == null) {
            output.printErrLn("error: rules file does not exist");
            return;
        }

        // build index based on rules in file
        ArrayList<File> index = new ArrayList<>();

        BufferedReader rulesReader;
        try {
            rulesReader = new BufferedReader(new FileReader(rulesFile));
            String line;
            while ((line = rulesReader.readLine()) != null) {
                String operation = line.substring(0, 1);
                String target = line.substring(1).trim();

                // check if target is a valid regex
                try {
                    Pattern.compile(target);
                } catch (PatternSyntaxException ex) {
                    output.printErrLn("error: invalid regular expression in '"+line+"' ("+ex.getMessage()+")");
                    continue;
                }

                // execute operation
                if (operation.equals("-")) { // remove from index
                    ArrayList<File> newIndex = new ArrayList<>();
                    for (int i = 0; i < index.size(); ++i) {
                        if (index.get(i).getName().matches(target)) {
                            continue; // omit removed files
                        }
                        newIndex.add(index.get(i));
                    }
                    index = newIndex;
                } else if (operation.equals("+")) { // add file from current directory
                    File[] pool = workDir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return (name.matches(target) &&
                                    !name.equals(Constants.DESCRIPTION_FILE) &&
                                    !name.equals(Constants.INDEX_FILE) &&
                                    !name.equals(Constants.INDEX_RULES_FILE));
                        }
                    });
                    index.addAll(Arrays.asList(pool));
                } else {
                    output.printDetailLn("warning: operation '" + operation + "' unknown and ignored");
                }
            }
        } catch (IOException ex) {
            output.printErrLn("error: parsing rules failed (" + ex.getMessage() + ")");
            return;
        }

        // convert indexed files to text
        StringBuilder indexStr = new StringBuilder();
        for (File f : index) {
            if (f.isDirectory()) {
                output.printDetailLn("warning: directory '" + f.getName() + "' ignored");
                continue;
            }
            indexStr.append(f.getName()).append("\n");
        }

        // write index to file
        File indexFile = filesystem.getFileInDir(workDir, Constants.INDEX_FILE);
        if (indexFile == null) {
            output.printDetailLn("creating new index file");
            try {
                indexFile = filesystem.createFile(workDir, Constants.INDEX_FILE, false);
            } catch (IOException ex) {
                output.printErrLn("error: creating index file failed (" + ex.getMessage() + ")");
                return;
            }
        } else {
            output.printDetailLn("old index will be overwritten");
        }

        try (BufferedWriter indexWriter = new BufferedWriter(new FileWriter(indexFile))) {
            indexWriter.write(indexStr.toString());
        } catch (IOException ex) {
            output.printErrLn("error: writing index failed (" + ex.getMessage() + ")");
        }
        
        output.println("Generated index:");
        output.print(indexStr.toString());

        FileSystem.releaseAccess(filesystem);
    }

}
