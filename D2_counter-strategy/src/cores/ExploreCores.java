package cores;

import java.util.ArrayList;
import java.util.List;

import tau.smlab.syntech.bddgenerator.BDDGenerator;
import tau.smlab.syntech.bddgenerator.BDDGenerator.TraceInfo;
import tau.smlab.syntech.cores.domainagnostic.AbstractDdmin;
import tau.smlab.syntech.cores.util.RealizabilityCheck;
import tau.smlab.syntech.gameinput.model.GameInput;
import tau.smlab.syntech.gameinputtrans.TranslationProvider;
import tau.smlab.syntech.gamemodel.GameModel;
import tau.smlab.syntech.gamemodel.util.SysTraceInfoBuilder;
import tau.smlab.syntech.gamemodel.util.TraceIdentifier;
import tau.smlab.syntech.jtlv.BDDPackage;
import tau.smlab.syntech.jtlv.BDDPackage.BBDPackageVersion;
import tau.smlab.syntech.jtlv.Env;
import tau.smlab.syntech.spectragameinput.SpectraInputProviderNoIDE;

public class ExploreCores {

	public static String specPath = "TrafficA1b.spectra";

	public static void main(String[] args) throws Exception {
		// alternative BDD engine for pure Java
		
//		BDDPackage.setCurrPackage(BDDPackage.JTLV, BBDPackageVersion.DEFAULT);
//		RealizabilityCheck.useCUDD = false;
		
		BDDPackage.setCurrPackage(BDDPackage.CUDD, BBDPackageVersion.CUDD_3_0);
		RealizabilityCheck.useCUDD = true;
		
		Env.enableReorder();

		// get the Xtext-based input parser
		SpectraInputProviderNoIDE sip = new SpectraInputProviderNoIDE();
		// parse (via Xtext) and translate to abstract syntax (Xtext independent)
		GameInput gi = sip.getGameInput(specPath);
		// important step to reduce language features to the Spectra Kernel
		TranslationProvider.translate(gi);
		GameModel m = BDDGenerator.generateGameModel(gi, TraceInfo.ALL);
		
		// set up a builder to build GameModels with subsets of the guarantees
		final SysTraceInfoBuilder builder = new SysTraceInfoBuilder(m);

		List<Integer> result = new ArrayList<Integer>();
		AbstractDdmin<Integer> ucmin = new AbstractDdmin<Integer>() {
			@Override 
			public boolean check(List<Integer> part) {
				return !RealizabilityCheck.isRealizable(builder.build(part));
			}
		};
		result.addAll(ucmin.minimize(builder.getTraceList()));
		
		System.out.println(
				"Found unrealizable core with " + result.size() + " elements, at lines "  + TraceIdentifier.formatLines(result) + ".");		
	}

}
