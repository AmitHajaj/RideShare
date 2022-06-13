import controller.algorithms.GraphAlgo;
import controller.rds.jsonHandler;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;
import view.MapView;
import controller.GraphUtils;
import controller.rds.addGraphToDB;
import crosby.binary.osmosis.OsmosisReader;
import model.OGraph;
import model.ONode;
import controller.osmProcessing.*;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

//osmconvert64.exe ariel2.osm > arielpbf.pbf --out-pbf

public class App {
    private static Map<Long, Double[]> Riders = new HashMap<>();
    private static List<Object> pathNodesID = new ArrayList<>();


    public static void main(String[] args) {
//        String filepath = ExtractMap.chooseFile();
//        CreateGraph(filepath);
        CreateGraph("server/java/data/israel.pbf");
    }

    static Point2D.Double topRight = new Point2D.Double(32.10070229573369, 34.84550004660088), bottomLeft = new Point2D.Double(32.10070229573369, 34.84550004660088);

    public static void CreateGraph(String pathToPBF) {
        InputStream inputStream;

        try {
            inputStream = new FileInputStream(pathToPBF);

            // read from osm pbf file:
            Reader custom = new Reader();
            OsmosisReader reader = new OsmosisReader(inputStream);

            reader.setSink(custom);

            // initial parsing of the .pbf file:
            reader.run();

            // secondary parsing of ways/creation of edges:
            Parser.parseMapWays(custom.ways, custom.MapObjects);

            // get riders & drivers
            OGraph graph = OGraph.getInstance();

            GraphAlgo.removeNodesThatNotConnectedTo(graph.getNode(2432701015l));

            System.out.println(graph);

            /** show graph */
            MapView.getInstance().run();


        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "File not found!", "ERROR",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
    }

    /**
     * TODO get riders from database SQL - Motti
     */
    private static void addRiders(){
        Riders.put(-1L, new Double[]{32.10590091133335, 35.192002782079705});
        Riders.put(-2L, new Double[]{32.10569527212209, 35.20159203268953});
        Riders.put(-3L, new Double[]{32.100656966794055, 35.20341277647622});
        Riders.put(-4L, new Double[]{32.10551550801045, 35.1700032533693});
    }

    /**
     * TODO get driver's path from database SQL - Motti
     */
    private static List<Object> setDriversPath(){
        pathNodesID.add(432349397l);
        pathNodesID.add(5329510675l);
        pathNodesID.add(985633358l);
        pathNodesID.add(257392128l);
        pathNodesID.add(5224948678l);
        pathNodesID.add(5177072076l);
        return pathNodesID;
    }

    private static String chooseFile() {
        // JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        JFileChooser jfc = new JFileChooser("server/java/data");
        jfc.setDialogTitle("Select .osm.pbf file to read");
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}
// TODO set & get graph user drive | sub graph | match rider and drivers | lock drive