package automata.automaton;

public class NotDeterministInitialStateException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotDeterministInitialStateException(State<?> e1, State<?> e2) {

	}

	public String getMessage() {
		return "two much initial states";
	}
}
