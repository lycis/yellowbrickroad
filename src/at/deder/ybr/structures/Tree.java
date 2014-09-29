package at.deder.ybr.structures;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Used for storing data in a tree structure. It does support only one root
 * node.
 *
 * @author lycis
 */
public abstract class Tree {

    protected Tree parent = null;
    protected Set<Tree> children = null;

    /**
     * Inistantiates an empty Tree node.
     */
    public Tree() {
        children = new HashSet<>();
    }

    /**
     * Instantiates a Tree node that contains the given data and is a child of
     * the given parent.
     *
     * @param data
     * @param parent
     */
    public Tree(Tree parent) {
        this.parent = parent;
    }

    /**
     * Mark the given Tree node as child of this node.
     *
     * @param child
     */
    public void addChild(Tree child) {
        addChild(child, true);
    }
    
    /**
     * execute adding of a child to the current node
     * @param child new child
     * @param callback if <code>false</code> do not call back to setParent 
     *                   to prevent circular processing
     */
    private void addChild(Tree child, boolean callback) {
        if (child != null) {
            children.add(child);
            if(callback) {
                child.setParent(this, false);
            }
        }
    }

    /**
     * Remove a child from the current node.
     *
     * @param child
     */
    public void removeChild(Tree child) {
        removeChild(child, true);
    }
    
    private void removeChild(Tree child, boolean callback) {
        if (child == null) {
            return;
        }
        
        children.remove(child);
        if(callback) {
            child.setParent(null, false);
        }
    }

    /**
     * Return all children of this node.
     *
     * @return an unmodifiable set containing the children
     */
    public Set<Tree> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * Return all siblings (child nodes of the same parent of this node.
     *
     * @return an unmodifiable set containing the siblings
     */
    public Set<Tree> getSiblings() {
        if (parent == null) {
            return null;
        }

        return parent.getChildren();
    }

    /**
     * Change parent of current tree.
     *
     * @param parent
     */
    public void setParent(Tree parent) {
        setParent(parent, true);
    }

    /**
     * executes parent change
     * @param parent new parent
     * @param callback if <code>false</code> do not call back to addChildren 
     *                   to prevent circular processing
     */
    private void setParent(Tree parent, boolean callback) {
        if (this.parent != null && callback) {
            this.parent.removeChild(this);
        }

        this.parent = parent;

        if (parent != null && callback) {
            parent.addChild(this, false);
        }
    }

    /**
     * Gives the parent of the current node.
     *
     * @return <code>null</code> will be returned when this is a root node
     */
    public Tree getParent() {
        return parent;
    }

    /**
     * @return <code>true</code> in case this node is the root of a tree
     */
    public boolean isRoot() {
        return (parent == null);
    }
    
    /**
     *
     * @return <code>true</code> if this node is a leaf (has no children)
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Checks if this node is a child of the given node.
     *
     * @param potentialChild
     * @return
     */
    public boolean isChildOf(Tree parent) {
        if (parent == null) {
            return false;
        }

        return parent.equals(this.parent);
    }

    /**
     * Checks if the given node is a child of this node.
     *
     * @param potentialChild
     * @return
     */
    public boolean hasChild(Tree potentialChild) {
        return children.contains(potentialChild);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.children);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tree other = (Tree) obj;
       /* if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }*/
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        return true;
    }

}
