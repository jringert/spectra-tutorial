package parsing;

import tau.smlab.syntech.gameinput.model.Constraint;
import tau.smlab.syntech.gameinput.model.GameInput;
import tau.smlab.syntech.gameinput.model.Player;
import tau.smlab.syntech.gameinput.model.Variable;
import tau.smlab.syntech.gameinputtrans.TranslationProvider;
import tau.smlab.syntech.spectragameinput.ErrorsInSpectraException;
import tau.smlab.syntech.spectragameinput.SpectraInputProviderNoIDE;
import tau.smlab.syntech.spectragameinput.SpectraTranslationException;

/**
 * Example of how to parse spectra specifications.
 */
public class RunSpectraParser {

	public static void main(String[] args) throws ErrorsInSpectraException, SpectraTranslationException {

		String specPath = "TrafficL3.spectra";

		// get the Xtext-based input parser
		SpectraInputProviderNoIDE sip = new SpectraInputProviderNoIDE();
		// parse (via Xtext) and translate to abstract syntax (Xtext independent)
		GameInput gi = sip.getGameInput(specPath);

		System.out.println("Printing vars and constraints of environment player:");
		Player env = gi.getEnv();
		printVars(env);
		printConstraints(env);

		System.out.println("\nPrinting vars and constraints of system player:");
		Player sys = gi.getSys();
		printVars(sys);
		printConstraints(sys);

		System.out.println("\nTranslating to Spectra Kernel.");
		// important step to reduce language features to the Spectra Kernel
		TranslationProvider.translate(gi);
		Player aux = gi.getAux();

		System.out.println("\nPrinting vars and constraints of environment player:");
		printVars(env);
		printConstraints(env);

		System.out.println("\nPrinting vars and constraints of system and aux players:");
		printVars(sys);
		printVars(aux);
		printConstraints(sys);
		printConstraints(aux);

	}

	private static void printVars(Player p) {
		for (Variable v : p.getVars()) {
			System.out.println(v.toString());
		}
	}

	private static void printConstraints(Player p) {
		for (Constraint c : p.getConstraints()) {
			switch (c.getKind()) {
			case INI:
				System.out.println("Initial constraint " + c.getSpec());
				break;
			case SAFETY:
				System.out.println("Safety constraint " + c.getSpec());
				break;
			case STATE_INV:
				System.out.println("State invariant " + c.getSpec());
				break;
			case JUSTICE:
				System.out.println("Justice constraint " + c.getSpec());
				break;
			case PATTERN:
				System.out.println("Pattern constraint " + c.getSpec());
				break;
			default:
				break;
			}
		}
	}

}
