package at.deder.ybr.test;

import at.deder.ybr.repository.Repository;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.test.mocks.MockUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author lycis
 */
public class RepositoryTest {

    @Test
    public void testGetPackageTopLevel() {
        IServerGateway mockGateway = mock(IServerGateway.class);
        ServerManifest dummyManifest = MockUtils.getMockManifest();
        when(mockGateway.getManifest()).thenReturn(dummyManifest);
        
        RepositoryEntry root = dummyManifest.getRepository();
        Repository instance = new Repository(mockGateway);
        RepositoryEntry expResult = root.getChildByName("com");
        RepositoryEntry result = instance.getPackage(".com");
        assertEquals(expResult, result);
        verify(mockGateway, times(1)).getManifest();
    }

    @Test
    public void testGetPackageDeep() {
        IServerGateway mockGateway = mock(IServerGateway.class);
        ServerManifest dummyManifest = MockUtils.getMockManifest();
        when(mockGateway.getManifest()).thenReturn(dummyManifest);
        
        RepositoryEntry root = dummyManifest.getRepository();
        Repository instance = new Repository(mockGateway);
        RepositoryEntry expResult = root.getChildByName("com")
                .getChildByName("java")
                .getChildByName("io")
                .getChildByName("file");
        RepositoryEntry result = instance.getPackage(".com.java.io.file");
        assertEquals(expResult, result);
        verify(mockGateway, times(1)).getManifest();
    }
    
    @Test
    public void testGetPackageNoLeadingDot() {
        IServerGateway mockGateway = mock(IServerGateway.class);
        ServerManifest dummyManifest = MockUtils.getMockManifest();
        when(mockGateway.getManifest()).thenReturn(dummyManifest);
        
        RepositoryEntry root = dummyManifest.getRepository();
        Repository instance = new Repository(mockGateway);
        RepositoryEntry expResult = root.getChildByName("com")
                .getChildByName("java")
                .getChildByName("io")
                .getChildByName("file");
        RepositoryEntry result = instance.getPackage("com.java.io.file");
        assertEquals(expResult, result);
        verify(mockGateway, times(1)).getManifest();
    }
    
    @Test
    public void testGetPackageNotExisting() {
        IServerGateway mockGateway = mock(IServerGateway.class);
        ServerManifest dummyManifest = MockUtils.getMockManifest();
        when(mockGateway.getManifest()).thenReturn(dummyManifest);
        
        RepositoryEntry root = dummyManifest.getRepository();
        Repository instance = new Repository(mockGateway);
        RepositoryEntry expResult = null;
        RepositoryEntry result = instance.getPackage("com.doesnotexist");
        assertEquals(expResult, result);
    }
    
}
