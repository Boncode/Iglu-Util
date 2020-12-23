package org.ijsberg.iglu.util.collection;

import org.ijsberg.iglu.util.misc.StringSupport;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class CompositeNode<T> {

    private T reflectedObject;
    private String name;
    private List<CompositeNode<T>> referencedNodes;
    private CompositeNode<T> superNode;


    public CompositeNode(T reflectedObject, String name) {
        this.reflectedObject = reflectedObject;
        this.name = name;
    }

    public void addReferencedNode(CompositeNode<T> referencedNode) {
        if(referencedNodes == null) {
            referencedNodes = new ArrayList<>();
        }
        referencedNodes.add(referencedNode);
        referencedNode.setSuperNode(this);
    }

    private void setSuperNode(CompositeNode<T> superNode) {
        this.superNode = superNode;
    }

    public int size() {
        return referencedNodes == null ? 0 : referencedNodes.size();
    }


    public void print(PrintStream out) {
        print(out, 0);
    }

    protected void print(PrintStream out, int depth) {
        out.print(new String(StringSupport.createCharArray(depth, ' ')));
        //out.print(depth);
        out.println((referencedNodes == null ? ". " : "- ") + name + ":" + reflectedObject.getClass().getSimpleName());
        if(referencedNodes != null) {
            for(CompositeNode compositeNode : referencedNodes) {
                compositeNode.print(out, depth + 1);
            }
        }
    }

    public String toString() {
        return name + ":" + reflectedObject.getClass().getSimpleName();
    }

    public List<CompositeNode<T>> getLeafs() {
        List<CompositeNode<T>> retval = new ArrayList<>();
        if(referencedNodes == null) {
            retval.add(this);
        } else {
            for(CompositeNode<T> referencedNode : referencedNodes) {
                retval.addAll(referencedNode.getLeafs());
            }
        }
        return retval;
    }

    public List<CompositeNode<T>> getLeafs(Class<?> type) {
        List<CompositeNode<T>> retval = new ArrayList<>();
        if(referencedNodes != null) {
            for(CompositeNode<T> referencedNode : referencedNodes) {
                retval.addAll(referencedNode.getLeafs(type));
            }
        }
        if(retval.isEmpty()) {
            if(type.isAssignableFrom(reflectedObject.getClass())) {
                retval.add(this);
            }
        }
        return retval;
    }

    public List<CompositeNode<T>> getPath() {
        List<CompositeNode<T>> retval = new ArrayList<>();
        if(superNode != null) {
            retval.addAll(superNode.getPath());
        }
        retval.add(this);
        return retval;
    }

    public T getReflectedObject() {
        return reflectedObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeNode)) return false;
        CompositeNode<?> that = (CompositeNode<?>) o;
        return reflectedObject.equals(that.reflectedObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reflectedObject);
    }
}
