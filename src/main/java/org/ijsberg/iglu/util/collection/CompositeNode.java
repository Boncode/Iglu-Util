package org.ijsberg.iglu.util.collection;

import org.ijsberg.iglu.util.misc.StringSupport;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CompositeNode<T> {

    private T reflectedObject;
    private String name;
    private List<CompositeNode<T>> referencedNodes;


    public CompositeNode(T reflectedObject, String name) {
        this.reflectedObject = reflectedObject;
        this.name = name;
    }

    public void addReferencedNode(CompositeNode<T> referencedNode) {
        if(referencedNodes == null) {
            referencedNodes = new ArrayList<>();
        }
        referencedNodes.add(referencedNode);
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
        out.println((referencedNodes == null ? ". " : "- ") + name);
        if(referencedNodes != null) {
            for(CompositeNode compositeNode : referencedNodes) {
                compositeNode.print(out, depth + 1);
            }
        }
    }
}
