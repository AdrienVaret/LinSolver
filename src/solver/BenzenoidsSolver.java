package solver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.chocosolver.graphsolver.GraphModel;
import org.chocosolver.graphsolver.variables.DirectedGraphVar;
import org.chocosolver.util.objects.graphs.DirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import graphs.Arc;
import graphs.Node;
import graphs.UndirGraph;
import graphs.UndirPonderateGraph;
import parser.GraphParser;
import utils.Cycle;
import utils.NodeComputer;
import utils.Utils;
import org.chocosolver.solver.search.strategy.strategy.*;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;

public class BenzenoidsSolver {
	
	private static int idStructure = 0;
	
    //LOGS
    private static BufferedWriter log = null;
    private static BufferedWriter debug;

    //DEBUG
    private static ArrayList<Integer> nbCyclesList = new ArrayList<Integer>();
    private static int nbStruct = 0;
    public static UndirGraph gr;
    
    private static final int MAX_CYCLE_SIZE = 10;
    private static int RCount[] = new int[MAX_CYCLE_SIZE];
    private static double RValues[] = new double[MAX_CYCLE_SIZE];

    //Edges's direction constants
    public static final int HIGH_RIGHT = 0;
    public static final int RIGHT = 1;
    public static final int LOW_RIGHT = 2;
    public static final int LOW_LEFT = 3;
    public static final int LEFT = 4;
    public static final int HIGHT_LEFT = 5;

    //Edges direction enum
    public enum Direction {
        HORAIRE, ANTI_HORAIRE;
    }

    public enum Sens {
        RIGHT, LEFT;
    }

    //Problem's attributes
    public static int dimension;
    public static int taille;
    public static int nbHexa;
    public static boolean nbHexaLimite;
    
