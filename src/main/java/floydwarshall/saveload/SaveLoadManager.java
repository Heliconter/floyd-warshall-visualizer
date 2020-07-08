package floydwarshall.saveload;

public class SaveLoadManager {

    private GraphReader graphReader;
    private GraphWriter graphWriter;

    public SaveLoadManager() {
        graphReader = new GraphReader();
        graphWriter = new GraphWriter();
    }

    public boolean saveGraphInFile(String path, String date) {
        boolean isCreateFile = false;
        try {
            isCreateFile = graphWriter.createFileWithGraphData(path, date);
        } catch (Exception ignored) {

        }
        return isCreateFile;
    }

    public GraphInform loadGraphFromFile(String path) {
        GraphInform graphInform = null;
        try {
            graphInform = graphReader.readGrpahFromFile(path);
            if (!graphInform.isDataReadCorrect()) graphInform = null;
        } catch (Exception ignored) {
            graphInform = null;
        }
        return graphInform;
    }

}
