package utils;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import graphs.Node;
import solver.BenzenoidsSolver;

public class Utils {

	//DEBUG
	public static int [] t = new int [54];
	
	public static int getHexagonId(int x, int y) {
		return x + y * BenzenoidsSolver.taille;
	}
	
	public static int getEdgeId(int x, int y, int edgeBoard) {
		
		if(edgeBoard == BenzenoidsSolver.HIGH_RIGHT && y > 0)
			return (x + (y - 1) * BenzenoidsSolver.taille) * 6 + 3;
		
		else if(edgeBoard == BenzenoidsSolver.LEFT && x > 0)
			return (x - 1 + y * BenzenoidsSolver.taille) * 6 + 1;
		
		else if (edgeBoard == BenzenoidsSolver.HIGHT_LEFT && x > 0 && y > 0)
			return (x - 1 + (y - 1) * BenzenoidsSolver.taille) * 6 + 2;
		
		else
			return (x + y * BenzenoidsSolver.taille) * 6 + edgeBoard;
	}
	
	public static Point getHexagonCoordinates(int index) {
		
		int y = (int) Math.floor(index / BenzenoidsSolver.taille);
		int x = index - (BenzenoidsSolver.taille * y);
		
		return new Point(x, y);
	}
	
	public static double angle(Vector2D u, Vector2D v) {
		double angle = Vector2D.angle(u, v);
		
		if(!(u.getX() * v.getY() - u.getY() * v.getX() < 0))
		    angle = -angle;
		
		return angle;
	}
	
	public static double orientedAngle(Node u, Node v, Node w) {

		   Vector2D v1 = new Vector2D(v.getX() - u.getX(), v.getY() - u.getY());
		   Vector2D v2 = new Vector2D(w.getX() - v.getX(), w.getY() - v.getY());
		    
		   return Math.atan2(v1.getX(), v1.getY()) - Math.atan2(v2.getX(), v2.getY());
	}
	
	public static ArrayList<Integer> computeDomainX1(Cycle cycle){
		ArrayList<Integer> domain = new ArrayList<Integer>();
		for (int i = 0 ; i < cycle.getNodes().length ; i++) {
			if (cycle.getNode(i) != -1) {
				domain.add(i);
			}
		}
		return domain;
	}
	
	public static ArrayList<Integer> computeDomainX2(Cycle cycle, int x1){
		ArrayList<Integer> domain = new ArrayList<Integer>();
		
		int v = cycle.getNode(x1);
		
		while (true) {
			domain.add(v);
			v = cycle.getNode(v);
			if (v == x1) break;
		}
		
		return domain;
	}
	
	public static ArrayList<Integer> computeDomainX3(Cycle cycle, int x1, int x2){
		ArrayList<Integer> domain = new ArrayList<Integer>();
		
		int v = x2;
		while(true) {
			domain.add(v);
			v = cycle.getNode(v);
			if (v == x1) break;
		}
		
		return domain;
	}

	public static ArrayList<Integer> computeDomainX4(Cycle cycle, int x1, int x2, int x3){
		ArrayList<Integer> domain = new ArrayList<Integer>();
		
		int v = cycle.getNode(x3);
		
		while(true) {
			
			if (v == x1) break;
			
			if (v != cycle.getNode(x3))
				domain.add(v);

			v = cycle.getNode(v);

		}
		
		return domain;
	}
	
	public static void initTab() {
		t[0] = 0;
		t[1] = 1;
		t[2] = 5;
		t[3] = 2;
		t[4] = 6;
		t[5] = 3;
		t[6] = 9;
		t[7] = 4;
		t[8] = 32;
		t[9] = 35;
		t[10] = 7;
		t[11] = 8;
		t[12] = 12;
		t[13] = 15;
		t[14] = 38;
		t[15] = 13;
		t[16] = 14;
		t[17] = 21;
		t[18] = 44;
		t[19] = 50;
		t[20] = 33;
		t[21] = 39;
		t[22] = 34;
		t[23] = 62;
		t[24] = 65;
		t[25] = 45;
		t[26] = 68;
		t[27] = 51;
		t[28] = 74;
		t[29] = 57;
		t[30] = 80;
		t[31] = 86;
		t[32] = 63;
		t[33] = 69;
		t[34] = 64;
		t[35] = 92;
		t[36] = 75;
		t[37] = 98;
		t[38] = 81;
		t[39] = 104;
		t[40] = 87;
		t[41] = 110;
		t[42] = 116;
		t[43] = 99;
		t[44] = 105;
		t[45] = 128;
		t[46] = 111;
		t[47] = 134;
		t[48] = 117;
		t[49] = 140;
		t[50] = 146;
		t[51] = 135;
		t[52] = 141;
		t[53] = 147;
	}
}

