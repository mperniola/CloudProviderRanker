package org.indigo.cloudproviderranker.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.indigo.cloudproviderranker.dto.Preference;
import org.indigo.cloudproviderranker.dto.Priority;
import org.indigo.cloudproviderranker.dto.Provider;
import org.indigo.cloudproviderranker.dto.RankRequest;
import org.indigo.cloudproviderranker.dto.RankResult;
import org.indigo.cloudproviderranker.dto.Service;
import org.indigo.cloudproviderranker.dto.Sla;
import org.indigo.cloudproviderranker.dto.RankedService;
import org.indigo.cloudproviderranker.service.RankService;
import org.indigo.cloudproviderranker.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class RankController {	
	
	private static final Logger logger = LoggerFactory.getLogger(RankController.class);
	
	@Autowired
	private RankService rankService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/rank", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<RankResult> rank(@Valid @RequestBody RankRequest request) {
		
		logger.debug("Calling API endpoint /rank");
		
		/**
		 * The request body contains three main structure:
		 * 1) SLA preferences
		 * 2) SLA targets
		 * 3) monitoring information for each service
		 * The following code creates an hash map starting from the information retrieved by the request
		 * the key is the service_id, the value is an Object of type RankedService that collects
		 * information about the SLA weight, the SLA targets and the monitoring metrics.
		 * Then this hash map is sent to the RankService to compute the score for each service
		 * and then the rank. 
		 */
		logger.debug("Rank Request: " + request);
		
		HashMap<String, RankedService> rankedServiceMap = new HashMap<>();
		
		for (Preference preference : request.getPreferences()) {
			List<Priority> priorities = preference.getPriority();
			for (Priority priority : priorities) {
				String slaId = priority.getSla_id();
				String serviceId = priority.getService_id();
				// filter SLA on service_id
				Sla sla = request.getSla().stream()
						          .filter(locSla -> slaId.equals(locSla.getId()))
						          .findAny().orElse(null);
				// filter services on service_id
				Service service = sla.getServices().stream().
						          filter(locService -> serviceId.equals(locService.getService_id()))
						          .findAny().orElse(null);
				
				rankedServiceMap.put(serviceId, RankedService.builder()
				        .serviceId(serviceId)
				        .serviceType(preference.getService_type())
				        .provider(sla.getProvider())
				        .targets(service.getTargets())
				        .slaWeight(priority.getWeight().floatValue()).build());
						        		  
			}
		}
		for (Provider provider : request.getMonitoring()) {
			for (Service service: provider.getServices()) {
				RankedService rankedService;
				String serviceId = service.getService_id();
				if (rankedServiceMap.containsKey(serviceId)) {
					rankedService = rankedServiceMap.get(serviceId);
					rankedService.setProvider(provider.getProvider());
					rankedService.setMetrics(Utils.metricsToMap(service.getMetrics()));
					rankedService.setServiceType(service.getType());
					rankedService.setServiceParentId(service.getService_parent_id());
				}
				else rankedService = RankedService.builder()
						             .provider(provider.getProvider())
						             .serviceId(serviceId)
						             .serviceType(service.getType())
						             .serviceParentId(service.getService_parent_id())
						             .metrics(Utils.metricsToMap(service.getMetrics())).build(); 
				
				
				rankedServiceMap.put(serviceId, rankedService);
			}
		}	
		
		return rankService.run(rankedServiceMap);
	}
	
}
	
	
