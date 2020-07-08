package floydwarshall.saveload;

import java.io.File;
import java.io.FileWriter;

public class GraphWriter {
    public boolean createFileWithGraphData(String path, String data) throws Exception {
        boolean isFileCreated = false;
        File file = new File(path);
        //if (file.createNewFile()) {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
            isFileCreated = true;
        //}
        return isFileCreated;
    }
}
