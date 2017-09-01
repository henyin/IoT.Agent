package com.ydttech.iot.util.pathwatch;

import java.nio.file.WatchKey;

/**
 * Callback interface for a PathWatchUtil client
 */
public interface PathWatchEventListener {

    /**
     * Called when a file is created
     *
     * @param watchKey
     * @param rootPath
     * @param name
     */
    void fileCreated(WatchKey watchKey, String rootPath, String name);

    /**
     * Called when a file is deleted
     *
     * @param watchKey
     * @param rootPath
     * @param name
     */
    void fileDeleted(WatchKey watchKey, String rootPath, String name);

    /**
     * Called when a file is modified
     *
     * @param watchKey
     * @param rootPath
     * @param name
     */
    void fileModified(WatchKey watchKey, String rootPath, String name);
}
