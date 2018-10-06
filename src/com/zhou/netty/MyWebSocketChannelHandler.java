package com.zhou.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
/**
 * ��ʼ������ʱ��ĸ������
 * @author zhouwq
 *
 */
public class MyWebSocketChannelHandler extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel e) throws Exception {
		// TODO Auto-generated method stub
		e.pipeline().addLast("http-codec",new HttpServerCodec());
		e.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
		e.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
		e.pipeline().addLast("handler", new MyWebSocketHandler());
	}

}
