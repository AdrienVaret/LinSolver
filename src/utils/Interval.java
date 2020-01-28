package utils;

import graphs.Node;

public class Interval {

	private Node u1, v1, u2, v2;
	
	private int x1, x2;
	private int y1, y2;
	private int size;
	
	public Interval(Node u1, Node v1, Node u2, Node v2) {
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
		
		x1 = Math.min(u1.getX(), u2.getX());
		x2 = Math.max(u1.getX(), u2.getX());
		
		y1 = Math.min(u1.getY(), v1.getY());
		y2 = Math.max(u1.getY(), v1.getY());
		
		size = Math.abs(x2 - x1);
	}

	public Node getU1() {
		return u1;
	}

	public Node getV1() {
		return v1;
	}

	public Node getU2() {
		return u2;
	}

	public Node getV2() {
		return v2;
	}

	public int getX1() {
		return x1;
	}

	public int getX2() {
		return x2;
	}

	public int getY1() {
		return y1;
	}
	
	public int getY2() {
		return y2;
	}

	public int getSize() {
		return size;
	}
	
}
