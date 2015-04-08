package at.deder.ybr.structures;

import java.util.Objects;

/**
 * This tree stores any kind of object.
 * 
 * @author lycis
 */
public class ObjectTree<E> extends Tree{
    private E data = null;
    
    public ObjectTree(E data) {
        super();
        this.data = data;
    }
    
    public ObjectTree(E data, Tree parent) {
        super(parent);
        this.data = data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public E getData() {
        return data;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.children);
        hash = 42 * hash + Objects.hashCode(this.data);
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
        @SuppressWarnings("rawtypes")
		final ObjectTree other = (ObjectTree) obj;
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
