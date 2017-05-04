package org.atlas.wsn.ble.util;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothProcessUtil {

    private static final Logger logger = LoggerFactory.getLogger(BluetoothProcessUtil.class);

    private static final ExecutorService processExecutor = Executors.newSingleThreadExecutor();

    public static BluetoothSafeProcess exec(String command) throws IOException {
        // Use StringTokenizer since this is the method documented by Runtime
        StringTokenizer st = new StringTokenizer(command);
        int count = st.countTokens();
        String[] cmdArray = new String[count];

        for (int i = 0; i < count; i++) {
            cmdArray[i] = st.nextToken();
        }

        return exec(cmdArray);
    }

    public static BluetoothSafeProcess exec(final String[] cmdarray) throws IOException {
        // Serialize process executions. One at a time so we can consume all streams.
        Future<BluetoothSafeProcess> futureSafeProcess = processExecutor.submit(new Callable<BluetoothSafeProcess>() {

            @Override
            public BluetoothSafeProcess call() throws Exception {
                Thread.currentThread().setName("SafeProcessExecutor");
                BluetoothSafeProcess safeProcess = new BluetoothSafeProcess();
                safeProcess.exec(cmdarray);
                return safeProcess;
            }
        });

        try {
            return futureSafeProcess.get();
        } catch (Exception e) {
            logger.error("Error waiting from SafeProcess output", e);
            throw new IOException(e);
        }
    }

    /**
     * @deprecated The method does nothing
     */
    @Deprecated
    public static void close(BluetoothSafeProcess proc) {
    }

    public static void destroy(BluetoothSafeProcess proc) {
        proc.destroy();
    }
}
