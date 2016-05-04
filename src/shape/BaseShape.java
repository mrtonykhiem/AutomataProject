package shape;

import java.io.Serializable;

abstract class BaseShape implements Shape, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean isDrawing = true;
	
	public String toString() {
		return getLabel();
	}
	
	public boolean isDrawing() {
		return isDrawing;
	}
	public void setDrawing(boolean draw) {
		isDrawing = draw;
	}
}
