package org.ijsberg.iglu.util.io;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 */
public class FileWatcher {
    private final Path[] paths;
    private final Map<WatchKey, Path> keys;
    private final int timeToWaitBeforeHandlingFileEvent;
    private WatchService watchService;

    public FileWatcher(final int timeToWaitBeforeHandlingFileEvent, final File ... directories) {
        this.paths = convertDirLocToPath(directories);
        this.keys = new HashMap<WatchKey, Path>();
        this.timeToWaitBeforeHandlingFileEvent = timeToWaitBeforeHandlingFileEvent;
    }

    public FileWatcher(final int timeToWaitBeforeHandlingFileEvent, final String[] directories) {
        this.paths = convertDirLocToPath(directories);
        this.keys = new HashMap<WatchKey, Path>();
        this.timeToWaitBeforeHandlingFileEvent = timeToWaitBeforeHandlingFileEvent;
    }

    public static Path[] convertDirLocToPath(String[] directories) {

        List<Path> paths = new ArrayList<Path>();
        for(String directory : directories) {
            paths.add(Paths.get(directory));
        }
        return paths.toArray(new Path[0]);
    }

    public static Path[] convertDirLocToPath(File[] directories) {

        List<Path> paths = new ArrayList<Path>();
        for(File directory : directories) {
            if(directory.isDirectory()) {
                paths.add(Paths.get(directory.getPath()));
            }
        }
        return paths.toArray(new Path[0]);
    }

    /**
     * Pass the WatcherHandler and start the monitoring the watch directory
     */
    public void startWatcher(final WatcherHandler watcherHandler) {
        try {
            watchService = FileSystems.getDefault()
                    .newWatchService();
            for (final Path path : paths) {
                final WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                listedDirectories.add(path.toFile());
//                System.out.println("WatcherService is registered to path: " + path.toAbsolutePath());
                keys.put(key, path);
            }
        } catch (final IOException e) {
            throw new RuntimeException("unable to start directory watcher", e);
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    watch(watcherHandler);
                } catch (IOException e) {
                    System.out.println("Error while starting the watcher. ");
                } catch(InterruptedException e) {
                    System.out.println("Error while starting the watcher. ");
                }
                System.out.println("Stopped watching");
            }
        };
        final Thread thread = new Thread(runnable);
        thread.setName("Watcher-Thread");
        thread.start();
    }

    public void stopWatcher() {
        System.out.println("Stopping " + this.getClass()
                .getSimpleName());

//        new Exception().printStackTrace();

        try {
            watchService.close();
        } catch (final IOException e) {
            System.out.println("Error while closing watch service");
        }
        final Thread handlerThreadRef = this.handlerThread;
        if (handlerThreadRef != null && handlerThreadRef != Thread.currentThread()) {
            try {
                handlerThread.join();
            } catch (final InterruptedException e) {
                System.out.println("HandlerThread interrupted");
            }
        }
        System.out.println("Stopped " + this.getClass()
                .getSimpleName());
    }

    private Thread handlerThread;

    private void watch(final WatcherHandler watcherHandler) throws IOException, InterruptedException {

        boolean valid = true;
        WatchKey watchKey;
        while (valid) {
            try {
                watchKey = watchService.take();
                handleWatchEvents(watchKey, watcherHandler);
                valid = watchKey.reset();
            } catch (final ClosedWatchServiceException e) {
                System.out.println("Watch service closed");
                valid = false;
            }
        }
    }

    private void handleWatchEvents(final WatchKey watchKey, final WatcherHandler watcherHandler) {
        doHandleWatchEvents(watchKey, watcherHandler);
    }


    private class FileToHandle {
        private File file;
        WatchEvent.Kind<?> eventKind;

        public FileToHandle(File file, WatchEvent.Kind<?> eventKind) {
            this.file = file;
            this.eventKind = eventKind;
        }
    }


    private final LinkedHashMap<String, FileToHandle> filesToHandle = new LinkedHashMap<String, FileToHandle>();
    private final HashSet<File> listedDirectories = new HashSet<File>();

    private void doHandleWatchEvents(final WatchKey watchKey, final WatcherHandler watcherHandler) {

        final Path dir = keys.get(watchKey);

        for (final WatchEvent<?> watchEvent : watchKey.pollEvents()) {
            final WatchEvent.Kind<?> kind = watchEvent.kind();
            if (kind == OVERFLOW) {
                continue;
            }
            final WatchEvent<Path> event = cast(watchEvent);
            final String fileName = dir.resolve(event.context())
                    .toString();
            File file = new File(fileName);
            synchronized(filesToHandle) {
                filesToHandle.put(fileName + "-" + kind.name(), new FileToHandle(file, kind));
            }
            handleFilesAsync(watcherHandler, filesToHandle, listedDirectories);
        }
    }

    private void handleFilesAsync(final WatcherHandler watcherHandler, final Map<String, FileToHandle> filesToHandle, final Set<File> modifiedDierctories) {
        handlerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeToWaitBeforeHandlingFileEvent);
                    List<FileToHandle> filesToHandleCopy;
                    synchronized(filesToHandle) {
                        filesToHandleCopy = new ArrayList(filesToHandle.values());
                        filesToHandle.clear();
                    }
                    for(FileToHandle fileToHandle : filesToHandleCopy) {
                        if (fileToHandle.eventKind == ENTRY_MODIFY) {
                            if(fileToHandle.file.isDirectory()) {
                                //watcherHandler.onDirectoryDeleted(fileToHandle.file);
                                listedDirectories.add(fileToHandle.file);
                            } else {
                                if(fileToHandle.file.exists()) {
                                    watcherHandler.onFileModified(fileToHandle.file);
                                }
                            }
                        } else if (fileToHandle.eventKind == ENTRY_CREATE) {
                            if(fileToHandle.file.isDirectory()) {
                                watcherHandler.onDirectoryCreated(fileToHandle.file);
                                listedDirectories.add(fileToHandle.file);
                            } else {
                                watcherHandler.onFileCreated(fileToHandle.file);
                            }
                        } else if (fileToHandle.eventKind == ENTRY_DELETE) {
                            if(modifiedDierctories.remove(fileToHandle.file)) {
                                // can no longer be seen
                                watcherHandler.onDirectoryDeleted(fileToHandle.file);
                            } else {
                                watcherHandler.onFileDeleted(fileToHandle.file);
                            }
                        }
                    }
                } catch (final InterruptedException e) {
                    System.out.println(e.getMessage());
                } catch (final Throwable t) {
                    System.out.println("unable to handle file event");
                    if (t instanceof Error) {
                        throw (Error) t;
                    }
                }
                handlerThread = null;
            }
        });
        handlerThread.start();
    }


    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(final WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
}