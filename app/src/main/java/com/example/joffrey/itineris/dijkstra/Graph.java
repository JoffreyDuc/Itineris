package com.example.joffrey.itineris.dijkstra;

import com.example.joffrey.itineris.utils.Point2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public String toString(){
        String s = "";
        for (Node node : nodes) {
            if(node.getDistance() != Double.MAX_VALUE){
                s += node.getName() + " " + node.getDistance() + "\n";
            }else{
                s += node.getName() + "\n";
            }
            for (Map.Entry<Node, Double> entry : node.getAdjacentNodes().entrySet()){
                s += "\t" + entry.getKey().getName() + " : " + entry.getValue() + "\n";
            }
            if(node.getShortestPath().size() > 0){
                s += "\t";
                for (Node n : node.getShortestPath() ) {
                    s += n.getName() + " -> ";
                }
                s += node.getName();
            }
            s += "\n";
        }
        return s;
    }

    static public Graph initialize(ArrayList<Point2D> pointsGPS, ArrayList<int[]> pointsGPSlinks){

        Graph graph = new Graph();
        ArrayList<Node> nodes = new ArrayList<>();
        int i = 0;

        for (Point2D point : pointsGPS) {
            nodes.add(new Node(String.valueOf(i)));
            i++;
        }

        for (int[] link : pointsGPSlinks) {
            Point2D p0 = pointsGPS.get(link[0]);
            Point2D p1 = pointsGPS.get(link[1]);
            double distance = Math.sqrt((p1.getY() - p0.getY()) * (p1.getY() - p0.getY()) + (p1.getX() - p0.getX()) * (p1.getX() - p0.getX()));
            nodes.get(link[0]).addDestination(nodes.get(link[1]) , distance);
            nodes.get(link[1]).addDestination(nodes.get(link[0]) , distance);
        }

        for (Node node : nodes) {
            graph.addNode(node);
        }

        return graph;
    }

    public static ArrayList<Node> itineraire(Graph graph, int arrivee) {
        ArrayList<Node> itineraire = new ArrayList<>();
        for (Node n: graph.getNodes()) {
            if(n.getName().equals(String.valueOf(arrivee))){
                for (Node node : n.getShortestPath()) {
                    itineraire.add(node);
                }
                itineraire.add(n);
            }
        }
        return itineraire;

    }

}