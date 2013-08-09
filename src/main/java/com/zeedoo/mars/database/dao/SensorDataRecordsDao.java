package com.zeedoo.mars.database.dao;

import java.net.URI;
import java.util.Collections;import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.zeedoo.commons.api.CoreApiPath;
import com.zeedoo.commons.domain.SensorDataRecord;
import com.zeedoo.commons.domain.SensorDataRecords;
import com.zeedoo.mars.database.aop.Transactional;

@Component
public class SensorDataRecordsDao extends EntityDao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SensorDataRecordsDao.class);
	
	// Insert a list of records
	// Note: This should be the preferred way to insert multiple records for performance reasons
	@Transactional
	public void insertDataRecords(List<SensorDataRecord> records) {
		Preconditions.checkArgument(records != null, "Records should not be null");
		URI uri = UriBuilder.fromPath(CoreApiPath.SENSOR_DATA.getPath()).build();
		SensorDataRecords sensorDataRecords = new SensorDataRecords(records);
		ClientResponse response = coreApiClient.put(uri.toASCIIString(), ClientResponse.class, sensorDataRecords);
		if (Status.OK.equals(response.getClientResponseStatus()))
		{
			LOGGER.debug("Succesfully inserted/updated SensorDataRecords with size={}", records.size());
		} else {
			LOGGER.error("Unable to insert/update SensorDataRecords. Core API response={}", response.getStatus());
		}
	}
	
	@Transactional
	public List<SensorDataRecord> get(String sensorId, DateTime start, DateTime end) {
		Preconditions.checkArgument(sensorId != null, "Sensor Id should not be null");
		UriBuilder uriBuilder = UriBuilder.fromPath(CoreApiPath.SENSOR_DATA.getPath()).path(sensorId);
		if (start != null) {
			uriBuilder = uriBuilder.queryParam("startDate", start);
		}
		if (end != null) {
			uriBuilder = uriBuilder.queryParam("endDate", end);
		}
        SensorDataRecords records = coreApiClient.get(uriBuilder.build().toString(), SensorDataRecords.class);
        if (records == null || records.getSensorDataRecords() == null || records.getSensorDataRecords().isEmpty()) {
        	LOGGER.warn("Could NOT find any records with sensorId={}, startDate={}, endDate={}. Returning empty list", new Object[]{sensorId, start, end});
        	return Collections.emptyList();
        }
		return records.getSensorDataRecords();
	}
	
	@Transactional
	public void insert(SensorDataRecord record) {
		Preconditions.checkArgument(record != null, "Record should not be null");
		List<SensorDataRecord> singleRecord = Lists.newArrayList(record);
		URI uri = UriBuilder.fromPath(CoreApiPath.SENSOR_DATA.getPath()).build();
		SensorDataRecords sensorDataRecords = new SensorDataRecords(singleRecord);
		ClientResponse response = coreApiClient.put(uri.toASCIIString(), ClientResponse.class, sensorDataRecords);
		if (Status.OK.equals(response.getClientResponseStatus()))
		{
			LOGGER.debug("Succesfully inserted/updated SensorDataRecords with size={}", singleRecord.size());
		} else {
			LOGGER.error("Unable to insert/update SensorDataRecords. Core API response={}", response.getStatus());
		}
	}
	
	// For testing purposes
	@Transactional
	public int delete(String sensorId, DateTime start, DateTime end) {
    	//Preconditions.checkArgument(sensorId != null, "Sensor Id should not be null");
    	//Date startDate = start != null? start.toDate() : null;
		//Date endDate = end != null? end.toDate() : null;
		//return mapper.delete(sensorId, startDate, endDate);
		return 1;
	}
}
