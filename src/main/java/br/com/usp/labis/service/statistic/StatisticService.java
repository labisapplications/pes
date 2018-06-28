package br.com.usp.labis.service.statistic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.inference.TestUtils;
import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.Replicate;

@Component
public class StatisticService {

    /**
     * ANOVA test for N independent samples.
     * @param  conditions  protein conditions to be tested
     * @return p-value calculated
     */
	public double oneWayAnovaTest(List<Condition> conditions) {
		
		double pValue = 0.00;
		
		try {
			List<double[]> classesToTest = getReplicatesValuesToTest(conditions);
			
			pValue = TestUtils.oneWayAnovaPValue(classesToTest);     // P-value
			System.out.println("Result ANOVA  P-value: " + pValue );
			
		} catch (Exception e) {
			System.out.println("Error ANOVA: " + e.getMessage() + e.getCause());
		}
		return pValue;
	}
	
	public List<double[]> getReplicatesValuesToTest(List<Condition> conditions) {
		List<double[]> classesToTest = new ArrayList<double[]>();
		for (Condition condition : conditions) {
			double	[] replicatesValues = new double[condition.getReplicates().size()];
			Integer count = 0;
			for (Replicate replicate : condition.getReplicates()) {
				replicatesValues[count] = replicate.getValue();
				count++;
			}

			classesToTest.add(replicatesValues);
		}
		return classesToTest;
	}

	/**
	 * T Test for 2 independent samples considering 2 tails
	 * 
	 * @param conditions
	 *            protein conditions to be tested
	 * @return p-value calculated
	 */
	public double tTest(List<Condition> conditions) {
		double pValue = 0.00;
		try {
			List<double[]> classesToTest = getReplicatesValuesToTest(conditions);

			//test for 2 samples, 2 tail for independent samples
			pValue = TestUtils.tTest(classesToTest.get(0), classesToTest.get(1));    // P-value
			System.out.println("Result tTest  P-value: " + pValue );
			
		} catch (Exception e) {
			System.out.println("Error tTest: " + e.getMessage() + e.getCause());
		}
		return pValue;
	}
}
