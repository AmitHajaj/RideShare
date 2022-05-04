package osmProcessing;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.*;

//osmconvert64.exe ariel2.osm > arielpbf.pbf --out-pbf
public class Reader implements Sink {
    // Variables for algorithms :)
    public ArrayList<OMapWay> ways = new ArrayList<>();
    // Reference by id:
    public Map<Long, MapObject> MapObjects = new HashMap<>();

    static int entityNum = 0;

    /**
     * Method called on each entity from original XML file
     * @param entityContainer incoming entity for procession
     * Method separates entities in different collections
     */
    public void process(EntityContainer entityContainer) {


        if (entityContainer instanceof NodeContainer) {
            MapObject temp;

            Node node = ((NodeContainer) entityContainer).getEntity();
            node.toString();

            // create node or add details if object was already created through ways (usually not the case, if .pbf file formatted correctly)
            if (MapObjects.get(node.getId()) != null) {
                temp = MapObjects.get(node.getId());
                temp.setLatitude(node.getLatitude());
                temp.setLongitude(node.getLongitude());
                temp.addAllTags(node.getTags());
            }
            else {
                temp = new MapObject(node.getLatitude(), node.getLongitude(), node.getId(), node.getTags());
                MapObjects.put(temp.getID(), temp);
            }

        // process your node //
        } else if (entityContainer instanceof WayContainer){
            Way way = ((WayContainer) entityContainer).getEntity();
            // you can filter ways/nodes //
            if (this.isAppropriate(way)) {

                OMapWay mway = new OMapWay(way.getId(), way.getTags());
                // process all nodes contained in the way //
                for(WayNode wn: way.getWayNodes()) {
                    MapObject temp;
                    // if object was already created through nodes:
                    if (MapObjects.get(wn.getNodeId()) != null) {
                        temp = MapObjects.get(wn.getNodeId());
                    }
                    else {
                        temp = new MapObject(wn.getNodeId());
                        MapObjects.put(temp.getID(), temp);
                    }
                    mway.addObject(temp);
                    // linkCounter of node counts on how many ways the node appears //
                    temp.linkCounter++;
                    temp.addWay(mway);
                }
                this.ways.add(mway);
            }
//            for (Tag myTag : way.getTags()) {
//                if ("highway".equalsIgnoreCase(myTag.getKey())) {
//                    System.out.println("highway: " + way.getId() + way.toString());
//
//                    break;
//                }
//            }
        // process your way //
        } else if (entityContainer instanceof RelationContainer){
//            RelationContainer relationContainer = ((RelationContainer) entityContainer);
//            System.out.println(relationContainer.getEntity().toString());
        } else {
//            System.out.println("Unknown Entity!");
        }
//        System.out.println("entity number: "+ ++entityNum);
    }

    // following methods are a requirement of the Sink interface //
    @Override
    public void initialize(Map<String, Object> map) {
    }

    @Override
    public void complete() {

    }

    @Override
    public void release() {

    }

    /**
     * Checks, whether a way is appropriate for our graph
     * @param way from pbf file to check
     * @return true if
     * (1) way contains highway tag
     * (2) way not for only pedestrians, not a footway
     * (3) way should have a name (street name)
     */
    private boolean isAppropriate(Way way) {
        // use Object-Boolean variable to track, if value changed
        Boolean carAllowed = null;
        Boolean namedStreet = null;

        for (Tag tag : way.getTags()) {
//            String a = tag.getKey();
//            System.out.println(a);
            if (tag.getKey().equals("name")) {
                namedStreet = true;
            }
            if (tag.getKey().equals("highway")) {
                carAllowed = this.noVehicleValues.contains(tag.getValue()) ? false : true;
            }
            // ... no need to iterate over all tags if both values already changed
            if (namedStreet != null && carAllowed != null) {
                break;
            }
        }
        // final decision:
        carAllowed = carAllowed == null ? false : carAllowed;
        namedStreet = namedStreet == null ? false : namedStreet;
        return (carAllowed && namedStreet);
    }

    /**
     * Values of tag 'highway' of ways in OSM where no cars are allowed
     * Feel free to fill the list: https://wiki.openstreetmap.org/wiki/Key:highway
     * TODO use more efficient collection
     */
    private final List<String> noVehicleValues = Arrays.asList("trunk", "motorway", "pedestrian", "footway", "bridleway", "steps", "path", "cycleway",
            "construction", "proposed", "bus_stop", "elevator", "street_lamp", "stop", "traffic_signals", "service", "track", "platform", "raceway",
            "abandoned", "road" , "escape" , "proposed" , "construction", "corridor", "bridleway" , "bus_guideway", "none", "motorway_link", "unclassified");

    /**
     * Check if a node from pbf file is appropriate for your purposes
     * TODO you can add required properties
     * @param node
     * @return
     */
    private boolean isAppropriate(Node node) {
        return false;
    }
}