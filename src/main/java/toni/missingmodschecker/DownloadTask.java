package toni.missingmodschecker;

import javax.swing.*;
import java.util.function.Consumer;

public class DownloadTask extends SwingWorker<Void, Void> {

    private final Runnable whenDone;
    private final Consumer<String> logger;

    public DownloadTask(Consumer<String> logger, Runnable whenDone) {
        this.logger = logger;
        this.whenDone = whenDone;
    }

    @Override
    protected void done() {
        whenDone.run();
    }

    @Override
    protected Void doInBackground() {
        try {
            MissingModsChecker.downloadAndInstall(logger);
        } catch (Exception e) {
            logger.accept("Download failed!");
            MissingModsChecker.LOGGER.error("Download failed", e);
        }
        return null;
    }
}