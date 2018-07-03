package br.com.usp.labis.useful;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;

public class DataUtil {

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

	public static Double getMaxValueFromList(List<Double> list) {
		Double max = 0.00;
		if (list != null && !list.isEmpty()) {
			max = Collections.max(list);
		}
		return max;
	}

	public static Map<String, List<Double>> getConditionMeans(List<Protein> proteins) {
		Map<String, List<Double>> conditionMeans = new HashMap<String, List<Double>>();
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
	
	public static List<Double> getProteinsStatisticTest(List<Protein> proteins) {
		List<Double> statistics = new ArrayList<Double>();
		for (Protein protein : proteins) {		
			statistics.add(protein.getStatisticTest());			
		}
		return statistics;
	}
	
	public static Map<String, List<Double>> getConditionCvs(List<Protein> proteins) {
		Map<String, List<Double>> conditionsCv = new HashMap<String, List<Double>>();
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
}
