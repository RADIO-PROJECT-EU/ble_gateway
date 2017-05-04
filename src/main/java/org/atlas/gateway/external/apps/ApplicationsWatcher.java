package org.atlas.gateway.external.apps;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.StandardWatchEventKinds.*;

public class ApplicationsWatcher {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationsWatcher.class);
	
	public ApplicationsWatcher(ApplicationsHandler hander, Path path){
		WatchService myWatcher = null;
		try {
			myWatcher = path.getFileSystem().newWatchService();
			MyWatchQueueReader fileWatcher = new MyWatchQueueReader(myWatcher, hander);
			Thread th = new Thread(fileWatcher, "FileWatcher");
	        th.start();
	        path.register(myWatcher, ENTRY_CREATE,ENTRY_DELETE); 
		} catch (IOException e) {
			logger.error("(IOException) Unable to start watcher for the applications.");
		}
		
	}
	
	private static class MyWatchQueueReader implements Runnable {
		 
        /** the watchService that is passed in from above */
        private WatchService myWatcher;
        private ApplicationsHandler handler;
        public MyWatchQueueReader(WatchService myWatcher, ApplicationsHandler handler) {
            this.myWatcher = myWatcher;
            this.handler = handler;
        }
 
        /**
         * In order to implement a file watcher, we loop forever 
         * ensuring requesting to take the next item from the file 
         * watchers queue.
         */
        @Override
        public void run() {
            try {
                WatchKey key = myWatcher.take();
                while(key != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                    	WatchEvent.Kind<?> kind = event.kind();
                    	WatchEvent<Path> ev = (WatchEvent<Path>)event;
                        Path filename = ev.context();
                    	
                       if( filename.getFileName().toString().endsWith(".jar") ){//TODO to be changed to check the content type of the File.
                        	if( kind == ENTRY_DELETE ){
                        		this.handler.shutdownApp(filename.getFileName().toString());
                        	}else if( kind == ENTRY_CREATE ){
                        		this.handler.restartApplication(filename.getFileName().toString());
                        	}
                        }
                    }
                    key.reset();
                    key = myWatcher.take();
                }
            } catch (InterruptedException e) {
            	logger.error("(MyWatchQueueReader)Unable to start watcher for the applications(Thread).",e);
            }
        }
    }
	
}
