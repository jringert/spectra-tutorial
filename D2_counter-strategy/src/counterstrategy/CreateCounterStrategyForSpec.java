package counterstrategy;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import tau.smlab.syntech.bddgenerator.BDDGenerator;
import tau.smlab.syntech.gameinput.model.GameInput;
import tau.smlab.syntech.gameinputtrans.TranslationProvider;
import tau.smlab.syntech.gamemodel.GameModel;
import tau.smlab.syntech.games.controller.enumerate.ConcreteControllerConstruction;
import tau.smlab.syntech.games.controller.enumerate.EnumStrategyI;
import tau.smlab.syntech.games.controller.enumerate.StateI;
import tau.smlab.syntech.games.controller.enumerate.printers.MAAMinimizeAutomatonPrinter;
import tau.smlab.syntech.games.controller.enumerate.printers.SimpleTextPrinter;
import tau.smlab.syntech.games.rabin.RabinConcreteControllerConstruction;
import tau.smlab.syntech.games.rabin.RabinGame;
import tau.smlab.syntech.jtlv.BDDPackage;
import tau.smlab.syntech.jtlv.BDDPackage.BBDPackageVersion;
import tau.smlab.syntech.jtlv.Env;
import tau.smlab.syntech.spectragameinput.SpectraInputProviderNoIDE;

public class CreateCounterStrategyForSpec {

	public static void main(String[] args) throws Exception {
		// alternative BDD engine for pure Java
//		 BDDPackage.setCurrPackage(BDDPackage.JTLV, BBDPackageVersion.DEFAULT);
		BDDPackage.setCurrPackage(BDDPackage.CUDD, BBDPackageVersion.CUDD_3_0);
		Env.enableReorder();

		// get the Xtext-based input parser
		SpectraInputProviderNoIDE sip = new SpectraInputProviderNoIDE();
		// parse (via Xtext) and translate to abstract syntax (Xtext independent)
		GameInput gi = sip.getGameInput(args[0]);
		// important step to reduce language features to the Spectra Kernel
		TranslationProvider.translate(gi);
		GameModel m = BDDGenerator.generateGameModel(gi);

		RabinGame rg = new RabinGame(m);
		if (!rg.checkRealizability()) {
			System.err.println("Error: Specification is realizable.");
			return;
		}

		// constructing concrete controller
		ConcreteControllerConstruction cc = new RabinConcreteControllerConstruction(rg.getMem(), m);
		EnumStrategyI cs = cc.calculateConcreteController();

		MAAMinimizeAutomatonPrinter.REMOVE_DEAD_STATES = false;
		new MAAMinimizeAutomatonPrinter(m).printController(System.out, cs);
	}

}
