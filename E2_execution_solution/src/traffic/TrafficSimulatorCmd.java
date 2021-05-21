package traffic;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;

public class TrafficSimulatorCmd {

	public static void main(String args[]) throws Exception {

		Map<String, String> inputs = new HashMap<>();
        Scanner in = new Scanner(System.in);
        
		// Instantiate a new controller executor
		ControllerExecutor executor = new ControllerExecutor(new BasicJitController(), "out/");

		boolean iniState = true;
		while (true) {
			inputs.clear();
			
			System.out.println("Is carA present? (Y/n)");
			String line = in.nextLine();
			boolean carA = !"n".equals(line);
			
			System.out.println("Is carB present? (Y/n)");
			line = in.nextLine();
			boolean carB = !"n".equals(line);
			
			// send inputs to controller
			inputs.put("carA", Boolean.toString(carA));
			inputs.put("carB", Boolean.toString(carB));
			System.out.println(inputs);
			
			// execute controller
			if (iniState) {
				executor.initState(inputs);
				iniState = false;
			} else {
				executor.updateState(inputs);
			}

			// read outputs
			boolean greenA = Boolean.parseBoolean(executor.getCurrValue("greenA"));
			boolean greenB = Boolean.parseBoolean(executor.getCurrValue("greenB"));
			
			if (greenA) {
				System.out.println("-- carA is allowed to go");
			} else if (greenB) {
				System.out.println("-- carB is allowed to go");
			} else {
				System.out.println("-- all cars must stop");
			}
		}
	}

}
