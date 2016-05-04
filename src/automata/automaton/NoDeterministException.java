package automata.automaton;

public class NoDeterministException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoDeterministException() {
	}

	public String getMessage() {
		return "Please add transition to your automata";
	}
}
