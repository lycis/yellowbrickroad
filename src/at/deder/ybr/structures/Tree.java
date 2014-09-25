package at.deder.ybr.structures;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Used for storing data in a tree structure. It does support only one root node.
 * 
 * @author lycis
 */
public class Tree<E> {
    private Tree<E>      parent   = null;
    private Set<Tree<E>> children = null;
    private E            data     = null;
    
    /**
     * Inistantiates an empty Tree node.
     */
    public Tree() {
        children = new HashSet<>();
    }
    
    /**
     * Instantiates a Tree node that contains the given data.
     * @param data 
     */
    public Tree(E data) {
        this();
        this.data = data;
    }
    
    /**
     * Instantiates a Tree node that contains the given data and
     * is a child of the given parent.
     * @param data
     * @param parent 
     */
    public Tree(E data, Tree<E> parent) {
        this(data);
        this.parent = parent;
    }
    
    /**
     * Mark the given Tree node as child of this node.
     * @param child 
     */
    public void addChild(Tree<E> child) {
        children.add(child);
    }
    
    /**
     * Creates a new Tree node with the given data and marks it as
     * child of this node.
     * @param data 
     */
    public Tree<E> addChild(E data) {
        Tree<E> child = new Tree<>(data);
        child.setParent(this);
        addChild(child);
        return child;
    }
    
    /**
     * Remove a child from the current node.
     * @param child 
     */
    public void removeChild(Tree<E> child) {
        children.remove(child);
        child.setParent((Tree<E>) null);
    }
    
    /**
     * Return all children of this node. 
     * @return an unmodifiable set containing the children
     */
    public Set<Tree<E>> getChildren() {
        return Collections.unmodifiableSet(children);
    }
    
     /**
     * Return all siblings (child nodes of the same parent of this node. 
     * @return an unmodifiable set containing the siblings
     */
    public Set<Tree<E>> getSiblings() {
        if(parent == null)
            return null;
        
        return parent.getChildren();
    }
    
    /**
     * Change parrent of current tree.
     * @param parent 
     */
    public void setParent(Tree<E> parent) {
        if(this.parent != null) {
            this.parent.removeChild(this);
        }
        
        this.parent = parent;
        
        if(parent != null) {
            parent.addChild(this);
        }
    }
    
    /**
     * Create a new node and use it as parent of this node.
     * @param data 
     */
    public void setParent(E data) {
        Tree<E> parent = new Tree<>(data);
        parent.addChild(this);
        setParent(parent);
    }
    
    /**
     * Gives the parent of the current node.
     * @return <code>null</code> will be returned when this is a root node
     */
    public Tree<E> getParent() {
        return parent;
    }
    
    /**
     * @return <code>true</code> in case this node is the root of a tree
     */
    public boolean isRoot() {
        return (parent == null);
    }
    
    /**
     * Returns the data that is stored in this node.
     * @return 
     */
    public E getData() {
        return data;
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
     * @param potentialChild
     * @return 
     */
    public boolean isChildOf(Tree<E> parent) {
        if(parent == null)
            return false;
        
        return parent.equals(parent);
    }
    
    /**
     * Checks if the given node is a child of this node.
     * @param potentialChild
     * @return 
     */
    public boolean hasChild(Tree<E> potentialChild) {
        return children.contains(potentialChild);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        if(parent != null) {
            hash = 41 * hash + Objects.hashCode(this.parent.getData());
        }
        hash = 41 * hash + Objects.hashCode(this.children);
        hash = 41 * hash + Objects.hashCode(getData());
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
        final Tree<?> other = (Tree<?>) obj;
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }
    
    
}
