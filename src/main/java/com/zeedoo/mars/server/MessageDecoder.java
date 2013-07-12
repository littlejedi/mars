package com.zeedoo.mars.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeedoo.mars.message.Message;
import com.zeedoo.mars.message.MessageDeserializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

/**
 * Decodes bytestream into {@link com.zeedoo.mars.message.Message} object
 * @author nzhu
 *
 */
public class MessageDecoder extends ByteToMessageDecoder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, MessageList<Object> out) throws Exception {
		// Convert to String first
		String rawJsonString = in.readBytes(in.readableBytes()).toString(CharsetUtil.UTF_8);	
		// De-serialize JSON to Message object
		Message message = MessageDeserializer.fromJSON(rawJsonString);
		out.add(message);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//TODO: Add exception handling'
	    LOGGER.error("Error occured", cause);
	}
}
