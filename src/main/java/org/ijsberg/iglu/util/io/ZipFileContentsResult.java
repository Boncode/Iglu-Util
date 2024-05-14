package org.ijsberg.iglu.util.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;

public class ZipFileContentsResult {

    private final Set<ZipEntry> zipEntries = new HashSet<>();
    private final List<Exception> exceptions = new ArrayList<>();

    public Set<ZipEntry> getZipEntries() {
        return zipEntries;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void addZipEntry(ZipEntry zipEntry) {
        zipEntries.add(zipEntry);
    }

    public void addException(Exception e) {
        exceptions.add(e);
    }
}
