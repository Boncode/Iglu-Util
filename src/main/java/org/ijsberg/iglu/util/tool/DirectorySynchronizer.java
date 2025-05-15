package org.ijsberg.iglu.util.tool;

import org.ijsberg.iglu.util.ResourceException;
import org.ijsberg.iglu.util.formatting.NumberFormatter;
import org.ijsberg.iglu.util.io.FSFileCollection;
import org.ijsberg.iglu.util.io.FileCollectionComparison;
import org.ijsberg.iglu.util.io.FileFilterRuleSet;
import org.ijsberg.iglu.util.io.FileSupport;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class DirectorySynchronizer {

    private String sourceDir;
    private String targetDir;

    private FSFileCollection sourceCollection;
    private FSFileCollection targetCollection;

    private FileFilterRuleSet filter;

    private FileCollectionComparison comparison;

    public DirectorySynchronizer(String sourceDir, String targetDir) {
        this(sourceDir, targetDir, new FileFilterRuleSet());
    }

    public DirectorySynchronizer(String sourceDir, String targetDir, FileFilterRuleSet filter) throws ResourceException {

        FileSupport.assertDirExists(new File(sourceDir));
        FileSupport.assertDirExists(new File(targetDir));

        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        this.filter = filter;

        sourceCollection = new FSFileCollection(sourceDir, filter);
        targetCollection = new FSFileCollection(targetDir, filter);
    }

    public void compareSourceToTarget() throws IOException {
        comparison = FileCollectionComparison.compare(sourceCollection, targetCollection);
    }

    public String getDescription() {
        return "directory synchronizer from " + sourceDir + " to " + targetDir + " using filter " + filter;
    }


    public void synchronizeSourceToTargetByApproval() throws IOException {

        boolean doAll = false;
        System.out.println("" + comparison.getFilesMissing().size() + " file(s) missing");
        for (String fileName : comparison.getFilesMissing().keySet()) {
            String copyQuestion = printFileData(fileName, sourceCollection) + " copy ? y(es) / n(o) / A(ll)";
            doAll = processCopyQuestion(doAll, fileName, copyQuestion);
        }
        doAll = false;
        System.out.println("" + comparison.getFilesOutdated().size() + " file(s) outdated");
        for (String fileName : comparison.getFilesOutdated().keySet()) {
            String copyQuestion = printFileData(fileName, sourceCollection) + " is newer than " + printFileData(fileName, targetCollection) + " copy ? y(es) / n(o) / A(ll)";
            doAll = processCopyQuestion(doAll, fileName, copyQuestion);
        }
    }

    public void synchronizeSourceToTarget() throws IOException {

        System.out.println("copying " + comparison.getFilesMissing().size() + " missing file(s)");
        for (String fileName : comparison.getFilesMissing().keySet()) {
            copyFile(fileName);
        }
        System.out.println("copying " + comparison.getFilesOutdated().size() + " outdated file(s)");
        for (String fileName : comparison.getFilesOutdated().keySet()) {
            copyFile(fileName);
        }
    }

    private boolean processCopyQuestion(boolean doAll, String fileName, String copyQuestion) throws IOException {
        if (!doAll) {
            System.out.print(copyQuestion);
            String input = getUserInput();
            if (input.startsWith("A")) {
                copyFile(fileName);
                doAll = true;
            } else if (input.startsWith("y")) {
                copyFile(fileName);
            }
        } else {
            copyFile(fileName);
        }
        return doAll;
    }

    private void copyFile(String fileName) throws IOException {
        System.out.println("copying " + printFileData(fileName, sourceCollection));
        sourceCollection.copyWithDateTo(fileName, targetCollection);
    }

    private String getUserInput() throws IOException {
        Scanner in = new Scanner(System.in);
        return in.next();
    }

    private String printFileData(String fileName, FSFileCollection collection) throws IOException {
        File fileA = collection.getActualFileByName(fileName);
        return "file " + fileName + " " + new Date(fileA.lastModified()) + " " + convertToReadableByteSize(fileA.length());

    }

    public static String convertToReadableByteSize(long size) {

        NumberFormatter numberFormatter = new NumberFormatter('.',',');
        if(size < 1024 * 1024) {
            double sizeInK = (1.0 * size) / 1024;
            return numberFormatter.format(sizeInK, 0) + "k";
        }
        double sizeInMb = (1.0 * size) / (1024 * 1024);
        return numberFormatter.format(sizeInMb, 2) + " Mb";
    }
}