    public static void generateLewisStructures(String path, boolean allSolutions) throws IOException {

        UndirGraph graph = GraphParser.parseUndirectedGraph(path);
        Model model = new Model("Lewis Structures");

        graph.exportToGraphviz("graph_output");
        
        BoolVar[] edges = new BoolVar[graph.getNbEdges()];

        for (int i = 0; i < graph.getNbEdges(); i++) {
            edges[i] = model.boolVar("edge " + (i + 1));
        }

        for (int i = 0; i < graph.getEdgeMatrix().size(); i++) {
            int nbAdjacentEdges = graph.getEdgeMatrix().get(i).size();
            BoolVar[] adjacentEdges = new BoolVar[nbAdjacentEdges];

            for (int j = 0; j < nbAdjacentEdges; j++) {
                adjacentEdges[j] = edges[graph.getEdgeMatrix().get(i).get(j)];
            }

            model.sum(adjacentEdges, "=", 1).post();
        }

        model.getSolver().setSearch(new IntStrategy(edges, new FirstFail(model), new IntDomainMin()));
        Solver solver = model.getSolver();
    
        ArrayList<UndirPonderateGraph> structures = new ArrayList<UndirPonderateGraph>();
        
        int i = 0;
        while (solver.solve()) {
            Solution solution = new Solution(model);
            solution.record();

            int[] edgesValues = new int[graph.getNbEdges()];

            for (int j = 0; j < graph.getNbEdges(); j++) {
                edgesValues[j] = solution.getIntVal(edges[j]);
            }

            UndirPonderateGraph structure = GraphParser.exportSolutionToPonderateGraph(graph, edgesValues);
            
            if (i == 0)
				try {
					structure.exportToDimacs("output_struct");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
            computeCycles(structure);

            structures.add(structure);
            
            nbStruct++;

            if (!allSolutions)
                break;
            
            i++;
        }
        
    }

    public static void exportGraph(DirectedGraphVar g, String directory, String name) {
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(new File(directory + "/" + name)));
            w.write(g.graphVizExport());
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Direction computeDirection(UndirPonderateGraph graph, Cycle cycle) {

        Node node = null;

        int maxHeight = Integer.MAX_VALUE;
        int minWidth = Integer.MAX_VALUE;

        for (int i = 0; i < cycle.getNodes().length; i++) {
            if (cycle.getNode(i) != -1) {
                if (graph.getNodeRef(i).getY() < maxHeight) {
                    maxHeight = graph.getNodeRef(i).getY();
                }
            }
        }

        for (int i = 0; i < cycle.getNodes().length; i++) {
            if (cycle.getNode(i) != -1 && graph.getNodeRef(i).getY() == maxHeight) {
                if (graph.getNodeRef(i).getX() < minWidth) {
                    minWidth = graph.getNodeRef(i).getX();
                    node = graph.getNodeRef(i);
                }
            }
        }

        Node nextNode = graph.getNodeRef(cycle.getNode(node.getIndex()));
        Node previousNode = null;

        for (int i = 0; i < cycle.getNodes().length; i++) {
            if (cycle.getNode(i) == node.getIndex()) {
                previousNode = graph.getNodeRef(i);
                break;
            }
        }

        if (nextNode.getX() == node.getX() + 2 && nextNode.getY() == node.getY())
            return Direction.HORAIRE;

        else if (nextNode.getX() == node.getX() - 1 && nextNode.getY() == node.getY() + 2)
            return Direction.ANTI_HORAIRE;

        else if (previousNode.getX() == node.getX() + 2 && previousNode.getY() == node.getY() &&
                nextNode.getX() == node.getX() + 1 && nextNode.getY() == node.getY() + 2)
            return Direction.ANTI_HORAIRE;

        else if (previousNode.getX() == node.getX() - 1 && previousNode.getY() == node.getY() + 2 &&
                nextNode.getX() == node.getX() + 1 && nextNode.getY() == node.getY() + 2)
            return Direction.HORAIRE;

        else
            return null;

    }

    public static double distance(Node u, Node v) {
        double xa = (double) u.getX();
        double ya = (double) u.getY();
        double xb = (double) v.getX();
        double yb = (double) v.getY();

        return Math.sqrt((xb - xa) * (xb - xa) + (yb - ya) * (yb - ya));
    }

    public static boolean hasTwoOutputArcs(int indexNode, int[][] adjacencyMatrix) {
        int n = 0;
        for (int i = 0; i < adjacencyMatrix[indexNode].length; i++) {
            if (adjacencyMatrix[indexNode][i] != -1) {
                n++;
                if (n == 2)
                    return true;
            }
        }
        return false;
    }

    public static int getNeighbour(Direction direction, UndirPonderateGraph graph, Cycle cycle, int indexNode, int[][] adjacencyMatrix) {
        Node u = null, w = null, x = null;
        Node v = graph.getNodeRef(indexNode);

        for (int i = 0; i < graph.getNbNodes(); i++) {
            if (cycle.getNode(i) == indexNode)
                u = graph.getNodeRef(i);

            if (cycle.getNode(indexNode) == i)
                w = graph.getNodeRef(i);

        }

        for (int i = 0; i < graph.getNbNodes(); i++) {
            if (i != w.getIndex() && adjacencyMatrix[indexNode][i] != -1)
                x = graph.getNodeRef(i);
        }


        Node internalNode = NodeComputer.computeInternalNode(direction, u, v, w, x);

        if (internalNode == null)
            return -1;
        else
            return internalNode.getIndex();
    }

    public static ArrayList<Integer> findInternalNodes(Cycle cycle, int cycleInitialNode, int initialNode, int[][] adjacencyMatrix) {

    	ArrayList<Integer> internalNodes = new ArrayList<Integer>();
    	
    	ArrayList<Integer> vertices = new ArrayList<Integer>();
    	ArrayList<Integer> neighbours = new ArrayList<Integer>();

    	vertices.add(initialNode);
    	neighbours.add(-1);

    	while (vertices.size() > 0) {
    		int vertice = vertices.get(vertices.size() - 1);
    		int neighbour = neighbours.get(neighbours.size() - 1);

    		internalNodes.add(vertice);
    		
    		if (cycle.getNode(vertice) != -1) {

    			vertices.remove(vertices.size() - 1);
    			neighbours.remove(neighbours.size() - 1);
    			

    		} else {
    			int newVertice = -1;

    			for (int i = neighbour + 1; i < cycle.getNodes().length; i++) {
    				if (adjacencyMatrix[vertice][i] != -1) {

    					if (((vertices.size() == 1 && adjacencyMatrix[vertice][i] != cycleInitialNode) ||
    							(vertices.size() > 1 && !vertices.contains(i)))) {

    						internalNodes.add(adjacencyMatrix[vertice][i]);
    						internalNodes.add(i);
    						
    						newVertice = i;
    						break;
    					}
    				}
    			}

    			if (newVertice != -1) {
    				neighbours.set(neighbours.size() - 1, newVertice);
    				neighbours.add(-1);
    				vertices.add(newVertice);
    			} else {
    				vertices.remove(vertices.size() - 1);
    				neighbours.remove(neighbours.size() - 1);
    			}
    		}
    	}

    	return internalNodes;
}
    
    public static ArrayList<Integer> getCoveredHexagons(UndirPonderateGraph graph, Cycle cycle, int [][] adjacencyMatrix) {
    	
    	int [] vertices = new int[graph.getAdjacencyMatrix().length];
    	
    	Direction direction = computeDirection(graph, cycle);
    	
    	for (int i = 0 ; i < cycle.getNodes().length ; i++) {
    		if (cycle.getNode(i) != -1) {
    			
    			vertices[i] = 1;
    			
    			for (int j = 0 ; j < adjacencyMatrix[i].length ; j++) {
    				if (adjacencyMatrix[i][j] != -1 && cycle.getNode(j) != -1) {
    					vertices[j] = 1;
    					vertices[adjacencyMatrix[i][j]] = 1;
    				}
    			}
    			
    			if (hasTwoOutputArcs(i, adjacencyMatrix)) {
    				int internalVertexId = -1;
                    internalVertexId = getNeighbour(direction, graph, cycle, i, adjacencyMatrix);
                    
                    if (internalVertexId != -1) {
                    	vertices[adjacencyMatrix[i][internalVertexId]] = 1;
                    	//Chercher tous les chemins internes
                    	ArrayList<Integer> internalNodes = findInternalNodes(cycle, i, internalVertexId, adjacencyMatrix);
                    	
                    	for (Integer node : internalNodes)
                    		vertices[node] = 1;
                    	
                    }
    			}
    		}
    			
    	}
    	
    	
    	ArrayList<Integer> hexagons = new ArrayList<Integer>();
    	
    	for (int i = 0 ; i < graph.getHexagons().length ; i++) {
    		
    		boolean covered = true;
    		for (int j = 0 ; j < 6 ; j++) {
    			if (vertices[graph.getHexagons()[i][j]] == 0){
    				covered = false;
    				break;
    			}
    		}
    		
    		if (covered) {
    			hexagons.add(i);
    		}
    	}
    	
    	return hexagons;
    }
    
    public static int computePath(Cycle cycle, int cycleInitialNode, int initialNode,
                                  ArrayList<Integer> targetDomain, int[] testNodes, int[][] adjacencyMatrix) {

        ArrayList<Integer> vertices = new ArrayList<Integer>();
        ArrayList<Integer> neighbours = new ArrayList<Integer>();

        vertices.add(initialNode);
        neighbours.add(-1);

        while (vertices.size() > 0) {
            int vertice = vertices.get(vertices.size() - 1);
            int neighbour = neighbours.get(neighbours.size() - 1);

            if (cycle.getNode(vertice) != -1) {

                if (targetDomain.contains(vertice) && testNodes[vertice] == 0)
                    return vertice;

                else {
                    vertices.remove(vertices.size() - 1);
                    neighbours.remove(neighbours.size() - 1);
                }

            } else {
                int newVertice = -1;

                for (int i = neighbour + 1; i < cycle.getNodes().length; i++) {
                    if (adjacencyMatrix[vertice][i] != -1) {

                        if (((vertices.size() == 1 && adjacencyMatrix[vertice][i] != cycleInitialNode) ||
                                (vertices.size() > 1 && !vertices.contains(i))) &&
                                testNodes[i] == 0) {

                            newVertice = i;
                            break;
                        }
                    }
                }

                if (newVertice != -1) {
                    neighbours.set(neighbours.size() - 1, newVertice);
                    neighbours.add(-1);
                    vertices.add(newVertice);
                } else {
                    vertices.remove(vertices.size() - 1);
                    neighbours.remove(neighbours.size() - 1);

                }
            }
        }

        return -1;
    }

    public static boolean isLinearyDependant(UndirPonderateGraph graph, Cycle cycle, int[][] adjacencyMatrix) {

        if (cycle.getNbEdges() == 3 || cycle.getNbEdges() == 5)
            return false;

        Direction direction = computeDirection(graph, cycle);

        ArrayList<Integer> domainX1 = Utils.computeDomainX1(cycle);

        for (Integer x1 : domainX1) {
            if (hasTwoOutputArcs(x1, adjacencyMatrix)) {
                int internalVertexId = -1;
                internalVertexId = getNeighbour(direction, graph, cycle, x1, adjacencyMatrix);

                if (internalVertexId != -1) {

                    ArrayList<Integer> domainX2 = Utils.computeDomainX2(cycle, x1);
                    int[] testedX2 = new int[graph.getNbNodes()];

                    while (true) {
                        int x2 = computePath(cycle, x1, internalVertexId, domainX2, testedX2, adjacencyMatrix);
                        if (x2 == -1) break;
                        testedX2[x2] = 1;

                        ArrayList<Integer> domainX3 = Utils.computeDomainX3(cycle, x1, x2);

                        for (Integer x3 : domainX3) {

                            if (hasTwoOutputArcs(x3, adjacencyMatrix)) {

                                int internalVertexId2;

                                internalVertexId2 = getNeighbour(direction, graph, cycle, x3, adjacencyMatrix);

                                if (internalVertexId2 != -1) {

                                    ArrayList<Integer> domainX4 = Utils.computeDomainX4(cycle, x1, x2, x3);
                                    int[] testedX4 = new int[graph.getNbNodes()];

                                    while (true) {
                                        int x4 = computePath(cycle, x3, internalVertexId2, domainX4, testedX4, adjacencyMatrix);
                                        if (x4 == -1)
                                            break;
                                        else
                                            return true;
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }


    public static void computeCycles(UndirPonderateGraph graph) throws IOException {
    	
    	ArrayList<ArrayList<Cycle>> table = new ArrayList<ArrayList<Cycle>>();
    	
    	for (int i = 0 ; i < 1000 ; i++)
    		table.add(new ArrayList<Cycle>());
    	
        int nbCycles = 0;

        int nbNode = graph.getNbNodes();

        int[] nodesSet = new int[nbNode];
        int[] visitedNodes = new int[nbNode];

        int deep = 0;
        int n = 0;

        ArrayList<Integer> q = new ArrayList<Integer>();

        q.add(0);

        visitedNodes[0] = 1;

        int count = 1;


        //Récupérer l'ensemble des "atomes étoilés"
        while (n < nbNode / 2) {

            int newCount = 0;

            for (int i = 0; i < count; i++) {

                int u = q.get(0);

                if (deep % 2 == 0) {
                    nodesSet[u] = 1;
                    n++;
                }

                for (int j = 0; j < graph.getNbNodes(); j++) {
                    if (graph.getAdjacencyMatrix()[u][j] != -1) {

                        if (visitedNodes[j] == 0) {
                            visitedNodes[j] = 1;
                            q.add(j);
                            newCount++;
                        }

                    }
                }

                q.remove(0);
            }
            deep++;
            count = newCount;
        }

        //Récupérer l'ensemble des couples d'arêtes alternantes
        int[][] adjacencyMatrix = new int[graph.getNbNodes()][graph.getNbNodes()];

        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix.length; j++) {
                adjacencyMatrix[i][j] = -1;
            }
        }

        ArrayList<Arc> edges = new ArrayList<Arc>();
        for (int u = 0; u < graph.getNbNodes(); u++) {
            if (nodesSet[u] == 1) {
                for (int v = 0; v < graph.getNbNodes(); v++) {

                    if (graph.getAdjacencyMatrix()[u][v] == 0) {

                        for (int w = 0; w < graph.getNbNodes(); w++) {
                            if (nodesSet[w] == 1 && graph.getAdjacencyMatrix()[v][w] == 1) {
                                edges.add(new Arc(u, w, v));
                                adjacencyMatrix[u][w] = v;
                            }
                        }
                    }
                }

            }
        }

/*
        System.out.println("");
        System.out.println("Edges : ");
        for (Arc e : edges)
            System.out.println(e.toString());
*/

        //Créer le problème
        GraphModel model = new GraphModel("Alternant Cycles");

        DirectedGraph GLB = new DirectedGraph(model, nbNode, SetType.BITSET, false);
        DirectedGraph GUB = new DirectedGraph(model, nbNode, SetType.BITSET, false);

        for (int i = 0; i < nbNode; i++) {
            if (nodesSet[i] == 1) {
                GUB.addNode(i);
            }
        }

        for (Arc edge : edges) {
            GUB.addArc(edge.getU(), edge.getV());
        }

        DirectedGraphVar g = model.digraphVar("g", GLB, GUB);

        BoolVar[] boolEdges = new BoolVar[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            boolEdges[i] = model.boolVar("(" + edges.get(i).getU() + "->" + edges.get(i).getV() + ")");
            model.arcChanneling(g, boolEdges[i], edges.get(i).getU(), edges.get(i).getV()).post();
        }
        model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));

        model.stronglyConnected(g).post();
        model.maxOutDegrees(g, 1).post();
        model.minOutDegrees(g, 1).post();
        model.arithm(model.nbNodes(g), ">", 1).post();

        IntVar nbArcs = model.intVar("arcCount", 0, edges.size(), true);
        model.nbArcs(g, nbArcs).post();

        model.sum(boolEdges, ">", 0).post();

        Solver solver = model.getSolver();

        List<Cycle> cycles = new ArrayList<>();

        while (solver.solve()) {
            Solution solution = new Solution(model);
            solution.record();

            int nbNodesSolution = nbArcs.getValue() * 2;

            int[] edgesCycle = new int[boolEdges.length];
            int[] nodesCycle = new int[graph.getNbNodes()];

            for (int j = 0; j < nodesCycle.length; j++)
                nodesCycle[j] = -1;

            int nbNodes = 0;
            int nbEdges = 0;
            for (int j = 0; j < boolEdges.length; j++) {
                edgesCycle[j] = solution.getIntVal(boolEdges[j]);
                if (edgesCycle[j] == 1) {
                    nbEdges++;
                    Arc e = edges.get(j);
                    if (nodesCycle[e.getU()] == -1) {
                        nodesCycle[e.getU()] = e.getV();
                        nbNodes++;
                    }
                }
            }
            
            Cycle cycle = new Cycle(edgesCycle, nodesCycle, nbNodesSolution, edges, nbNodes, nbEdges);
            

            ArrayList<Integer> hexagons = getCoveredHexagons(graph, cycle, adjacencyMatrix);
			cycle.setHexagonsCovered(hexagons);
            
			cycles.add(cycle);
        }

        if (idStructure == 23)
        	System.out.println("");
        
        Collections.sort(cycles);
 
        int [] hexagonsCovered =  new int[graph.getNbHexagons()];
        Cycle [] hexagonsCov = new Cycle[graph.getNbHexagons()];
        
        for (int i = 0 ; i < hexagonsCovered.length ; i++)
        	hexagonsCovered[i] = -1;
        
        int [] localRCount = new int[10];
        
        if (graph.exportToDebugLine().equals("0 5 1 6 2 9 3 32 4 35 7 8 12 13 14 21 15 44 33 62 34 65 38 45 39 68 50 57 51 80 63 64 69 98 74 75 81 110 86 87 92 99 104 105 111 140 116 117 128 135 134 141 146 147 "))
        		System.out.print("");
        
        for (Cycle cycle : cycles) {
        	int cycleSize = getR(cycle.getNbEdges() * 2);
        	
        	for (Integer hexagon : cycle.getHexagonsCovered()) {
        		if (hexagonsCovered[hexagon] == -1 || hexagonsCovered[hexagon] > (cycleSize)) {
        			hexagonsCovered[hexagon] = (cycleSize);
        			hexagonsCov[hexagon] = cycle;
        		}
        	}
        }
        
        for (int i = 0 ; i < hexagonsCovered.length ; i++) {
    		if (hexagonsCovered[i] != -1) {
    			RCount[hexagonsCovered[i]] ++;
    			localRCount[hexagonsCovered[i]] ++;
    			nbCycles ++;
    		}
    	}
        
        debug.write(idStructure + " => ");
        for (int i = 0 ; i < localRCount.length ; i++) {
        	debug.write(localRCount[i] + " ");
        }
        debug.write("\n");
        idStructure ++;
        
        nbCyclesList.add(nbCycles);
    }

    public static int getR(int nbEdges) {
        return (int) ((nbEdges - 2) / 4) - 1;
    }

    public static void initRValues() {
        for (double i = 0; i < RValues.length; i++) {
            RValues[(int) i] = 1.0 / ((i + 1) * (i + 1));
        }
    }

    public static void computeEnergy(double nbKekuleStructures) throws IOException {
        initRValues();

        double sum = 0;

        for (int i = 0; i < RCount.length; i++) {
            System.out.print("(" + RCount[i] + " * R" + (i + 1) + ")");

            if (log != null)
                log.write("(" + RCount[i] + " * R" + (i + 1) + ")");

            sum += ((double) RCount[i]) * RValues[i];

            if (i < RCount.length - 1) {
                System.out.print(" + ");
                if (log != null)
                    log.write(" + ");
            }
        }

        sum = sum / nbKekuleStructures;

        System.out.println(" / " + nbKekuleStructures + " = " + sum);
        if (log != null)
            log.write(" / " + nbKekuleStructures + " = " + sum);

        int index = 0;
        for (Integer i : nbCyclesList) {
            System.out.println("structure " + index + " : " + i + "cycles indep");
            index++;
        }

    }

    public static void analyzeMolecule(String filename) throws IOException {
    	
        generateLewisStructures(filename, true);

        try {
            computeEnergy(nbStruct);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void displayUsage() {
        System.err.println("USAGE : java -jar ${EXEC_NAME} [${LOG_FILE}]");
        System.exit(1);
    }

    public static void main(String[] args) {

    	Utils.initTab();
/*    	
        if (args.length == 0) {
            displayUsage();
            System.exit(1);
        }

        String path = args[0];
        
        if (args.length >= 2) {
            String logFile = args[1];
            try {
                log = new BufferedWriter(new FileWriter(new File(logFile), true));
                log.write(path + " : ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
*/
        
        try {
			debug = new BufferedWriter(new FileWriter(new File("debug_naive")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        long begin = System.currentTimeMillis();

        //analyzeMolecule(path); 
     
        try {
			analyzeMolecule("/Users/adrien/CLionProjects/ConjugatedCycles/molecules/coronnoids/3_crowns.graph_coord");
        	//analyzeMolecule("/home/adrien/CLionProjects/ConjugatedCycle/molecules/coronnoids/3_crowns.graph_coord");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        long end = System.currentTimeMillis();
        long time = end - begin;

        if (log != null) {
            try {
                log.write(" time : " + time + " ms." + "\n");
                log.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if (debug != null) {
        	try {
				debug.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
