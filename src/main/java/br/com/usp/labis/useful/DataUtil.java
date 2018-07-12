package br.com.usp.labis.useful;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.GoTermCondition;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;

public class DataUtil {
	
	private static final String PROTEIN_IDS = "Protein IDs";

	public static double[] getReplicatesValuesForCondition(Condition condition) {

		double[] values = new double[condition.getReplicates().size()];

		for (int index = 0; index < condition.getReplicates().size(); index++) {
			Replicate replicate = condition.getReplicates().get(index);
			values[index] = replicate.getValue();
		}

		return values;
	}

	public static double getSumDoubleValues(double[] values) {
		double sum = 0.00;
		for (int index = 0; index < values.length; index++) {
			sum += values[index];
		}
		return sum;
	}
	
	public static double[] getArray(List<Double> values) {
		double[] array = new double[values.size()];
		for(int i = 0; i < values.size(); i++) {
			array[i] = values.get(i);
		};
		return array;
	}
	
	public static String[] getProteinIdArray(List<Protein> values) {
		String[] array = new String [values.size()];
		Iterator it = values.iterator();
		int i = 0;
		while (it.hasNext()) {
			Protein protein = (Protein) it.next();
			array[i] = protein.getProteinId() ;
			i++;
		}
		return array;
	}

	public static Double getMaxValueFromList(List<Double> list) {
		Double max = 0.00;
		if (list != null && !list.isEmpty()) {
			max = Collections.max(list);
		}
		return max;
	}

	public static Map<String, List<Double>> getConditionMeans(List<Protein> proteins, 	Map<String, List<Double>> conditionMeans) {
		for (Protein protein : proteins) {
			for (Condition condition : protein.getConditions()) {

				if (conditionMeans.get(condition.getName()) == null) {
					conditionMeans.put(condition.getName(), new ArrayList<Double>());
				}
				conditionMeans.get(condition.getName()).add(condition.getMean());
			}
		}
		return conditionMeans;
	}
	
	public static List<Double> getProteinsStatisticTest(List<Protein> proteins, List<Double> statistics ) {
		for (Protein protein : proteins) {
			if(protein.getStatisticTest() >= 0.00) {
				statistics.add(protein.getStatisticTest());	
			}
		}
		return statistics;
	}
	
	public static Map<String, List<Double>> getConditionCvs(List<Protein> proteins, Map<String, List<Double>> conditionsCv) {
		for (Protein protein : proteins) {
			for (Condition condition : protein.getConditions()) {

				if (conditionsCv.get(condition.getName()) == null) {
					conditionsCv.put(condition.getName(), new ArrayList<Double>());
				}
				conditionsCv.get(condition.getName()).add(condition.getCv());
			}
		}
		return conditionsCv;
	}
	
	public static void filterGoTermsAndProteins(Map<String, List<Protein>> goTermWithProteins, Map<String, List<Protein>> goTermWithProteinsFiltered,
			Integer minProteinsPerGoTerm) {
		
		Iterator it = goTermWithProteins.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			List<Protein> proteinsGoTerm = (List<Protein>) pairs.getValue();
			if(!proteinsGoTerm.isEmpty() && proteinsGoTerm.size() >= minProteinsPerGoTerm) {
				goTermWithProteinsFiltered.put((String) pairs.getKey(), new ArrayList<Protein>());
				for(Protein protein :  proteinsGoTerm) {
					goTermWithProteinsFiltered.get((String) pairs.getKey()).add(protein);
					System.out.println(pairs.getKey() + " = " + protein.getProteinId());
				}
			}
		}
		System.out.println("Filters go applied");

	}
	
	public static List<Protein> removeWhatIsNotProteinData(List<Protein> proteins) {
		List<Protein> filteredList = proteins.stream().filter(i -> i.getProteinId() != null && 
				i.getProteinId() != "" && !i.getProteinId().equalsIgnoreCase(PROTEIN_IDS)).collect(Collectors.toList());
		return filteredList;
	}
	
	public static String[] shuffleProteins(String[] array) {
	    int n = array.length;
	    for (int i = 0; i < array.length; i++) {
	        // Get a random index of the array past i.
	        int random = i + (int) (Math.random() * (n - i));
	        // Swap the random element with the present element.
	        String randomElement = array[random];
	        array[random] = array[i];
	        array[i] = randomElement;
	    }
	    return array;
	}
	
	public static List<Protein> getRandomlySelectedProteins(Integer quantity, List<Protein> proteins) {
		List<Protein> proteinsRandomlySelected = new ArrayList<Protein>();
		String[] proteinsId = DataUtil.getProteinIdArray(proteins);
		proteinsId = DataUtil.shuffleProteins(proteinsId);
		for (int i = 0; i < quantity; i++) {
			for (Protein prot : proteins) {
				if (prot.getProteinId().equalsIgnoreCase(proteinsId[i])) {
					proteinsRandomlySelected.add(prot);
					break;
				}
			}
		}
		return proteinsRandomlySelected;
	}
	
	public static void printGoTermConditionWeights(HashMap<String, HashMap<String, Double>> goTermWeightPerCondition) {
		
		Iterator itConditionMap = goTermWeightPerCondition.entrySet().iterator();

		while (itConditionMap.hasNext()) {

			Map.Entry pairs = (Map.Entry) itConditionMap.next();
			
			HashMap<String, Double> goMap = (HashMap<String, Double>) pairs.getValue();
			
			Iterator itGoMap = goMap.entrySet().iterator();
			
			System.out.println("CONDITION =>" + pairs.getKey() );
			
			while (itGoMap.hasNext()) {
				
				Map.Entry pairs2 = (Map.Entry) itGoMap.next();
			
				Double weight = (Double) pairs2.getValue();
				
				System.out.println("---GO_ID:" + pairs2.getKey() + " - WEIGHT: " + weight );

			}
		}
	}
	
	public static ArrayList<Protein> orderProteinConditionWeightAsc(List<Protein> originalProteins, Condition condition) {
		Map<Protein, Double> proteinConditionWeight = new HashMap<Protein, Double>();

		//get the proteins weight to the condition
		for(Protein prot : originalProteins) {
			for(Condition cond : prot.getConditions()) {
				if(cond.getName().equalsIgnoreCase(condition.getName())) {
					proteinConditionWeight.put(prot, cond.getWeight());

				}
			}
		}
		//get the proteins ordered asc by condition weight
		Map<Protein, Double> proteinConditionWeightSorted = 
				proteinConditionWeight.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		
		return new ArrayList<>(proteinConditionWeightSorted.keySet());

	}
	
	
	public static ArrayList<GoTermCondition> orderGoTermConditionByPvalueAsc(List<GoTerm> goTerms, Condition condition) {
		Map<GoTermCondition, Double> goTermConditionPvalue = new HashMap<GoTermCondition, Double>();

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition goTermCondition : goTerm.getConditions()) {
				if(goTermCondition.getCondition().getName().equalsIgnoreCase(condition.getName())) {
					goTermConditionPvalue.put(goTermCondition, goTermCondition.getFinalPvalue());
				}
			}
		}
		
		//get the proteins ordered asc by condition weight
		Map<GoTermCondition, Double> goTermConditionPvalueSorted = 
				goTermConditionPvalue.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		
		return new ArrayList<>(goTermConditionPvalueSorted.keySet());

	}
}
