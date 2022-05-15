import Gui.MapView;
import crosby.binary.osmosis.OsmosisReader;
import org.apache.commons.math3.geometry.Point;
import osmProcessing.OGraph;
import osmProcessing.Parser;
import osmProcessing.Reader;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
//32.101267 35.2040791

public class App {
    private static Map<Long, Double[]> Riders = new HashMap<>();
    public static void main(String[] args) {

//        String filepath = ExtractMap.chooseFile();
//        CreateGraph(filepath);
        CreateGraph("data/arielpbf.pbf");
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


    public static void CreateGraph(String pathToPBF) {
        InputStream inputStream;

        try {
            inputStream = new FileInputStream(pathToPBF);

            // read from osm pbf file:
            Point2D.Double mapBounds = new Point2D.Double(31.99721786061472, 34.72968352639412);

            Reader custom = new Reader();
            custom.setMapBounds(mapBounds);
            OsmosisReader reader = new OsmosisReader(inputStream);

            custom.removeTertiary();

            reader.setSink(custom);

            // initial parsing of the .pbf file:
            reader.run();

            // get riders
            addRiders();

            // create graph:
            OGraph graph = OGraph.getInstance();
            HashSet<String> boundTags = Reader.getBoundTags(), nodeTags = Reader.getNodeTags(), wayTags = Reader.getWayTags();

            // secondary parsing of ways/creation of edges:
            Parser.parseMapWays(custom.ways, custom.MapObjects);

            for (Map.Entry<Long, Double[]> entry: Riders.entrySet()) {
                graph.addRiderNode(entry.getKey(), entry.getValue());
            }
            //TODO add data to map
            MapView.getInstance().run();

            //TODO add algorithms here
            System.out.println(graph.toString());

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "File not found!", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            // exit after error //
            System.exit(0);
        }
    }
}
