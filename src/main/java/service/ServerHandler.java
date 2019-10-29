package service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import task.TaskEvent;
import task.TaskWork;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private TaskWork taskWork = new TaskWork();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("receive msg :" + s);
        //taskWork.sendTask(new TaskEvent(1));
        channelHandlerContext.writeAndFlush("msg received ").addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                System.out.println("server response finish");
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

}
