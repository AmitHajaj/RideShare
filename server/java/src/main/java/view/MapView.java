package view;

import model.Drive;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import model.RoadMap;
import model.Node;

import java.util.*;
import java.util.stream.Collectors;

public class MapView {
    private RoadMap map;
    private Graph displayGraph;
    private List<Drive> onGoingDrives;
    private HashSet<org.graphstream.graph.Node> pedestrian;
    private RealTimeEvents events;
    private double timeSpeed;

    private static MapView instance = new MapView();

    private MapView() {
        displayGraph = new MultiGraph("map simulation");
        map = RoadMap.getInstance();
        onGoingDrives = new ArrayList<>();
        events = new RealTimeEvents(null, timeSpeed);
    }

    public static MapView getInstance() {
        return instance;
    }

//    public List<Drive> initDrives() {
//        List<Drive> drives = new ArrayList<>();
//        Calendar cl = Calendar.getInstance();
//        Random rand = new Random();
//        Date initializeDate = new Date();
//        List<Node> nodes = RoadMap.getInstance().getNodes().values().stream().collect(Collectors.toList());
//        for (int i = 0; i < 50; i++) {
//            Node src = nodes.get(rand.nextInt(nodes.size() - 1));
//            Node dst = nodes.get(rand.nextInt(nodes.size() - 1));
//
//            new Drive();
//        }
//        Calendar.SECOND
//        new Drive()
//        return onGoingDrives;
//    }

    public void show(){
        // draw map components
        Viewer viewer = new Viewer(displayGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        ViewerPipe pipeIn = viewer.newViewerPipe();
        viewer.addView("view1", new J2DGraphRenderer());
        viewer.disableAutoLayout();
        viewer.getView("view1").setMouseManager(new CustomMouseManager());

        pipeIn.addAttributeSink( displayGraph );
        pipeIn.pump();

        drawMapComponents();

        displayGraph.setAttribute("ui.stylesheet", styleSheet);
        displayGraph.setAttribute("ui.quality");
        displayGraph.setAttribute("ui.antialias");

        while(true){
            pipeIn.pump();

            sleep(1000);

            getUpdates();

            moveCars();

//            drawPedestrian();
        }
    }


    private void getUpdates(){
        List<Drive> startedEvents = events.getStartedEvents();
        onGoingDrives.addAll(startedEvents);
    }

    private void moveCars(){
        onGoingDrives.forEach( drive ->{
            model.Edge currentEdge = drive.getCurrentEdge();
            org.graphstream.graph.Node node;

            if(currentEdge != null){
                node = displayGraph.getNode(currentEdge.getStartNode().getOsmID().toString());
                node.removeAttribute("ui.class");
                node = displayGraph.getNode(currentEdge.getEndNode().getOsmID().toString());
                node.addAttribute("ui.class", "car");
            }else{
                onGoingDrives.remove(drive);
            }
        });
    }

    private void sleep(long ms ) {
        try { Thread.sleep( ms ) ; }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    private Boolean drawMapComponents(){
        map.getEdges().forEach(e -> {
            if(displayGraph.getEdge(e.getId()) == null) {
                org.graphstream.graph.Node start = drawNode(e.getStartNode());
                org.graphstream.graph.Node end = drawNode(e.getEndNode());
                Edge edge = displayGraph.addEdge(e.getId(), start, end);//, e.isDirected());

                //            if(e.isDirected())
                //                edge.setAttribute("ui.style", "arrow-size: 1px;");
            }

        });
        return true;
    }

    private org.graphstream.graph.Node drawNode(Node node){

        String keyStr = String.valueOf(node.getOsmID());
        org.graphstream.graph.Node displayNode = displayGraph.getNode(keyStr);

        if(displayNode == null){

            displayNode = displayGraph.addNode(keyStr);
            displayNode.setAttribute("xy", node.getLongitude(), node.getLatitude());
            displayNode.setAttribute("ui.label", node.getOsmID().toString());
//            if(node.getOsmID() == 2432701015l || node.getOsmID() == 1671579963l){
//                displayNode.setAttribute("ui.style", "z-index: 2; size: 10px; fill-color: green;");
//            }else
//            if(node.getType() == Node.userType.Passenger){
//                displayNode.setAttribute("ui.class", "passenger");
//            } else if(node.getType() == Node.userType.Driver) {
//                displayNode.setAttribute("ui.class", "car");
//            }
        }

        return displayNode;
    }


    private String styleSheet=
            "node {"+
                " text-mode: hidden;"+
                " z-index: 1;"+
                " fill-color: grey;"+
                " size: 3px;"+
            "}"+
            "node.car {"+
                " z-index: 2;"+
                " fill-color: blue;"+
                " size: 10px;"+
            "}"+
            "node.passenger {"+
                " fill-color: red;"+
                " size: 10px;"+
            "}";
}

/**
 * "fill-mode" ...
 *     "fill-color" ...
 *     "fill-image" ...
 *     "stroke-mode" ...
 *     "stroke-color" ...
 *     "stroke-width" ...
 *     "shadow-mode" ...
 *     "shadow-color" ...
 *     "shadow-width" ...
 *     "shadow-offset" ...
 *     "text-mode" ...
 *     "text-color" ...
 *     "text-style" ...
 *     "text-font" ...
 *     "text-size" ...
 *     "text-visibility-mode" ...
 *     "text-visibility" ...
 *     "text-background-mode" ...
 *     "text-background-color" ...
 *     "text-offset" ...
 *     "text-padding" ...
 *     "icon-mode" ...
 *     "icon" ...
 *     "padding" ...
 *     "z-index" ...
 *     "visibility-mode" ...
 *     "visibility" ...
 *     "shape" ...
 *     "size" ...
 *     "size-mode" ...
 *     "shape-points" ...
 *     "text-alignment" ...
 *     "jcomponent" ...
 *     "arrow-image" ...
 *     "arrow-size" ...
 *     "arrow-shape" ...
 *     "sprite-orientation" ...
 *     "canvas-color" ...
 */