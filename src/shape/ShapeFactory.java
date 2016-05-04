package shape;

public class ShapeFactory {
	public static final int NORMAL_STATE = 0;
	public static final int INIT_FINISH = 1;
	public static final int INIT_STATE = 2;
	public static final int FINISH_STATE = 3;
	public static final int EDGE = 4;
	public static final int EDGE_LABEL = 6;
	public static final int CHECK = 7;

	private int ShapeNumber = 0;
	public Shape getShape(int shapeType)
	{
		Shape shape;
		switch (shapeType) {
			case NORMAL_STATE:
				shape = new NormalState();
				shape.setLabel( Integer.toString(ShapeNumber++));
				return shape;
			case INIT_FINISH:
				shape = new InitFinishState();
				shape.setLabel(Integer.toString(ShapeNumber++));
				return shape;
			case INIT_STATE:
				shape = new InitState();
				shape.setLabel(Integer.toString(ShapeNumber++));
				return shape;
			case FINISH_STATE:
				shape = new FinishState();
				shape.setLabel(Integer.toString(ShapeNumber++));
				return shape;
			case EDGE:
				return new Edge();
			case EDGE_LABEL:
				return new EdgeLabel();
			case CHECK:
				return new Check();
		}
		return null;
		
	}
}
