package at.deder.ybr.test.mocks;

import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.repository.RepositoryEntry;

/**
 * Implements helper methods for the generation of test data.
 * @author lycis
 */
public class MockUtils {
    
    /**
     * Provides a complex repository structure.
     * @return 
     */
    public static RepositoryEntry generateComplexRepository() {
        // build test repository tree:
        // +- root
        //  \
        //   +- com
        //   |\
        //   | +- java
        //   | |\
        //   | | +- util
        //   | | |
        //   | | +- io
        //   | |  \
        //   | |   +- file
        //   | +- cpp
        //   |  \
        //   |   +- util
        //   |    \
        //   |     +- x64
        //   |     |
        //   |     +- x32
        //   +- org
        //    \
        //     +- junit

        RepositoryEntry root = new RepositoryEntry();
        root.setName("repository");

        // TODO describe all packages
        // build org tree
        RepositoryEntry org = new RepositoryEntry();
        org.setName("org");
        root.addChild(org);
        RepositoryEntry orgJunit = new RepositoryEntry();
        orgJunit.setName("junit");
        orgJunit.setDescription("JUnit unit test suite");
        org.addChild(orgJunit);

        // build com tree
        RepositoryEntry com = new RepositoryEntry();
        com.setName("com");
        root.addChild(com);

        RepositoryEntry comJava = new RepositoryEntry();
        comJava.setName("java");
        com.addChild(comJava);
        RepositoryEntry comJavaUtil = new RepositoryEntry();
        comJavaUtil.setName("util");
        comJava.addChild(comJavaUtil);
        RepositoryEntry comJavaIo = new RepositoryEntry();
        comJavaIo.setName("io");
        comJava.addChild(comJavaIo);
        RepositoryEntry comJavaIoFile = new RepositoryEntry();
        comJavaIoFile.setName("file");
        comJavaIo.addChild(comJavaIoFile);

        RepositoryEntry comCpp = new RepositoryEntry();
        comCpp.setName("cpp");
        com.addChild(comCpp);
        RepositoryEntry comCppUtil = new RepositoryEntry();
        comCppUtil.setName("util");
        comCpp.addChild(comCppUtil);
        RepositoryEntry comCppUtilX64 = new RepositoryEntry();
        comCppUtilX64.setName("x64");
        comCppUtil.addChild(comCppUtilX64);
        RepositoryEntry comCppUtilX32 = new RepositoryEntry();
        comCppUtilX32.setName("x32");
        comCppUtil.addChild(comCppUtilX32);
        comCppUtilX32.setDescription("c++ utilities for 32bit architecture");

        return root;
    }
    
    /**
     * Provides a manifest.
     * @return 
     */
     public static ServerManifest getMockManifest() {
        ServerManifest manifest = new ServerManifest();
        manifest.initDefaults();
        manifest.setRepository(MockUtils.generateComplexRepository());
        return manifest;
    }
}
