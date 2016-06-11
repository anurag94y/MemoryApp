/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mainmemoryapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.memoryappservice.MemoryAppService;

/**
 *
 * @author Aturag
 */
public class MemoryAppClient {
    public static void main(String[] args)
  {
    try
    {
      TSocket transport = new TSocket("localhost", 7911);
      transport.open();
      TProtocol protocol = new TBinaryProtocol(transport);
 
      MemoryAppService.Client service = new MemoryAppService.Client(protocol);
 
      operations(service);
      
      System.out.println("Disconnect client from Server");
      transport.close();
    } 
    catch (TException e)
    {
      e.printStackTrace();
    }
  }
    
  private static void operations(MemoryAppService.Client service) throws TException {
      int cond = 1;
        while(cond > 0) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = "";
            try {
                s = br.readLine();
            } catch (IOException ex) {
                System.out.println("Error in Input");
            }
            
            String words[] = s.split("\\s+");
            
            if(words[0].equals("set")) {
                if(words.length == 3) {
                    service.setvalue(words[1], words[2]);
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("get")) {
                if(words.length == 2) {
                    String value = service.get(words[1]);
                    System.out.println(value);
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("delete")) {
                if(words.length == 2) {
                    service.delet(words[1]);
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("count")) {
                if(words.length == 2) {
                    int value = service.count(words[1]);
                    System.out.println(value);
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("start")) {
                localTransaction(service);
            }      
        }
    } 

    private static void localTransaction(MemoryAppService.Client service) throws TException {
        int cond = 1;
        ConcurrentHashMap<String, String> local_map = new ConcurrentHashMap();
        ConcurrentHashMap<String, Integer> local_count = new ConcurrentHashMap();
        ConcurrentHashMap<String, Integer> local_delete = new ConcurrentHashMap();
        while(cond > 0) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = "";
            try {
                s = br.readLine();
            } catch (IOException ex) {
                System.out.println("Error in Input");
            }

            String words[] = s.split("\\s+");
            if(words[0].equals("set")) {
                if(words.length == 3) {
                    String temp_key = null;
                    if(!local_delete.containsKey(words[1])) {
                        if(local_map.containsKey(words[1]))
                             temp_key = local_map.get(words[1]);
                        else
                            temp_key = service.get(words[1]);

                        if(local_count.containsKey(temp_key)) {
                            local_count.put(temp_key, local_count.get(temp_key) - 1);
                        }
                        else {
                            local_count.put(temp_key, service.count(temp_key) - 1);
                        }
                    }
                        
                    local_map.put(words[1],words[2]);
                    local_delete.remove(words[1]);
                    if(local_count.containsKey(words[2])) {
                        local_count.put(words[2], local_count.get(words[2]) + 1);
                    } else {
                       local_count.put(words[2], service.count(words[2]) + 1);
                    }
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("get")) {
                if(words.length == 2) {
                    String value = null;
                    if(local_map.containsKey(words[1]))
                        value = local_map.get(words[1]);
                    else if(!local_delete.containsKey(words[1]))
                        value = service.get(words[1]); 

                    System.out.println(value);
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("delete")) {
                if(words.length == 2) {
                    if(!local_delete.containsKey(words[1])) {
                        local_delete.put(words[1], 1);
                        String value = null;
                        if(local_map.containsKey(words[1]))
                            value = local_map.get(words[1]);
                        else
                            value = service.get(words[1]);

                        int count = 0;
                        if(local_count.containsKey(value))
                            count = local_count.get(value);
                        else
                            count = service.count(value);
                        local_count.put(value, count - 1);
                        if(local_map.containsKey(words[1]))
                            local_map.remove(words[1]);
                    }
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("count")) {
                if(words.length == 2) {
                    int value = 0;
                    if(local_count.containsKey(words[1])) {
                        value = local_count.get(words[1]);
                    }
                    else
                        value = service.count(words[1]);
                    System.out.println(value);
                }
                else {
                    System.out.println("Wrong Input");
                }
            }
            else if(words[0].equals("commit")) {
                Set entryset = local_map.entrySet();
                Iterator it = entryset.iterator();                
                while(it.hasNext()) {
                    Map.Entry<String, String> temp = (Map.Entry)it.next();
                    service.setvalue(temp.getKey(), temp.getValue());
                }

                entryset = local_count.entrySet();
                it = entryset.iterator();                
                while(it.hasNext()) {
                    Map.Entry<String, Integer> temp = (Map.Entry)it.next();
                    service.setcount(temp.getKey(), temp.getValue());
                }

                entryset = local_delete.entrySet();
                it = entryset.iterator();                
                while(it.hasNext()) {
                    Map.Entry<String, Integer> temp = (Map.Entry)it.next();
                    if(temp.getValue() == 1)
                        service.delet(temp.getKey());
                }
                break;
            }      
            else if(words[0].equals("rollback")) {
                break;
            }
        }
    }
   
}
