package at.deder.ybr.test;

import at.deder.ybr.repository.Repository;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.server.IServerGateway;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author lycis
 */
public class RepositoryTest {

    private final IServerGateway mockGateway = mock(IServerGateway.class);

    @Test
    public void testGetPackageTopLevel() {
        RepositoryEntry root = generateComplexRepository();
        Repository instance = new Repository(root, mockGateway);
        RepositoryEntry expResult = root.getChildByName("com");
        RepositoryEntry result = instance.getPackage(".com");
        assertEquals(expResult, result);
    }

    @Test
    public void testGetPackageDeep() {
        RepositoryEntry root = generateComplexRepository();
        Repository instance = new Repository(root, mockGateway);
        RepositoryEntry expResult = root.getChildByName("com")
                .getChildByName("java")
                .getChildByName("io")
                .getChildByName("file");
        RepositoryEntry result = instance.getPackage(".com.java.io.file");
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetPackageNoLeadingDot() {
        RepositoryEntry root = generateComplexRepository();
        Repository instance = new Repository(root, mockGateway);
        RepositoryEntry expResult = root.getChildByName("com")
                .getChildByName("java")
                .getChildByName("io")
                .getChildByName("file");
        RepositoryEntry result = instance.getPackage("com.java.io.file");
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetPackageNotExisting() {
        RepositoryEntry root = generateComplexRepository();
        Repository instance = new Repository(root, mockGateway);
        RepositoryEntry expResult = null;
        RepositoryEntry result = instance.getPackage("com.doesnotexist");
        assertEquals(expResult, result);
    }

    private RepositoryEntry generateComplexRepository() {
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
        root.setName("root");

        // build org tree
        RepositoryEntry org = new RepositoryEntry();
        org.setName("org");
        root.addChild(org);
        RepositoryEntry orgJunit = new RepositoryEntry();
        orgJunit.setName("junit");
        org.addChild(org);

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

        return root;
    }
}
