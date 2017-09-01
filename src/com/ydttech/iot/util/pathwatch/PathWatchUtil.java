package com.ydttech.iot.util.pathwatch;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class PathWatchUtil {

    private String pathName;
    private int watchTimer = 600;

    private Path directory;
    private WatchService watchService;
    private Thread selfthread = null;
    private boolean threadIsRunning = false;
    private PathWatchEventListener pathWatchEventListener;



    public PathWatchUtil(String pathName, WatchEvent.Kind<?> ... events) throws IOException {
        this.pathName = pathName;

        directory = FileSystems.getDefault().getPath(pathName);
        watchService = FileSystems.getDefault().newWatchService();

        directory.register(watchService, events);
    }

    public void activeEvent() {

        if (selfthread == null) {
            selfthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (threadIsRunning) {
                        WatchKey watchKey = null;
                        try {
                            watchKey = watchService.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            threadIsRunning = false;
                        }

                        for (WatchEvent watchEvent : watchKey.pollEvents()) {
                            WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                            Path filename = watchEventPath.context();
                            Path rootPath = directory.toAbsolutePath();

                            if (watchEvent.kind() == ENTRY_MODIFY) {
                                pathWatchEventListener.fileModified(watchKey, rootPath.toString(), filename.toString());
                            }
                            else if (watchEvent.kind() == ENTRY_CREATE) {
                                pathWatchEventListener.fileCreated(watchKey, rootPath.toString(), filename.toString());
                            }
                            else if (watchEvent.kind() == ENTRY_DELETE) {
                                pathWatchEventListener.fileDeleted(watchKey, rootPath.toString(), filename.toString());
                            } else {
                                continue;
                            }

                            boolean valid = watchKey.reset();
                            if (!valid) {
                                break;
                            }
                        }
                    }
                }
            });
            selfthread.setDaemon(true);
            threadIsRunning = true;
            selfthread.start();

        }
    }

    public void registerEvent(PathWatchEventListener pathWatchEventListener) {

        this.pathWatchEventListener = pathWatchEventListener;
    }

    public void standbyEvent() {
        threadIsRunning = false;
    }


}
