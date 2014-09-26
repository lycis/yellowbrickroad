package at.deder.ybr.test;

import at.deder.ybr.structures.ObjectTree;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Tree structure for consistency.
 * @author lycis
 */
public class TreeTest {
    
    /**
     * Verify that adding children works.
     */
    @Test
    public void testAddChildren() {
        ObjectTree<String> root = new ObjectTree<>("root");
        
        ObjectTree<String> firstChild = new ObjectTree<>("child1");
        root.addChild(firstChild);
        
        Assert.assertTrue("1st child is not child of root", firstChild.isChildOf(root));
        Assert.assertTrue("root is not parent of 1st child", root.hasChild(firstChild));
        
        ObjectTree<String> secondChild = new ObjectTree<>("child2");
        root.addChild(secondChild);
        Assert.assertTrue("2nd child is not child of root", secondChild.isChildOf(root));
        Assert.assertTrue("root is not parent of 2nd child", root.hasChild(secondChild));
    }
    
    // TODO more tests!
}
