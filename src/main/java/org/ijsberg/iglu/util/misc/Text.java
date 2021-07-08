package org.ijsberg.iglu.util.misc;

import java.util.List;

public class Text {

    private List<Line> lines;

    public Text(List<Line> lines) {
        this.lines = lines;
    }

    public Text getSubText(int fromIndex, int toIndex) {
        List<Line> subLines = lines.subList(fromIndex, toIndex);
        return new Text(subLines);
    }

    public String toString() {
        StringBuffer retval = new StringBuffer();
        for(Line line : lines) {
            retval.append(line.getLine() + "\n");
        }
        return retval.toString();
    }

}
