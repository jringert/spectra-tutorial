package counterstrategy;

import tau.smlab.syntech.bddgenerator.BDDGenerator;
import tau.smlab.syntech.gameinput.model.GameInput;
import tau.smlab.syntech.gameinputtrans.TranslationProvider;
import tau.smlab.syntech.gamemodel.GameModel;
import tau.smlab.syntech.games.controller.enumerate.ConcreteControllerConstruction;
import tau.smlab.syntech.games.controller.enumerate.EnumStrategyI;
import tau.smlab.syntech.games.controller.enumerate.printers.MAAMinimizeAutomatonPrinter;
import tau.smlab.syntech.games.controller.enumerate.printers.SimpleTextPrinter;
import tau.smlab.syntech.games.rabin.RabinConcreteControllerConstruction;
import tau.smlab.syntech.games.rabin.RabinGame;
import tau.smlab.syntech.games.rabin.RabinGameImplC;
import tau.smlab.syntech.jtlv.BDDPackage;
import tau.smlab.syntech.jtlv.BDDPackage.BBDPackageVersion;
import tau.smlab.syntech.jtlv.Env;
import tau.smlab.syntech.spectragameinput.SpectraInputProviderNoIDE;

public class CreateCounterStrategy {

	public static String specPath = "TrafficA1b.spectra";

	public static void main(String[] args) throws Exception {		
		// BDDPackage.setCurrPackage(BDDPackage.JTLV, BBDPackageVersion.DEFAULT);
		BDDPackage.setCurrPackage(BDDPackage.CUDD, BBDPackageVersion.CUDD_3_0);
		Env.enableReorder();

		// get the Xtext-based input parser
		SpectraInputProviderNoIDE sip = new SpectraInputProviderNoIDE();
		// parse (via Xtext) and translate to abstract syntax (Xtext independent)
		GameInput gi = sip.getGameInput(specPath);
		// important step to reduce language features to the Spectra Kernel
		TranslationProvider.translate(gi);
		GameModel m = BDDGenerator.generateGameModel(gi);
		
		RabinGame rg = new RabinGameImplC(m);
		if (!rg.checkRealizability()) {
			System.err.println("Error: Specification is realizable.");
			return;
		}
		
		ConcreteControllerConstruction cc = new RabinConcreteControllerConstruction(rg.getMem(), m);
		EnumStrategyI cs = cc.calculateConcreteController();
		
		
		System.out.println("Printing counter-strategy in classic JTLV format:");
		new SimpleTextPrinter().printController(System.out, cs);
		
		System.out.println("Printing counter-strategy in minimized I/O automaton format:");
		MAAMinimizeAutomatonPrinter.REMOVE_DEAD_STATES = false;
		new MAAMinimizeAutomatonPrinter(m).printController(System.out, cs);
	}
	
	
}
