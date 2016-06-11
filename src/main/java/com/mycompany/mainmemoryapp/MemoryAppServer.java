/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mainmemoryapp;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.memoryappservice.MemoryAppService;

/**
 *
 * @author Aturag
 */
public class MemoryAppServer {
    public static void main(String[] args)
  {
    try
    {
      TServerSocket serverTransport = new TServerSocket(7911);
      MemoryAppService.Processor processor = new MemoryAppService.Processor(new MemoryAppServiceImpl());
      TServer server = new TThreadPoolServer(
              new TThreadPoolServer.Args(serverTransport).processor(processor));
      System.out.println("Starting server on port 7911 ...");
      server.serve();
    } 
    catch (TTransportException e)
    {
      e.printStackTrace();
    }
  }
    
}
