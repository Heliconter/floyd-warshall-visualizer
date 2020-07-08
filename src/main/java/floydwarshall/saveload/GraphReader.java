package floydwarshall.saveload;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

class GraphReader {

    private Pattern nodeRegex = Pattern.compile("^\\(\\D\\)$");
    private Pattern edgeRegex = Pattern.compile("^\\(\\D,\\D,\\d+\\)$");

    GraphInform readGrpahFromFile(String path) throws Exception {
        int countNodes = 0;
        int countEdges = 0;
        boolean isDataReadCorrect = false;
        ArrayList<NodeInform> localNodes = new ArrayList<>();
        ArrayList<EdgeInfrom> localEdges = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            countNodes = readCountNodes(bufferedReader.readLine());
            for (int i = 0; i < countNodes; i++) {
                localNodes.add(readNode(bufferedReader.readLine(), nodeRegex));
            }
            countEdges = readCountLines(bufferedReader.readLine());
            for (int i = 0; i < countEdges; i++) {
                localEdges.add(readEdge(bufferedReader.readLine(), edgeRegex));
            }
            if (bufferedReader.readLine() != null) throw new ExcRead("Extra data in file");
            bufferedReader.close();
            isDataReadCorrect = true;
        } catch (java.io.FileNotFoundException | ExcRead ex) {
            /*if (ex instanceof ExcRead) {
                ((ExcRead) ex).myOwnExceptionMsg();
            } else {
                System.out.println("File not found");
            }*/
            localEdges = null;
            localNodes = null;
        }

        return new GraphInform(isDataReadCorrect,localNodes,localEdges);
    }


    private int readCountNodes(String string) throws ExcRead {
        if (string != null && string.startsWith("Nodes:")) {
            string = string.replace("Nodes:", "");
            if (isDigitString(string) && Integer.parseInt(string) > 0) {
                return Integer.parseInt(string);
            }
        }
        throw new ExcRead("ReadCountNode exception");
    }

    private int readCountLines(String string) throws ExcRead {
        if (string != null && string.startsWith("Lines:")) {
            string = string.replace("Lines:", "");
            if (isDigitString(string) && Integer.parseInt(string) >= 0) {
                return Integer.parseInt(string);
            }
        }
        throw new ExcRead("ReadCountEdge exception");
    }

    private NodeInform readNode(String string, Pattern pattern) throws ExcRead {
        if (string != null && pattern != null && pattern.matcher(string).find()) {
            string = string.replace("(", "");
            string = string.replace(")", "");
//            String[] strings = string.split(",");
            if (Character.isLetter(string.charAt(0)) && string.length()==1/* && isDigitString(strings[1]) && isDigitString(strings[2])*/) {
                return new NodeInform(string.charAt(0)/*, Integer.valueOf(strings[1]), Integer.valueOf(strings[2])*/);
            }
        }
        throw new ExcRead("ReadNode exception");
    }

    private EdgeInfrom readEdge(String string, Pattern pattern) throws ExcRead {
        if (string != null && pattern != null && pattern.matcher(string).find()) {
            string = string.replace("(", "");
            string = string.replace(")", "");
            String[] strings = string.split(",");
            if (Character.isLetter(strings[0].charAt(0)) && Character.isLetter(strings[0].charAt(0)) && Integer.valueOf(strings[2]) >= 0) {
                return new EdgeInfrom(strings[0].charAt(0), strings[1].charAt(0), Integer.valueOf(strings[2]));
            }
        }
        throw new ExcRead("ReadEdge exception");
    }

    private boolean isDigitString(String string) {
        for (char sign : string.toCharArray()) {
            if (!Character.isDigit(sign)) {
                return false;
            }
        }
        return true;
    }

    public class ExcRead extends Exception {
        private String someString;

        ExcRead(String string) {
            this.someString = string;
        }

        void myOwnExceptionMsg() {
            System.err.println("Eception message: " + someString);
        }
    }
}



