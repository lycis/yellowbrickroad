package at.deder.ybr.commands;

import java.util.List;

import at.deder.ybr.Constants;

// TODO implement

public class PrepareServer implements ICliCommand {
	private boolean verbose = false;

	@Override
	public void setOption(String name, String value) {
		if(Constants.OPTION_VERBOSE.equals(name) && Constants.VALUE_TRUE.equals(value)) {
			verbose = true;
		}
	}

	@Override
	public void setData(List<String> cliData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() {
		System.out.println("Preparing server structure...");
		
		if(verbose) {
			System.out.print("Creating folder structure ... ");
		}
		
		// TODO create server folder structure
		
		if(verbose) {
			System.out.println("done");
		}
		
		if(verbose) {
			System.out.print("Writing manifest ... ");
		}
		
		// TODO write empty yml-manifest
		
		if(verbose) {
			System.out.println("done");
		}
		
		System.out.println("done.");
	}

}
