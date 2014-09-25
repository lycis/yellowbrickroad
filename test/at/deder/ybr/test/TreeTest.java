package at.deder.ybr.test;

import at.deder.ybr.structures.Tree;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ederda
 */
public class TreeTest {

    /**
     * Check that storing and retrieving data from a node works.
     */
    @Test
    public void testDataStorage() {
        Tree<String> tree = new Tree("test");
        Assert.assertEquals(tree.getData(), "test");
    }
    
    /**
     * Verify that adding children works.
     */
    @Test
    public void testAddChildren() {
        Tree<String> root = new Tree("root");
        Tree<String> child = new Tree("child");
        
        root.addChild(child);
        
        Assert.assertTrue("child is not child of root", child.isChildOf(root));
        Assert.assertTrue("root is not parent of child", root.hasChild(child));
    }
}
