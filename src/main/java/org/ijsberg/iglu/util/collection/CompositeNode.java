package org.ijsberg.iglu.util.collection;

import org.ijsberg.iglu.util.misc.StringSupport;

import java.io.PrintStream;
import java.util.*;

public class CompositeNode<O,E> {

    private O reflectedObject;
    private String name;
    private List<CompositeNode<O,E>> referencedNodes;
    private CompositeNode<O,E> referringNode;
    private O loopEntry;

  //  private LinkedHashMap<CompositeNode<T>,>

    public CompositeNode(O reflectedObject, String name) {
        this.reflectedObject = reflectedObject;
        this.name = name;
    }

    public void addReferencedNode(CompositeNode<O,E> referencedNode) {
        if(referencedNodes == null) {
            referencedNodes = new ArrayList<>();
        }
        referencedNodes.add(referencedNode);
        referencedNode.setReferringNode(this);
    }

    private void setReferringNode(CompositeNode<O,E> referringNode) {
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

    public List<CompositeNode<O,E>> getLeafs() {
        List<CompositeNode<O,E>> retval = new ArrayList<>();
        if(referencedNodes == null || referencedNodes.isEmpty()) {
            retval.add(this);
        } else {
            for(CompositeNode<O,E> referencedNode : referencedNodes) {
                retval.addAll(referencedNode.getLeafs());
            }
        }
        return retval;
    }

    public List<CompositeNode<O,E>> getLeafs(Class<?> type) {
        List<CompositeNode<O,E>> retval = new ArrayList<>();
        if(referencedNodes != null) {
            for(CompositeNode<O,E> referencedNode : referencedNodes) {
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

    public List<CompositeNode<O,E>> getPath() {
        List<CompositeNode<O,E>> retval = new ArrayList<>();
        if(referringNode != null) {
            retval.addAll(referringNode.getPath());
        }
        retval.add(this);
        return retval;
    }

    public boolean isInPath(O someNode) {
        if(reflectedObject.equals(someNode)) {
            return true;
        } else {
            if(referringNode != null) {
                return referringNode.isInPath(someNode);
            }
        }
        return false;
    }

    public List<CompositeNode<O,E>> getAllFromTree() {
        List<CompositeNode<O,E>> retval = new ArrayList<>();
        if(referencedNodes != null) {
            retval.addAll(referencedNodes);
            for (CompositeNode<O,E> referencedNode : referencedNodes) {
                retval.addAll(referencedNode.getAllFromTree());
            }
        }
        return retval;
    }

    public List<CompositeNode<O,E>> getReferencedNodes() {
        return referencedNodes != null ? referencedNodes : Collections.EMPTY_LIST;
    }

    public List<O> getAllNodesInHierarchy() {
        List<O> retval = new ArrayList<>();
        retval.add(this.reflectedObject);
        if(referencedNodes != null) {
            for (CompositeNode<O,E> referencedNode : referencedNodes) {
                retval.add(referencedNode.reflectedObject);
                retval.addAll(referencedNode.getAllNodesInHierarchy());
            }
        }
        return retval;
    }

    public int getDepth() {
        return getDepth(new Matcher() {
            @Override
            public boolean matches(Object o) {
                return true;
            }
        });
    }

    public int getDepth(Matcher matcher) {
        int largestDepthFound = 0;
        if(referencedNodes != null) {
            for (CompositeNode<O,E> referencedNode : referencedNodes) {
                int referencedNodeDepth = referencedNode.getDepth(matcher);
                if(referencedNodeDepth > largestDepthFound) {
                    largestDepthFound = referencedNodeDepth;
                }
            }
        }
        return largestDepthFound + (matcher.matches(this.reflectedObject) ? 1 : 0);
    }

    public List<CompositeNode<O,E>> getPathUntilExaminedNode(O entryNode) {
        List<CompositeNode<O,E>> retval = new ArrayList<>();
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
    public O getReflectedObject() {
        return reflectedObject;
    }

    public void setLoopEntry(O loopEntry) {
        this.loopEntry = loopEntry;
    }

    public O getLoopEntry() {
        return this.loopEntry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeNode)) return false;
        CompositeNode<?,?> that = (CompositeNode<?,?>) o;
        return reflectedObject.equals(that.reflectedObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reflectedObject);
    }

/*    public void addReferencedNodes(List<CompositeNode<T>> referencedNodes) {
        referencedNodes.addAll(referencedNodes);
    }*/
}
