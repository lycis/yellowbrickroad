package at.deder.ybr.commands;

import java.util.List;

import at.deder.ybr.access.IFileSystemAccessor;

// TODO implement

public class PrepareServer implements ICliCommand {
	
	private String targetFolder = ".";
	private IFileSystemAccessor fileSystem = null;

	@Override
	public void setOption(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setData(List<String> cliData) {	
		if(cliData.size() < 1) {
			// no target folder given -> use current folder
			targetFolder = ".";
			return;
		}
		
		targetFolder = cliData.get(0);
	}

	@Override
	public void execute() {
		
		// check target folder
		
		
		System.out.println("Preparing server...");
		// TODO create folder structure
		
		// TODO create manifest
		System.out.println("done.");
	}

	@Override
	public void setFileSystemAccessor(IFileSystemAccessor f) {
		fileSystem = f;		
	}

}
