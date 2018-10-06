package com.zhou.netty;

import java.nio.charset.Charset;
import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

/**
 * ����/����/��Ӧ�ͻ���websocket����ĺ���ҵ������
 * @author zhouwq
 *
 */
public class MyWebSocketHandler extends SimpleChannelInboundHandler<Object> {
	private WebSocketServerHandshaker handshaker;
	private static final String WEB_SOCKET_URL="ws://localhost:8888/websocket";
	/**
	 * �ͻ��������˴������ӵ�ʱ�����
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.channelActive(ctx);
		NettyConfig.group.add(ctx.channel());
		System.out.println("�ͻ������������ӿ���...");
	}

	/**
	 * �ͻ��������˶Ͽ����ӵ�ʱ�����
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.channelInactive(ctx);
		NettyConfig.group.remove(ctx.channel());
		System.out.println("�ͻ������������ӹر�...");
	}

	/**
	 * ����˽��ܿͻ��˷��͹��������ݽ���֮�����
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.channelReadComplete(ctx);
		ctx.flush();
	}

	/**
	 * ���̳����쳣��ʱ�����
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		//super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}
	
	/**
	 * ����˴���ͻ���websocket����ĺ��ķ���
	 */
	@Override
	protected void messageReceived(ChannelHandlerContext context, Object msg) throws Exception {
		// ����ͻ��������˷���Http���������ҵ��
		if(msg instanceof FullHttpRequest) {
			handHttpRequest(context, (FullHttpRequest)msg);
		}else if(msg instanceof WebSocketFrame) {//����websocket����ҵ��
			handWebsocketFrame(context, (WebSocketFrame) msg);
		}
	}

	/**
	 * ����ͻ���������֮���websocketҵ��
	 * @param ctx
	 * @param frame
	 */
	private void handWebsocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame) {
		//�ж��Ƿ��ǹر�websocketָ��
		if(frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
		}
		//�ж��Ƿ���ping��Ϣ
		if(frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
		}
		//�ж��Ƿ��Ƕ�������Ϣ������Ƕ�������Ϣ���׳��쳣
		if(!(frame instanceof TextWebSocketFrame)) {
			System.out.println("Ŀǰ���ǲ�֧�ֶ�������Ϣ");
			throw new RuntimeException("��"+this.getClass().getName()+"����֧����Ϣ");
		}
		
		//����Ӧ����Ϣ
		//��ȡ�ͻ��������˷��͵���Ϣ
		String request = ((TextWebSocketFrame)frame).text();
		System.out.println("������յ��ͻ��˵���Ϣ===>>>"+request);
		TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()+ctx.channel().id()+"===>>>"+request);
		
		//Ⱥ�����������ÿ�����������Ŀͻ���Ⱥ����Ϣ
		NettyConfig.group.writeAndFlush(tws);
	}
	/**
	 * ����ͻ��������˷���http���������ҵ��
	 * @param ctx
	 * @param req
	 */
	private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		if(!req.getDecoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
		handshaker  = wsFactory.newHandshaker(req);
		if(handshaker ==  null) {
			WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		}else{
			handshaker.handshake(ctx.channel(), req);
		}
	}
	
	/**
	 * �������ͻ�����Ӧ��Ϣ
	 * @param ctx
	 * @param req
	 * @param res
	 */
	private void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest req,DefaultFullHttpResponse res) {
		if(res.getStatus().code()!=200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		//�������ͻ��˷�������
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if(res.getStatus().code()!=200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
