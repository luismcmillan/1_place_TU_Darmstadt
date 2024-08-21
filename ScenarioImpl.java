package solutions.exercise6;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.sopra.api.Game;
import org.sopra.api.Scenario;
import org.sopra.api.Statistics;
import org.sopra.api.exercises.ExerciseSubmission;
import org.sopra.api.exercises.exercise3.FlowGraph;
import org.sopra.api.exercises.exercise5.AbstractEnergyNetworkAnalyzer;
import org.sopra.api.model.EnergyNode;
import org.sopra.api.model.PlantLocation;
import org.sopra.api.model.PlayfieldElement;
import org.sopra.api.model.PlayfieldElement.ElementType;
import org.sopra.api.model.PowerLine;
import org.sopra.api.model.PowerLineType;
import org.sopra.api.model.consumer.City;
import org.sopra.api.model.consumer.CommercialPark;
import org.sopra.api.model.consumer.Consumer;
import org.sopra.api.model.consumer.ConsumerType;
import org.sopra.api.model.consumer.ControllableConsumer;
import org.sopra.api.model.consumer.IndustrialPark;
import org.sopra.api.model.producer.BioGasFiredPowerPlant;
import org.sopra.api.model.producer.CoalFiredPowerPlant;
import org.sopra.api.model.producer.ControllableProducer;
import org.sopra.api.model.producer.GasFiredPowerPlant;
import org.sopra.api.model.producer.HydroPowerPlant;
import org.sopra.api.model.producer.NuclearPowerPlant;
import org.sopra.api.model.producer.Producer;
import org.sopra.api.model.producer.ProducerType;
import org.sopra.api.model.producer.SolarPowerPlant;
import org.sopra.api.model.producer.WindPowerPlant;
import org.sopra.exceptions.CannotAssignCommandException;
import org.sopra.exceptions.CannotExecuteCommandException;

import solutions.exercise3.FlowGraphImpl;
import solutions.exercise5.EnergyNetworkAnalyzerImpl;
/**
*Implements algorithm which controlls the game
*@author McMillan
*@version 1.0
**/

public class ScenarioImpl implements Game, ExerciseSubmission {
	
