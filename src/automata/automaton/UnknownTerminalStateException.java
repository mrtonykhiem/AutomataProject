package automata.automaton;

public class UnknownTerminalStateException extends Exception {
	private static final long serialVersionUID = 1L;

	public String getMessage() {
		return "Please add finish states in your automata";
	}
}
