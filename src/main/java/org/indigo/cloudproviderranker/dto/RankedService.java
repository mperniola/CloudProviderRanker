package org.indigo.cloudproviderranker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

//TODO docs

/**
 * 
 * 
 *
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankedService {
	
	/**
	 * 
	 */
	private String provider;
	
	/**
	 * 
	 */
	@JsonProperty("service_id")
    private String serviceId;
    
	/**
	 * 
	 */
	private String serviceParentId;
    
	/**
	 * 
	 */
    private String serviceType;
	
    /**
     * 
     */
    private List<Target> targets;    
    
    /**
     * 
     */
    private Map<String, Float> metrics;
    
    /**
     * 
     */
    private Float metricsScore;
    
    /**
     * 
     */
    private Float slaScore;
    
    /**
     * 
     */
    private Float slaWeight;
    
    /**
     * 
     */
    @JsonProperty("total_score")
    private Float totalScore;
    
    /**
     * 
     */
    private int rank;
    
    /**
     * 
     */
    private boolean ranked;
    
//    /**
//     * 
//     * @param m
//     */
//    public void setMetrics(List<Metric> m) {
//    	metrics = m.stream().collect(Collectors.toMap(Metric::getMetricName, Metric::getMetricValue));
//    }
//    
//    /**
//     * 
//     * 
//     *
//     */
//    public static class RankedServiceBuilder {
//    	private Map<String, Float> metrics;
//    	
//    	public RankedServiceBuilder metrics(List<Metric> m) {
//    		this.metrics = m.stream().collect(Collectors.toMap(Metric::getMetricName, Metric::getMetricValue));
//    		return this;
//    	}
//    }

    /**
     * 
     * @param parentService
     */
	public void computeTotalScore(RankedService parentService) {
		this.totalScore = this.slaScore + this.metricsScore; 
		if( parentService != null) {
			this.totalScore += (parentService.getSlaScore() + parentService.getMetricsScore());
			
		}		
	}
}