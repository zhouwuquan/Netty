package com.zhou.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * �������ڣ���������Ӧ��
 * @author zhouwq
 *
 */
public class Main {
	public static void main(String[] args) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new MyWebSocketChannelHandler());
			//����˿����ȴ��ͻ�������
			System.out.println("����˿����ȴ��ͻ������ӡ�����");
			Channel ch = b.bind(8888).sync().channel();
			ch.closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			//���ŵ��˳�����
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}	
