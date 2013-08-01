package com.zeedoo.mars.message.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.zeedoo.mars.database.dao.SensorStatusDao;
import com.zeedoo.commons.domain.SensorStatus;
import com.zeedoo.mars.message.Message;
import com.zeedoo.mars.message.MessageDeserializer;
import com.zeedoo.mars.message.MessageType;

@Component
public class SensorAliveStatusMessageHandler extends AbstractMessageHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SensorAliveStatusMessageHandler.class);
	
	@Autowired
	private SensorStatusDao sensorStatusDao;

	@Override
	protected Optional<Message> doHandleMessage(Message message,
			ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("Handling Message={}", message);
		Preconditions.checkArgument(message.getPayload() != null, "Payload should not be null");
		List<SensorStatus> statusList = MessageDeserializer.deserializeSensorAliveStatusPayload(message.getPayloadAsRawJson());
		int affectedRecords = 0;
		for (SensorStatus status : statusList) {
			// check if sensor status already exists
			SensorStatus existingStatus = sensorStatusDao.get(status.getSensorId());
			int result = 0;
			if (existingStatus != null) {
				result = sensorStatusDao.update(status);
			} else {
				result = sensorStatusDao.insert(status);
			}
			affectedRecords += result;
		}
		LOGGER.debug("Inserted/updated {} sensor alive status records", affectedRecords);
		return Optional.<Message>absent();
	}

	@Override
	public MessageType getHandledType() {
		return MessageType.SENSOR_ALIVE_STATUS;
	}
}
