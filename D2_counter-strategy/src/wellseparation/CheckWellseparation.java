package wellseparation;

import java.util.List;

import tau.smlab.syntech.bddgenerator.BDDGenerator;
import tau.smlab.syntech.bddgenerator.BDDGenerator.TraceInfo;
import tau.smlab.syntech.cores.util.RealizabilityCheck;
import tau.smlab.syntech.gameinput.model.GameInput;
import tau.smlab.syntech.gameinputtrans.TranslationProvider;
import tau.smlab.syntech.gamemodel.GameModel;
import tau.smlab.syntech.games.gr1.wellseparation.WellSeparationChecker;
import tau.smlab.syntech.games.gr1.wellseparation.WellSeparationChecker.EnvSpecPart;
import tau.smlab.syntech.games.gr1.wellseparation.WellSeparationChecker.Positions;
import tau.smlab.syntech.games.gr1.wellseparation.WellSeparationChecker.Systems;
import tau.smlab.syntech.jtlv.BDDPackage;
import tau.smlab.syntech.jtlv.BDDPackage.BBDPackageVersion;
import tau.smlab.syntech.jtlv.Env;
import tau.smlab.syntech.spectragameinput.SpectraInputProviderNoIDE;

public class CheckWellseparation {

	public static String specPath = "GridA3.spectra";
//	public static String specPath = "TrafficA1b.spectra";

	public static void main(String[] args) throws Exception {
		// alternative BDD engine for pure Java

		BDDPackage.setCurrPackage(BDDPackage.JTLV, BBDPackageVersion.DEFAULT);
		RealizabilityCheck.useCUDD = false;

//		BDDPackage.setCurrPackage(BDDPackage.CUDD, BBDPackageVersion.CUDD_3_0);
//		RealizabilityCheck.useCUDD = true;

		Env.enableReorder();

		// get the Xtext-based input parser
		SpectraInputProviderNoIDE sip = new SpectraInputProviderNoIDE();
		// parse (via Xtext) and translate to abstract syntax (Xtext independent)
		GameInput gi = sip.getGameInput(specPath);
		// important step to reduce language features to the Spectra Kernel
		TranslationProvider.translate(gi);
		GameModel m = BDDGenerator.generateGameModel(gi, TraceInfo.ALL);

		WellSeparationChecker wsc = new WellSeparationChecker();
		// most general check for wellseparation:
		// takes system guarantees into account, checks for justice assumptions
		// (includes all other assumptions), from all positions
		boolean wellsep = wsc.checkEnvWellSeparated(m, Systems.SPEC, EnvSpecPart.JUSTICE, Positions.ALL, false);
		System.out.println(wellsep);

		// not sure if it is safe to run it again, the checker might modify the game
		// model
		List<String> report = wsc.diagnose(m, Systems.SPEC);
		System.out.println(report);
	}

}
