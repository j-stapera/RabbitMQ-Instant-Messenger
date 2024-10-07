import java.io.IOException;
import java.nio.file.*;


// Code taken from: https://www.baeldung.com/java-nio2-watchservice
// and from: https://stackoverflow.com/questions/16251273/can-i-watch-for-single-file-change-with-watchservice-not-the-whole-directory
public class FileWatch  {
    private static final Path StreamListPath = Path.of("src\\main\\resources");
        FileWatch(Path filePath) throws IOException, InterruptedException {
            WatchService watchService
                    = FileSystems.getDefault().newWatchService();

            System.out.println(StreamListPath.toString());
            StreamListPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind()
                                    + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        }
    }