	Map<Producer, ArrayList<Double>> current_producer_status = new HashMap<Producer, ArrayList<Double>>();
	Map<Consumer, ArrayList<Double>> current_consumer_status = new HashMap<Consumer, ArrayList<Double>>();
	Map<Producer, Double> producer_multiplicator = new HashMap<Producer, Double>();
	Map<Producer, ArrayList<Double>> wind_correction = new HashMap<Producer, ArrayList<Double>>();
	Map<Producer, ArrayList<Double>> solar_plants = new HashMap<Producer, ArrayList<Double>>();
	Map<Producer, ArrayList<Double>> wind_plants = new HashMap<Producer, ArrayList<Double>>();
	Map<Producer, ArrayList<Double>> hydro_plants = new HashMap<Producer, ArrayList<Double>>();
	Map<Producer, ArrayList<Double>> biogas_plants = new HashMap<Producer, ArrayList<Double>>();
	Map<Producer, ArrayList<Double>> coal_plants = new HashMap<Producer, ArrayList<Double>>();
	Map<Producer, ArrayList<Double>> gas_plants = new HashMap<Producer, ArrayList<Double>>();
	Map<Producer, ArrayList<Double>> nuclear_plants = new HashMap<Producer, ArrayList<Double>>();
	Map<Consumer, ArrayList<Double>> industri_plants = new HashMap<Consumer,  ArrayList<Double>>();
	Map<Consumer, ArrayList<Double>> city_plants = new HashMap<Consumer,  ArrayList<Double>>();
	Map<Consumer, ArrayList<Double>> commercial_plants = new HashMap<Consumer,  ArrayList<Double>>();
	Map<Producer, Integer> producer_summary = new HashMap<Producer, Integer>();
	Map<Consumer, Integer> consumer_summary = new HashMap<Consumer, Integer>();
	Map<Consumer, Integer> maximum_receivable_power_list = new HashMap<Consumer, Integer>();
	Map<Producer, Integer> maximum_sendable_power_list = new HashMap<Producer, Integer>();
	double city_usage[] = {0.2,0.2,0.1,0.1,0.3,0.7,0.9,1,1,0.8,0.3,0.4,0.7,0.8,0.7,0.6,0.5,0.6,0.7,0.8,0.9,0.8,0.7,0.4};
	double commercial_usage[] = {0.05,0,0,0,0.05,0.3,0.6,0.8,0.9,1,1,1,0.7,0.9,1,1,1,0.8,0.7,0.6,0.3,0.2,0.1,0.05};
	private double overall_needed_power[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	double current_needed_power[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	double overall_needed_power_without_greenpeace[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	int on_time_coal[] = new int[24];
	Integer maximum_possible_required_power = 0;
	Integer maximum_receivable_power_sum = 0;
	
	
	
	@Override

	/**
	*This phase is called before the scenario actually starts. (In other words in round 0.) During this phase, power lines can be upgraded and power plants can be built on transformer station locations.
	*@param arg0 Scenario
	**/
	
	public void buildPhase(Scenario arg0) {
		int counter = 0;
		//System.out.println("GEBAUT");
		for(PlantLocation node : arg0.getPlantLocations()) {
			counter++;
			if(node.getPlayfieldElement().getElementType().equals(ElementType.SEA) ||node.getPlayfieldElement().getElementType().equals(ElementType.BEACH )||node.getPlayfieldElement().getElementType().equals(ElementType.MOUNTAIN)) {
				if(node.isBuilt() == false) {
					try {
						arg0.getCommandFactory().createBuildPlantCommand(node, ProducerType.WIND_POWER_PLANT).execute();
						//System.out.println("WIND GEBAUT");
					} catch (CannotExecuteCommandException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(node.getPlayfieldElement().getElementType().equals(ElementType.RIVER)) {
				if(node.isBuilt() == false) {
					try {
						arg0.getCommandFactory().createBuildPlantCommand(node, ProducerType.HYDRO_POWER_PLANT).execute();
						//System.out.println("WIND GEBAUT");
					} catch (CannotExecuteCommandException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
				
			else {
				if(node.isBuilt() == false) {
					try {
						arg0.getCommandFactory().createBuildPlantCommand(node, ProducerType.GASFIRED_POWER_PLANT).execute();
					} catch (CannotExecuteCommandException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				/*
				if(node.isBuilt() == false && (counter % 2 ==1)) {
					try {
						arg0.getCommandFactory().createBuildPlantCommand(node, ProducerType.COALFIRED_POWER_PLANT).execute();
					} catch (CannotExecuteCommandException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				*/
				
			}
			
		}
		ArrayList<PowerLine> one_powerLine = new ArrayList<PowerLine>();
		ArrayList<PowerLine> two_powerLine = new ArrayList<PowerLine>();
		Map<EnergyNode, ArrayList<PowerLine>> nodes_amount_of_edges = new HashMap<EnergyNode, ArrayList<PowerLine>>();
		for(EnergyNode node : arg0.getGraph().getNodes()) {
			ArrayList<PowerLine> pline_list = new ArrayList<PowerLine>();
			nodes_amount_of_edges.put(node, pline_list);
		}
		for(EnergyNode node : arg0.getGraph().getNodes()) {
			for(PowerLine pline : arg0.getGraph().getEdges()) {
				if(pline.getEnd() == node) {
					nodes_amount_of_edges.get(node).add(pline);
				}
			}
		}
		for(EnergyNode node : nodes_amount_of_edges.keySet()) {
			if(nodes_amount_of_edges.get(node).size() == 1) {
				one_powerLine.add(nodes_amount_of_edges.get(node).get(0));
			}
			if(nodes_amount_of_edges.get(node).size() == 2) {
				two_powerLine.add(nodes_amount_of_edges.get(node).get(0));
				two_powerLine.add(nodes_amount_of_edges.get(node).get(1));
			}
		}
		
		for(PowerLine pline : arg0.getGraph().getEdges()) {
			if(one_powerLine.contains(pline)) {
				try {
					//System.out.println(pline);
					if(pline.getCapacity()< 800 ) {
						arg0.getCommandFactory().createUpgradeLineCommand(pline, PowerLineType.HIGH_VOLTAGE).execute();
					}
					
				} catch (CannotExecuteCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}else if(two_powerLine.contains(pline)) {
				
				try {
					if(pline.getCapacity()< 800 ) {
						arg0.getCommandFactory().createUpgradeLineCommand(pline, PowerLineType.HIGH_VOLTAGE).execute();
					}
				} catch (CannotExecuteCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}else {
				try {
					if(pline.getCapacity()< 300 ) {
						arg0.getCommandFactory().createUpgradeLineCommand(pline, PowerLineType.MEDIUM_VOLTAGE).execute();
					}
				} catch (CannotExecuteCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
					
			}
		}
	}

	@Override
	/**
	*This phase is called for each round of the execution phase. In each round, the non-controllable energy nodes change their input or output of energy. This can be a random change or one that depends on specific factors (e.g.: solar plants which produce specific energy dependent on day time). During each round several commands can be executed to regulate controllable energy nodes.
	*@param scenario Scenario
	*@param runde number of round
	**/
	public void executionPhase(Scenario scenario, int runde) {
		Statistics statistics = scenario.getStatistics();
		double wind =statistics.getWindStrength();
		//System.out.println("runde = "+runde+"   windstrength = "+wind);
		update_wind(runde,wind);
		/*
		for(int i = 0;i<24;i++) {
			System.out.println(overall_needed_power[i]+"     "+overall_needed_power_without_greenpeace[i]+"     " +current_needed_power[i]);
		}
		for(Producer prod : wind_plants.keySet()) {
			System.out.println(prod+"   "+wind_plants.get(prod));
		}
		*/
		
		if(runde == 0) {
			filling_all_Producer_and_Consumer_Lists(scenario);
			calculate_maximum_possible_required_power();
			calculate_maximum_receivable_power_sum(scenario);
			calculate_maximum_receivable_power_for_each_consumer(scenario);
			calculate_maximum_sendable_power_for_each_producer(scenario);
			
			calculate_overall_needed_power();
			calculate_current_needed_power(scenario);
			calculate_overall_required_power_without_greenpeace(scenario);
			
			
			//NUCLEAR
			calculation_for_nuclear_timeslots(scenario);
			for(Producer prod : nuclear_plants.keySet()) {
				calculate_current_needed_power(scenario);
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*nuclear_plants.get(prod).get(6))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				}
			}
			
			//COAL
			calculation_for_coal_timeslots(scenario);
			for(Producer prod : coal_plants.keySet()) {
				calculate_current_needed_power(scenario);
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(3))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				}
			}
			
			//GAS
			calculation_for_gas_timeslots(scenario);
			for(Producer prod : gas_plants.keySet()) {
				calculate_current_needed_power(scenario);
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(1))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				}
			}
			
			//BIOGAS
			calculation_for_biogas_timeslots(scenario);
			for(Producer prod : biogas_plants.keySet()) {
				calculate_current_needed_power(scenario);
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(2))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				}
			}
			
			//INDUSTRIE
			calculation_for_industri_timeslots(scenario);
			for(Consumer cons : industri_plants.keySet()) {
				calculate_current_needed_power(scenario);
				ControllableConsumer cont_cons = (ControllableConsumer) cons;
				try {
					scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(cons)*industri_plants.get(cons).get(3)-cons.getMaximumEnergyLevel())).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				}
			}
		}
		//for(Producer prod: gas_plants.keySet()) {
		//	System.out.println(prod +"   "+ gas_plants.get(prod));
		//}
		
		if((runde+10)%24 ==18) {
			for(Producer prod: nuclear_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*nuclear_plants.get(prod).get(18)-maximum_sendable_power_list.get(prod)*nuclear_plants.get(prod).get(6))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				}
			}
		}
		if((runde+10)%24 ==6) {//Nuclear
			for(Producer prod: nuclear_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*nuclear_plants.get(prod).get(6)-maximum_sendable_power_list.get(prod)*nuclear_plants.get(prod).get(18))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		
		
		 
		if((runde+3)%24 ==3) {//Coal
			for(Producer prod: coal_plants.keySet()) {
				//accurate_power = calculate_if_new_power_is_good(scenario, prod, coal_plants.get(prod).get(3)*maximum_sendable_power_list.get(prod));
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					if(cont_prod.getRemainingAdjustmentTime() == 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(3)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(0))).assign();
					}
					} 
				catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
			
		}
		if((runde+3)%24 ==6) {//Coal
			for(Producer prod: coal_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(6)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(3))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
			
		}
		if((runde+3)%24 ==9) {
			for(Producer prod: coal_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(9)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(6))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
			
		}
		if((runde+3)%24 ==12) {//Coal
			for(Producer prod: coal_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(12)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(9))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
			
		}
		if((runde+3)%24 ==15) {//Coal
			for(Producer prod: coal_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(15)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(12))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
			
		}
		if((runde+3)%24 ==18) {//Coal
			for(Producer prod: coal_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(18)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(15))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
			
		}
		if((runde+3)%24 ==21) {//Coal
			for(Producer prod: coal_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(21)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(18))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==0) {//Coal
			for(Producer prod: coal_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(0)-maximum_sendable_power_list.get(prod)*coal_plants.get(prod).get(21))).assign();
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		
		if((runde+1)%24 ==0) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(0),23)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(0));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(0),0)/maximum_sendable_power_list.get(prod),23);
					//System.out.println("1 "+gas_plants.get(prod).get(0)+"   -  "+gas_plants.get(prod).get(23));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(0)-gas_plants.get(prod).get(23))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(0)-gas_plants.get(prod).get(23))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(0)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(23))).assign();
					}
						
					} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==1) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					if(cont_prod.getRemainingAdjustmentTime() == 0) {
						//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(1),0)+"     original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(1));
						update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(1),1)/maximum_sendable_power_list.get(prod),1);
						//System.out.println("2 "+gas_plants.get(prod).get(1)+"   -  "+gas_plants.get(prod).get(0));
						if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(1)-gas_plants.get(prod).get(0))) < 0) {
							scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
						}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(1)-gas_plants.get(prod).get(0))) > prod.getMaximumEnergyLevel()){
							scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
						}else {
							scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(1)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(0))).assign();
						}
					}
					} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==2) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(2),1)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(2));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(2),2)/maximum_sendable_power_list.get(prod),2);
					//System.out.println("3 "+gas_plants.get(prod).get(2)+"   -  "+gas_plants.get(prod).get(1));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(2)-gas_plants.get(prod).get(1))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(2)-gas_plants.get(prod).get(1))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(2)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(1))).assign();
					}
					} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==3) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(3),2)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(3));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(3),3)/maximum_sendable_power_list.get(prod),3);
					//System.out.println(gas_plants.get(prod).get(3)+"   -  "+gas_plants.get(prod).get(2));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(3)-gas_plants.get(prod).get(2))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(3)-gas_plants.get(prod).get(2))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(2)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(2))).assign();
					}
					} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==4) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(4),3)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(4));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(4),4)/maximum_sendable_power_list.get(prod),4);
					//System.out.println(gas_plants.get(prod).get(4)+"   -  "+gas_plants.get(prod).get(3));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(4)-gas_plants.get(prod).get(3))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(4)-gas_plants.get(prod).get(3))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(4)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(3))).assign();
					}
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==5) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try { 
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(5),4)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(5));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(5),5)/maximum_sendable_power_list.get(prod),5);
					//System.out.println(gas_plants.get(prod).get(5)+"   -  "+gas_plants.get(prod).get(4));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(5)-gas_plants.get(prod).get(4))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(5)-gas_plants.get(prod).get(4))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(5)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(4))).assign();
					}
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==6) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(6),5)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(6));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(6),6)/maximum_sendable_power_list.get(prod),6);
					//System.out.println(gas_plants.get(prod).get(6)+"   -  "+gas_plants.get(prod).get(5));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(6)-gas_plants.get(prod).get(5))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(6)-gas_plants.get(prod).get(5))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(6)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(5))).assign();
					}
				} catch (CannotAssignCommandException e) {
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==7) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(7),6)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(7));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(7),7)/maximum_sendable_power_list.get(prod),7);
					//System.out.println(gas_plants.get(prod).get(7)+"   -  "+gas_plants.get(prod).get(6));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(7)-gas_plants.get(prod).get(6))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(7)-gas_plants.get(prod).get(6))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(7)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(6))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==8) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(8),7)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(8));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(8),8)/maximum_sendable_power_list.get(prod),8);
					//System.out.println(gas_plants.get(prod).get(8)+"   -  "+gas_plants.get(prod).get(7));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(8)-gas_plants.get(prod).get(7))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(8)-gas_plants.get(prod).get(7))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(8)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(7))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==9) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(9),8)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(9));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(9),9)/maximum_sendable_power_list.get(prod),9);
					//System.out.println(gas_plants.get(prod).get(9)+"   -  "+gas_plants.get(prod).get(8));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(9)-gas_plants.get(prod).get(8))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(9)-gas_plants.get(prod).get(8))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(9)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(8))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==10) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(10),9)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(10));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(10),10)/maximum_sendable_power_list.get(prod),10);
					//System.out.println(gas_plants.get(prod).get(10)+"   -  "+gas_plants.get(prod).get(9));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(10)-gas_plants.get(prod).get(9))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(10)-gas_plants.get(prod).get(9))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(10)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(9))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==11) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(11),10)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(11));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(11),11)/maximum_sendable_power_list.get(prod),11);
					//System.out.println(gas_plants.get(prod).get(11)+"   -  "+gas_plants.get(prod).get(10));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(11)-gas_plants.get(prod).get(10))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(11)-gas_plants.get(prod).get(10))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(11)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(10))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==12) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(12),11)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(12));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(12),12)/maximum_sendable_power_list.get(prod),12);
					//System.out.println(gas_plants.get(prod).get(12)+"   -  "+gas_plants.get(prod).get(11));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(12)-gas_plants.get(prod).get(11))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(12)-gas_plants.get(prod).get(11))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(12)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(11))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==13) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(13),12)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(13));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(13),13)/maximum_sendable_power_list.get(prod),13);
					//System.out.println(gas_plants.get(prod).get(13)+"   -  "+gas_plants.get(prod).get(12));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(13)-gas_plants.get(prod).get(12))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(13)-gas_plants.get(prod).get(12))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(13)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(12))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==14) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(14),13)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(14));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(14),14)/maximum_sendable_power_list.get(prod),14);
					//System.out.println(gas_plants.get(prod).get(14)+"   -  "+gas_plants.get(prod).get(13));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(14)-gas_plants.get(prod).get(13))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(14)-gas_plants.get(prod).get(13))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(14)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(13))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==15) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(15),14)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(15));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(15),15)/maximum_sendable_power_list.get(prod),15);
					//System.out.println(gas_plants.get(prod).get(15)+"   -  "+gas_plants.get(prod).get(14));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(15)-gas_plants.get(prod).get(14))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(15)-gas_plants.get(prod).get(14))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(15)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(14))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==16) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(16),15)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(16));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(16),16)/maximum_sendable_power_list.get(prod),16);
					//System.out.println(gas_plants.get(prod).get(16)+"   -  "+gas_plants.get(prod).get(15));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(16)-gas_plants.get(prod).get(15))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(16)-gas_plants.get(prod).get(15))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(16)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(15))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==17) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(17),16)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(17));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(17),17)/maximum_sendable_power_list.get(prod),17);
					//System.out.println(gas_plants.get(prod).get(17)+"   -  "+gas_plants.get(prod).get(16));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(17)-gas_plants.get(prod).get(6))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(17)-gas_plants.get(prod).get(16))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(17)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(16))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==18) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(18),17)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(18));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(18),18)/maximum_sendable_power_list.get(prod),18);
					//System.out.println(gas_plants.get(prod).get(18)+"   -  "+gas_plants.get(prod).get(17));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(18)-gas_plants.get(prod).get(17))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(18)-gas_plants.get(prod).get(17))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(18)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(17))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==19) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(19),18)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(19));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(19),19)/maximum_sendable_power_list.get(prod),19);
					//System.out.println(gas_plants.get(prod).get(19)+"   -  "+gas_plants.get(prod).get(18));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(19)-gas_plants.get(prod).get(18))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(19)-gas_plants.get(prod).get(18))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(19)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(18))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==20) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(20),19)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(20));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(20),20)/maximum_sendable_power_list.get(prod),20);
					//System.out.println(gas_plants.get(prod).get(20)+"   -  "+gas_plants.get(prod).get(19));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(20)-gas_plants.get(prod).get(19))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(20)-gas_plants.get(prod).get(19))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(20)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(19))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==21) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(21),20)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(21));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(21),21)/maximum_sendable_power_list.get(prod),21);
					//System.out.println(gas_plants.get(prod).get(21)+"   -  "+gas_plants.get(prod).get(20));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(21)-gas_plants.get(prod).get(20))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(21)-gas_plants.get(prod).get(20))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(21)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(20))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==22) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(22),21)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(22));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(22),22)/maximum_sendable_power_list.get(prod),22);
					//System.out.println(gas_plants.get(prod).get(22)+"   -  "+gas_plants.get(prod).get(21));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(22)-gas_plants.get(prod).get(21))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(22)-gas_plants.get(prod).get(21))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(22)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(21))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+1)%24 ==23) {//Gas
			for(Producer prod: gas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					//System.out.println("WIND KORREKTUR= "+calculate_correction_with_WIND(scenario,prod,maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(23),22)+"    original = "+maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(23));
					update_wind_corrections(prod,calculate_correction_with_WIND(scenario,prod, maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(23),23)/maximum_sendable_power_list.get(prod),23);
					//System.out.println(gas_plants.get(prod).get(23)+"   -  "+gas_plants.get(prod).get(22));
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(23)-gas_plants.get(prod).get(22))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(gas_plants.get(prod).get(23)-gas_plants.get(prod).get(22))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(23)-maximum_sendable_power_list.get(prod)*gas_plants.get(prod).get(22))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		
		if((runde+2)%24 ==1) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(1)-biogas_plants.get(prod).get(23))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(1)-biogas_plants.get(prod).get(23))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(1)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(23))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==3) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(3)-biogas_plants.get(prod).get(1))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(3)-biogas_plants.get(prod).get(1))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(3)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(1))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==5) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(5)-biogas_plants.get(prod).get(3))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(5)-biogas_plants.get(prod).get(3))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(5)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(3))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==7) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(7)-biogas_plants.get(prod).get(5))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(7)-biogas_plants.get(prod).get(5))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(7)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(6))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==9) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(9)-biogas_plants.get(prod).get(7))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(9)-biogas_plants.get(prod).get(7))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(9)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(7))).assign();
					}
					} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==11) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
					
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(11)-biogas_plants.get(prod).get(9))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(11)-biogas_plants.get(prod).get(9))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(11)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(9))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==13) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
				
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(13)-biogas_plants.get(prod).get(11))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(13)-biogas_plants.get(prod).get(11))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(13)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(11))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==15) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
				
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(15)-biogas_plants.get(prod).get(13))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(15)-biogas_plants.get(prod).get(13))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(15)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(13))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==17) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
				
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(17)-biogas_plants.get(prod).get(15))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(17)-biogas_plants.get(prod).get(15))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(17)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(15))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==19) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
				
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(19)-biogas_plants.get(prod).get(17))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(19)-biogas_plants.get(prod).get(17))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(19)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(17))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==21) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
				
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(21)-biogas_plants.get(prod).get(19))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(21)-biogas_plants.get(prod).get(19))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(21)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(19))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+2)%24 ==23) {//BioGas
			for(Producer prod: biogas_plants.keySet()) {
				ControllableProducer cont_prod = (ControllableProducer) prod;
				try {
				
					if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(23)-biogas_plants.get(prod).get(21))) < 0) {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, -prod.getProvidedPower()).assign();
					}else if((prod.getProvidedPower() + maximum_sendable_power_list.get(prod)*(biogas_plants.get(prod).get(23)-biogas_plants.get(prod).get(1))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, prod.getMaximumEnergyLevel()-prod.getProvidedPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustProducerCommand(cont_prod, (int)(maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(23)-maximum_sendable_power_list.get(prod)*biogas_plants.get(prod).get(1))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		
		if((runde+3)%24 ==3) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if(cont_cons.getRemainingAdjustmentTime() == 0) {
						if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(3)-industri_plants.get(prod).get(0))) < 0) {
							scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
						}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(3)-industri_plants.get(prod).get(0))) > prod.getMaximumEnergyLevel()){
							scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
						}else {
							scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(3)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(0))).assign();
						}
					}
					} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==6) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(6)-industri_plants.get(prod).get(3))) < 0) {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
					}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(6)-industri_plants.get(prod).get(3))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(6)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(3))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==9) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(9)-industri_plants.get(prod).get(6))) < 0) {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
					}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(9)-industri_plants.get(prod).get(6))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(9)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(6))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==12) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(12)-industri_plants.get(prod).get(9))) < 0) {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
					}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(12)-industri_plants.get(prod).get(9))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(12)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(9))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==15) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(15)-industri_plants.get(prod).get(12))) < 0) {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
					}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(15)-industri_plants.get(prod).get(12))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(15)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(12))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==18) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(18)-industri_plants.get(prod).get(15))) < 0) {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
					}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(18)-industri_plants.get(prod).get(15))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(18)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(15))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==21) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(21)-industri_plants.get(prod).get(18))) < 0) {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
					}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(21)-industri_plants.get(prod).get(18))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(21)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(18))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		if((runde+3)%24 ==0) {//Industri
			for(Consumer prod: industri_plants.keySet()) {
				ControllableConsumer cont_cons = (ControllableConsumer) prod;
				try {
					if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(0)-industri_plants.get(prod).get(21))) < 0) {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, -prod.getRequiredPower()).assign();
					}else if((prod.getRequiredPower() + maximum_receivable_power_list.get(prod)*(industri_plants.get(prod).get(0)-industri_plants.get(prod).get(21))) > prod.getMaximumEnergyLevel()){
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, prod.getMaximumEnergyLevel()-prod.getRequiredPower()).assign();
					}else {
						scenario.getCommandFactory().createAdjustConsumerCommand(cont_cons, (int)(maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(0)-maximum_receivable_power_list.get(prod)*industri_plants.get(prod).get(21))).assign();
					}
				} catch (CannotAssignCommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
	}
		
	
	
	private void filling_all_Producer_and_Consumer_Lists(Scenario scenario) {
		Statistics statistics = scenario.getStatistics();
		double sunperday[] = statistics.getSunIntensityPerDay();
		for(EnergyNode node : scenario.getGraph().getNodes()) {
			
			if(node instanceof SolarPowerPlant) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(i, sunperday[i]);;
				}
				for(int i = 0;i<24;i++) {
					powers.add(i, sunperday[i]*200);;
				}
				solar_plants.put((Producer) node, times);
				producer_summary.put((Producer) node, 200);
				current_producer_status.put((Producer) node, powers);
			}
			if(node instanceof WindPowerPlant) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(i,0.0);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,0.0*350);
				}
				wind_plants.put((Producer) node, times);
				producer_summary.put((Producer) node, 350);
				current_producer_status.put((Producer) node, powers);
			}
			if(node instanceof BioGasFiredPowerPlant) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(0.0);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,0.0);
				}
				biogas_plants.put((Producer) node, times);
				producer_summary.put((Producer) node, 350);
				current_producer_status.put((Producer) node, powers);
			}
			if(node instanceof HydroPowerPlant) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(i,1.0);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,600.0);
				}
				hydro_plants.put((Producer) node, times);
				producer_summary.put((Producer) node, 600);
				current_producer_status.put((Producer) node, powers);
			}
			if(node instanceof CoalFiredPowerPlant) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(0.0);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,0.0);
				}
				coal_plants.put((Producer) node, times);
				producer_summary.put((Producer) node, 1000);
				current_producer_status.put((Producer) node, powers);
			}
			if(node instanceof GasFiredPowerPlant) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				ArrayList<Double> wind_factor = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(0.0);
					wind_factor.add(0.0);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,0.0);
				}
				gas_plants.put((Producer) node, times);
				producer_summary.put((Producer) node, 500);
				current_producer_status.put((Producer) node, powers);
				wind_correction.put((Producer) node, wind_factor);
			}
			if(node instanceof NuclearPowerPlant) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(0.0);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,0.0);
				}
				nuclear_plants.put((Producer) node, times);
				producer_summary.put((Producer) node, 1500);
				current_producer_status.put((Producer) node, powers);
			}
			if(node instanceof IndustrialPark) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(1.0);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,900.0);
				}
				industri_plants.put((Consumer) node, times);
				consumer_summary.put((Consumer) node, 900);
				current_consumer_status.put((Consumer) node, powers);
			}
			if(node instanceof City) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(i,city_usage[i]);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,city_usage[i]*500);
				}
				city_plants.put((Consumer) node, times);
				consumer_summary.put((Consumer) node, 500);
				current_consumer_status.put((Consumer) node, powers);
			}
			if(node instanceof CommercialPark) {
				ArrayList<Double> times = new ArrayList<Double>();
				ArrayList<Double> powers = new ArrayList<Double>();
				for(int i = 0;i<24;i++) {
					times.add(i,commercial_usage[i]);
				}
				for(int i = 0;i<24;i++) {
					powers.add(i,commercial_usage[i]*500);
				}
				commercial_plants.put((Consumer) node, times);
				consumer_summary.put((Consumer) node, 200);
				current_consumer_status.put((Consumer) node, powers);
			}
		}
	}
	
	private void calculate_overall_needed_power() {
		for(Consumer cons : industri_plants.keySet()) {
			for(int i = 0; i < 24; i++) {
				this.overall_needed_power[i] = this.overall_needed_power[i] +  maximum_receivable_power_list.get(cons);
			}
		}
		for(Consumer cons : city_plants.keySet()) {
			for(int i = 0; i < 24; i++) {
				if(city_usage[i]*500 > maximum_receivable_power_list.get(cons)) {
					this.overall_needed_power[i] = this.overall_needed_power[i] + maximum_receivable_power_list.get(cons);
				}else{
					this.overall_needed_power[i] = this.overall_needed_power[i] + city_usage[i]*500 ;
				}
			}
		}
		for(Consumer cons : commercial_plants.keySet()) {
			for(int i = 0; i < 24; i++) {
				if(commercial_usage[i]*200 > maximum_receivable_power_list.get(cons)) {
					this.overall_needed_power[i] = this.overall_needed_power[i] +maximum_receivable_power_list.get(cons);
				}else {
					this.overall_needed_power[i] = this.overall_needed_power[i]  + commercial_usage[i]*200;
				}
				
			}
		}
	
	}
	
	private double calculate_if_new_power_is_good_producer(Scenario scenario,Producer new_prod,double new_power,int runde) {
		AbstractEnergyNetworkAnalyzer initialize_list = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
		Map<Producer,Integer> producer_list = initialize_list.getProducerLevels();
		Map<Consumer,Integer> consumer_list = initialize_list.getConsumerLevels();
		int current_received_power = 0;
		int future_received_power = 0;
		for(Producer current_prod : current_producer_status.keySet()) {
			
			if(current_prod instanceof HydroPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*hydro_plants.get(current_prod).get(runde)));
			if(current_prod instanceof SolarPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*solar_plants.get(current_prod).get(runde)));
			if(current_prod instanceof WindPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*0.1));
			if(current_prod instanceof NuclearPowerPlant)	producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*nuclear_plants.get(current_prod).get(runde)));
			if(current_prod instanceof CoalFiredPowerPlant) producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*coal_plants.get(current_prod).get(runde)));
			if(current_prod instanceof GasFiredPowerPlant)  producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*gas_plants.get(current_prod).get(runde)));
			if(current_prod instanceof BioGasFiredPowerPlant)  producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*biogas_plants.get(current_prod).get(runde)));
		}
		
		for(Consumer cons : consumer_list.keySet()) {
			if(cons instanceof IndustrialPark) {
				consumer_list.put(cons,(int)(maximum_receivable_power_list.get(cons)*industri_plants.get(cons).get(runde)) );
			}
			if(cons instanceof City) {
				consumer_list.put(cons,(int)(consumer_summary.get(cons)*city_usage[runde]) );
			}
			if(cons instanceof CommercialPark) {
				consumer_list.put(cons,(int)(consumer_summary.get(cons)*commercial_usage[runde]) );
			}
		}
		AbstractEnergyNetworkAnalyzer analyzer = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer.calculateMaxFlow();
		for(Consumer cons : analyzer.getConsumerLevels().keySet()) {
			current_received_power = current_received_power + analyzer.getConsumerLevels().get(cons);
		}
		if(new_power >=0) {
			producer_list.put(new_prod, (int) new_power);
		}else {
			producer_list.put(new_prod, 0);
		}
		AbstractEnergyNetworkAnalyzer analyzer_new = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer_new.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer_new.calculateMaxFlow();
		for(Consumer cons : analyzer.getConsumerLevels().keySet()) {
			future_received_power = future_received_power + analyzer_new.getConsumerLevels().get(cons);
		}
		return new_power-(future_received_power-current_received_power);
	}
	
	private void update_wind(int runde, double windStrength) {
		for(Producer prod : wind_plants.keySet()) {
			wind_plants.get(prod).set((runde%24), windStrength);
		}
	}
	
	private void update_wind_corrections(Producer prod, double wind_correct, int runde) {
		if(wind_correct > 0) {
			if(wind_correct > 1.0) {
				gas_plants.get(prod).set(runde, 1.0);
			}else {
				gas_plants.get(prod).set(runde, wind_correct);
			}
				
		}else {
			gas_plants.get(prod).set(runde, (0.0+gas_plants.get(prod).get(runde))/4);
		}
		
	}
	
	private double calculate_correction_with_WIND(Scenario scenario,Producer new_prod,double new_power,int runde) {
		AbstractEnergyNetworkAnalyzer initialize_list = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
		Map<Producer,Integer> producer_list = initialize_list.getProducerLevels();
		Map<Consumer,Integer> consumer_list = initialize_list.getConsumerLevels();
		int current_received_power = 0;
		int future_received_power = 0;
		for(Producer current_prod : current_producer_status.keySet()) {
			
			if(current_prod instanceof HydroPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*hydro_plants.get(current_prod).get(runde)));
			if(current_prod instanceof SolarPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*solar_plants.get(current_prod).get(runde)));
			if(current_prod instanceof WindPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*0.1));
			if(current_prod instanceof NuclearPowerPlant)	producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*nuclear_plants.get(current_prod).get(runde)));
			if(current_prod instanceof CoalFiredPowerPlant) producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*coal_plants.get(current_prod).get(runde)));
			if(current_prod instanceof GasFiredPowerPlant)  producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*gas_plants.get(current_prod).get(runde)));
			if(current_prod instanceof BioGasFiredPowerPlant)  producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*biogas_plants.get(current_prod).get(runde)));
		}
		if(new_power >=0) {
			producer_list.put(new_prod, (int) new_power);
		}else {
			producer_list.put(new_prod, 0);
		}
		for(Consumer cons : consumer_list.keySet()) {
			if(cons instanceof IndustrialPark) {
				consumer_list.put(cons,(int)(maximum_receivable_power_list.get(cons)*industri_plants.get(cons).get(runde)) );
			}
			if(cons instanceof City) {
				consumer_list.put(cons,(int)(consumer_summary.get(cons)*city_usage[runde]) );
			}
			if(cons instanceof CommercialPark) {
				consumer_list.put(cons,(int)(consumer_summary.get(cons)*commercial_usage[runde]) );
			}
		}
		AbstractEnergyNetworkAnalyzer analyzer = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer.calculateMaxFlow();
		for(Consumer cons : analyzer.getConsumerLevels().keySet()) {
			current_received_power = current_received_power + analyzer.getConsumerLevels().get(cons);
		}
		//WindFactor neu
		for(Producer prod : wind_plants.keySet()) {
			producer_list.put(prod, (int)(maximum_sendable_power_list.get(prod)*wind_plants.get(prod).get(runde)));
		}
		
		AbstractEnergyNetworkAnalyzer analyzer_new = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer_new.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer_new.calculateMaxFlow();
		for(Consumer cons : analyzer.getConsumerLevels().keySet()) {
			future_received_power = future_received_power + analyzer_new.getConsumerLevels().get(cons);
		}
		return future_received_power-current_received_power;
	}
	
	private double calculate_if_new_power_is_good_consumer(Scenario scenario,Consumer new_cons,double new_power,int runde) {
		AbstractEnergyNetworkAnalyzer initialize_list = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
		Map<Producer,Integer> producer_list = initialize_list.getProducerLevels();
		Map<Consumer,Integer> consumer_list = initialize_list.getConsumerLevels();
		int current_received_power = 0;
		int future_received_power = 0;
		for(Producer current_prod : current_producer_status.keySet()) {
			
			if(current_prod instanceof HydroPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*hydro_plants.get(current_prod).get(runde)));
			if(current_prod instanceof SolarPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*solar_plants.get(current_prod).get(runde)));
			if(current_prod instanceof WindPowerPlant)		producer_list.put(current_prod, (int)(maximum_sendable_power_list.get(current_prod)*wind_plants.get(current_prod).get(runde)));
			if(current_prod instanceof NuclearPowerPlant)	producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*nuclear_plants.get(current_prod).get(runde)));
			if(current_prod instanceof CoalFiredPowerPlant) producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*coal_plants.get(current_prod).get(runde)));
			if(current_prod instanceof GasFiredPowerPlant)  producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*gas_plants.get(current_prod).get(runde)));
			if(current_prod instanceof BioGasFiredPowerPlant)  producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*biogas_plants.get(current_prod).get(runde)));
		}
		
		for(Consumer cons : consumer_list.keySet()) {
			if(cons instanceof IndustrialPark) {
				consumer_list.put(cons,(int)(maximum_receivable_power_list.get(cons)*industri_plants.get(cons).get(runde)) );
				
			}
			if(cons instanceof City) {
				consumer_list.put(cons,(int)(maximum_receivable_power_list.get(cons)*city_usage[runde]) );
			}
			if(cons instanceof CommercialPark) {
				consumer_list.put(cons,(int)(maximum_receivable_power_list.get(cons)*commercial_usage[runde]) );
			}
		}
		AbstractEnergyNetworkAnalyzer analyzer = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer.calculateMaxFlow();
		for(Consumer cons : analyzer.getConsumerLevels().keySet()) {
			current_received_power = current_received_power + analyzer.getConsumerLevels().get(cons);
		}
		if(new_power >=0) {
			consumer_list.put(new_cons, (int) new_power);
		}else {
			consumer_list.put(new_cons, 0);
		}
		AbstractEnergyNetworkAnalyzer analyzer_new = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer_new.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer_new.calculateMaxFlow();
		for(Consumer cons : analyzer.getConsumerLevels().keySet()) {
			future_received_power = future_received_power + analyzer_new.getConsumerLevels().get(cons);
		}
		return new_power-(future_received_power-current_received_power);
	}
	
	private void calculate_current_needed_power(Scenario scenario) {
		AbstractEnergyNetworkAnalyzer initialize_list = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
		Map<Producer,Integer> producer_list = initialize_list.getProducerLevels();
		Map<Consumer,Integer> consumer_list = initialize_list.getConsumerLevels();
		double[] tmp = new double[24];
		for(int iter = 0;iter < 24;iter++) {
			tmp[iter] = overall_needed_power[iter];
		}
		
		double sum_of_power;
		for(int i = 0; i < 24; i++) {
			sum_of_power = 0;
			for(Producer current_prod : current_producer_status.keySet()) {
				if(current_prod instanceof HydroPowerPlant)		producer_list.put(current_prod,   (int)(maximum_sendable_power_list.get(current_prod)*hydro_plants.get(current_prod).get(i)));
				if(current_prod instanceof SolarPowerPlant)		producer_list.put(current_prod,   (int)(maximum_sendable_power_list.get(current_prod)*solar_plants.get(current_prod).get(i)));
				if(current_prod instanceof WindPowerPlant)		producer_list.put(current_prod,   (int)(maximum_sendable_power_list.get(current_prod)*wind_plants.get(current_prod).get(i)));
				if(current_prod instanceof NuclearPowerPlant)	producer_list.put(current_prod,   (int)(maximum_sendable_power_list.get(current_prod)*nuclear_plants.get(current_prod).get(i)));
				if(current_prod instanceof CoalFiredPowerPlant) producer_list.put(current_prod,   (int)(maximum_sendable_power_list.get(current_prod)*coal_plants.get(current_prod).get(i)));
				if(current_prod instanceof GasFiredPowerPlant)  producer_list.put(current_prod,   (int)(maximum_sendable_power_list.get(current_prod)*gas_plants.get(current_prod).get(i)));
				if(current_prod instanceof BioGasFiredPowerPlant)  producer_list.put(current_prod,(int)(maximum_sendable_power_list.get(current_prod)*biogas_plants.get(current_prod).get(i)));
			}
			for(Consumer cons : consumer_list.keySet()) {
				if(cons instanceof IndustrialPark) {
					consumer_list.put(cons,(int)(maximum_receivable_power_list.get(cons)*industri_plants.get(cons).get(i)) );
				}
				if(cons instanceof City) {
					
					consumer_list.put(cons,(int)(consumer_summary.get(cons)*city_usage[i]) );
				}
				if(cons instanceof CommercialPark) {
					consumer_list.put(cons,(int)(consumer_summary.get(cons)*commercial_usage[i]) );
				}	
			}
			AbstractEnergyNetworkAnalyzer analyzer = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
			analyzer.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
			analyzer.calculateMaxFlow();
			for(Producer prod_result : analyzer.getProducerLevels().keySet()) {
				current_producer_status.get(prod_result).set(i,(double)analyzer.getProducerLevels().get(prod_result));
			}
		
			for(Consumer prod_result : analyzer.getConsumerLevels().keySet()) {
				current_consumer_status.get(prod_result).set(i,(double)analyzer.getConsumerLevels().get(prod_result));
			}
			for(Producer prod : analyzer.getProducerLevels().keySet()) {
				sum_of_power = sum_of_power + analyzer.getProducerLevels().get(prod);
			}
			tmp[i] = tmp[i] -sum_of_power;
		}
		for(int iter = 0;iter < 24;iter++) {
			current_needed_power[iter] = tmp[iter];
		}
	}
	
	
	
	
	
	private void calculate_maximum_possible_required_power() {
		this.maximum_possible_required_power = industri_plants.size()*900 + city_plants.size()*500 + commercial_plants.size()*200;	
	}
	
	private void calculate_maximum_receivable_power_sum(Scenario scenario) {
		
		AbstractEnergyNetworkAnalyzer Initialize_list = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
		Map<Producer,Integer> producer_list = Initialize_list.getProducerLevels();
		Map<Consumer,Integer> consumer_list = Initialize_list.getConsumerLevels();
		for(Producer prod : solar_plants.keySet()) {
			producer_list.put(prod, prod.getMaximumEnergyLevel());
		}
		for(Producer prod : wind_plants.keySet()) {
			producer_list.put(prod, prod.getMaximumEnergyLevel());
		}
		for(Producer prod : biogas_plants.keySet()) {
			producer_list.put(prod, prod.getMaximumEnergyLevel());
		}
		for(Producer prod : hydro_plants.keySet()) {
			producer_list.put(prod, prod.getMaximumEnergyLevel());
		}
		for(Producer prod : coal_plants.keySet()) {
			producer_list.put(prod, prod.getMaximumEnergyLevel());
		}
		for(Producer prod : gas_plants.keySet()) {
			producer_list.put(prod, prod.getMaximumEnergyLevel());
		}
		for(Producer prod : nuclear_plants.keySet()) {
			producer_list.put(prod, prod.getMaximumEnergyLevel());
		}
		for(Consumer cons : industri_plants.keySet()) {
			consumer_list.put(cons, cons.getMaximumEnergyLevel());
		}
		for(Consumer cons : city_plants.keySet()) {
			consumer_list.put(cons, cons.getMaximumEnergyLevel());
		}
		for(Consumer cons : commercial_plants.keySet()) {
			consumer_list.put(cons, cons.getMaximumEnergyLevel());
		}
		AbstractEnergyNetworkAnalyzer analyzer = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
		analyzer.createFlowGraph(scenario.getGraph(), Optional.of(producer_list),Optional.of(consumer_list));
		analyzer.calculateMaxFlow();
		for(Consumer cons : analyzer.getConsumerLevels().keySet()) {
			maximum_receivable_power_sum = maximum_receivable_power_sum + analyzer.getConsumerLevels().get(cons);
		}
	}
	
	private void calculate_maximum_receivable_power_for_each_consumer(Scenario scenario){
		AbstractEnergyNetworkAnalyzer list_initialize = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
		Map<Consumer,Integer> consumer_list = list_initialize.getConsumerLevels();
		Map<Producer,Integer> producer_list = list_initialize.getProducerLevels();
		for(Consumer cons : consumer_list.keySet()) {
			for(Producer prod : producer_list.keySet()) {
				producer_list.put(prod, producer_summary.get(prod));
			}
			for(Consumer cons_fill : consumer_list.keySet()) {
				consumer_list.put(cons_fill, 0);
			}
			consumer_list.put(cons, cons.getMaximumEnergyLevel());
			AbstractEnergyNetworkAnalyzer analyzer = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
			analyzer.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
			analyzer.calculateMaxFlow();
			maximum_receivable_power_list.put(cons, analyzer.getConsumerLevels().get(cons));
		}
	} 
	
	private void calculate_maximum_sendable_power_for_each_producer(Scenario scenario){
		AbstractEnergyNetworkAnalyzer list_initialize_prod = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
		Map<Consumer,Integer> consumer_list = list_initialize_prod.getConsumerLevels();
		Map<Producer,Integer> producer_list = list_initialize_prod.getProducerLevels();
		for(Producer prod : producer_list.keySet()) {
			for(Consumer cons :consumer_list.keySet()) {
				consumer_list.put(cons, consumer_summary.get(cons));
			}
			for(Producer prod_fill : producer_list.keySet()) {
				producer_list.put(prod_fill, 0);
			}
			double multiplicator = give_multiplikator(scenario, prod);
			producer_multiplicator.put(prod, multiplicator);
			producer_list.put(prod, (int)(prod.getMaximumEnergyLevel()*multiplicator));
			AbstractEnergyNetworkAnalyzer analyzer_prod = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
			analyzer_prod.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
			analyzer_prod.calculateMaxFlow();
			maximum_sendable_power_list.put(prod, analyzer_prod.getProducerLevels().get(prod));
		}
	} 
	
	private double give_multiplikator(Scenario scenario,Producer prod) {
		if( prod instanceof BioGasFiredPowerPlant) {
			if(give_ProducerType(scenario, prod, ConsumerType.CITY)) {
				return 0.8;
			}
			if(give_ProducerType(scenario, prod, ConsumerType.COMMERCIAL_PARK)) {
				return 0.8;
			}
		}
		if( prod instanceof CoalFiredPowerPlant) {
			if(give_ProducerType(scenario, prod, ConsumerType.CITY)) {
				return 0.8;
			}
			if(give_ProducerType(scenario, prod, ConsumerType.COMMERCIAL_PARK)) {
				return 0.8;
			}
		}
		if( prod instanceof HydroPowerPlant) {
			if(scenario.getPlayfield().getPlayfieldElement(prod.getXPos(), prod.getYPos()).getElementType()== PlayfieldElement.ElementType.BEACH) {
				return 0.9;
			}
			
		}
		if( prod instanceof NuclearPowerPlant) {
			if(give_ProducerType(scenario, prod,  ConsumerType.CITY) ) {
				return 0.8;
			}
		}
		if( prod instanceof SolarPowerPlant) {
			//System.out.println("SOLAR TEST = "+scenario.getPlayfield().getPlayfieldElement(prod.getXPos(), prod.getYPos()).getElementType());
			if(scenario.getPlayfield().getPlayfieldElement(prod.getXPos(), prod.getYPos()).getElementType()== PlayfieldElement.ElementType.MOUNTAIN) {
				return 1.1;
			}
		}
		if( prod instanceof SolarPowerPlant) {
			//System.out.println("SOLAR TEST = "+scenario.getPlayfield().getPlayfieldElement(prod.getXPos(), prod.getYPos()).getElementType());
			
			if(give_playfieldelement(scenario, prod, PlayfieldElement.ElementType.MOUNTAIN)) {
				return 0.5;
			}
		}
		
		if( prod instanceof WindPowerPlant) {
				if(give_playfieldelement(scenario, prod, PlayfieldElement.ElementType.MOUNTAIN)) { 
					return 2;
				}
			}
		
		return 1.0;
		
	}
	
	private boolean give_playfieldelement(Scenario scenario, Producer prod,PlayfieldElement.ElementType p) {
		int currentX = prod.getXPos();
		int currentY = prod.getYPos();
		int currentx;
		int currenty;
					// up
				
					for (int x = -1; x < 1 ; x++) {
						currenty = currentY - 1;
						if(currentX+x >=0 && currentX+x < scenario.getPlayfield().getHorizontalSize() && currenty >=0 && currenty < scenario.getPlayfield().getVerticalSize()) {
							if(scenario.getPlayfield().getPlayfieldElement(currentX + x, currenty).getElementType() == p) {
								return true;
							}
						}
					}
					// right
					for (int y = -1; y < 1 ; y++) {
						currentx = currentX + 1;
						if(currentx >=0 && currentx < scenario.getPlayfield().getHorizontalSize() && currentY+y >=0 && currentY+y < scenario.getPlayfield().getVerticalSize()) {
							if(scenario.getPlayfield().getPlayfieldElement(currentx, currentY+y).getElementType() == p) {
								return true;
							}
						}
					}
					// down
					for (int x = 1; x > -1 ; x--) {
						currenty = currentY + 1;
						if(currentX+x >=0 && currentX+x < scenario.getPlayfield().getHorizontalSize() && currenty >=0 && currenty < scenario.getPlayfield().getVerticalSize()) {
							
							if(scenario.getPlayfield().getPlayfieldElement(currentX+x, currenty).getElementType() == p) {
								return true;
							}	
						}
					}
					// left
					for (int y = 1; y > -1; y--) {
						currentx = currentX - 1;
						if(currentx >=0 && currentx < scenario.getPlayfield().getHorizontalSize() && currentY+y >=0 && currentY +y< scenario.getPlayfield().getVerticalSize()) {
							if(scenario.getPlayfield().getPlayfieldElement(currentx, currentY+y).getElementType() == p) {
								return true;
							}	
						}
					}
					return false;
	}
	
	private boolean give_ProducerType(Scenario scenario, Producer prod,ConsumerType cp) {
		int currentX = prod.getXPos();
		int currentY = prod.getYPos();
		int currentx;
		int currenty;
					// up
			if(cp == ConsumerType.CITY) {
				for(Consumer cons: city_plants.keySet()){
					for (int x = -1; x < 1 ; x++) {
						currenty = currentY -1;
						if(currentX+x >=0 && currentX+x < scenario.getPlayfield().getHorizontalSize() && currenty >=0 && currenty < scenario.getPlayfield().getVerticalSize()) {
							if(cons.getXPos() == currentX+x && cons.getYPos()== currenty) {
								return true;
							}
						}
					}
					// right
					for (int y = -1; y < 1 ; y++) {
						currentx = currentX + 1;
						if(currentx >=0 && currentx < scenario.getPlayfield().getHorizontalSize() && currentY+y >=0 && currentY+y < scenario.getPlayfield().getVerticalSize()) {
							if(cons.getXPos() == currentX +1 && cons.getYPos()== currentY+y) {
								return true;
							}
						}
					}
					// down
					for (int x = 1; x > -1 ; x--) {
						currenty = currentY + 1;
						if(currentX+x >=0 && currentX+x < scenario.getPlayfield().getHorizontalSize() && currenty >=0 && currenty < scenario.getPlayfield().getVerticalSize()) {
							
							if(cons.getXPos() == currentX+x && cons.getYPos()== currenty) {
								return true;
							}	
						}
					}
					// left
					for (int y = 1; y > -1; y--) {
						currentx = currentX - 1;
						if(currentx >=0 && currentx < scenario.getPlayfield().getHorizontalSize() && currentY+y >=0 && currentY +y< scenario.getPlayfield().getVerticalSize()) {
							if(cons.getXPos() == currentX && cons.getYPos()== currentY+y) {
								return true;
							}	
						}
					}
					
				}
			}
			if(cp == ConsumerType.COMMERCIAL_PARK) {
				for(Consumer cons: commercial_plants.keySet()){
					for (int x = -1; x < 1 ; x++) {
						currenty = currentY - 1;
						if(currentX+x >=0 && currentX+x < scenario.getPlayfield().getHorizontalSize() && currenty >=0 && currenty < scenario.getPlayfield().getVerticalSize()) {
							if(cons.getXPos() == currentX+x && cons.getYPos()== currenty) {
								return true;
							}
						}
					}
					// right
					for (int y = -1; y < 1 ; y++) {
						currentx = currentX + 1;
						if(currentx >=0 && currentx < scenario.getPlayfield().getHorizontalSize() && currentY+y >=0 && currentY+y < scenario.getPlayfield().getVerticalSize()) {
							if(cons.getXPos() == currentx && cons.getYPos()== currentY+y) {
								return true;
							}
						}
					}
					// down
					for (int x = 1; x > -1 ; x--) {
						currenty = currentY + 1;
						if(currentX+x >=0 && currentX+x < scenario.getPlayfield().getHorizontalSize() && currenty >=0 && currenty < scenario.getPlayfield().getVerticalSize()) {
							
							if(cons.getXPos() == currentX+x && cons.getYPos()== currenty) {
								return true;
							}	
						}
					}
					// left
					for (int y = 1; y > -1; y--) {
						currentx = currentX - 1;
						if(currentx >=0 && currentx < scenario.getPlayfield().getHorizontalSize() && currentY+y >=0 && currentY +y< scenario.getPlayfield().getVerticalSize()) {
							if(cons.getXPos() == currentx && cons.getYPos()== currentY+y) {
								return true;
							}	
						}
					}
					
				}
			}
				return false;
					
	}
	
	private void calculation_for_nuclear_timeslots(Scenario scenario) {
		double on_time[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		double average1;
		double average2;
		for(Producer prod : nuclear_plants.keySet()) {
			calculate_current_needed_power(scenario);
			if(current_needed_power[0] >=  0)  on_time[0] = 0.0;
			if(current_needed_power[0] > 0.01*maximum_sendable_power_list.get(prod))  on_time[0] = 0.01;
			if(current_needed_power[0] > 0.02*maximum_sendable_power_list.get(prod))  on_time[0] = 0.02;
			if(current_needed_power[0] > 0.05*maximum_sendable_power_list.get(prod))  on_time[0] = 0.05;
			if(current_needed_power[0] > 0.1*maximum_sendable_power_list.get(prod))  on_time[0] = 0.1;
			if(current_needed_power[0] > 0.25*maximum_sendable_power_list.get(prod))  on_time[0] = 0.25;
			if(current_needed_power[0] > 0.5*maximum_sendable_power_list.get(prod))  on_time[0] = 0.5;
			if(current_needed_power[0] > 0.75*maximum_sendable_power_list.get(prod))  on_time[0] = 0.75;	
			if(current_needed_power[0] > 1*maximum_sendable_power_list.get(prod))  on_time[0] = 1;
			
			if(current_needed_power[1] >= 0)  on_time[1] = 0.0;
			if(current_needed_power[1] > 0.01*maximum_sendable_power_list.get(prod))  on_time[1] = 0.01;
			if(current_needed_power[1] > 0.02*maximum_sendable_power_list.get(prod))  on_time[1] = 0.02;
			if(current_needed_power[1] > 0.05*maximum_sendable_power_list.get(prod))  on_time[1] = 0.05;
			if(current_needed_power[1] > 0.1*maximum_sendable_power_list.get(prod))  on_time[1] = 0.1;
			if(current_needed_power[1] > 0.25*maximum_sendable_power_list.get(prod))  on_time[1] = 0.25;
			if(current_needed_power[1] > 0.5*maximum_sendable_power_list.get(prod))  on_time[1] = 0.5;
			if(current_needed_power[1] > 0.75*maximum_sendable_power_list.get(prod))  on_time[1] = 0.75;	
			if(current_needed_power[1] > 1*maximum_sendable_power_list.get(prod))  on_time[1] = 1;
			
			if(current_needed_power[2] >= 0)  on_time[2] = 0.0;
			if(current_needed_power[2] > 0.01*maximum_sendable_power_list.get(prod))  on_time[2] = 0.01;
			if(current_needed_power[2] > 0.02*maximum_sendable_power_list.get(prod))  on_time[2] = 0.02;
			if(current_needed_power[2] > 0.05*maximum_sendable_power_list.get(prod))  on_time[2] = 0.05;
			if(current_needed_power[2] > 0.1*maximum_sendable_power_list.get(prod))  on_time[2] = 0.1;
			if(current_needed_power[2] > 0.25*maximum_sendable_power_list.get(prod))  on_time[2] = 0.25;
			if(current_needed_power[2] > 0.5*maximum_sendable_power_list.get(prod))  on_time[2] = 0.5;
			if(current_needed_power[2] > 0.75*maximum_sendable_power_list.get(prod))  on_time[2] = 0.75;	
			if(current_needed_power[2] > 1*maximum_sendable_power_list.get(prod))  on_time[2] = 1;
			
			if(current_needed_power[3] >= 0)  on_time[3] = 0.0;
			if(current_needed_power[3] > 0.01*maximum_sendable_power_list.get(prod))  on_time[3] = 0.01;
			if(current_needed_power[3] > 0.02*maximum_sendable_power_list.get(prod))  on_time[3] = 0.02;
			if(current_needed_power[3] > 0.05*maximum_sendable_power_list.get(prod))  on_time[3] = 0.05;
			if(current_needed_power[3] > 0.1*maximum_sendable_power_list.get(prod))  on_time[3] = 0.1;
			if(current_needed_power[3] > 0.25*maximum_sendable_power_list.get(prod))  on_time[3] = 0.25;
			if(current_needed_power[3] > 0.5*maximum_sendable_power_list.get(prod))  on_time[3] = 0.5;
			if(current_needed_power[3] > 0.75*maximum_sendable_power_list.get(prod))  on_time[3] = 0.75;	
			if(current_needed_power[3] > 1*maximum_sendable_power_list.get(prod))  on_time[3] = 1;
			
			if(current_needed_power[4] >= 0)  on_time[4] = 0.0;
			if(current_needed_power[4] > 0.01*maximum_sendable_power_list.get(prod))  on_time[4] = 0.01;
			if(current_needed_power[4] > 0.02*maximum_sendable_power_list.get(prod))  on_time[4] = 0.02;
			if(current_needed_power[4] > 0.05*maximum_sendable_power_list.get(prod))  on_time[4] = 0.05;
			if(current_needed_power[4] > 0.1*maximum_sendable_power_list.get(prod))  on_time[4] = 0.1;
			if(current_needed_power[4] > 0.25*maximum_sendable_power_list.get(prod))  on_time[4] = 0.25;
			if(current_needed_power[4] > 0.5*maximum_sendable_power_list.get(prod))  on_time[4] = 0.5;
			if(current_needed_power[4] > 0.75*maximum_sendable_power_list.get(prod))  on_time[4] = 0.75;	
			if(current_needed_power[4] > 1*maximum_sendable_power_list.get(prod))  on_time[4] = 1;
			
			if(current_needed_power[5] >= 0)  on_time[5] = 0.0;
			if(current_needed_power[5] > 0.01*maximum_sendable_power_list.get(prod))  on_time[5] = 0.01;
			if(current_needed_power[5] > 0.02*maximum_sendable_power_list.get(prod))  on_time[5] = 0.02;
			if(current_needed_power[5] > 0.05*maximum_sendable_power_list.get(prod))  on_time[5] = 0.05;
			if(current_needed_power[5] > 0.1*maximum_sendable_power_list.get(prod))  on_time[5] = 0.1;
			if(current_needed_power[5] > 0.25*maximum_sendable_power_list.get(prod))  on_time[5] = 0.25;
			if(current_needed_power[5] > 0.5*maximum_sendable_power_list.get(prod))  on_time[5] = 0.5;
			if(current_needed_power[5] > 0.75*maximum_sendable_power_list.get(prod))  on_time[5] = 0.75;	
			if(current_needed_power[5] > 1*maximum_sendable_power_list.get(prod))  on_time[5] = 1;
			
			if(current_needed_power[6] >= 0)  on_time[6] = 0.0;
			if(current_needed_power[6] > 0.01*maximum_sendable_power_list.get(prod))  on_time[6] = 0.01;
			if(current_needed_power[6] > 0.02*maximum_sendable_power_list.get(prod))  on_time[6] = 0.02;
			if(current_needed_power[6] > 0.05*maximum_sendable_power_list.get(prod))  on_time[6] = 0.05;
			if(current_needed_power[6] > 0.1*maximum_sendable_power_list.get(prod))  on_time[6] = 0.1;
			if(current_needed_power[6] > 0.25*maximum_sendable_power_list.get(prod))  on_time[6] = 0.25;
			if(current_needed_power[6] > 0.5*maximum_sendable_power_list.get(prod))  on_time[6] = 0.5;
			if(current_needed_power[6] > 0.75*maximum_sendable_power_list.get(prod))  on_time[6] = 0.75;	
			if(current_needed_power[6] > 1*maximum_sendable_power_list.get(prod))  on_time[6] = 1;
			
			if(current_needed_power[7] >= 0)  on_time[7] = 0.0;
			if(current_needed_power[7] > 0.01*maximum_sendable_power_list.get(prod))  on_time[7] = 0.01;
			if(current_needed_power[7] > 0.02*maximum_sendable_power_list.get(prod))  on_time[7] = 0.02;
			if(current_needed_power[7] > 0.05*maximum_sendable_power_list.get(prod))  on_time[7] = 0.05;
			if(current_needed_power[7] > 0.1*maximum_sendable_power_list.get(prod))  on_time[7] = 0.1;
			if(current_needed_power[7] > 0.25*maximum_sendable_power_list.get(prod))  on_time[7] = 0.25;
			if(current_needed_power[7] > 0.5*maximum_sendable_power_list.get(prod))  on_time[7] = 0.5;
			if(current_needed_power[7] > 0.75*maximum_sendable_power_list.get(prod))  on_time[7] = 0.75;	
			if(current_needed_power[7] > 1*maximum_sendable_power_list.get(prod))  on_time[7] = 1;
			
			if(current_needed_power[8] >= 0)  on_time[8] = 0.0;
			if(current_needed_power[8] > 0.01*maximum_sendable_power_list.get(prod))  on_time[8] = 0.01;
			if(current_needed_power[8] > 0.02*maximum_sendable_power_list.get(prod))  on_time[8] = 0.02;
			if(current_needed_power[8] > 0.05*maximum_sendable_power_list.get(prod))  on_time[8] = 0.05;
			if(current_needed_power[8] > 0.1*maximum_sendable_power_list.get(prod))  on_time[8] = 0.1;
			if(current_needed_power[8] > 0.25*maximum_sendable_power_list.get(prod))  on_time[8] = 0.25;
			if(current_needed_power[8] > 0.5*maximum_sendable_power_list.get(prod))  on_time[8] = 0.5;
			if(current_needed_power[8] > 0.75*maximum_sendable_power_list.get(prod))  on_time[8] = 0.75;	
			if(current_needed_power[8] > 1*maximum_sendable_power_list.get(prod))  on_time[8] = 1;
			
			if(current_needed_power[9] >= 0)  on_time[9] = 0.0;
			if(current_needed_power[9] > 0.01*maximum_sendable_power_list.get(prod))  on_time[9] = 0.01;
			if(current_needed_power[9] > 0.02*maximum_sendable_power_list.get(prod))  on_time[9] = 0.02;
			if(current_needed_power[9] > 0.05*maximum_sendable_power_list.get(prod))  on_time[9] = 0.05;
			if(current_needed_power[9] > 0.1*maximum_sendable_power_list.get(prod))  on_time[9] = 0.1;
			if(current_needed_power[9] > 0.25*maximum_sendable_power_list.get(prod))  on_time[9] = 0.25;
			if(current_needed_power[9] > 0.5*maximum_sendable_power_list.get(prod))  on_time[9] = 0.5;
			if(current_needed_power[9] > 0.75*maximum_sendable_power_list.get(prod))  on_time[9] = 0.75;	
			if(current_needed_power[9] > 1*maximum_sendable_power_list.get(prod))  on_time[9] = 1;
			
			if(current_needed_power[10] >= 0)  on_time[10] = 0.0;
			if(current_needed_power[10] > 0.01*maximum_sendable_power_list.get(prod))  on_time[10] = 0.01;
			if(current_needed_power[10] > 0.02*maximum_sendable_power_list.get(prod))  on_time[10] = 0.02;
			if(current_needed_power[10] > 0.05*maximum_sendable_power_list.get(prod))  on_time[10] = 0.05;
			if(current_needed_power[10] > 0.1*maximum_sendable_power_list.get(prod))  on_time[10] = 0.1;
			if(current_needed_power[10] > 0.25*maximum_sendable_power_list.get(prod))  on_time[10] = 0.25;
			if(current_needed_power[10] > 0.5*maximum_sendable_power_list.get(prod))  on_time[10] = 0.5;
			if(current_needed_power[10] > 0.75*maximum_sendable_power_list.get(prod))  on_time[10] = 0.75;	
			if(current_needed_power[10] > 1*maximum_sendable_power_list.get(prod))  on_time[10] = 1;
			
			if(current_needed_power[11] >= 0)  on_time[11] = 0.0;
			if(current_needed_power[11] > 0.01*maximum_sendable_power_list.get(prod))  on_time[11] = 0.01;
			if(current_needed_power[11] > 0.02*maximum_sendable_power_list.get(prod))  on_time[11] = 0.02;
			if(current_needed_power[11] > 0.05*maximum_sendable_power_list.get(prod))  on_time[11] = 0.05;
			if(current_needed_power[11] > 0.1*maximum_sendable_power_list.get(prod))  on_time[11] = 0.1;
			if(current_needed_power[11] > 0.25*maximum_sendable_power_list.get(prod))  on_time[11] = 0.25;
			if(current_needed_power[11] > 0.5*maximum_sendable_power_list.get(prod))  on_time[11] = 0.5;
			if(current_needed_power[11] > 0.75*maximum_sendable_power_list.get(prod))  on_time[11] = 0.75;	
			if(current_needed_power[11] > 1*maximum_sendable_power_list.get(prod))  on_time[11] = 1;
			
			if(current_needed_power[12] >= 0)  on_time[12] = 0.0;
			if(current_needed_power[12] > 0.01*maximum_sendable_power_list.get(prod))  on_time[12] = 0.01;
			if(current_needed_power[12] > 0.02*maximum_sendable_power_list.get(prod))  on_time[12] = 0.02;
			if(current_needed_power[12] > 0.05*maximum_sendable_power_list.get(prod))  on_time[12] = 0.05;
			if(current_needed_power[12] > 0.1*maximum_sendable_power_list.get(prod))  on_time[12] = 0.1;
			if(current_needed_power[12] > 0.25*maximum_sendable_power_list.get(prod))  on_time[12] = 0.25;
			if(current_needed_power[12] > 0.5*maximum_sendable_power_list.get(prod))  on_time[12] = 0.5;
			if(current_needed_power[12] > 0.75*maximum_sendable_power_list.get(prod))  on_time[12] = 0.75;	
			if(current_needed_power[12] > 1*maximum_sendable_power_list.get(prod))  on_time[12] = 1;
			
			if(current_needed_power[13] >= 0)  on_time[13] = 0.0;
			if(current_needed_power[13] > 0.01*maximum_sendable_power_list.get(prod))  on_time[13] = 0.01;
			if(current_needed_power[13] > 0.02*maximum_sendable_power_list.get(prod))  on_time[13] = 0.02;
			if(current_needed_power[13] > 0.05*maximum_sendable_power_list.get(prod))  on_time[13] = 0.05;
			if(current_needed_power[13] > 0.1*maximum_sendable_power_list.get(prod))  on_time[13] = 0.1;
			if(current_needed_power[13] > 0.25*maximum_sendable_power_list.get(prod))  on_time[13] = 0.25;
			if(current_needed_power[13] > 0.5*maximum_sendable_power_list.get(prod))  on_time[13] = 0.5;
			if(current_needed_power[13] > 0.75*maximum_sendable_power_list.get(prod))  on_time[13] = 0.75;	
			if(current_needed_power[13] > 1*maximum_sendable_power_list.get(prod))  on_time[13] = 1;
			
			if(current_needed_power[14] >= 0)  on_time[14] = 0.0;
			if(current_needed_power[14] > 0.01*maximum_sendable_power_list.get(prod))  on_time[14] = 0.01;
			if(current_needed_power[14] > 0.02*maximum_sendable_power_list.get(prod))  on_time[14] = 0.02;
			if(current_needed_power[14] > 0.05*maximum_sendable_power_list.get(prod))  on_time[14] = 0.05;
			if(current_needed_power[14] > 0.1*maximum_sendable_power_list.get(prod))  on_time[14] = 0.1;
			if(current_needed_power[14] > 0.25*maximum_sendable_power_list.get(prod))  on_time[14] = 0.25;
			if(current_needed_power[14] > 0.5*maximum_sendable_power_list.get(prod))  on_time[14] = 0.5;
			if(current_needed_power[14] > 0.75*maximum_sendable_power_list.get(prod))  on_time[14] = 0.75;	
			if(current_needed_power[14] > 1*maximum_sendable_power_list.get(prod))  on_time[14] = 1;
			
			if(current_needed_power[15] >= 0)  on_time[15] = 0.0;
			if(current_needed_power[15] > 0.01*maximum_sendable_power_list.get(prod))  on_time[15] = 0.01;
			if(current_needed_power[15] > 0.02*maximum_sendable_power_list.get(prod))  on_time[15] = 0.02;
			if(current_needed_power[15] > 0.05*maximum_sendable_power_list.get(prod))  on_time[15] = 0.05;
			if(current_needed_power[15] > 0.1*maximum_sendable_power_list.get(prod))  on_time[15] = 0.1;
			if(current_needed_power[15] > 0.25*maximum_sendable_power_list.get(prod))  on_time[15] = 0.25;
			if(current_needed_power[15] > 0.5*maximum_sendable_power_list.get(prod))  on_time[15] = 0.5;
			if(current_needed_power[15] > 0.75*maximum_sendable_power_list.get(prod))  on_time[15] = 0.75;	
			if(current_needed_power[15] > 1*maximum_sendable_power_list.get(prod))  on_time[15] = 1;
			
			if(current_needed_power[16] >= 0)  on_time[16] = 0.0;
			if(current_needed_power[16] > 0.01*maximum_sendable_power_list.get(prod))  on_time[16] = 0.01;
			if(current_needed_power[16] > 0.02*maximum_sendable_power_list.get(prod))  on_time[16] = 0.02;
			if(current_needed_power[16] > 0.05*maximum_sendable_power_list.get(prod))  on_time[16] = 0.05;
			if(current_needed_power[16] > 0.1*maximum_sendable_power_list.get(prod))  on_time[16] = 0.1;
			if(current_needed_power[16] > 0.25*maximum_sendable_power_list.get(prod))  on_time[16] = 0.25;
			if(current_needed_power[16] > 0.5*maximum_sendable_power_list.get(prod))  on_time[16] = 0.5;
			if(current_needed_power[16] > 0.75*maximum_sendable_power_list.get(prod))  on_time[16] = 0.75;	
			if(current_needed_power[16] > 1*maximum_sendable_power_list.get(prod))  on_time[16] = 1;
			
			if(current_needed_power[17] >= 0)  on_time[17] = 0.0;
			if(current_needed_power[17] > 0.01*maximum_sendable_power_list.get(prod))  on_time[17] = 0.01;
			if(current_needed_power[17] > 0.02*maximum_sendable_power_list.get(prod))  on_time[17] = 0.02;
			if(current_needed_power[17] > 0.05*maximum_sendable_power_list.get(prod))  on_time[17] = 0.05;
			if(current_needed_power[17] > 0.1*maximum_sendable_power_list.get(prod))  on_time[17] = 0.1;
			if(current_needed_power[17] > 0.25*maximum_sendable_power_list.get(prod))  on_time[17] = 0.25;
			if(current_needed_power[17] > 0.5*maximum_sendable_power_list.get(prod))  on_time[17] = 0.5;
			if(current_needed_power[17] > 0.75*maximum_sendable_power_list.get(prod))  on_time[17] = 0.75;	
			if(current_needed_power[17] > 1*maximum_sendable_power_list.get(prod))  on_time[17] = 1;
			
			if(current_needed_power[18] >= 0)  on_time[18] = 0.0;
			if(current_needed_power[18] > 0.01*maximum_sendable_power_list.get(prod))  on_time[18] = 0.01;
			if(current_needed_power[18] > 0.02*maximum_sendable_power_list.get(prod))  on_time[18] = 0.02;
			if(current_needed_power[18] > 0.05*maximum_sendable_power_list.get(prod))  on_time[18] = 0.05;
			if(current_needed_power[18] > 0.1*maximum_sendable_power_list.get(prod))  on_time[18] = 0.1;
			if(current_needed_power[18] > 0.25*maximum_sendable_power_list.get(prod))  on_time[18] = 0.25;
			if(current_needed_power[18] > 0.5*maximum_sendable_power_list.get(prod))  on_time[18] = 0.5;
			if(current_needed_power[18] > 0.75*maximum_sendable_power_list.get(prod))  on_time[18] = 0.75;	
			if(current_needed_power[18] > 1*maximum_sendable_power_list.get(prod))  on_time[18] = 1;
			
			if(current_needed_power[19] >= 0)  on_time[19] = 0.0;
			if(current_needed_power[19] > 0.01*maximum_sendable_power_list.get(prod))  on_time[19] = 0.01;
			if(current_needed_power[19] > 0.02*maximum_sendable_power_list.get(prod))  on_time[19] = 0.02;
			if(current_needed_power[19] > 0.05*maximum_sendable_power_list.get(prod))  on_time[19] = 0.05;
			if(current_needed_power[19] > 0.1*maximum_sendable_power_list.get(prod))  on_time[19] = 0.1;
			if(current_needed_power[19] > 0.25*maximum_sendable_power_list.get(prod))  on_time[19] = 0.25;
			if(current_needed_power[19] > 0.5*maximum_sendable_power_list.get(prod))  on_time[19] = 0.5;
			if(current_needed_power[19] > 0.75*maximum_sendable_power_list.get(prod))  on_time[19] = 0.75;	
			if(current_needed_power[19] > 1*maximum_sendable_power_list.get(prod))  on_time[19] = 1;
			
			if(current_needed_power[20] >= 0)  on_time[20] = 0.0;
			if(current_needed_power[20] > 0.01*maximum_sendable_power_list.get(prod))  on_time[20] = 0.01;
			if(current_needed_power[20] > 0.02*maximum_sendable_power_list.get(prod))  on_time[20] = 0.02;
			if(current_needed_power[20] > 0.05*maximum_sendable_power_list.get(prod))  on_time[20] = 0.05;
			if(current_needed_power[20] > 0.1*maximum_sendable_power_list.get(prod))  on_time[20] = 0.1;
			if(current_needed_power[20] > 0.25*maximum_sendable_power_list.get(prod))  on_time[20] = 0.25;
			if(current_needed_power[20] > 0.5*maximum_sendable_power_list.get(prod))  on_time[20] = 0.5;
			if(current_needed_power[20] > 0.75*maximum_sendable_power_list.get(prod))  on_time[20] = 0.75;	
			if(current_needed_power[20] > 1*maximum_sendable_power_list.get(prod))  on_time[20] = 1;
			
			if(current_needed_power[21] >= 0)  on_time[21] = 0.0;
			if(current_needed_power[21] > 0.01*maximum_sendable_power_list.get(prod))  on_time[21] = 0.01;
			if(current_needed_power[21] > 0.02*maximum_sendable_power_list.get(prod))  on_time[21] = 0.02;
			if(current_needed_power[21] > 0.05*maximum_sendable_power_list.get(prod))  on_time[21] = 0.05;
			if(current_needed_power[21] > 0.1*maximum_sendable_power_list.get(prod))  on_time[21] = 0.1;
			if(current_needed_power[21] > 0.25*maximum_sendable_power_list.get(prod))  on_time[21] = 0.25;
			if(current_needed_power[21] > 0.5*maximum_sendable_power_list.get(prod))  on_time[21] = 0.5;
			if(current_needed_power[21] > 0.75*maximum_sendable_power_list.get(prod))  on_time[21] = 0.75;	
			if(current_needed_power[21] > 1*maximum_sendable_power_list.get(prod))  on_time[21] = 1;
			
			if(current_needed_power[22] >= 0)  on_time[22] = 0.0;
			if(current_needed_power[22] > 0.01*maximum_sendable_power_list.get(prod))  on_time[22] = 0.01;
			if(current_needed_power[22] > 0.02*maximum_sendable_power_list.get(prod))  on_time[22] = 0.02;
			if(current_needed_power[22] > 0.05*maximum_sendable_power_list.get(prod))  on_time[22] = 0.05;
			if(current_needed_power[22] > 0.1*maximum_sendable_power_list.get(prod))  on_time[22] = 0.1;
			if(current_needed_power[22] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
			if(current_needed_power[22] > 0.5*maximum_sendable_power_list.get(prod))  on_time[22] = 0.5;
			if(current_needed_power[22] > 0.75*maximum_sendable_power_list.get(prod))  on_time[22] = 0.75;	
			if(current_needed_power[22] > 1*maximum_sendable_power_list.get(prod))  on_time[22] = 1;
			
			if(current_needed_power[23] >= 0)  on_time[23] = 0.0;
			if(current_needed_power[23] > 0.01*maximum_sendable_power_list.get(prod))  on_time[23] = 0.01;
			if(current_needed_power[23] > 0.02*maximum_sendable_power_list.get(prod))  on_time[23] = 0.02;
			if(current_needed_power[23] > 0.05*maximum_sendable_power_list.get(prod))  on_time[23] = 0.05;
			if(current_needed_power[23] > 0.1*maximum_sendable_power_list.get(prod))  on_time[23] = 0.1;
			if(current_needed_power[23] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
			if(current_needed_power[23] > 0.5*maximum_sendable_power_list.get(prod))  on_time[23] = 0.5;
			if(current_needed_power[23] > 0.75*maximum_sendable_power_list.get(prod))  on_time[23] = 0.75;	
			if(current_needed_power[23] > 1*maximum_sendable_power_list.get(prod))  on_time[23] = 1;
			
			average1 = (on_time[0]+on_time[1]+on_time[2]+on_time[3]+on_time[4]+on_time[5]+on_time[18]+on_time[19]+on_time[20]+on_time[21]+on_time[22]+on_time[23])/12;
			double currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),0);
			double perhaps_highest = 0;
			for(int test1 = 0;test1 < 6;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			for(int test2 = 18;test2 < 24;test2++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),test2);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			
			if(average1 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average1 = average1 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average1 = 0;
			}
			
			for(int i = 0 ; i< 6;i++) {
				on_time[i] = average1;
			}
			for(int m = 18 ; m< 24;m++) {
				on_time[m] = average1;
			}
			average2 = (on_time[6]+on_time[7]+on_time[8]+on_time[9]+on_time[10]+on_time[11]+on_time[12]+on_time[13]+on_time[14]+on_time[15]+on_time[16]+on_time[17])/12;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average2*maximum_sendable_power_list.get(prod),6);
			for(int test3 = 6;test3 < 18;test3++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average2*maximum_sendable_power_list.get(prod),test3);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			if(average2 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average2 = average2 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average2 = 0;
			}
			
			for(int k = 6 ; k< 18;k++) {
				on_time[k] = average2;
			}
			
			for(int h = 0; h< 24;h++) {
				nuclear_plants.get(prod).set(h, on_time[h] );
			}
			
		}
	}
	
	private void calculation_for_coal_timeslots(Scenario scenario) {
		double on_time[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		double average1;
		double average2;
		double average3;
		double average4;
		double average5;
		double average6;
		double average7;
		double average8;
		for(Producer prod : coal_plants.keySet()) {
			calculate_current_needed_power(scenario);
			if(current_needed_power[0] >=  0)  on_time[0] = 0.0;
			if(current_needed_power[0] > 0.01*maximum_sendable_power_list.get(prod))  on_time[0] = 0.01;
			if(current_needed_power[0] > 0.02*maximum_sendable_power_list.get(prod))  on_time[0] = 0.02;
			if(current_needed_power[0] > 0.05*maximum_sendable_power_list.get(prod))  on_time[0] = 0.05;
			if(current_needed_power[0] > 0.1*maximum_sendable_power_list.get(prod))  on_time[0] = 0.1;
			if(current_needed_power[0] > 0.25*maximum_sendable_power_list.get(prod))  on_time[0] = 0.25;
			if(current_needed_power[0] > 0.5*maximum_sendable_power_list.get(prod))  on_time[0] = 0.5;
			if(current_needed_power[0] > 0.75*maximum_sendable_power_list.get(prod))  on_time[0] = 0.75;	
			if(current_needed_power[0] > 1*maximum_sendable_power_list.get(prod))  on_time[0] = 1;
			
			if(current_needed_power[1] >= 0)  on_time[1] = 0.0;
			if(current_needed_power[1] > 0.01*maximum_sendable_power_list.get(prod))  on_time[1] = 0.01;
			if(current_needed_power[1] > 0.02*maximum_sendable_power_list.get(prod))  on_time[1] = 0.02;
			if(current_needed_power[1] > 0.05*maximum_sendable_power_list.get(prod))  on_time[1] = 0.05;
			if(current_needed_power[1] > 0.1*maximum_sendable_power_list.get(prod))  on_time[1] = 0.1;
			if(current_needed_power[1] > 0.25*maximum_sendable_power_list.get(prod))  on_time[1] = 0.25;
			if(current_needed_power[1] > 0.5*maximum_sendable_power_list.get(prod))  on_time[1] = 0.5;
			if(current_needed_power[1] > 0.75*maximum_sendable_power_list.get(prod))  on_time[1] = 0.75;	
			if(current_needed_power[1] > 1*maximum_sendable_power_list.get(prod))  on_time[1] = 1;
			
			if(current_needed_power[2] >= 0)  on_time[2] = 0.0;
			if(current_needed_power[2] > 0.01*maximum_sendable_power_list.get(prod))  on_time[2] = 0.01;
			if(current_needed_power[2] > 0.02*maximum_sendable_power_list.get(prod))  on_time[2] = 0.02;
			if(current_needed_power[2] > 0.05*maximum_sendable_power_list.get(prod))  on_time[2] = 0.05;
			if(current_needed_power[2] > 0.1*maximum_sendable_power_list.get(prod))  on_time[2] = 0.1;
			if(current_needed_power[2] > 0.25*maximum_sendable_power_list.get(prod))  on_time[2] = 0.25;
			if(current_needed_power[2] > 0.5*maximum_sendable_power_list.get(prod))  on_time[2] = 0.5;
			if(current_needed_power[2] > 0.75*maximum_sendable_power_list.get(prod))  on_time[2] = 0.75;	
			if(current_needed_power[2] > 1*maximum_sendable_power_list.get(prod))  on_time[2] = 1;
			
			if(current_needed_power[3] >= 0)  on_time[3] = 0.0;
			if(current_needed_power[3] > 0.01*maximum_sendable_power_list.get(prod))  on_time[3] = 0.01;
			if(current_needed_power[3] > 0.02*maximum_sendable_power_list.get(prod))  on_time[3] = 0.02;
			if(current_needed_power[3] > 0.05*maximum_sendable_power_list.get(prod))  on_time[3] = 0.05;
			if(current_needed_power[3] > 0.1*maximum_sendable_power_list.get(prod))  on_time[3] = 0.1;
			if(current_needed_power[3] > 0.25*maximum_sendable_power_list.get(prod))  on_time[3] = 0.25;
			if(current_needed_power[3] > 0.5*maximum_sendable_power_list.get(prod))  on_time[3] = 0.5;
			if(current_needed_power[3] > 0.75*maximum_sendable_power_list.get(prod))  on_time[3] = 0.75;	
			if(current_needed_power[3] > 1*maximum_sendable_power_list.get(prod))  on_time[3] = 1;
			
			if(current_needed_power[4] >= 0)  on_time[4] = 0.0;
			if(current_needed_power[4] > 0.01*maximum_sendable_power_list.get(prod))  on_time[4] = 0.01;
			if(current_needed_power[4] > 0.02*maximum_sendable_power_list.get(prod))  on_time[4] = 0.02;
			if(current_needed_power[4] > 0.05*maximum_sendable_power_list.get(prod))  on_time[4] = 0.05;
			if(current_needed_power[4] > 0.1*maximum_sendable_power_list.get(prod))  on_time[4] = 0.1;
			if(current_needed_power[4] > 0.25*maximum_sendable_power_list.get(prod))  on_time[4] = 0.25;
			if(current_needed_power[4] > 0.5*maximum_sendable_power_list.get(prod))  on_time[4] = 0.5;
			if(current_needed_power[4] > 0.75*maximum_sendable_power_list.get(prod))  on_time[4] = 0.75;	
			if(current_needed_power[4] > 1*maximum_sendable_power_list.get(prod))  on_time[4] = 1;
			
			if(current_needed_power[5] >= 0)  on_time[5] = 0.0;
			if(current_needed_power[5] > 0.01*maximum_sendable_power_list.get(prod))  on_time[5] = 0.01;
			if(current_needed_power[5] > 0.02*maximum_sendable_power_list.get(prod))  on_time[5] = 0.02;
			if(current_needed_power[5] > 0.05*maximum_sendable_power_list.get(prod))  on_time[5] = 0.05;
			if(current_needed_power[5] > 0.1*maximum_sendable_power_list.get(prod))  on_time[5] = 0.1;
			if(current_needed_power[5] > 0.25*maximum_sendable_power_list.get(prod))  on_time[5] = 0.25;
			if(current_needed_power[5] > 0.5*maximum_sendable_power_list.get(prod))  on_time[5] = 0.5;
			if(current_needed_power[5] > 0.75*maximum_sendable_power_list.get(prod))  on_time[5] = 0.75;	
			if(current_needed_power[5] > 1*maximum_sendable_power_list.get(prod))  on_time[5] = 1;
			
			if(current_needed_power[6] >= 0)  on_time[6] = 0.0;
			if(current_needed_power[6] > 0.01*maximum_sendable_power_list.get(prod))  on_time[6] = 0.01;
			if(current_needed_power[6] > 0.02*maximum_sendable_power_list.get(prod))  on_time[6] = 0.02;
			if(current_needed_power[6] > 0.05*maximum_sendable_power_list.get(prod))  on_time[6] = 0.05;
			if(current_needed_power[6] > 0.1*maximum_sendable_power_list.get(prod))  on_time[6] = 0.1;
			if(current_needed_power[6] > 0.25*maximum_sendable_power_list.get(prod))  on_time[6] = 0.25;
			if(current_needed_power[6] > 0.5*maximum_sendable_power_list.get(prod))  on_time[6] = 0.5;
			if(current_needed_power[6] > 0.75*maximum_sendable_power_list.get(prod))  on_time[6] = 0.75;	
			if(current_needed_power[6] > 1*maximum_sendable_power_list.get(prod))  on_time[6] = 1;
			
			if(current_needed_power[7] >= 0)  on_time[7] = 0.0;
			if(current_needed_power[7] > 0.01*maximum_sendable_power_list.get(prod))  on_time[7] = 0.01;
			if(current_needed_power[7] > 0.02*maximum_sendable_power_list.get(prod))  on_time[7] = 0.02;
			if(current_needed_power[7] > 0.05*maximum_sendable_power_list.get(prod))  on_time[7] = 0.05;
			if(current_needed_power[7] > 0.1*maximum_sendable_power_list.get(prod))  on_time[7] = 0.1;
			if(current_needed_power[7] > 0.25*maximum_sendable_power_list.get(prod))  on_time[7] = 0.25;
			if(current_needed_power[7] > 0.5*maximum_sendable_power_list.get(prod))  on_time[7] = 0.5;
			if(current_needed_power[7] > 0.75*maximum_sendable_power_list.get(prod))  on_time[7] = 0.75;	
			if(current_needed_power[7] > 1*maximum_sendable_power_list.get(prod))  on_time[7] = 1;
			
			if(current_needed_power[8] >= 0)  on_time[8] = 0.0;
			if(current_needed_power[8] > 0.01*maximum_sendable_power_list.get(prod))  on_time[8] = 0.01;
			if(current_needed_power[8] > 0.02*maximum_sendable_power_list.get(prod))  on_time[8] = 0.02;
			if(current_needed_power[8] > 0.05*maximum_sendable_power_list.get(prod))  on_time[8] = 0.05;
			if(current_needed_power[8] > 0.1*maximum_sendable_power_list.get(prod))  on_time[8] = 0.1;
			if(current_needed_power[8] > 0.25*maximum_sendable_power_list.get(prod))  on_time[8] = 0.25;
			if(current_needed_power[8] > 0.5*maximum_sendable_power_list.get(prod))  on_time[8] = 0.5;
			if(current_needed_power[8] > 0.75*maximum_sendable_power_list.get(prod))  on_time[8] = 0.75;	
			if(current_needed_power[8] > 1*maximum_sendable_power_list.get(prod))  on_time[8] = 1;
			
			if(current_needed_power[9] >= 0)  on_time[9] = 0.0;
			if(current_needed_power[9] > 0.01*maximum_sendable_power_list.get(prod))  on_time[9] = 0.01;
			if(current_needed_power[9] > 0.02*maximum_sendable_power_list.get(prod))  on_time[9] = 0.02;
			if(current_needed_power[9] > 0.05*maximum_sendable_power_list.get(prod))  on_time[9] = 0.05;
			if(current_needed_power[9] > 0.1*maximum_sendable_power_list.get(prod))  on_time[9] = 0.1;
			if(current_needed_power[9] > 0.25*maximum_sendable_power_list.get(prod))  on_time[9] = 0.25;
			if(current_needed_power[9] > 0.5*maximum_sendable_power_list.get(prod))  on_time[9] = 0.5;
			if(current_needed_power[9] > 0.75*maximum_sendable_power_list.get(prod))  on_time[9] = 0.75;	
			if(current_needed_power[9] > 1*maximum_sendable_power_list.get(prod))  on_time[9] = 1;
			
			if(current_needed_power[10] >= 0)  on_time[10] = 0.0;
			if(current_needed_power[10] > 0.01*maximum_sendable_power_list.get(prod))  on_time[10] = 0.01;
			if(current_needed_power[10] > 0.02*maximum_sendable_power_list.get(prod))  on_time[10] = 0.02;
			if(current_needed_power[10] > 0.05*maximum_sendable_power_list.get(prod))  on_time[10] = 0.05;
			if(current_needed_power[10] > 0.1*maximum_sendable_power_list.get(prod))  on_time[10] = 0.1;
			if(current_needed_power[10] > 0.25*maximum_sendable_power_list.get(prod))  on_time[10] = 0.25;
			if(current_needed_power[10] > 0.5*maximum_sendable_power_list.get(prod))  on_time[10] = 0.5;
			if(current_needed_power[10] > 0.75*maximum_sendable_power_list.get(prod))  on_time[10] = 0.75;	
			if(current_needed_power[10] > 1*maximum_sendable_power_list.get(prod))  on_time[10] = 1;
			
			if(current_needed_power[11] >= 0)  on_time[11] = 0.0;
			if(current_needed_power[11] > 0.01*maximum_sendable_power_list.get(prod))  on_time[11] = 0.01;
			if(current_needed_power[11] > 0.02*maximum_sendable_power_list.get(prod))  on_time[11] = 0.02;
			if(current_needed_power[11] > 0.05*maximum_sendable_power_list.get(prod))  on_time[11] = 0.05;
			if(current_needed_power[11] > 0.1*maximum_sendable_power_list.get(prod))  on_time[11] = 0.1;
			if(current_needed_power[11] > 0.25*maximum_sendable_power_list.get(prod))  on_time[11] = 0.25;
			if(current_needed_power[11] > 0.5*maximum_sendable_power_list.get(prod))  on_time[11] = 0.5;
			if(current_needed_power[11] > 0.75*maximum_sendable_power_list.get(prod))  on_time[11] = 0.75;	
			if(current_needed_power[11] > 1*maximum_sendable_power_list.get(prod))  on_time[11] = 1;
			
			if(current_needed_power[12] >= 0)  on_time[12] = 0.0;
			if(current_needed_power[12] > 0.01*maximum_sendable_power_list.get(prod))  on_time[12] = 0.01;
			if(current_needed_power[12] > 0.02*maximum_sendable_power_list.get(prod))  on_time[12] = 0.02;
			if(current_needed_power[12] > 0.05*maximum_sendable_power_list.get(prod))  on_time[12] = 0.05;
			if(current_needed_power[12] > 0.1*maximum_sendable_power_list.get(prod))  on_time[12] = 0.1;
			if(current_needed_power[12] > 0.25*maximum_sendable_power_list.get(prod))  on_time[12] = 0.25;
			if(current_needed_power[12] > 0.5*maximum_sendable_power_list.get(prod))  on_time[12] = 0.5;
			if(current_needed_power[12] > 0.75*maximum_sendable_power_list.get(prod))  on_time[12] = 0.75;	
			if(current_needed_power[12] > 1*maximum_sendable_power_list.get(prod))  on_time[12] = 1;
			
			if(current_needed_power[13] >= 0)  on_time[13] = 0.0;
			if(current_needed_power[13] > 0.01*maximum_sendable_power_list.get(prod))  on_time[13] = 0.01;
			if(current_needed_power[13] > 0.02*maximum_sendable_power_list.get(prod))  on_time[13] = 0.02;
			if(current_needed_power[13] > 0.05*maximum_sendable_power_list.get(prod))  on_time[13] = 0.05;
			if(current_needed_power[13] > 0.1*maximum_sendable_power_list.get(prod))  on_time[13] = 0.1;
			if(current_needed_power[13] > 0.25*maximum_sendable_power_list.get(prod))  on_time[13] = 0.25;
			if(current_needed_power[13] > 0.5*maximum_sendable_power_list.get(prod))  on_time[13] = 0.5;
			if(current_needed_power[13] > 0.75*maximum_sendable_power_list.get(prod))  on_time[13] = 0.75;	
			if(current_needed_power[13] > 1*maximum_sendable_power_list.get(prod))  on_time[13] = 1;
			
			if(current_needed_power[14] >= 0)  on_time[14] = 0.0;
			if(current_needed_power[14] > 0.01*maximum_sendable_power_list.get(prod))  on_time[14] = 0.01;
			if(current_needed_power[14] > 0.02*maximum_sendable_power_list.get(prod))  on_time[14] = 0.02;
			if(current_needed_power[14] > 0.05*maximum_sendable_power_list.get(prod))  on_time[14] = 0.05;
			if(current_needed_power[14] > 0.1*maximum_sendable_power_list.get(prod))  on_time[14] = 0.1;
			if(current_needed_power[14] > 0.25*maximum_sendable_power_list.get(prod))  on_time[14] = 0.25;
			if(current_needed_power[14] > 0.5*maximum_sendable_power_list.get(prod))  on_time[14] = 0.5;
			if(current_needed_power[14] > 0.75*maximum_sendable_power_list.get(prod))  on_time[14] = 0.75;	
			if(current_needed_power[14] > 1*maximum_sendable_power_list.get(prod))  on_time[14] = 1;
			
			if(current_needed_power[15] >= 0)  on_time[15] = 0.0;
			if(current_needed_power[15] > 0.01*maximum_sendable_power_list.get(prod))  on_time[15] = 0.01;
			if(current_needed_power[15] > 0.02*maximum_sendable_power_list.get(prod))  on_time[15] = 0.02;
			if(current_needed_power[15] > 0.05*maximum_sendable_power_list.get(prod))  on_time[15] = 0.05;
			if(current_needed_power[15] > 0.1*maximum_sendable_power_list.get(prod))  on_time[15] = 0.1;
			if(current_needed_power[15] > 0.25*maximum_sendable_power_list.get(prod))  on_time[15] = 0.25;
			if(current_needed_power[15] > 0.5*maximum_sendable_power_list.get(prod))  on_time[15] = 0.5;
			if(current_needed_power[15] > 0.75*maximum_sendable_power_list.get(prod))  on_time[15] = 0.75;	
			if(current_needed_power[15] > 1*maximum_sendable_power_list.get(prod))  on_time[15] = 1;
			
			if(current_needed_power[16] >= 0)  on_time[16] = 0.0;
			if(current_needed_power[16] > 0.01*maximum_sendable_power_list.get(prod))  on_time[16] = 0.01;
			if(current_needed_power[16] > 0.02*maximum_sendable_power_list.get(prod))  on_time[16] = 0.02;
			if(current_needed_power[16] > 0.05*maximum_sendable_power_list.get(prod))  on_time[16] = 0.05;
			if(current_needed_power[16] > 0.1*maximum_sendable_power_list.get(prod))  on_time[16] = 0.1;
			if(current_needed_power[16] > 0.25*maximum_sendable_power_list.get(prod))  on_time[16] = 0.25;
			if(current_needed_power[16] > 0.5*maximum_sendable_power_list.get(prod))  on_time[16] = 0.5;
			if(current_needed_power[16] > 0.75*maximum_sendable_power_list.get(prod))  on_time[16] = 0.75;	
			if(current_needed_power[16] > 1*maximum_sendable_power_list.get(prod))  on_time[16] = 1;
			
			if(current_needed_power[17] >= 0)  on_time[17] = 0.0;
			if(current_needed_power[17] > 0.01*maximum_sendable_power_list.get(prod))  on_time[17] = 0.01;
			if(current_needed_power[17] > 0.02*maximum_sendable_power_list.get(prod))  on_time[17] = 0.02;
			if(current_needed_power[17] > 0.05*maximum_sendable_power_list.get(prod))  on_time[17] = 0.05;
			if(current_needed_power[17] > 0.1*maximum_sendable_power_list.get(prod))  on_time[17] = 0.1;
			if(current_needed_power[17] > 0.25*maximum_sendable_power_list.get(prod))  on_time[17] = 0.25;
			if(current_needed_power[17] > 0.5*maximum_sendable_power_list.get(prod))  on_time[17] = 0.5;
			if(current_needed_power[17] > 0.75*maximum_sendable_power_list.get(prod))  on_time[17] = 0.75;	
			if(current_needed_power[17] > 1*maximum_sendable_power_list.get(prod))  on_time[17] = 1;
			
			if(current_needed_power[18] >= 0)  on_time[18] = 0.0;
			if(current_needed_power[18] > 0.01*maximum_sendable_power_list.get(prod))  on_time[18] = 0.01;
			if(current_needed_power[18] > 0.02*maximum_sendable_power_list.get(prod))  on_time[18] = 0.02;
			if(current_needed_power[18] > 0.05*maximum_sendable_power_list.get(prod))  on_time[18] = 0.05;
			if(current_needed_power[18] > 0.1*maximum_sendable_power_list.get(prod))  on_time[18] = 0.1;
			if(current_needed_power[18] > 0.25*maximum_sendable_power_list.get(prod))  on_time[18] = 0.25;
			if(current_needed_power[18] > 0.5*maximum_sendable_power_list.get(prod))  on_time[18] = 0.5;
			if(current_needed_power[18] > 0.75*maximum_sendable_power_list.get(prod))  on_time[18] = 0.75;	
			if(current_needed_power[18] > 1*maximum_sendable_power_list.get(prod))  on_time[18] = 1;
			
			if(current_needed_power[19] >= 0)  on_time[19] = 0.0;
			if(current_needed_power[19] > 0.01*maximum_sendable_power_list.get(prod))  on_time[19] = 0.01;
			if(current_needed_power[19] > 0.02*maximum_sendable_power_list.get(prod))  on_time[19] = 0.02;
			if(current_needed_power[19] > 0.05*maximum_sendable_power_list.get(prod))  on_time[19] = 0.05;
			if(current_needed_power[19] > 0.1*maximum_sendable_power_list.get(prod))  on_time[19] = 0.1;
			if(current_needed_power[19] > 0.25*maximum_sendable_power_list.get(prod))  on_time[19] = 0.25;
			if(current_needed_power[19] > 0.5*maximum_sendable_power_list.get(prod))  on_time[19] = 0.5;
			if(current_needed_power[19] > 0.75*maximum_sendable_power_list.get(prod))  on_time[19] = 0.75;	
			if(current_needed_power[19] > 1*maximum_sendable_power_list.get(prod))  on_time[19] = 1;
			
			if(current_needed_power[20] >= 0)  on_time[20] = 0.0;
			if(current_needed_power[20] > 0.01*maximum_sendable_power_list.get(prod))  on_time[20] = 0.01;
			if(current_needed_power[20] > 0.02*maximum_sendable_power_list.get(prod))  on_time[20] = 0.02;
			if(current_needed_power[20] > 0.05*maximum_sendable_power_list.get(prod))  on_time[20] = 0.05;
			if(current_needed_power[20] > 0.1*maximum_sendable_power_list.get(prod))  on_time[20] = 0.1;
			if(current_needed_power[20] > 0.25*maximum_sendable_power_list.get(prod))  on_time[20] = 0.25;
			if(current_needed_power[20] > 0.5*maximum_sendable_power_list.get(prod))  on_time[20] = 0.5;
			if(current_needed_power[20] > 0.75*maximum_sendable_power_list.get(prod))  on_time[20] = 0.75;	
			if(current_needed_power[20] > 1*maximum_sendable_power_list.get(prod))  on_time[20] = 1;
			
			if(current_needed_power[21] >= 0)  on_time[21] = 0.0;
			if(current_needed_power[21] > 0.01*maximum_sendable_power_list.get(prod))  on_time[21] = 0.01;
			if(current_needed_power[21] > 0.02*maximum_sendable_power_list.get(prod))  on_time[21] = 0.02;
			if(current_needed_power[21] > 0.05*maximum_sendable_power_list.get(prod))  on_time[21] = 0.05;
			if(current_needed_power[21] > 0.1*maximum_sendable_power_list.get(prod))  on_time[21] = 0.1;
			if(current_needed_power[21] > 0.25*maximum_sendable_power_list.get(prod))  on_time[21] = 0.25;
			if(current_needed_power[21] > 0.5*maximum_sendable_power_list.get(prod))  on_time[21] = 0.5;
			if(current_needed_power[21] > 0.75*maximum_sendable_power_list.get(prod))  on_time[21] = 0.75;	
			if(current_needed_power[21] > 1*maximum_sendable_power_list.get(prod))  on_time[21] = 1;
			
			if(current_needed_power[22] >= 0)  on_time[22] = 0.0;
			if(current_needed_power[22] > 0.01*maximum_sendable_power_list.get(prod))  on_time[22] = 0.01;
			if(current_needed_power[22] > 0.02*maximum_sendable_power_list.get(prod))  on_time[22] = 0.02;
			if(current_needed_power[22] > 0.05*maximum_sendable_power_list.get(prod))  on_time[22] = 0.05;
			if(current_needed_power[22] > 0.1*maximum_sendable_power_list.get(prod))  on_time[22] = 0.1;
			if(current_needed_power[22] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
			if(current_needed_power[22] > 0.5*maximum_sendable_power_list.get(prod))  on_time[22] = 0.5;
			if(current_needed_power[22] > 0.75*maximum_sendable_power_list.get(prod))  on_time[22] = 0.75;	
			if(current_needed_power[22] > 1*maximum_sendable_power_list.get(prod))  on_time[22] = 1;
			
			if(current_needed_power[23] >= 0)  on_time[23] = 0.0;
			if(current_needed_power[23] > 0.01*maximum_sendable_power_list.get(prod))  on_time[23] = 0.01;
			if(current_needed_power[23] > 0.02*maximum_sendable_power_list.get(prod))  on_time[23] = 0.02;
			if(current_needed_power[23] > 0.05*maximum_sendable_power_list.get(prod))  on_time[23] = 0.05;
			if(current_needed_power[23] > 0.1*maximum_sendable_power_list.get(prod))  on_time[23] = 0.1;
			if(current_needed_power[23] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
			if(current_needed_power[23] > 0.5*maximum_sendable_power_list.get(prod))  on_time[23] = 0.5;
			if(current_needed_power[23] > 0.75*maximum_sendable_power_list.get(prod))  on_time[23] = 0.75;	
			if(current_needed_power[23] > 1*maximum_sendable_power_list.get(prod))  on_time[23] = 1;
			average1 = (on_time[0]+on_time[1]+on_time[2])/3;
			double currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),0);
			double perhaps_highest = 0;
			for(int test1 = 0;test1 < 3;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			if(average1 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average1 = average1 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average1 = 0;
			}
			
			average2 = (on_time[3]+on_time[4]+on_time[5])/3;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average2*maximum_sendable_power_list.get(prod),3);
			for(int test1 = 3;test1 < 6;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average2*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			if(average2 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average2 = average2 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average2 = 0;
			}
			
			average3 = (on_time[6]+on_time[7]+on_time[8])/3;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average3*maximum_sendable_power_list.get(prod),6);
			for(int test1 = 6;test1 < 9;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average3*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			if(average3 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average3 = average3 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average3 = 0;
			}
			
			average4 = (on_time[9]+on_time[10]+on_time[11])/3;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average4*maximum_sendable_power_list.get(prod),9);
			for(int test1 = 9;test1 < 12;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average4*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			if(average4 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average4 = average4 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average4 = 0;
			}
			
			average5 = (on_time[12]+on_time[13]+on_time[14])/3;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average5*maximum_sendable_power_list.get(prod),12);
			for(int test1 = 12;test1 < 15;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average5*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			if(average5 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average5 = average5 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average5 = 0;
			}
			
			average6 = (on_time[15]+on_time[16]+on_time[17])/3;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average6*maximum_sendable_power_list.get(prod),15);
			for(int test1 = 15;test1 < 18;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average6*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			if(average6 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average6 = average6 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average6 = 0;
			}
			
			average7 = (on_time[18]+on_time[19]+on_time[20])/3;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average7*maximum_sendable_power_list.get(prod),18);
			for(int test1 = 18;test1 < 21;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average7*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest= perhaps_highest;
				}
			}
			if(average7 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average7 = average7 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average7 = 0;
			}
			
			average8 = (on_time[21]+on_time[22]+on_time[23])/3;
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average8*maximum_sendable_power_list.get(prod),21);
			for(int test1 = 21;test1 < 24;test1++ ) {
				perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average8*maximum_sendable_power_list.get(prod),test1);
				if(perhaps_highest > currenthighest) {
					currenthighest = perhaps_highest;
				}
			}
			
			if(average8 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average8 = average8 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average8 = 0;
			}
			for(int a = 0 ; a< 3;a++) {
				on_time[a] = average1;
			}
			for(int b = 3 ; b< 6;b++) {
				on_time[b] = average2;
			}
			for(int c = 6 ; c< 9;c++) {
				on_time[c] = average3;
			}
			for(int d = 9 ; d< 12;d++) {
				on_time[d] = average4;
			}
			for(int f = 12 ; f< 15;f++) {
				on_time[f] = average5;
			}
			for(int g = 15 ; g< 18;g++) {
				on_time[g] = average6;
			}
			for(int h = 18 ; h< 21;h++) {
				on_time[h] = average7;
			}
			for(int i = 21 ; i< 24;i++) {
				on_time[i] = average8;
			}
			for(int h = 0; h< 24;h++) {
				coal_plants.get(prod).set(h,on_time[h]);
			}
			
		}
	}
		
	private void calculation_for_gas_timeslots(Scenario scenario) {
			
		double on_time[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		double average1;
		double average2;
		double average3;
		double average4;
		double average5;
		double average6;
		double average7;
		double average8;
		double average9;
		double average10;
		double average11;
		double average12;
		double average13;
		double average14;
		double average15;
		double average16;
		double average17;
		double average18;
		double average19;
		double average20;
		double average21;
		double average22;
		double average23;
		double average24;
		for(Producer prod : gas_plants.keySet()) {
			calculate_current_needed_power(scenario);
			if(current_needed_power[0] >=  0)  on_time[0] = 0.0;
			if(current_needed_power[0] > 0.01*maximum_sendable_power_list.get(prod))  on_time[0] = 0.01;
			if(current_needed_power[0] > 0.02*maximum_sendable_power_list.get(prod))  on_time[0] = 0.02;
			if(current_needed_power[0] > 0.05*maximum_sendable_power_list.get(prod))  on_time[0] = 0.05;
			if(current_needed_power[0] > 0.1*maximum_sendable_power_list.get(prod))  on_time[0] = 0.1;
			if(current_needed_power[0] > 0.25*maximum_sendable_power_list.get(prod))  on_time[0] = 0.25;
			if(current_needed_power[0] > 0.5*maximum_sendable_power_list.get(prod))  on_time[0] = 0.5;
			if(current_needed_power[0] > 0.75*maximum_sendable_power_list.get(prod))  on_time[0] = 0.75;	
			if(current_needed_power[0] > 1*maximum_sendable_power_list.get(prod))  on_time[0] = 1;
			
			if(current_needed_power[1] >= 0)  on_time[1] = 0.0;
			if(current_needed_power[1] > 0.01*maximum_sendable_power_list.get(prod))  on_time[1] = 0.01;
			if(current_needed_power[1] > 0.02*maximum_sendable_power_list.get(prod))  on_time[1] = 0.02;
			if(current_needed_power[1] > 0.05*maximum_sendable_power_list.get(prod))  on_time[1] = 0.05;
			if(current_needed_power[1] > 0.1*maximum_sendable_power_list.get(prod))  on_time[1] = 0.1;
			if(current_needed_power[1] > 0.25*maximum_sendable_power_list.get(prod))  on_time[1] = 0.25;
			if(current_needed_power[1] > 0.5*maximum_sendable_power_list.get(prod))  on_time[1] = 0.5;
			if(current_needed_power[1] > 0.75*maximum_sendable_power_list.get(prod))  on_time[1] = 0.75;	
			if(current_needed_power[1] > 1*maximum_sendable_power_list.get(prod))  on_time[1] = 1;
			
			if(current_needed_power[2] >= 0)  on_time[2] = 0.0;
			if(current_needed_power[2] > 0.01*maximum_sendable_power_list.get(prod))  on_time[2] = 0.01;
			if(current_needed_power[2] > 0.02*maximum_sendable_power_list.get(prod))  on_time[2] = 0.02;
			if(current_needed_power[2] > 0.05*maximum_sendable_power_list.get(prod))  on_time[2] = 0.05;
			if(current_needed_power[2] > 0.1*maximum_sendable_power_list.get(prod))  on_time[2] = 0.1;
			if(current_needed_power[2] > 0.25*maximum_sendable_power_list.get(prod))  on_time[2] = 0.25;
			if(current_needed_power[2] > 0.5*maximum_sendable_power_list.get(prod))  on_time[2] = 0.5;
			if(current_needed_power[2] > 0.75*maximum_sendable_power_list.get(prod))  on_time[2] = 0.75;	
			if(current_needed_power[2] > 1*maximum_sendable_power_list.get(prod))  on_time[2] = 1;
			
			if(current_needed_power[3] >= 0)  on_time[3] = 0.0;
			if(current_needed_power[3] > 0.01*maximum_sendable_power_list.get(prod))  on_time[3] = 0.01;
			if(current_needed_power[3] > 0.02*maximum_sendable_power_list.get(prod))  on_time[3] = 0.02;
			if(current_needed_power[3] > 0.05*maximum_sendable_power_list.get(prod))  on_time[3] = 0.05;
			if(current_needed_power[3] > 0.1*maximum_sendable_power_list.get(prod))  on_time[3] = 0.1;
			if(current_needed_power[3] > 0.25*maximum_sendable_power_list.get(prod))  on_time[3] = 0.25;
			if(current_needed_power[3] > 0.5*maximum_sendable_power_list.get(prod))  on_time[3] = 0.5;
			if(current_needed_power[3] > 0.75*maximum_sendable_power_list.get(prod))  on_time[3] = 0.75;	
			if(current_needed_power[3] > 1*maximum_sendable_power_list.get(prod))  on_time[3] = 1;
			
			if(current_needed_power[4] >= 0)  on_time[4] = 0.0;
			if(current_needed_power[4] > 0.01*maximum_sendable_power_list.get(prod))  on_time[4] = 0.01;
			if(current_needed_power[4] > 0.02*maximum_sendable_power_list.get(prod))  on_time[4] = 0.02;
			if(current_needed_power[4] > 0.05*maximum_sendable_power_list.get(prod))  on_time[4] = 0.05;
			if(current_needed_power[4] > 0.1*maximum_sendable_power_list.get(prod))  on_time[4] = 0.1;
			if(current_needed_power[4] > 0.25*maximum_sendable_power_list.get(prod))  on_time[4] = 0.25;
			if(current_needed_power[4] > 0.5*maximum_sendable_power_list.get(prod))  on_time[4] = 0.5;
			if(current_needed_power[4] > 0.75*maximum_sendable_power_list.get(prod))  on_time[4] = 0.75;	
			if(current_needed_power[4] > 1*maximum_sendable_power_list.get(prod))  on_time[4] = 1;
			
			if(current_needed_power[5] >= 0)  on_time[5] = 0.0;
			if(current_needed_power[5] > 0.01*maximum_sendable_power_list.get(prod))  on_time[5] = 0.01;
			if(current_needed_power[5] > 0.02*maximum_sendable_power_list.get(prod))  on_time[5] = 0.02;
			if(current_needed_power[5] > 0.05*maximum_sendable_power_list.get(prod))  on_time[5] = 0.05;
			if(current_needed_power[5] > 0.1*maximum_sendable_power_list.get(prod))  on_time[5] = 0.1;
			if(current_needed_power[5] > 0.25*maximum_sendable_power_list.get(prod))  on_time[5] = 0.25;
			if(current_needed_power[5] > 0.5*maximum_sendable_power_list.get(prod))  on_time[5] = 0.5;
			if(current_needed_power[5] > 0.75*maximum_sendable_power_list.get(prod))  on_time[5] = 0.75;	
			if(current_needed_power[5] > 1*maximum_sendable_power_list.get(prod))  on_time[5] = 1;
			
			if(current_needed_power[6] >= 0)  on_time[6] = 0.0;
			if(current_needed_power[6] > 0.01*maximum_sendable_power_list.get(prod))  on_time[6] = 0.01;
			if(current_needed_power[6] > 0.02*maximum_sendable_power_list.get(prod))  on_time[6] = 0.02;
			if(current_needed_power[6] > 0.05*maximum_sendable_power_list.get(prod))  on_time[6] = 0.05;
			if(current_needed_power[6] > 0.1*maximum_sendable_power_list.get(prod))  on_time[6] = 0.1;
			if(current_needed_power[6] > 0.25*maximum_sendable_power_list.get(prod))  on_time[6] = 0.25;
			if(current_needed_power[6] > 0.5*maximum_sendable_power_list.get(prod))  on_time[6] = 0.5;
			if(current_needed_power[6] > 0.75*maximum_sendable_power_list.get(prod))  on_time[6] = 0.75;	
			if(current_needed_power[6] > 1*maximum_sendable_power_list.get(prod))  on_time[6] = 1;
			
			if(current_needed_power[7] >= 0)  on_time[7] = 0.0;
			if(current_needed_power[7] > 0.01*maximum_sendable_power_list.get(prod))  on_time[7] = 0.01;
			if(current_needed_power[7] > 0.02*maximum_sendable_power_list.get(prod))  on_time[7] = 0.02;
			if(current_needed_power[7] > 0.05*maximum_sendable_power_list.get(prod))  on_time[7] = 0.05;
			if(current_needed_power[7] > 0.1*maximum_sendable_power_list.get(prod))  on_time[7] = 0.1;
			if(current_needed_power[7] > 0.25*maximum_sendable_power_list.get(prod))  on_time[7] = 0.25;
			if(current_needed_power[7] > 0.5*maximum_sendable_power_list.get(prod))  on_time[7] = 0.5;
			if(current_needed_power[7] > 0.75*maximum_sendable_power_list.get(prod))  on_time[7] = 0.75;	
			if(current_needed_power[7] > 1*maximum_sendable_power_list.get(prod))  on_time[7] = 1;
			
			if(current_needed_power[8] >= 0)  on_time[8] = 0.0;
			if(current_needed_power[8] > 0.01*maximum_sendable_power_list.get(prod))  on_time[8] = 0.01;
			if(current_needed_power[8] > 0.02*maximum_sendable_power_list.get(prod))  on_time[8] = 0.02;
			if(current_needed_power[8] > 0.05*maximum_sendable_power_list.get(prod))  on_time[8] = 0.05;
			if(current_needed_power[8] > 0.1*maximum_sendable_power_list.get(prod))  on_time[8] = 0.1;
			if(current_needed_power[8] > 0.25*maximum_sendable_power_list.get(prod))  on_time[8] = 0.25;
			if(current_needed_power[8] > 0.5*maximum_sendable_power_list.get(prod))  on_time[8] = 0.5;
			if(current_needed_power[8] > 0.75*maximum_sendable_power_list.get(prod))  on_time[8] = 0.75;	
			if(current_needed_power[8] > 1*maximum_sendable_power_list.get(prod))  on_time[8] = 1;
			
			if(current_needed_power[9] >= 0)  on_time[9] = 0.0;
			if(current_needed_power[9] > 0.01*maximum_sendable_power_list.get(prod))  on_time[9] = 0.01;
			if(current_needed_power[9] > 0.02*maximum_sendable_power_list.get(prod))  on_time[9] = 0.02;
			if(current_needed_power[9] > 0.05*maximum_sendable_power_list.get(prod))  on_time[9] = 0.05;
			if(current_needed_power[9] > 0.1*maximum_sendable_power_list.get(prod))  on_time[9] = 0.1;
			if(current_needed_power[9] > 0.25*maximum_sendable_power_list.get(prod))  on_time[9] = 0.25;
			if(current_needed_power[9] > 0.5*maximum_sendable_power_list.get(prod))  on_time[9] = 0.5;
			if(current_needed_power[9] > 0.75*maximum_sendable_power_list.get(prod))  on_time[9] = 0.75;	
			if(current_needed_power[9] > 1*maximum_sendable_power_list.get(prod))  on_time[9] = 1;
			
			if(current_needed_power[10] >= 0)  on_time[10] = 0.0;
			if(current_needed_power[10] > 0.01*maximum_sendable_power_list.get(prod))  on_time[10] = 0.01;
			if(current_needed_power[10] > 0.02*maximum_sendable_power_list.get(prod))  on_time[10] = 0.02;
			if(current_needed_power[10] > 0.05*maximum_sendable_power_list.get(prod))  on_time[10] = 0.05;
			if(current_needed_power[10] > 0.1*maximum_sendable_power_list.get(prod))  on_time[10] = 0.1;
			if(current_needed_power[10] > 0.25*maximum_sendable_power_list.get(prod))  on_time[10] = 0.25;
			if(current_needed_power[10] > 0.5*maximum_sendable_power_list.get(prod))  on_time[10] = 0.5;
			if(current_needed_power[10] > 0.75*maximum_sendable_power_list.get(prod))  on_time[10] = 0.75;	
			if(current_needed_power[10] > 1*maximum_sendable_power_list.get(prod))  on_time[10] = 1;
			
			if(current_needed_power[11] >= 0)  on_time[11] = 0.0;
			if(current_needed_power[11] > 0.01*maximum_sendable_power_list.get(prod))  on_time[11] = 0.01;
			if(current_needed_power[11] > 0.02*maximum_sendable_power_list.get(prod))  on_time[11] = 0.02;
			if(current_needed_power[11] > 0.05*maximum_sendable_power_list.get(prod))  on_time[11] = 0.05;
			if(current_needed_power[11] > 0.1*maximum_sendable_power_list.get(prod))  on_time[11] = 0.1;
			if(current_needed_power[11] > 0.25*maximum_sendable_power_list.get(prod))  on_time[11] = 0.25;
			if(current_needed_power[11] > 0.5*maximum_sendable_power_list.get(prod))  on_time[11] = 0.5;
			if(current_needed_power[11] > 0.75*maximum_sendable_power_list.get(prod))  on_time[11] = 0.75;	
			if(current_needed_power[11] > 1*maximum_sendable_power_list.get(prod))  on_time[11] = 1;
			
			if(current_needed_power[12] >= 0)  on_time[12] = 0.0;
			if(current_needed_power[12] > 0.01*maximum_sendable_power_list.get(prod))  on_time[12] = 0.01;
			if(current_needed_power[12] > 0.02*maximum_sendable_power_list.get(prod))  on_time[12] = 0.02;
			if(current_needed_power[12] > 0.05*maximum_sendable_power_list.get(prod))  on_time[12] = 0.05;
			if(current_needed_power[12] > 0.1*maximum_sendable_power_list.get(prod))  on_time[12] = 0.1;
			if(current_needed_power[12] > 0.25*maximum_sendable_power_list.get(prod))  on_time[12] = 0.25;
			if(current_needed_power[12] > 0.5*maximum_sendable_power_list.get(prod))  on_time[12] = 0.5;
			if(current_needed_power[12] > 0.75*maximum_sendable_power_list.get(prod))  on_time[12] = 0.75;	
			if(current_needed_power[12] > 1*maximum_sendable_power_list.get(prod))  on_time[12] = 1;
			
			if(current_needed_power[13] >= 0)  on_time[13] = 0.0;
			if(current_needed_power[13] > 0.01*maximum_sendable_power_list.get(prod))  on_time[13] = 0.01;
			if(current_needed_power[13] > 0.02*maximum_sendable_power_list.get(prod))  on_time[13] = 0.02;
			if(current_needed_power[13] > 0.05*maximum_sendable_power_list.get(prod))  on_time[13] = 0.05;
			if(current_needed_power[13] > 0.1*maximum_sendable_power_list.get(prod))  on_time[13] = 0.1;
			if(current_needed_power[13] > 0.25*maximum_sendable_power_list.get(prod))  on_time[13] = 0.25;
			if(current_needed_power[13] > 0.5*maximum_sendable_power_list.get(prod))  on_time[13] = 0.5;
			if(current_needed_power[13] > 0.75*maximum_sendable_power_list.get(prod))  on_time[13] = 0.75;	
			if(current_needed_power[13] > 1*maximum_sendable_power_list.get(prod))  on_time[13] = 1;
			
			if(current_needed_power[14] >= 0)  on_time[14] = 0.0;
			if(current_needed_power[14] > 0.01*maximum_sendable_power_list.get(prod))  on_time[14] = 0.01;
			if(current_needed_power[14] > 0.02*maximum_sendable_power_list.get(prod))  on_time[14] = 0.02;
			if(current_needed_power[14] > 0.05*maximum_sendable_power_list.get(prod))  on_time[14] = 0.05;
			if(current_needed_power[14] > 0.1*maximum_sendable_power_list.get(prod))  on_time[14] = 0.1;
			if(current_needed_power[14] > 0.25*maximum_sendable_power_list.get(prod))  on_time[14] = 0.25;
			if(current_needed_power[14] > 0.5*maximum_sendable_power_list.get(prod))  on_time[14] = 0.5;
			if(current_needed_power[14] > 0.75*maximum_sendable_power_list.get(prod))  on_time[14] = 0.75;	
			if(current_needed_power[14] > 1*maximum_sendable_power_list.get(prod))  on_time[14] = 1;
			
			if(current_needed_power[15] >= 0)  on_time[15] = 0.0;
			if(current_needed_power[15] > 0.01*maximum_sendable_power_list.get(prod))  on_time[15] = 0.01;
			if(current_needed_power[15] > 0.02*maximum_sendable_power_list.get(prod))  on_time[15] = 0.02;
			if(current_needed_power[15] > 0.05*maximum_sendable_power_list.get(prod))  on_time[15] = 0.05;
			if(current_needed_power[15] > 0.1*maximum_sendable_power_list.get(prod))  on_time[15] = 0.1;
			if(current_needed_power[15] > 0.25*maximum_sendable_power_list.get(prod))  on_time[15] = 0.25;
			if(current_needed_power[15] > 0.5*maximum_sendable_power_list.get(prod))  on_time[15] = 0.5;
			if(current_needed_power[15] > 0.75*maximum_sendable_power_list.get(prod))  on_time[15] = 0.75;	
			if(current_needed_power[15] > 1*maximum_sendable_power_list.get(prod))  on_time[15] = 1;
			
			if(current_needed_power[16] >= 0)  on_time[16] = 0.0;
			if(current_needed_power[16] > 0.01*maximum_sendable_power_list.get(prod))  on_time[16] = 0.01;
			if(current_needed_power[16] > 0.02*maximum_sendable_power_list.get(prod))  on_time[16] = 0.02;
			if(current_needed_power[16] > 0.05*maximum_sendable_power_list.get(prod))  on_time[16] = 0.05;
			if(current_needed_power[16] > 0.1*maximum_sendable_power_list.get(prod))  on_time[16] = 0.1;
			if(current_needed_power[16] > 0.25*maximum_sendable_power_list.get(prod))  on_time[16] = 0.25;
			if(current_needed_power[16] > 0.5*maximum_sendable_power_list.get(prod))  on_time[16] = 0.5;
			if(current_needed_power[16] > 0.75*maximum_sendable_power_list.get(prod))  on_time[16] = 0.75;	
			if(current_needed_power[16] > 1*maximum_sendable_power_list.get(prod))  on_time[16] = 1;
			
			if(current_needed_power[17] >= 0)  on_time[17] = 0.0;
			if(current_needed_power[17] > 0.01*maximum_sendable_power_list.get(prod))  on_time[17] = 0.01;
			if(current_needed_power[17] > 0.02*maximum_sendable_power_list.get(prod))  on_time[17] = 0.02;
			if(current_needed_power[17] > 0.05*maximum_sendable_power_list.get(prod))  on_time[17] = 0.05;
			if(current_needed_power[17] > 0.1*maximum_sendable_power_list.get(prod))  on_time[17] = 0.1;
			if(current_needed_power[17] > 0.25*maximum_sendable_power_list.get(prod))  on_time[17] = 0.25;
			if(current_needed_power[17] > 0.5*maximum_sendable_power_list.get(prod))  on_time[17] = 0.5;
			if(current_needed_power[17] > 0.75*maximum_sendable_power_list.get(prod))  on_time[17] = 0.75;	
			if(current_needed_power[17] > 1*maximum_sendable_power_list.get(prod))  on_time[17] = 1;
			
			if(current_needed_power[18] >= 0)  on_time[18] = 0.0;
			if(current_needed_power[18] > 0.01*maximum_sendable_power_list.get(prod))  on_time[18] = 0.01;
			if(current_needed_power[18] > 0.02*maximum_sendable_power_list.get(prod))  on_time[18] = 0.02;
			if(current_needed_power[18] > 0.05*maximum_sendable_power_list.get(prod))  on_time[18] = 0.05;
			if(current_needed_power[18] > 0.1*maximum_sendable_power_list.get(prod))  on_time[18] = 0.1;
			if(current_needed_power[18] > 0.25*maximum_sendable_power_list.get(prod))  on_time[18] = 0.25;
			if(current_needed_power[18] > 0.5*maximum_sendable_power_list.get(prod))  on_time[18] = 0.5;
			if(current_needed_power[18] > 0.75*maximum_sendable_power_list.get(prod))  on_time[18] = 0.75;	
			if(current_needed_power[18] > 1*maximum_sendable_power_list.get(prod))  on_time[18] = 1;
			
			if(current_needed_power[19] >= 0)  on_time[19] = 0.0;
			if(current_needed_power[19] > 0.01*maximum_sendable_power_list.get(prod))  on_time[19] = 0.01;
			if(current_needed_power[19] > 0.02*maximum_sendable_power_list.get(prod))  on_time[19] = 0.02;
			if(current_needed_power[19] > 0.05*maximum_sendable_power_list.get(prod))  on_time[19] = 0.05;
			if(current_needed_power[19] > 0.1*maximum_sendable_power_list.get(prod))  on_time[19] = 0.1;
			if(current_needed_power[19] > 0.25*maximum_sendable_power_list.get(prod))  on_time[19] = 0.25;
			if(current_needed_power[19] > 0.5*maximum_sendable_power_list.get(prod))  on_time[19] = 0.5;
			if(current_needed_power[19] > 0.75*maximum_sendable_power_list.get(prod))  on_time[19] = 0.75;	
			if(current_needed_power[19] > 1*maximum_sendable_power_list.get(prod))  on_time[19] = 1;
			
			if(current_needed_power[20] >= 0)  on_time[20] = 0.0;
			if(current_needed_power[20] > 0.01*maximum_sendable_power_list.get(prod))  on_time[20] = 0.01;
			if(current_needed_power[20] > 0.02*maximum_sendable_power_list.get(prod))  on_time[20] = 0.02;
			if(current_needed_power[20] > 0.05*maximum_sendable_power_list.get(prod))  on_time[20] = 0.05;
			if(current_needed_power[20] > 0.1*maximum_sendable_power_list.get(prod))  on_time[20] = 0.1;
			if(current_needed_power[20] > 0.25*maximum_sendable_power_list.get(prod))  on_time[20] = 0.25;
			if(current_needed_power[20] > 0.5*maximum_sendable_power_list.get(prod))  on_time[20] = 0.5;
			if(current_needed_power[20] > 0.75*maximum_sendable_power_list.get(prod))  on_time[20] = 0.75;	
			if(current_needed_power[20] > 1*maximum_sendable_power_list.get(prod))  on_time[20] = 1;
			
			if(current_needed_power[21] >= 0)  on_time[21] = 0.0;
			if(current_needed_power[21] > 0.01*maximum_sendable_power_list.get(prod))  on_time[21] = 0.01;
			if(current_needed_power[21] > 0.02*maximum_sendable_power_list.get(prod))  on_time[21] = 0.02;
			if(current_needed_power[21] > 0.05*maximum_sendable_power_list.get(prod))  on_time[21] = 0.05;
			if(current_needed_power[21] > 0.1*maximum_sendable_power_list.get(prod))  on_time[21] = 0.1;
			if(current_needed_power[21] > 0.25*maximum_sendable_power_list.get(prod))  on_time[21] = 0.25;
			if(current_needed_power[21] > 0.5*maximum_sendable_power_list.get(prod))  on_time[21] = 0.5;
			if(current_needed_power[21] > 0.75*maximum_sendable_power_list.get(prod))  on_time[21] = 0.75;	
			if(current_needed_power[21] > 1*maximum_sendable_power_list.get(prod))  on_time[21] = 1;
			
			if(current_needed_power[22] >= 0)  on_time[22] = 0.0;
			if(current_needed_power[22] > 0.01*maximum_sendable_power_list.get(prod))  on_time[22] = 0.01;
			if(current_needed_power[22] > 0.02*maximum_sendable_power_list.get(prod))  on_time[22] = 0.02;
			if(current_needed_power[22] > 0.05*maximum_sendable_power_list.get(prod))  on_time[22] = 0.05;
			if(current_needed_power[22] > 0.1*maximum_sendable_power_list.get(prod))  on_time[22] = 0.1;
			if(current_needed_power[22] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
			if(current_needed_power[22] > 0.5*maximum_sendable_power_list.get(prod))  on_time[22] = 0.5;
			if(current_needed_power[22] > 0.75*maximum_sendable_power_list.get(prod))  on_time[22] = 0.75;	
			if(current_needed_power[22] > 1*maximum_sendable_power_list.get(prod))  on_time[22] = 1;
			
			if(current_needed_power[23] >= 0)  on_time[23] = 0.0;
			if(current_needed_power[23] > 0.01*maximum_sendable_power_list.get(prod))  on_time[23] = 0.01;
			if(current_needed_power[23] > 0.02*maximum_sendable_power_list.get(prod))  on_time[23] = 0.02;
			if(current_needed_power[23] > 0.05*maximum_sendable_power_list.get(prod))  on_time[23] = 0.05;
			if(current_needed_power[23] > 0.1*maximum_sendable_power_list.get(prod))  on_time[23] = 0.1;
			if(current_needed_power[23] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
			if(current_needed_power[23] > 0.5*maximum_sendable_power_list.get(prod))  on_time[23] = 0.5;
			if(current_needed_power[23] > 0.75*maximum_sendable_power_list.get(prod))  on_time[23] = 0.75;	
			if(current_needed_power[23] > 1*maximum_sendable_power_list.get(prod))  on_time[23] = 1;
			average1 = on_time[0];
			double currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),0);
			if(average1 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average1 = average1 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average1 = 0;
			}
			
			average2 = on_time[1];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average2*maximum_sendable_power_list.get(prod),1);
			if(average2 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average2= average2 -  (currenthighest)/maximum_sendable_power_list.get(prod);
				}else {
				average2 = 0;
			}
			
			average3 = on_time[2];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average3*maximum_sendable_power_list.get(prod),2);
			if(average3 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average3 = average3 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average3 = 0;
			}
				
			average4 = on_time[3];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average4*maximum_sendable_power_list.get(prod),3);
			if(average4 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average4 = average4 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average4 = 0;
			}
				
			average5 = on_time[4];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average5*maximum_sendable_power_list.get(prod),4);
			if(average5 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average5 = average5 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average5 = 0;
			}
				
			average6 = on_time[5];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average6*maximum_sendable_power_list.get(prod),5);
			if(average6 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average6 = average6 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average6 = 0;
			}
				
			average7 = on_time[6];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average7*maximum_sendable_power_list.get(prod),6);
			if(average7 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average7 = average7 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average7 = 0;
			}
				
			average8 = on_time[7];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average8*maximum_sendable_power_list.get(prod),7);
			if(average8 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average8 = average8 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average8 = 0;
			}
				
			average9 = on_time[8];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average9*maximum_sendable_power_list.get(prod),8);
			if(average9 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average9 = average9 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average9 = 0;
			}
				
			average10 = on_time[9];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average10*maximum_sendable_power_list.get(prod),9);
			if(average10 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average10 = average10 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average10 = 0;
			}
				
			average11 = on_time[10];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average11*maximum_sendable_power_list.get(prod),10);
			if(average11 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average11 = average11 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average11 = 0;
			}
				
			average12 = on_time[11];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average12*maximum_sendable_power_list.get(prod),11);
			if(average12 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average12 = average12 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average12 = 0;
			}
				
			average13 = on_time[12];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average13*maximum_sendable_power_list.get(prod),12);
			if(average13 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average13 = average13 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average13 = 0;
			}
				
			average14 = on_time[13];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average14*maximum_sendable_power_list.get(prod),13);
			if(average14 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average14 = average14 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average14 = 0;
			}
				
			average15 = on_time[14];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average15*maximum_sendable_power_list.get(prod),14);
			if(average15 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average15 = average15 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average15 = 0;
			}
				
			average16 = on_time[15];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average16*maximum_sendable_power_list.get(prod),15);
			if(average16 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average16 = average16 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average16 = 0;
			}
			
			average17 = on_time[16];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average17*maximum_sendable_power_list.get(prod),16);
			if(average17 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average17 = average17 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average17 = 0;
			}
				
			average18 = on_time[17];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average18*maximum_sendable_power_list.get(prod),17);
			if(average18 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average18 = average18 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average18 = 0;
			}
				
			average19 = on_time[18];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average19*maximum_sendable_power_list.get(prod),18);
			if(average19 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average19 = average19 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average19 = 0;
			}
				
			average20 = on_time[19];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average20*maximum_sendable_power_list.get(prod),19);
			if(average20 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average20 = average20 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average20 = 0;
			}
			
			average21 = on_time[20];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average21*maximum_sendable_power_list.get(prod),20);
			if(average21 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average21 = average21 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average21 = 0;
			}
				
			average22 = on_time[21];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average22*maximum_sendable_power_list.get(prod),21);
			if(average22 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average22 = average22 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average22 = 0;
			}
				
			average23 = on_time[22];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average23*maximum_sendable_power_list.get(prod),22);
			if(average23 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average23 = average23 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average23 = 0;
			}
				
			average24 = on_time[23];
			currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average24*maximum_sendable_power_list.get(prod),23);
			if(average24 -(currenthighest)/maximum_sendable_power_list.get(prod)>=0) {
				average24 = average24 -  (currenthighest)/maximum_sendable_power_list.get(prod);
			}else {
				average24 = 0;
			}
			on_time[0] = average1;
			on_time[1] = average2;
			on_time[2] = average3;
			on_time[3] = average4;
			on_time[4] = average5;
			on_time[5] = average6;
			on_time[6] = average7;
			on_time[7] = average8;
			on_time[8] = average9;
			on_time[9] = average10;
			on_time[10] = average11;
			on_time[11] = average12;
			on_time[12] = average13;
			on_time[13] = average14;
			on_time[14] = average15;
			on_time[15] = average16;
			on_time[16] = average17;
			on_time[17] = average18;
			on_time[18] = average19;
			on_time[19] = average20;
			on_time[20] = average21;
			on_time[21] = average22;
			on_time[22] = average23;
			on_time[23] = average24;
				
			for(int h = 0; h< 24;h++) {
				gas_plants.get(prod).set(h,on_time[h]);
			}
				
		}
	}
			
	private void calculation_for_biogas_timeslots(Scenario scenario) {

				double on_time[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
				double average1;
				double average2;
				double average3;
				double average4;
				double average5;
				double average6;
				double average7;
				double average8;
				double average9;
				double average10;
				double average11;
				double average12;
				for(Producer prod : biogas_plants.keySet()) {
					calculate_current_needed_power(scenario);
					if(current_needed_power[0] >=  0)  on_time[0] = 0.0;
					if(current_needed_power[0] > 0.01*maximum_sendable_power_list.get(prod))  on_time[0] = 0.01;
					if(current_needed_power[0] > 0.02*maximum_sendable_power_list.get(prod))  on_time[0] = 0.02;
					if(current_needed_power[0] > 0.05*maximum_sendable_power_list.get(prod))  on_time[0] = 0.05;
					if(current_needed_power[0] > 0.1*maximum_sendable_power_list.get(prod))  on_time[0] = 0.1;
					if(current_needed_power[0] > 0.25*maximum_sendable_power_list.get(prod))  on_time[0] = 0.25;
					if(current_needed_power[0] > 0.5*maximum_sendable_power_list.get(prod))  on_time[0] = 0.5;
					if(current_needed_power[0] > 0.75*maximum_sendable_power_list.get(prod))  on_time[0] = 0.75;	
					if(current_needed_power[0] > 1*maximum_sendable_power_list.get(prod))  on_time[0] = 0.99;
					
					if(current_needed_power[1] >= 0)  on_time[1] = 0.0;
					if(current_needed_power[1] > 0.01*maximum_sendable_power_list.get(prod))  on_time[1] = 0.01;
					if(current_needed_power[1] > 0.02*maximum_sendable_power_list.get(prod))  on_time[1] = 0.02;
					if(current_needed_power[1] > 0.05*maximum_sendable_power_list.get(prod))  on_time[1] = 0.05;
					if(current_needed_power[1] > 0.1*maximum_sendable_power_list.get(prod))  on_time[1] = 0.1;
					if(current_needed_power[1] > 0.25*maximum_sendable_power_list.get(prod))  on_time[1] = 0.25;
					if(current_needed_power[1] > 0.5*maximum_sendable_power_list.get(prod))  on_time[1] = 0.5;
					if(current_needed_power[1] > 0.75*maximum_sendable_power_list.get(prod))  on_time[1] = 0.75;	
					if(current_needed_power[1] > 1*maximum_sendable_power_list.get(prod))  on_time[1] = 0.99;
					
					if(current_needed_power[2] >= 0)  on_time[2] = 0.0;
					if(current_needed_power[2] > 0.01*maximum_sendable_power_list.get(prod))  on_time[2] = 0.01;
					if(current_needed_power[2] > 0.02*maximum_sendable_power_list.get(prod))  on_time[2] = 0.02;
					if(current_needed_power[2] > 0.05*maximum_sendable_power_list.get(prod))  on_time[2] = 0.05;
					if(current_needed_power[2] > 0.1*maximum_sendable_power_list.get(prod))  on_time[2] = 0.1;
					if(current_needed_power[2] > 0.25*maximum_sendable_power_list.get(prod))  on_time[2] = 0.25;
					if(current_needed_power[2] > 0.5*maximum_sendable_power_list.get(prod))  on_time[2] = 0.5;
					if(current_needed_power[2] > 0.75*maximum_sendable_power_list.get(prod))  on_time[2] = 0.75;	
					if(current_needed_power[2] > 1*maximum_sendable_power_list.get(prod))  on_time[2] = 0.99;
					
					if(current_needed_power[3] >= 0)  on_time[3] = 0.0;
					if(current_needed_power[3] > 0.01*maximum_sendable_power_list.get(prod))  on_time[3] = 0.01;
					if(current_needed_power[3] > 0.02*maximum_sendable_power_list.get(prod))  on_time[3] = 0.02;
					if(current_needed_power[3] > 0.05*maximum_sendable_power_list.get(prod))  on_time[3] = 0.05;
					if(current_needed_power[3] > 0.1*maximum_sendable_power_list.get(prod))  on_time[3] = 0.1;
					if(current_needed_power[3] > 0.25*maximum_sendable_power_list.get(prod))  on_time[3] = 0.25;
					if(current_needed_power[3] > 0.5*maximum_sendable_power_list.get(prod))  on_time[3] = 0.5;
					if(current_needed_power[3] > 0.75*maximum_sendable_power_list.get(prod))  on_time[3] = 0.75;	
					if(current_needed_power[3] > 1*maximum_sendable_power_list.get(prod))  on_time[3] = 0.99;
					
					if(current_needed_power[4] >= 0)  on_time[4] = 0.0;
					if(current_needed_power[4] > 0.01*maximum_sendable_power_list.get(prod))  on_time[4] = 0.01;
					if(current_needed_power[4] > 0.02*maximum_sendable_power_list.get(prod))  on_time[4] = 0.02;
					if(current_needed_power[4] > 0.05*maximum_sendable_power_list.get(prod))  on_time[4] = 0.05;
					if(current_needed_power[4] > 0.1*maximum_sendable_power_list.get(prod))  on_time[4] = 0.1;
					if(current_needed_power[4] > 0.25*maximum_sendable_power_list.get(prod))  on_time[4] = 0.25;
					if(current_needed_power[4] > 0.5*maximum_sendable_power_list.get(prod))  on_time[4] = 0.5;
					if(current_needed_power[4] > 0.75*maximum_sendable_power_list.get(prod))  on_time[4] = 0.75;	
					if(current_needed_power[4] > 1*maximum_sendable_power_list.get(prod))  on_time[4] = 0.99;
					
					if(current_needed_power[5] >= 0)  on_time[5] = 0.0;
					if(current_needed_power[5] > 0.01*maximum_sendable_power_list.get(prod))  on_time[5] = 0.01;
					if(current_needed_power[5] > 0.02*maximum_sendable_power_list.get(prod))  on_time[5] = 0.02;
					if(current_needed_power[5] > 0.05*maximum_sendable_power_list.get(prod))  on_time[5] = 0.05;
					if(current_needed_power[5] > 0.1*maximum_sendable_power_list.get(prod))  on_time[5] = 0.1;
					if(current_needed_power[5] > 0.25*maximum_sendable_power_list.get(prod))  on_time[5] = 0.25;
					if(current_needed_power[5] > 0.5*maximum_sendable_power_list.get(prod))  on_time[5] = 0.5;
					if(current_needed_power[5] > 0.75*maximum_sendable_power_list.get(prod))  on_time[5] = 0.75;	
					if(current_needed_power[5] > 1*maximum_sendable_power_list.get(prod))  on_time[5] = 0.99;
					
					if(current_needed_power[6] >= 0)  on_time[6] = 0.0;
					if(current_needed_power[6] > 0.01*maximum_sendable_power_list.get(prod))  on_time[6] = 0.01;
					if(current_needed_power[6] > 0.02*maximum_sendable_power_list.get(prod))  on_time[6] = 0.02;
					if(current_needed_power[6] > 0.05*maximum_sendable_power_list.get(prod))  on_time[6] = 0.05;
					if(current_needed_power[6] > 0.1*maximum_sendable_power_list.get(prod))  on_time[6] = 0.1;
					if(current_needed_power[6] > 0.25*maximum_sendable_power_list.get(prod))  on_time[6] = 0.25;
					if(current_needed_power[6] > 0.5*maximum_sendable_power_list.get(prod))  on_time[6] = 0.5;
					if(current_needed_power[6] > 0.75*maximum_sendable_power_list.get(prod))  on_time[6] = 0.75;	
					if(current_needed_power[6] > 1*maximum_sendable_power_list.get(prod))  on_time[6] = 0.99;
					
					if(current_needed_power[7] >= 0)  on_time[7] = 0.0;
					if(current_needed_power[7] > 0.01*maximum_sendable_power_list.get(prod))  on_time[7] = 0.01;
					if(current_needed_power[7] > 0.02*maximum_sendable_power_list.get(prod))  on_time[7] = 0.02;
					if(current_needed_power[7] > 0.05*maximum_sendable_power_list.get(prod))  on_time[7] = 0.05;
					if(current_needed_power[7] > 0.1*maximum_sendable_power_list.get(prod))  on_time[7] = 0.1;
					if(current_needed_power[7] > 0.25*maximum_sendable_power_list.get(prod))  on_time[7] = 0.25;
					if(current_needed_power[7] > 0.5*maximum_sendable_power_list.get(prod))  on_time[7] = 0.5;
					if(current_needed_power[7] > 0.75*maximum_sendable_power_list.get(prod))  on_time[7] = 0.75;	
					if(current_needed_power[7] > 1*maximum_sendable_power_list.get(prod))  on_time[7] = 0.99;
					
					if(current_needed_power[8] >= 0)  on_time[8] = 0.0;
					if(current_needed_power[8] > 0.01*maximum_sendable_power_list.get(prod))  on_time[8] = 0.01;
					if(current_needed_power[8] > 0.02*maximum_sendable_power_list.get(prod))  on_time[8] = 0.02;
					if(current_needed_power[8] > 0.05*maximum_sendable_power_list.get(prod))  on_time[8] = 0.05;
					if(current_needed_power[8] > 0.1*maximum_sendable_power_list.get(prod))  on_time[8] = 0.1;
					if(current_needed_power[8] > 0.25*maximum_sendable_power_list.get(prod))  on_time[8] = 0.25;
					if(current_needed_power[8] > 0.5*maximum_sendable_power_list.get(prod))  on_time[8] = 0.5;
					if(current_needed_power[8] > 0.75*maximum_sendable_power_list.get(prod))  on_time[8] = 0.75;	
					if(current_needed_power[8] > 1*maximum_sendable_power_list.get(prod))  on_time[8] = 0.99;
					
					if(current_needed_power[9] >= 0)  on_time[9] = 0.0;
					if(current_needed_power[9] > 0.01*maximum_sendable_power_list.get(prod))  on_time[9] = 0.01;
					if(current_needed_power[9] > 0.02*maximum_sendable_power_list.get(prod))  on_time[9] = 0.02;
					if(current_needed_power[9] > 0.05*maximum_sendable_power_list.get(prod))  on_time[9] = 0.05;
					if(current_needed_power[9] > 0.1*maximum_sendable_power_list.get(prod))  on_time[9] = 0.1;
					if(current_needed_power[9] > 0.25*maximum_sendable_power_list.get(prod))  on_time[9] = 0.25;
					if(current_needed_power[9] > 0.5*maximum_sendable_power_list.get(prod))  on_time[9] = 0.5;
					if(current_needed_power[9] > 0.75*maximum_sendable_power_list.get(prod))  on_time[9] = 0.75;	
					if(current_needed_power[9] > 1*maximum_sendable_power_list.get(prod))  on_time[9] = 0.99;
					
					if(current_needed_power[10] >= 0)  on_time[10] = 0.0;
					if(current_needed_power[10] > 0.01*maximum_sendable_power_list.get(prod))  on_time[10] = 0.01;
					if(current_needed_power[10] > 0.02*maximum_sendable_power_list.get(prod))  on_time[10] = 0.02;
					if(current_needed_power[10] > 0.05*maximum_sendable_power_list.get(prod))  on_time[10] = 0.05;
					if(current_needed_power[10] > 0.1*maximum_sendable_power_list.get(prod))  on_time[10] = 0.1;
					if(current_needed_power[10] > 0.25*maximum_sendable_power_list.get(prod))  on_time[10] = 0.25;
					if(current_needed_power[10] > 0.5*maximum_sendable_power_list.get(prod))  on_time[10] = 0.5;
					if(current_needed_power[10] > 0.75*maximum_sendable_power_list.get(prod))  on_time[10] = 0.75;	
					if(current_needed_power[10] > 1*maximum_sendable_power_list.get(prod))  on_time[10] = 0.99;
					
					if(current_needed_power[11] >= 0)  on_time[11] = 0.0;
					if(current_needed_power[11] > 0.01*maximum_sendable_power_list.get(prod))  on_time[11] = 0.01;
					if(current_needed_power[11] > 0.02*maximum_sendable_power_list.get(prod))  on_time[11] = 0.02;
					if(current_needed_power[11] > 0.05*maximum_sendable_power_list.get(prod))  on_time[11] = 0.05;
					if(current_needed_power[11] > 0.1*maximum_sendable_power_list.get(prod))  on_time[11] = 0.1;
					if(current_needed_power[11] > 0.25*maximum_sendable_power_list.get(prod))  on_time[11] = 0.25;
					if(current_needed_power[11] > 0.5*maximum_sendable_power_list.get(prod))  on_time[11] = 0.5;
					if(current_needed_power[11] > 0.75*maximum_sendable_power_list.get(prod))  on_time[11] = 0.75;	
					if(current_needed_power[11] > 1*maximum_sendable_power_list.get(prod))  on_time[11] = 0.99;
					
					if(current_needed_power[12] >= 0)  on_time[12] = 0.0;
					if(current_needed_power[12] > 0.01*maximum_sendable_power_list.get(prod))  on_time[12] = 0.01;
					if(current_needed_power[12] > 0.02*maximum_sendable_power_list.get(prod))  on_time[12] = 0.02;
					if(current_needed_power[12] > 0.05*maximum_sendable_power_list.get(prod))  on_time[12] = 0.05;
					if(current_needed_power[12] > 0.1*maximum_sendable_power_list.get(prod))  on_time[12] = 0.1;
					if(current_needed_power[12] > 0.25*maximum_sendable_power_list.get(prod))  on_time[12] = 0.25;
					if(current_needed_power[12] > 0.5*maximum_sendable_power_list.get(prod))  on_time[12] = 0.5;
					if(current_needed_power[12] > 0.75*maximum_sendable_power_list.get(prod))  on_time[12] = 0.75;	
					if(current_needed_power[12] > 1*maximum_sendable_power_list.get(prod))  on_time[12] = 0.99;
					
					if(current_needed_power[13] >= 0)  on_time[13] = 0.0;
					if(current_needed_power[13] > 0.01*maximum_sendable_power_list.get(prod))  on_time[13] = 0.01;
					if(current_needed_power[13] > 0.02*maximum_sendable_power_list.get(prod))  on_time[13] = 0.02;
					if(current_needed_power[13] > 0.05*maximum_sendable_power_list.get(prod))  on_time[13] = 0.05;
					if(current_needed_power[13] > 0.1*maximum_sendable_power_list.get(prod))  on_time[13] = 0.1;
					if(current_needed_power[13] > 0.25*maximum_sendable_power_list.get(prod))  on_time[13] = 0.25;
					if(current_needed_power[13] > 0.5*maximum_sendable_power_list.get(prod))  on_time[13] = 0.5;
					if(current_needed_power[13] > 0.75*maximum_sendable_power_list.get(prod))  on_time[13] = 0.75;	
					if(current_needed_power[13] > 1*maximum_sendable_power_list.get(prod))  on_time[13] = 0.99;
					
					if(current_needed_power[14] >= 0)  on_time[14] = 0.0;
					if(current_needed_power[14] > 0.01*maximum_sendable_power_list.get(prod))  on_time[14] = 0.01;
					if(current_needed_power[14] > 0.02*maximum_sendable_power_list.get(prod))  on_time[14] = 0.02;
					if(current_needed_power[14] > 0.05*maximum_sendable_power_list.get(prod))  on_time[14] = 0.05;
					if(current_needed_power[14] > 0.1*maximum_sendable_power_list.get(prod))  on_time[14] = 0.1;
					if(current_needed_power[14] > 0.25*maximum_sendable_power_list.get(prod))  on_time[14] = 0.25;
					if(current_needed_power[14] > 0.5*maximum_sendable_power_list.get(prod))  on_time[14] = 0.5;
					if(current_needed_power[14] > 0.75*maximum_sendable_power_list.get(prod))  on_time[14] = 0.75;	
					if(current_needed_power[14] > 1*maximum_sendable_power_list.get(prod))  on_time[14] = 0.99;
					
					if(current_needed_power[15] >= 0)  on_time[15] = 0.0;
					if(current_needed_power[15] > 0.01*maximum_sendable_power_list.get(prod))  on_time[15] = 0.01;
					if(current_needed_power[15] > 0.02*maximum_sendable_power_list.get(prod))  on_time[15] = 0.02;
					if(current_needed_power[15] > 0.05*maximum_sendable_power_list.get(prod))  on_time[15] = 0.05;
					if(current_needed_power[15] > 0.1*maximum_sendable_power_list.get(prod))  on_time[15] = 0.1;
					if(current_needed_power[15] > 0.25*maximum_sendable_power_list.get(prod))  on_time[15] = 0.25;
					if(current_needed_power[15] > 0.5*maximum_sendable_power_list.get(prod))  on_time[15] = 0.5;
					if(current_needed_power[15] > 0.75*maximum_sendable_power_list.get(prod))  on_time[15] = 0.75;	
					if(current_needed_power[15] > 1*maximum_sendable_power_list.get(prod))  on_time[15] = 0.99;
					
					if(current_needed_power[16] >= 0)  on_time[16] = 0.0;
					if(current_needed_power[16] > 0.01*maximum_sendable_power_list.get(prod))  on_time[16] = 0.01;
					if(current_needed_power[16] > 0.02*maximum_sendable_power_list.get(prod))  on_time[16] = 0.02;
					if(current_needed_power[16] > 0.05*maximum_sendable_power_list.get(prod))  on_time[16] = 0.05;
					if(current_needed_power[16] > 0.1*maximum_sendable_power_list.get(prod))  on_time[16] = 0.1;
					if(current_needed_power[16] > 0.25*maximum_sendable_power_list.get(prod))  on_time[16] = 0.25;
					if(current_needed_power[16] > 0.5*maximum_sendable_power_list.get(prod))  on_time[16] = 0.5;
					if(current_needed_power[16] > 0.75*maximum_sendable_power_list.get(prod))  on_time[16] = 0.75;	
					if(current_needed_power[16] > 1*maximum_sendable_power_list.get(prod))  on_time[16] = 0.99;
					
					if(current_needed_power[17] >= 0)  on_time[17] = 0.0;
					if(current_needed_power[17] > 0.01*maximum_sendable_power_list.get(prod))  on_time[17] = 0.01;
					if(current_needed_power[17] > 0.02*maximum_sendable_power_list.get(prod))  on_time[17] = 0.02;
					if(current_needed_power[17] > 0.05*maximum_sendable_power_list.get(prod))  on_time[17] = 0.05;
					if(current_needed_power[17] > 0.1*maximum_sendable_power_list.get(prod))  on_time[17] = 0.1;
					if(current_needed_power[17] > 0.25*maximum_sendable_power_list.get(prod))  on_time[17] = 0.25;
					if(current_needed_power[17] > 0.5*maximum_sendable_power_list.get(prod))  on_time[17] = 0.5;
					if(current_needed_power[17] > 0.75*maximum_sendable_power_list.get(prod))  on_time[17] = 0.75;	
					if(current_needed_power[17] > 1*maximum_sendable_power_list.get(prod))  on_time[17] = 0.99;
					
					if(current_needed_power[18] >= 0)  on_time[18] = 0.0;
					if(current_needed_power[18] > 0.01*maximum_sendable_power_list.get(prod))  on_time[18] = 0.01;
					if(current_needed_power[18] > 0.02*maximum_sendable_power_list.get(prod))  on_time[18] = 0.02;
					if(current_needed_power[18] > 0.05*maximum_sendable_power_list.get(prod))  on_time[18] = 0.05;
					if(current_needed_power[18] > 0.1*maximum_sendable_power_list.get(prod))  on_time[18] = 0.1;
					if(current_needed_power[18] > 0.25*maximum_sendable_power_list.get(prod))  on_time[18] = 0.25;
					if(current_needed_power[18] > 0.5*maximum_sendable_power_list.get(prod))  on_time[18] = 0.5;
					if(current_needed_power[18] > 0.75*maximum_sendable_power_list.get(prod))  on_time[18] = 0.75;	
					if(current_needed_power[18] > 1*maximum_sendable_power_list.get(prod))  on_time[18] = 0.99;
					
					if(current_needed_power[19] >= 0)  on_time[19] = 0.0;
					if(current_needed_power[19] > 0.01*maximum_sendable_power_list.get(prod))  on_time[19] = 0.01;
					if(current_needed_power[19] > 0.02*maximum_sendable_power_list.get(prod))  on_time[19] = 0.02;
					if(current_needed_power[19] > 0.05*maximum_sendable_power_list.get(prod))  on_time[19] = 0.05;
					if(current_needed_power[19] > 0.1*maximum_sendable_power_list.get(prod))  on_time[19] = 0.1;
					if(current_needed_power[19] > 0.25*maximum_sendable_power_list.get(prod))  on_time[19] = 0.25;
					if(current_needed_power[19] > 0.5*maximum_sendable_power_list.get(prod))  on_time[19] = 0.5;
					if(current_needed_power[19] > 0.75*maximum_sendable_power_list.get(prod))  on_time[19] = 0.75;	
					if(current_needed_power[19] > 1*maximum_sendable_power_list.get(prod))  on_time[19] = 0.99;
					
					if(current_needed_power[20] >= 0)  on_time[20] = 0.0;
					if(current_needed_power[20] > 0.01*maximum_sendable_power_list.get(prod))  on_time[20] = 0.01;
					if(current_needed_power[20] > 0.02*maximum_sendable_power_list.get(prod))  on_time[20] = 0.02;
					if(current_needed_power[20] > 0.05*maximum_sendable_power_list.get(prod))  on_time[20] = 0.05;
					if(current_needed_power[20] > 0.1*maximum_sendable_power_list.get(prod))  on_time[20] = 0.1;
					if(current_needed_power[20] > 0.25*maximum_sendable_power_list.get(prod))  on_time[20] = 0.25;
					if(current_needed_power[20] > 0.5*maximum_sendable_power_list.get(prod))  on_time[20] = 0.5;
					if(current_needed_power[20] > 0.75*maximum_sendable_power_list.get(prod))  on_time[20] = 0.75;	
					if(current_needed_power[20] > 1*maximum_sendable_power_list.get(prod))  on_time[20] = 0.99;
					
					if(current_needed_power[21] >= 0)  on_time[21] = 0.0;
					if(current_needed_power[21] > 0.01*maximum_sendable_power_list.get(prod))  on_time[21] = 0.01;
					if(current_needed_power[21] > 0.02*maximum_sendable_power_list.get(prod))  on_time[21] = 0.02;
					if(current_needed_power[21] > 0.05*maximum_sendable_power_list.get(prod))  on_time[21] = 0.05;
					if(current_needed_power[21] > 0.1*maximum_sendable_power_list.get(prod))  on_time[21] = 0.1;
					if(current_needed_power[21] > 0.25*maximum_sendable_power_list.get(prod))  on_time[21] = 0.25;
					if(current_needed_power[21] > 0.5*maximum_sendable_power_list.get(prod))  on_time[21] = 0.5;
					if(current_needed_power[21] > 0.75*maximum_sendable_power_list.get(prod))  on_time[21] = 0.75;	
					if(current_needed_power[21] > 1*maximum_sendable_power_list.get(prod))  on_time[21] = 0.99;
					
					if(current_needed_power[22] >= 0)  on_time[22] = 0.0;
					if(current_needed_power[22] > 0.01*maximum_sendable_power_list.get(prod))  on_time[22] = 0.01;
					if(current_needed_power[22] > 0.02*maximum_sendable_power_list.get(prod))  on_time[22] = 0.02;
					if(current_needed_power[22] > 0.05*maximum_sendable_power_list.get(prod))  on_time[22] = 0.05;
					if(current_needed_power[22] > 0.1*maximum_sendable_power_list.get(prod))  on_time[22] = 0.1;
					if(current_needed_power[22] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
					if(current_needed_power[22] > 0.5*maximum_sendable_power_list.get(prod))  on_time[22] = 0.5;
					if(current_needed_power[22] > 0.75*maximum_sendable_power_list.get(prod))  on_time[22] = 0.75;	
					if(current_needed_power[22] > 1*maximum_sendable_power_list.get(prod))  on_time[22] = 0.99;
					
					if(current_needed_power[23] >= 0)  on_time[23] = 0.0;
					if(current_needed_power[23] > 0.01*maximum_sendable_power_list.get(prod))  on_time[23] = 0.01;
					if(current_needed_power[23] > 0.02*maximum_sendable_power_list.get(prod))  on_time[23] = 0.02;
					if(current_needed_power[23] > 0.05*maximum_sendable_power_list.get(prod))  on_time[23] = 0.05;
					if(current_needed_power[23] > 0.1*maximum_sendable_power_list.get(prod))  on_time[23] = 0.1;
					if(current_needed_power[23] > 0.25*maximum_sendable_power_list.get(prod))  on_time[23] = 0.25;
					if(current_needed_power[23] > 0.5*maximum_sendable_power_list.get(prod))  on_time[23] = 0.5;
					if(current_needed_power[23] > 0.75*maximum_sendable_power_list.get(prod))  on_time[23] = 0.75;	
					if(current_needed_power[23] > 1*maximum_sendable_power_list.get(prod))  on_time[23] = 0.99;
					average1 = (on_time[0]+on_time[1])/2;
					double currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),0);
					double perhaps_highest = 0;
					for(int test1 = 0;test1 <2;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average1*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average1 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average1 = average1 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average1 = 0.01;
					}
					
					
					average2 = (on_time[2]+on_time[3])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average2*maximum_sendable_power_list.get(prod),1);
					for(int test1 = 2;test1 <4;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average2*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average2 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average2 = average2 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average2 = 0.01;
					}
					average3 = (on_time[4]+on_time[5])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average3*maximum_sendable_power_list.get(prod),3);
					for(int test1 = 4;test1 <6;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average3*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average3 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average3 = average3 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average3 = 0.01;
					}
					average4 = (on_time[6]+on_time[7])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average4*maximum_sendable_power_list.get(prod),5);
					for(int test1 = 6;test1 <8;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average4*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average4 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average4 = average4 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average4 = 0.01;
					}
					average5 = (on_time[8]+on_time[9])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average5*maximum_sendable_power_list.get(prod),7);
					for(int test1 = 8;test1 <10;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average5*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average5 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average5 = average5 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average5 = 0.01;
					}
					average6 = (on_time[10]+on_time[11])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average6*maximum_sendable_power_list.get(prod),9);
					for(int test1 = 10;test1 <12;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average6*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average6 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average6 = average6 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average6 = 0.01;
					}
					average7 = (on_time[12]+on_time[13])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average7*maximum_sendable_power_list.get(prod),11);
					for(int test1 = 12;test1 <14;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average7*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average7 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average7 = average7 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average7 = 0.01;
					}
					average8 = (on_time[14]+on_time[15])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average8*maximum_sendable_power_list.get(prod),13);
					for(int test1 = 14;test1 <16;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average8*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average8 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average8 = average8 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average8 = 0.01;
					}
					average9 = (on_time[16]+on_time[17])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average9*maximum_sendable_power_list.get(prod),15);
					for(int test1 = 16;test1 <18;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average9*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average9 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average9 = average9 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average9 = 0.01;
					}
					average10 = (on_time[18]+on_time[19])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average10*maximum_sendable_power_list.get(prod),17);
					for(int test1 = 18;test1 <20;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average10*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average10 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average10 = average10 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average10 = 0.01;
					}
					average11 = (on_time[20]+on_time[21])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average11*maximum_sendable_power_list.get(prod),19);
					for(int test1 = 20;test1 <22;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average11*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average11 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average11 = average11 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average11 = 0.01;
					}
					average12 = (on_time[22]+on_time[23])/2;
					currenthighest = calculate_if_new_power_is_good_producer(scenario, prod, average12*maximum_sendable_power_list.get(prod),21);
					for(int test1 = 22;test1 <24;test1++ ) {
						perhaps_highest = calculate_if_new_power_is_good_producer(scenario, prod, average12*maximum_sendable_power_list.get(prod),test1);
						if(perhaps_highest > currenthighest) {
							currenthighest = perhaps_highest;
						}
					}
					if(average12 -(currenthighest)/maximum_sendable_power_list.get(prod)>0) {
						average12 = average12 -  (currenthighest)/maximum_sendable_power_list.get(prod);
					}else {
						average12 = 0.01;
					}
					
					for(int a = 0 ; a< 2;a++) {
						on_time[a] = average2;
					}
					for(int b = 2 ; b< 4;b++) {
						on_time[b] = average3;
					}
					for(int c = 4 ; c< 6;c++) {
						on_time[c] = average4;
					}
					for(int d = 6 ; d< 8;d++) {
						on_time[d] = average5;
					}
					for(int f = 8 ; f< 10;f++) {
						on_time[f] = average6;
					}
					for(int g = 10 ; g< 12;g++) {
						on_time[g] = average7;
					}
					for(int h = 12 ; h< 14;h++) {
						on_time[h] = average8;
					}
					for(int i = 14 ; i< 16;i++) {
						on_time[i] = average9;
					}
					for(int j = 16 ; j< 18;j++) {
						on_time[j] = average10;
					}
					for(int j = 18 ; j< 20;j++) {
						on_time[j] = average11;
					}
					for(int j = 20 ; j< 22;j++) {
						on_time[j] = average12;
					}
					for(int j = 22 ; j< 24;j++) {
						on_time[j] = average12;
					}
					for(int h = 0; h< 24;h++) {
						biogas_plants.get(prod).set(h,on_time[h]);
					}
				}
		
		
		
		
	}
			
			private void calculation_for_industri_timeslots(Scenario scenario) {//,ArrayList<Double> map) {
				int amount_industri = industri_plants.size();
				double average1;
				double average2;
				double average3;
				double average4;
				double average5;
				double average6;
				double average7;
				double average8;
				for(Consumer cons : industri_plants.keySet()) {
					calculate_current_needed_power(scenario);
					//System.out.println("ANTEIL TEST"+ maximum_receivable_power_list.get(cons)+"    /    "+consumer_summary.get(cons));
					
					double max_reiceivable = 1;
					//System.out.println("ANTEIL TEST"+ max_reiceivable);
					double on_time[]= {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
					
					if((maximum_receivable_power_list.get(cons) - current_needed_power[0]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[0] = (maximum_receivable_power_list.get(cons) - current_needed_power[0]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[0] = 0;
					}
				
					if((maximum_receivable_power_list.get(cons) - current_needed_power[1]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[1] = (maximum_receivable_power_list.get(cons) - current_needed_power[1]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[1] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[2]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[2] = (maximum_receivable_power_list.get(cons) - current_needed_power[2]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[2] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[3]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[3] = (maximum_receivable_power_list.get(cons) - current_needed_power[3]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[3] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[4]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[4] = (maximum_receivable_power_list.get(cons) - current_needed_power[4]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[4] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[5]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[5] = (maximum_receivable_power_list.get(cons) - current_needed_power[5]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[5] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[6]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[6] = (maximum_receivable_power_list.get(cons) - current_needed_power[6]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[6] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[7]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[7] = (maximum_receivable_power_list.get(cons) - current_needed_power[7]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[7] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[8]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[8] = (maximum_receivable_power_list.get(cons) - current_needed_power[8]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[8] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[9]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[9] = (maximum_receivable_power_list.get(cons) - current_needed_power[9]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[9] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[10]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[10] = (maximum_receivable_power_list.get(cons) - current_needed_power[10]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[10] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[11]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[11] = (maximum_receivable_power_list.get(cons) - current_needed_power[11]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[11] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[12]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[12] = (maximum_receivable_power_list.get(cons) - current_needed_power[12]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[12] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[13]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[13] = (maximum_receivable_power_list.get(cons) - current_needed_power[13]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[13] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[14]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[14] = (maximum_receivable_power_list.get(cons) - current_needed_power[14]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[14] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[15]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[15] = (maximum_receivable_power_list.get(cons) - current_needed_power[15]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[15] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[16]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[16] = (maximum_receivable_power_list.get(cons) - current_needed_power[16]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[16] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[17]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[17] = (maximum_receivable_power_list.get(cons) - current_needed_power[17]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[17] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[18]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[18] = (maximum_receivable_power_list.get(cons) - current_needed_power[18]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[18] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[19]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[19] = (maximum_receivable_power_list.get(cons) - current_needed_power[19]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[19] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[20]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[20] = (maximum_receivable_power_list.get(cons) - current_needed_power[20]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[20] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[21]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[21] = (maximum_receivable_power_list.get(cons) - current_needed_power[21]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[21] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[22]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[22] = (maximum_receivable_power_list.get(cons) - current_needed_power[22]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[22] = 0;
					}
					if((maximum_receivable_power_list.get(cons) - current_needed_power[23]*(1/amount_industri))/(maximum_receivable_power_list.get(cons)) > 0) {
						on_time[23] = (maximum_receivable_power_list.get(cons) - current_needed_power[23]*(1/amount_industri))/(maximum_receivable_power_list.get(cons));
					}else {
						on_time[23] = 0;
					}
					
					
						//accurate = calculate_if_new_power_is_good_consumer(scenario, cons, (on_time[1]*maximum_receivable_power_list.get(cons)), 0);
					//on_time[1] = on_time[1]-accurate/(on_time[1]*maximum_receivable_power_list.get(cons));
					
					average1 = (on_time[0]+on_time[1]+on_time[2])/3;
					
					average2 = (on_time[3]+on_time[4]+on_time[5])/3;
					
					average3 = (on_time[6]+on_time[7]+on_time[8])/3;
				
					average4 = (on_time[9]+on_time[10]+on_time[11])/3;
				
					average5 = (on_time[12]+on_time[13]+on_time[14])/3;
					
					average6 = (on_time[15]+on_time[16]+on_time[17])/3;
				
					average7 = (on_time[18]+on_time[19]+on_time[20])/3;
					
					average8 = (on_time[21]+on_time[22]+on_time[23])/2;
				
					for(int a = 0 ; a< 3;a++) {
						on_time[a] = average1;
					}
					for(int b = 3 ; b< 6;b++) {
						on_time[b] = average2;
					}
					for(int c = 6 ; c< 9;c++) {
						on_time[c] = average3;
					}
					for(int d = 9 ; d< 12;d++) {
						on_time[d] = average4;
					}
					for(int f = 12 ; f< 15;f++) {
						on_time[f] = average5;
					}
					for(int g = 15 ; g< 18;g++) {
						on_time[g] = average6;
					}
					for(int h = 18 ; h< 21;h++) {
						on_time[h] = average7;
					}
					for(int i = 21 ; i< 24;i++) {
						on_time[i] = average8;
					}

					for(int h = 0; h< 24;h++) {
						industri_plants.get(cons).set(h,on_time[h]);
					}
				}
				
			}
			
			private void calculate_overall_required_power_without_greenpeace(Scenario scenario) {
				Statistics statistics = scenario.getStatistics();
				double sun_power_per_hour[] = statistics.getSunIntensityPerDay();
				AbstractEnergyNetworkAnalyzer initialize_list = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.empty(), Optional.empty());
				Map<Producer,Integer> producer_list = initialize_list.getProducerLevels();
				Map<Consumer,Integer> consumer_list = initialize_list.getConsumerLevels();
				double[] tmp = new double[24];
				for(int iter = 0;iter < 24;iter++) {
					tmp[iter] = overall_needed_power[iter];
				}
				double sum_of_power;
				for(int i = 0; i < 24; i++) {
					sum_of_power = 0;
					for(Producer prod : solar_plants.keySet()) {
						if(sun_power_per_hour[i]*200 > maximum_sendable_power_list.get(prod)) {
							producer_list.put(prod, maximum_sendable_power_list.get(prod));
						}else {
							producer_list.put(prod, (int) (sun_power_per_hour[i]*200));
						}
					}
					for(Producer prod : hydro_plants.keySet()) {
						producer_list.put(prod, maximum_sendable_power_list.get(prod));
					}
					for(Producer prod : wind_plants.keySet()) {
						producer_list.put(prod, (int)(0.5*maximum_sendable_power_list.get(prod)));
					}
					for(Consumer cons : consumer_list.keySet()) {
						if(cons instanceof IndustrialPark) {
							consumer_list.put(cons,maximum_receivable_power_list.get(cons) );
						}
						if(cons instanceof City) {
							consumer_list.put(cons,(int)(consumer_summary.get(cons)*city_usage[i]) );
						}
						if(cons instanceof CommercialPark) {
							consumer_list.put(cons,(int)(consumer_summary.get(cons)*commercial_usage[i]) );
						}
					}
					AbstractEnergyNetworkAnalyzer analyzer = new EnergyNetworkAnalyzerImpl(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
					analyzer.createFlowGraph(scenario.getGraph(), Optional.of(producer_list), Optional.of(consumer_list));
					analyzer.calculateMaxFlow();
					
					for(Producer prod_result : analyzer.getProducerLevels().keySet()) {
						current_producer_status.get(prod_result).set(i, (double)analyzer.getProducerLevels().get(prod_result));
					}
					
					for(Consumer prod_result : analyzer.getConsumerLevels().keySet()) {
						current_consumer_status.get(prod_result).set(i, (double)analyzer.getConsumerLevels().get(prod_result));
					}
					for(Producer prod : analyzer.getProducerLevels().keySet()) {
						sum_of_power = sum_of_power + analyzer.getProducerLevels().get(prod);
					}
					tmp[i] = tmp[i] -sum_of_power;
				}
				for(int iter = 0;iter < 24;iter++) {
					overall_needed_power_without_greenpeace[iter] = tmp[iter];
				}
			}
			
	@Override
	/**
	 * returns TeamIdentifier
	 * @return TeamIdentifier
	 */
	public String getTeamIdentifier() {
		return "G01T03";
	}

}
