package continuousdomain;


import burlap.behavior.functionapproximation.dense.DenseCrossProductFeatures;
import burlap.behavior.functionapproximation.dense.NormalizedVariableFeatures;
import burlap.behavior.functionapproximation.dense.fourier.FourierBasis;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.learning.lspi.LSPI;
import burlap.behavior.singleagent.learning.lspi.SARSCollector;
import burlap.behavior.singleagent.learning.lspi.SARSData;
import burlap.behavior.singleagent.learning.lspi.SARSData.SARS;
import burlap.domain.singleagent.mountaincar.MCRandomStateGenerator;
import burlap.domain.singleagent.mountaincar.MCState;
import burlap.domain.singleagent.mountaincar.MountainCar;
import burlap.domain.singleagent.mountaincar.MountainCarVisualizer;
import burlap.domain.singleagent.mountaincar.MountainCar.MCModel;
import burlap.mdp.auxiliary.StateGenerator;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.VisualActionObserver;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.visualizer.Visualizer;

public class ContinuousDomainTutorial {
	// To solve this problem, we will use Least-Squares Policy Iteration (LSPI).
	// LSPI requires a collection of state-action-reward-state (SARS) transition tuples
	// that are sampled from the domain.
	
	// LSPI starts by initializing with a random policy and then
	// uses the collected SARS samples to approximate the Q-value function of that policy
	// for the continuous state space.
	// Afterwards, the policy is updated by choosing actions that have the highest Q-values (policy improvement).
	// This process repeats until the approximate Q-value function (and consequently the policy) stops changing much.
	
	// LSPI approximates the Q-value function by
	// fitting a linear function of state basis features to the SARS data it collected,
	// similar to a typical regression problem, which is what enables the value function to generalize to unseen states
	
	public static void main(String[] args) {
		MCLSPIFB();
	}
	
	public static void MCLSPIFB(){
		// MCLSPIFB: mountain car, Least-Squares Policy Iteration, Fourier basis function
		
		// Generate domain
		MountainCar mcGen = new MountainCar(); // default: reward 100 on the right side and 0 everywhere else
		
		
		// Define terminal function and reward function	
		StateConditionTest terminalTest = new StateConditionTest() {
			
			@Override
			public boolean satisfies(State s) {
				// TODO Auto-generated method stub
				if ((Double)s.get("x") >= 0.5) {
					return true;
				}
				return false;
			}
		};
		
		TerminalFunction tf = new TerminalFunction() {
			
			@Override
			public boolean isTerminal(State s) {
				// TODO Auto-generated method stub
				return terminalTest.satisfies(s);
			}
		};
		
		RewardFunction rf = /*new GoalBasedRF(tf, 100, 0);*/
				new RewardFunction() {
			
			@Override
			public double reward(State s1, Action a, State s2) {
				// TODO Auto-generated method stub
				if ((Double)s2.get("x") >= 0.5) {
					return 100;
				}
				if ((Double)s2.get("x") == mcGen.physParams.xmin) {
					return -100;
				}
				return 0;
			}
		};
		mcGen.setRf(rf);
		SADomain domain = mcGen.generateDomain();
		System.out.println("physParams:\n===========================");
		System.out.println("xmin:" + mcGen.physParams.xmin + ", xmax:" + mcGen.physParams.xmax + ", vallypos:" + mcGen.physParams.valleyPos());
		System.out.println("vmin:" + mcGen.physParams.vmin + ", vmax:" + mcGen.physParams.vmax);
		System.out.println("===========================");
		
		// Generate data from random state
		StateGenerator rstateGenerator = new MCRandomStateGenerator(mcGen.physParams);
		SARSCollector collector = new SARSCollector.UniformRandomSARSCollector(domain);
		SARSData dataset = collector.collectNInstances(rstateGenerator, domain.getModel(), 5000, 20, null);

		
		SARS xData = dataset.get(0);
		MCState xState = (MCState) xData.s;
		MCState xState2 = (MCState) xData.sp;
		System.out.println("s: x:" + xState.x
				+ ", v:" + xState.v + 
				", action:" + xData.a.actionName() + 
				", reward:" + xData.r);
		System.out.println("s': x:" + xState2.x
				+ ", v:" + xState2.v);
		System.out.println("===========================");
		
		// Generate 5000 SARS tuple instances for our dataset.
		// Car state generator for no more than 20 steps at a time or until we hit a terminal state.
		
		// Generate Fourier Basis
		// Fourier Basis: mapping from states to features
		NormalizedVariableFeatures inputFeatures = new NormalizedVariableFeatures()
				.variableDomain("x", new VariableDomain(mcGen.physParams.xmin, mcGen.physParams.xmax))
				.variableDomain("v", new VariableDomain(mcGen.physParams.vmin, mcGen.physParams.vmax));
		FourierBasis fBasis = new FourierBasis(inputFeatures, 4); // order(4) means the number of basis functions
		
		// Instantiate LSPI
		// LSPI needs mapping from state to action features, so
		// first define a state feature representation, 
		// and then construct state-action features by taking the cross product of those features with each action
		LSPI lspi = new LSPI(domain, 0.99, new DenseCrossProductFeatures(fBasis, 3), dataset); // 3: number of actions: FORWARD, COSTA, BACKWARD
		Policy policy = lspi.runPolicyIteration(30, 1e-6);
		
		// Visualization
		MCState laststate = new MCState(mcGen.physParams.valleyPos(), 0);
		System.out.println("===========================\nState process according to policy:\n");

		
		for (int i = 0; i < 1000; i++) {
			if (terminalTest.satisfies(laststate)) {
				break;
			}
			Action curAction = policy.action(laststate);
			MCModel model = new MCModel(mcGen.physParams);
			MCState curState = (MCState) model.sample(laststate, curAction);
			if (i % 10 == 0) {
				System.out.println("State " + i +
						":\tx = " + laststate.x + 
						";\tv = " + laststate.v + 
						";\taction = " + curAction.actionName());
			}			
			laststate = curState;
		}
		Visualizer visualizer = MountainCarVisualizer.getVisualizer(mcGen);
		VisualActionObserver vo = new VisualActionObserver(visualizer);
		vo.initGUI();
		
		SimulatedEnvironment environment = new SimulatedEnvironment(domain, new MCState(mcGen.physParams.valleyPos(), 0));
		environment.addObservers(vo);
		
		for (int i = 0; i < 5; i++) {
			PolicyUtils.rollout(policy, environment);
			environment.resetEnvironment();
		}
		System.out.println("Finished");
		System.out.println(policy.action(new MCState(0.05, -8)));
	}
}
