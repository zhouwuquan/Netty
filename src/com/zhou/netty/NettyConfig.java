package com.zhou.netty;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * �洢�������̵�ȫ������
 * @author zhouwq
 *
 */
public class NettyConfig {
	/**
	 * �洢ÿһ���ͻ��˽���ʱ��Channel����
	 */
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
