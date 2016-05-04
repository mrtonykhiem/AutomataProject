package shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class InitState extends NormalState {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void draw(Graphics2D ga) {
		
		super.draw(ga);
		ga.setColor(Color.BLACK);
		Point2D point = this.getPosition();
		Line2D line = new Line2D.Double(point.getX(), point.getY() - 60, point.getX(), point.getY() - 10);
		ga.draw(line);
		Edge.drawArrow(ga, line);
		ga.setColor(Color.BLACK);
	}
}
