package at.deder.ybr.repository;

/**
 * A more specialised implementation oft a repository entry specifically for
 * Nexus repositories.
 * 
 * @author lycis
 *
 */
public class NexusRepositoryEntry extends RepositoryEntry {

	private String groupId = "";
	private String artefactId = "";
	private String version = "";
	private String packageType = "";

	// com.automic.ae:ucdj:10.0.5-SNAPSHOT:jar
	public NexusRepositoryEntry(String name) {
		super();
		setName(name);

		String[] parts = name.split(":");
		if (parts.length != 4) {
			throw new IllegalArgumentException("malformed package name");
		}

		this.groupId = parts[0];
		this.artefactId = parts[1];
		this.version = parts[2];
		this.packageType = parts[3];
	}
	
	public String getName() {
		return groupId+":"+artefactId+":"+version+":"+packageType;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getArtefactId() {
		return artefactId;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getPackageType() {
		return packageType;
	}
}
