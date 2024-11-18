package org.ijsberg.iglu.util.collection;

import org.ijsberg.iglu.util.misc.StringSupport;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CompositeNode<T> {

    private T reflectedObject;
    private String name;
    private List<CompositeNode<T>> referencedNodes;
    private CompositeNode<T> referringNode;
    private T loopEntry;

    public CompositeNode(T reflectedObject, String name) {
        this.reflectedObject = reflectedObject;
        this.name = name;
    }

    public void addReferencedNode(CompositeNode<T> referencedNode) {
        if(referencedNodes == null) {
            referencedNodes = new ArrayList<>();
        }
        referencedNodes.add(referencedNode);
        referencedNode.setReferringNode(this);
    }

    private void setReferringNode(CompositeNode<T> referringNode) {
        this.referringNode = referringNode;
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

    public String getName() {
        return name;
    }

    public List<CompositeNode<T>> getLeafs() {
        List<CompositeNode<T>> retval = new ArrayList<>();
        if(referencedNodes == null || referencedNodes.isEmpty()) {
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
        if(referringNode != null) {
            retval.addAll(referringNode.getPath());
        }
        retval.add(this);
        return retval;
    }

    public boolean isInPath(T someNode) {
        if(reflectedObject.equals(someNode)) {
            return true;
        } else {
            if(referringNode != null) {
                return referringNode.isInPath(someNode);
            }
        }
        return false;
    }

    public List<CompositeNode<T>> getAllFromTree() {
        List<CompositeNode<T>> retval = new ArrayList<>();
        if(referencedNodes != null) {
            retval.addAll(referencedNodes);
            for (CompositeNode<T> referencedNode : referencedNodes) {
                retval.addAll(referencedNode.getAllFromTree());
            }
        }
        return retval;
    }

    public List<CompositeNode<T>> getReferencedNodes() {
        return referencedNodes != null ? referencedNodes : Collections.EMPTY_LIST;
    }

    public List<T> getAllNodesInHierarchy() {
        List<T> retval = new ArrayList<>();
        retval.add(this.reflectedObject);
        if(referencedNodes != null) {
            for (CompositeNode<T> referencedNode : referencedNodes) {
                retval.add(referencedNode.reflectedObject);
                retval.addAll(referencedNode.getAllNodesInHierarchy());
            }
        }
        return retval;
    }

    public int getDepth() {
        int largestDepthFound = 0;
        if(referencedNodes != null) {
            for (CompositeNode<T> referencedNode : referencedNodes) {
                int referencedNodeDepth = referencedNode.getDepth();
                if(referencedNodeDepth > largestDepthFound) {
                    largestDepthFound = referencedNodeDepth;
                }
            }
        }
        return largestDepthFound + 1;
    }

    public List<CompositeNode<T>> getPathUntilExaminedNode(T entryNode) {
        List<CompositeNode<T>> retval = new ArrayList<>();
        if(referringNode != null) {
            if(referringNode.getReflectedObject().equals(entryNode)) {
                retval.add(referringNode);
            } else {
                retval.addAll(referringNode.getPathUntilExaminedNode(entryNode));
            }
        }
        retval.add(this);
        return retval;
    }
    public T getReflectedObject() {
        return reflectedObject;
    }

    public void setLoopEntry(T loopEntry) {
        this.loopEntry = loopEntry;
    }

    public T getLoopEntry() {
        return this.loopEntry;
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
