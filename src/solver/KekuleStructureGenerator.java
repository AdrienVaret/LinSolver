package solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import graphs.UndirGraph;
import parser.GraphParser;

public class KekuleStructureGenerator {

	private static BufferedWriter w;
	
	private static long timeAllDiffConstraint;
	private static long timeSumConstraints;
	
	public static int computeKekuleStructuresAllDiffConstraint(UndirGraph molecule) {
	
		long begin = System.currentTimeMillis();
		
        int nbNode = molecule.getNbNodes();

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

                for (int j = 0; j < molecule.getNbNodes(); j++) {
                    if (molecule.getAdjacencyMatrix()[u][j] != 0) {

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
		
        /*
         * Générer le modèle
         */
        
        Model model = new Model("Kekule structures with all diff constraint");
        
        IntVar[] variables = new IntVar[molecule.getNbNodes() / 2];
        
        int indexVariable = 0;
        
        for (int i = 0 ; i < nodesSet.length ; i++) {
        	if (nodesSet[i] == 1) {
        		
        		int nbAdjacentEdges = molecule.getEdgeMatrix().get(i).size();
        		int [] domain = new int [nbAdjacentEdges];
        		
        		int indexDomain = 0;
        		
        		for (int j = 0 ; j < molecule.getNbNodes() ; j++) {
        			if (molecule.getAdjacencyMatrix()[i][j] != 0) {
        				domain[indexDomain] = j;
        				indexDomain ++;
        			}
        		}
        		
        		variables[indexVariable] = model.intVar("x_" + i, domain);
        		
        		indexVariable ++;
        	}
        }
        
        model.allDifferent(variables).post();
        
        model.getSolver().setSearch(new IntStrategy(variables, new FirstFail(model), new IntDomainMin()));
        Solver solver = model.getSolver();
        
        int nbStructures = 0;
        
        while (solver.solve()) {
        	Solution solution = new Solution(model);
            solution.record();
            nbStructures ++;
        }
        
        long end = System.currentTimeMillis();
        timeAllDiffConstraint = end - begin;
        
        return nbStructures;
	}
	
	public static int computeKekuleStructuresSumConstraints(UndirGraph molecule) {
		
		long begin = System.currentTimeMillis();
		
		Model model = new Model("Kekule structures with sum constraints");
		
		BoolVar[] edges = new BoolVar[molecule.getNbEdges()];

        for (int i = 0; i < molecule.getNbEdges(); i++) {
            edges[i] = model.boolVar("edge " + (i + 1));
        }
        
        for (int i = 0; i < molecule.getEdgeMatrix().size(); i++) {
            int nbAdjacentEdges = molecule.getEdgeMatrix().get(i).size();
            BoolVar[] adjacentEdges = new BoolVar[nbAdjacentEdges];

            for (int j = 0; j < nbAdjacentEdges; j++) {
                adjacentEdges[j] = edges[molecule.getEdgeMatrix().get(i).get(j)];
            }

            model.sum(adjacentEdges, "=", 1).post();
        }

        model.getSolver().setSearch(new IntStrategy(edges, new FirstFail(model), new IntDomainMin()));
        Solver solver = model.getSolver();
        
        int nbStructures = 0;
        
        while (solver.solve()) {
        	Solution solution = new Solution(model);
            solution.record();
            nbStructures ++;
        }
        
        long end = System.currentTimeMillis();
        timeSumConstraints = end - begin;
        
        return nbStructures;
	}
	
	public static void main(String [] args) throws IOException {
		
		//String filename = args[0];
		
		String filename = "/Users/adrien/CLionProjects/ConjugatedCycles/molecules/coronnoids/3_crowns.graph_coord";
		
		w = new BufferedWriter(new FileWriter(new File("kekule_structures"), true));
		
		UndirGraph molecule = GraphParser.parseUndirectedGraph(filename);
		
		int nbStructuresSumConstraints = computeKekuleStructuresSumConstraints(molecule);
		int nbStructuresAllDiffConstraint = computeKekuleStructuresAllDiffConstraint(molecule);
		
		w.write(filename + " " + nbStructuresSumConstraints + " " + timeSumConstraints + "\n");
		w.write(filename + " " + nbStructuresAllDiffConstraint + " " + timeAllDiffConstraint + "\n");
		w.write("\n");
		
		w.close();
	}
}
