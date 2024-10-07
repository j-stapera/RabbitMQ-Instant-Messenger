import java.io.IOException;
import java.nio.file.*;


// Code taken from: https://www.baeldung.com/java-nio2-watchservice
public class FileWatch  {
        FileWatch(Path filePath) throws IOException, InterruptedException {
            WatchService watchService
                    = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(System.getProperty("user.home"));
            System.out.println(path.toString());
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
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