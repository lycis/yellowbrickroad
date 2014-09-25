package at.deder.ybr.test;

import at.deder.ybr.structures.Tree;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Tree structure for consistency.
 * @author lycis
 */
public class TreeTest {

    /**
     * Check that storing and retrieving data from a node works.
     */
    @Test
    public void testDataStorage() {
        Tree<String> tree = new Tree<>("test");
        Assert.assertEquals("data stored incorrectly", tree.getData(), "test");
    }
    
    /**
     * Verify that adding children works.
     */
    @Test
    public void testAddChildren() {
        Tree<String> root = new Tree<>("root");
        
        Tree<String> firstChild = new Tree<>("child1");
        root.addChild(firstChild);
        
        Assert.assertTrue("1st child is not child of root", firstChild.isChildOf(root));
        Assert.assertTrue("root is not parent of 1st child", root.hasChild(firstChild));
        
        Tree<String> secondChild = new Tree<>("child2");
        root.addChild(secondChild);
        Assert.assertTrue("2nd child is not child of root", secondChild.isChildOf(root));
        Assert.assertTrue("root is not parent of 2nd child", root.hasChild(secondChild));
    }
    
    /**
     * Check that adding children by implicitly creating them is working.
     */
    @Test
    public void testImplicitAddChild() {
        Tree<String> root = new Tree<>("root");
        Tree<String> child = root.addChild("child");
        
        Assert.assertTrue("child is not child of root", child.isChildOf(root));
        Assert.assertTrue("root is not parent of child", root.hasChild(child));
        Assert.assertTrue("child is incorrect data", "child".equals(child.getData()));
    }
    
    // TODO more tests!
}
