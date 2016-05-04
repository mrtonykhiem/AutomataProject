package draw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import shape.Shape;
import shape.ShapeFactory;
import automata.Automata;

public class GraphFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graph graphComponent;
	private JScrollPane scrollPane;
	private JToolBar toolbox, toolbox2, toolbox3;
	private LabelStatusMessage label;
	private List<JButton> buttons;
	private ShapeFactory factory;
	private int width = 75;
	private int shapeTypeSelected = ShapeFactory.INIT_STATE;
	public GraphFrame()
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WikiTeX");
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Automata automata = new Automata();
		graphComponent = new GraphWithEditor();
		graphComponent.setAutomata(automata);
		
		// Graph Component Component
		scrollPane = new JScrollPane(this.graphComponent.getComponent());
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		// State Tool Component
		buttons = new ArrayList<>();
		toolbox = new JToolBar();
		factory = new ShapeFactory();
		addShapeType("Init State", ShapeFactory.INIT_STATE);
		addShapeType("Finish State", ShapeFactory.FINISH_STATE);
		addShapeType("Normal State", ShapeFactory.NORMAL_STATE);
		addShapeType("Init-Fin State", ShapeFactory.INIT_FINISH);
		graphComponent.setPaintType(shapeTypeSelected);
		toolbox.setLayout(new GridLayout(buttons.size(),1));
		for (JButton button : buttons) {
			toolbox.add(button);
		}
	    toolbox.setPreferredSize(new Dimension(width, toolbox.getPreferredSize().height));
		getContentPane().add(toolbox, BorderLayout.WEST);
		
		// Language check Component
		toolbox2 = new JToolBar();
		FlowLayout ly = new FlowLayout();
		ly.setAlignment(FlowLayout.LEFT);
		toolbox2.setLayout(ly);
		JLabel lab = new JLabel();
		lab.setText("Language");
		toolbox2.add(lab);
		JTextField test = new JTextField() {
			
			/**
			 * Add PlaceHolder to JTextField
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(java.awt.Graphics g) {
			    super.paintComponent(g);

			    if(getText().isEmpty() && ! (FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)){
			        Graphics2D g2 = (Graphics2D)g.create();
			        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			        g2.setBackground(Color.gray);
			        g2.drawString("Enter word here", 10, 20);
			        g2.dispose();
			    }
			  }
		};
		test.setPreferredSize(new Dimension(200, 30));
		test.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField textField = (JTextField) e.getSource();
				String text = textField.getText();
				graphComponent.recognizeAutomata(text);
	        }

	        public void keyTyped(KeyEvent e) {
	        }

	        public void keyPressed(KeyEvent e) {
	        }
		});
		toolbox2.add(test);
		getContentPane().add(toolbox2, BorderLayout.NORTH);
		
		toolbox3 = new JToolBar();
		toolbox3.setLayout(new GridLayout(1,1));
		label = LabelStatusMessage.getInstance();
		label.setPreferredSize(new Dimension(30,15));
		toolbox3.add(label);
		setAutomata(automata);
		getContentPane().add(toolbox3, BorderLayout.SOUTH);
		this.setJMenuBar(new GraphMenu(this, graphComponent));
		this.setPreferredSize(new Dimension(1100, 700));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void addShapeType(String name, final int type) {
		ButtonWithIcon button = new ButtonWithIcon(centerBreakLine(name), type);
	    button.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		graphComponent.setPaintType(type);
	    		shapeTypeSelected = type;
	    		toolbox.revalidate();
	    		toolbox.repaint();
	    		requestFocus();
	    	}
	    });
	    buttons.add(button);
	}
	
	public void setAutomata(Automata automata) {
		automata.setStatusPanel(label);
	}
	
	private String centerBreakLine(String string) {
		String result = string.replace(" ", "<br />");
		return "<html><br /><br /><center>" + result + "</center></html>";
	}
	
	private class ButtonWithIcon extends JButton
	{
		private Shape shape;
		private int type;
		private Point2D point;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ButtonWithIcon(String name, int type) {
			super(name);
			this.type = type;
			shape = factory.getShape(type);
			point = getLocation();
			shape.setPosition(new Point2D.Double(point.getX() + width/2 - 7, point.getY()+ 30));
			shape.setAsIcon();
		}

		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			Graphics2D ga = (Graphics2D) g;
			ga.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			shape.draw(ga);
			if (shapeTypeSelected == type) {
				Shape check = factory.getShape(ShapeFactory.CHECK);
				check.setPosition(new Point2D.Double(point.getX() + width/2 - 7, point.getY()+ 30));
				check.draw(ga);
			}
		}
		
		
	}
	
	public static class LabelStatusMessage extends JLabel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String message;
		private boolean status;
		private String error;
		private static LabelStatusMessage INSTANCE;
		private static final String START_MESSAGE = "Automaton Editor is started, waiting for states";
		private static final String SUCCESS_MESSAGE = "Automaton is corrected";
		private static final String FAILED_MESSAGE = "Automaton is incorrect, Error: ";
		private static final String SUCCESS_RECOGNIZED = "Automaton has success to recognize word: ";
		private static final String FAILED_RECOGNIZED = "Automaton has unable to recognize word: ";
		
		public static LabelStatusMessage getInstance()
		{
			return (INSTANCE == null) ? new LabelStatusMessage() : INSTANCE;
		}
		
		private LabelStatusMessage() {
			setMessage(START_MESSAGE);
			this.status = true;
		}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			this.setText(message);
			Graphics2D ga = (Graphics2D) g;
			ga.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			super.paintComponent(ga);
			Point2D p = this.getLocation();
			Color current = ga.getColor();
			if (status) {
				ga.setColor(Color.green);
			} else {
				ga.setColor(Color.red);
			}
				
			ga.fillOval((int)p.getX()+2, (int)p.getY(), 10, 10);
			
			ga.setColor(current);
		}

		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = "          "+ message;
		}
		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
			if (status) {
				setMessage(SUCCESS_MESSAGE);
			} else {
				setMessage(FAILED_MESSAGE + error);
			}
			this.revalidate();
			this.repaint();
		}
		
		public void setRecognize(boolean status, String word) {
			this.status = status;
			if (status) {
				setMessage(SUCCESS_RECOGNIZED + word);
			} else {
				setMessage(FAILED_RECOGNIZED + word);
			}
		}
		
		public void setError(String error) {
			this.error = error;
		}
		

	}

	
	public static List<JFrame> instances = null;
	
	public static JFrame createNewWindows()
	{
		if (instances == null) {
			instances = new ArrayList<>();
		}
		GraphFrame graph = new GraphFrame();
		graph.setVisible(true);
		graph.pack();
		instances.add(graph);
		return graph;
	}
}
