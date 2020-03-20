package io.axoniq.labs.chat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.h2.tools.Server;

public class Servers
{
    public static void main(final String[] args) throws Exception
    {
        final Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
        server.start();
        System.out.println("Database running on port 9092");

        Method startMethod = null;
        Object gossipRouter = null;
        Method stopMethod = null;
        try
        {
            final Class<?> gossipRouterClass = Servers.class.getClassLoader().loadClass("org.jgroups.stack.GossipRouter");
            final Constructor<?> constructor = gossipRouterClass.getDeclaredConstructor(String.class, int.class);
            gossipRouter = constructor.newInstance("127.0.0.1", 12001);
            startMethod = gossipRouterClass.getMethod("start");
            stopMethod = gossipRouterClass.getMethod("stop");
        }
        catch (final ClassNotFoundException e)
        {
            // Gossip Router not on class path
        }

        if (startMethod != null)
        {
            startMethod.invoke(gossipRouter);
            System.out.println("Gossip Router started on port 12001");
        }

        System.out.println("Press any key to shut down");
        System.in.read();

        System.out.println("Stopping database.");
        server.stop();

        if (stopMethod != null)
        {
            System.out.println("Stopping Gossip Router");
            stopMethod.invoke(gossipRouter);
        }
    }
}
