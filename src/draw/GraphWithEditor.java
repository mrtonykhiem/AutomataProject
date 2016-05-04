package draw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import automata.Automata;
import shape.Edge;
import shape.EdgeLabel;
import shape.Shape;
import shape.ShapeFactory;

public class GraphWithEditor extends JComponent implements MouseInputListener, Graph, KeyListener {

	/**
	 * This is the heart of application
	 * This contain all Shapes and Paint area
	 * This design how Shapes connect to another
	 * And take care how user interactive with Shapes
	 */
	private static final long serialVersionUID = 1L;
	private Vector<Shape> shapes;
	
	/**
	 * Shape factory to procedure shape
	 */
	private ShapeFactory shapeFactory;
	
	/**
	 * Determine which shape is currently
	 */
	private Shape currentShape;
	
	/**
	 * Determine which shape will be draw
	 * This value must be one of ShapeFactory type number
	 */
	private int shapeType;
	
	/**
	 * Determine if user hold ALT key or not
	 * True: holding ALT key
	 */
	private boolean holdShift = false;
	
	/**
	 * Determine which color is currently using 
	 */
	private Color currentColor;
	
	/**
	 * Object to verify automata
	 */
	private Automata automata;
	
	/**
	 * Determine which point is currently mouse in
	 * Change when mouse drag. see function mouseDragged
	 */
	public GraphWithEditor() {
		this.shapes = new Vector<>();
		shapeFactory = new ShapeFactory();
		addMouseListener(this);
		addMouseMotionListener(this);
		this.currentShape = null;
		setPreferredSize(new Dimension(2000, 2000));
		addKeyListener(this);
	}
	public void addShape(Shape shape) {
		this.shapes.add(shape);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D ga = (Graphics2D)g;
		ga.setColor(currentColor);
		for (Shape shape : shapes) {
			// For increase render look like
			ga.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			shape.draw(ga);
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		requestFocusInWindow();
		Point2D point = new Point2D.Double(e.getX(), e.getY());
		for (Shape shape: shapes) {
			/**
			 * For check if it have any shape here
			 */
			if (shape.verifyShapeExists(point)){
				/**
				 * If is right mouse is clicked, remove this shape
				 */
				if (e.getButton() == MouseEvent.BUTTON3) {
						removeShape(shape);
					this.validate();
					this.repaint();
					setAutomataChanges();
				}
				/**
				 * Otherwise, this action is move shape
				 */
				else {
					createEdgeForShape(shape);
				}
				return;
			}
		}
		
		/**
		 * If it doesn't have any shape here, create a new shape
		 */
		Shape shape = shapeFactory.getShape(shapeType);
		shape.setPosition(point);
		addShape(shape);
		createEdgeForShape(shape);
		this.validate();
		this.repaint();
		
		/**
		 * This prevent bug when user hold ALT and drag in empty space
		 * In this case, a new Edge is created
		 * But this Edge has no Destination Shape
		 * Therefore, we need to set which is destination shape for this Edge first
		 */
		if (!holdShift) {
			setAutomataChanges();
		}		
	}

	
	/**
	 * Remove Shape which type is not JoinPoint
	 * @param shapeToRemove
	 */
	private void removeShape(Shape shapeToRemove) {
		// Find all Edges which come to this Shape
		Vector<Edge> sources = getEdgeSource(shapeToRemove, shapes);
		
		// Find all Edges which come from this Shape
		Vector<Edge> dests = getEdgeDest(shapeToRemove, shapes);
		shapes.remove(shapeToRemove);
		
		// Remove all Edge and JointPoint
		removeEdgeJointPointSource(sources);
		removeEdgeJointPointDest(dests);
	}
	
	/**
	 * Get all Edges from list Shapes which come to a given Shape
	 * @param shapeToFind
	 * @param shapes
	 * @return
	 */
	public static Vector<Edge> getEdgeSource(Shape shapeToFind, Vector<Shape> shapes) {
		Vector<Edge> sources = new Vector<>();
		for (Shape shape : shapes) {
			if (shape instanceof Edge) {
				if ( ((Edge) shape).getDest().compare(shapeToFind)) {
					sources.add((Edge) shape);
				}
			}
		}
		return sources;
	}
	
	/**
	 * Get all Edges from list Shapes which come from a given Shape
	 * @param shapeToFind
	 * @param shapes
	 * @return
	 */
	public static Vector<Edge> getEdgeDest(Shape shapeToFind, Vector<Shape> shapes) {
		Vector<Edge> sources = new Vector<>();
		for (Shape shape : shapes) {
			if (shape instanceof Edge) {
				if ( ((Edge) shape).getSource().compare(shapeToFind)) {
					sources.add((Edge) shape);
				}
			}
		}
		return sources;
	}
	
	/**
	 * From a given list of Edges
	 * Remove all Edges, JointPoints until find another type of Shape
	 * Go from Source to Destination
	 * @param edges
	 */
	private void removeEdgeJointPointDest(Vector<Edge> edges) {
		for (Edge e: edges) {
				Edge currentEdge = e;
				removeEdge(e);
				while (shapes.contains(currentEdge.getDest()) && currentEdge.getDest() instanceof Edge) {
					Edge join = (Edge) currentEdge.getDest();
					shapes.remove(join);
					currentEdge = (Edge) join.getDest();
					removeEdge(currentEdge);
				}
		}
	}
	
	/**
	 * From a given list of Edges
	 * Remove all Edges, JointPoints until find another type of Shape
	 * Go from Destination to Source
	 * @param edges
	 */
	private void removeEdgeJointPointSource(Vector<Edge> edges) {
		for (Edge e: edges) {
			Edge currentEdge = e;
			removeEdge(e);
			while (shapes.contains(currentEdge.getSource()) && currentEdge.getSource() instanceof Edge) {
				Edge join = (Edge) currentEdge.getSource();
				shapes.remove(join);
				currentEdge = (Edge) join.getSource();
				removeEdge(currentEdge);
			}
		}
	}
	
	/**
	 * From a given Shape
	 * Create Edge which have source is this Shape
	 * @param shape
	 */
	private void createEdgeForShape(Shape shape) {
		currentShape = shape;
		if (holdShift && !(shape instanceof Edge)) {
			Shape edge = createEdge();
			((Edge)edge).setSource(currentShape);
			addShape(edge);
			currentShape.setDrawing(false);
			currentShape = edge;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		/**
		 * When mouse release & we are drawing edge
		 */
		if (this.currentShape instanceof Edge) {
			Point2D point = new Point2D.Double(e.getX(), e.getY());
			for (Shape shape: shapes) {
				if (shape.verifyShapeExists(point)){
					/**
					 * Ignore if current shape is JoinPoint or EdgeLabel
					 */
					if (shape instanceof Edge || shape instanceof EdgeLabel) {
						continue;
					}
					/**
					 * Otherwise, set current edge point to this shape
					 */
					((Edge) currentShape).setDest(shape);
					currentShape.setDrawing(false);
					currentShape = null;
					validate();
					repaint();
					/**
					 * Notify to automata that it have changes
					 */
					setAutomataChanges();
					return;
				}
			}
			/**
			 * If it doesn't have any shape, create a new one
			 */
			Shape shape = shapeFactory.getShape(shapeType);
			shape.setPosition(point);
			addShape(shape);
			((Edge) currentShape).setDest(shape);
			setAutomataChanges();
		}
		if (currentShape != null) {
			currentShape.setDrawing(false);
			currentShape = null;
		}
		validate();
		repaint();
	}



	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D point = new Point2D.Double(e.getX(), e.getY());
		/**
		 * In case we hold Alt key & create connection edge  
		 */
		if (holdShift && !(currentShape instanceof Edge)) {
			createEdgeForShape(currentShape);
		}
		/**
		 * Other wise
		 */
		else if (this.currentShape != null) {
			/**
			 * If current shape is Edge
			 * We just move this shape to new ppoint
			 */
			if (currentShape instanceof Edge) {
				((Edge) currentShape).setDest(point);
			} else {
				currentShape.setPosition(point);
			}
		} else {
			return;
		}
		this.validate();
		this.repaint();
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void addComponent(Component comp) {
		add(comp);
		revalidate();
		repaint();
	}

	@Override
	public void setPaintType(int type) {
		// TODO Auto-generated method stub
		this.shapeType = type;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isShiftDown()) {
			holdShift = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		holdShift = false;
	}
	
	/**
	 * Create Edge
	 * And also create EdgeLabel for this Edge
	 * @return
	 */
	private Shape createEdge() {
		Edge edge = (Edge)shapeFactory.getShape(ShapeFactory.EDGE);
		EdgeLabel edgeLabel = (EdgeLabel) shapeFactory.getShape(ShapeFactory.EDGE_LABEL);
		edgeLabel.setEdge(edge);
		edge.setEdgeLabel(edgeLabel);
		shapes.add(edgeLabel);
		add(edgeLabel);
		return edge;
	}
	
	/**
	 * Remove Edge
	 * And also remove Edge Label belong to this Edge
	 * @param edge
	 */
	private void removeEdge(Edge edge) {
		shapes.remove(edge);
		shapes.remove(edge.getEdgeLabel());
		remove(edge.getEdgeLabel());
	}
	@Override
	public void setShapeColor(Color color) {
		// TODO Auto-generated method stub
		this.currentColor = color;
	}
	@Override
	public void setAutomata(Automata automata) {
		// TODO Auto-generated method stub
		this.automata = automata;
	}
	
	/**
	 * From a given Edge
	 * Find a Shape which Edge come from - Ignore JoinPoint
	 * @param edge
	 * @return
	 */
	public static Shape findSourceShape(Edge edge) {
		Shape shape = null;
		Edge currentEdge = edge;
		while (shape == null) {
			if (currentEdge.getSource() instanceof Edge) {
				currentEdge = (Edge) ((Edge)currentEdge.getSource()).getSource();
			} else {
				shape = currentEdge.getSource();
			}
		}
		return shape;
	}
	
	/**
	 * From a given Edge
	 * Find a Shape which Edge come to - Ignore JoinPoint
	 * @param edge
	 * @return
	 */
	public static Shape findDestShape(Edge edge) {
		Shape shape = null;
		Edge currentEdge = edge;
		while (shape == null) {
			if (currentEdge.getDest() instanceof Edge) {
				currentEdge = (Edge) ((Edge)currentEdge.getDest()).getDest();
			} else {
				shape = currentEdge.getDest();
			}
		}
		return shape;
	}
	
	/**
	 * From Shape start & Shape end
	 * Find the right way (Edge path, include JoinPoint) which have EdgeLabel as same as given Label
	 * And then, highlight it using color green
	 * @param start
	 * @param end
	 * @param shapes
	 * @param label
	 */
	public static void showPath(Shape start, Shape end, Vector<Shape> shapes, String label) {
		Vector<Edge> paths = getEdgeDest(start, shapes);
		for (Edge edge : paths) {
			if (edge.getEdgeLabel().getLabel() == label) {
				recursiveFindPath(edge, end);
			}
		}
	}
	
	/**
	 * Using recursive to detect if from given edge, can we go to Shape end
	 * If yes, highlight it using Color green
	 * @param edge
	 * @param end
	 * @return
	 */
	public static boolean recursiveFindPath(Edge edge, Shape end) {
		if (edge.getDest() instanceof Edge) {
			if (recursiveFindPath((Edge) ((Edge)edge.getDest()).getDest(), end)) {
				edge.setColor(Color.green);
				return true;
			}
		} else if (edge.getDest().equals(end)) {
			edge.setColor(Color.green);
			return true;
		}
		return false;
	}
	
	@Override
	public void setAutomataChanges() {
		// TODO Auto-generated method stub
		automata.transformShapeToState(shapes);
		automata.verifyAutomata();
	}
	
	public void recognizeAutomata(String word) {
		automata.recognizeAutomata(word, shapes);
		revalidate();
		repaint();
	}
	
	public void requestFocus() {
		requestFocusInWindow();
	}
	@Override
	public void setShapes(Vector<Shape> shapes) {
		// TODO Auto-generated method stub
		this.shapes = shapes;
		for (Shape shape : shapes) {
			if (shape instanceof EdgeLabel) {
				add((EdgeLabel)shape);
			}
		}
		this.revalidate();
		this.repaint();
	}
	@Override
	public Vector<Shape> getShapes() {
		// TODO Auto-generated method stub
		return this.shapes;
	}
}
