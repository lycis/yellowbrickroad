package at.deder.ybr;

import ml.options.Options;
import ml.options.Options.Multiplicity;

/**
 * This class is the main entry point for the application. It parses all
 * command line parameters and triggers according actions.
 * 
 * @author lycis
 *
 */
public class Main {

	public static void main(String[] args) {		
		Options cliOptions = new Options(args, 0, 99);
		
		// define possible command line options
		cliOptions.getSet().addOption("version", Multiplicity.ZERO_OR_ONE);
		cliOptions.getSet().addOption("help",    Multiplicity.ZERO_OR_ONE);
		
		// evaluate options
		if(!cliOptions.check(false, false)) {
			System.out.println("error: "+cliOptions.getCheckErrors());
			printUsageHint();
			System.exit(1);
		}
		
		if(cliOptions.getSet().isSet("version")) {
			printVersionInfo();
			return;
		}
		
		if(cliOptions.getSet().isSet("help")) {
			printUsageHint();
			return;
		}
	}

	public static void printUsageHint() {
		System.out.println("ybr [options] <command>");
		System.out.println("");
		System.out.println("options:");
		System.out.println("-help\t\tprint this information");
		System.out.println("-version\tprint version information");
		System.out.println("");
		System.out.println("commands:");
		System.out.println("");
	}
	
	public static void printVersionInfo() {
		System.out.println(Version.getCompleteVersion());
	}
}
