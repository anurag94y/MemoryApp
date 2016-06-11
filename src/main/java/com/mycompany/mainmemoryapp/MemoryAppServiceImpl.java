/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mainmemoryapp;

import java.util.concurrent.ConcurrentHashMap;
import org.memoryappservice.MemoryAppService;

/**
 *
 * @author Aturag
 */
public class MemoryAppServiceImpl implements MemoryAppService.Iface{
     private static ConcurrentHashMap<String, String> global_map = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, Integer> global_count = new ConcurrentHashMap();
    
    public String get(String key) {
        System.out.println(key);
        if(global_map.containsKey(key)) {
            return global_map.get(key);
        }
        return "";
    }
    
    public int count(String value) {
        if(global_count.containsKey(value)) {
            return global_count.get(value);
        }
        return 0;
    } 
    
    public void delet(String key) {
        if(global_map.containsKey(key)) {
            String value = global_map.get(key);
            int count = global_count.get(value);
            global_count.put(value, count - 1);
            global_map.remove(key);
        }
    }
    
    public void setvalue(String key, String value) {
        if(global_map.containsKey(key)) {
            String temp_key = global_map.get(key);
            global_count.put(temp_key, global_count.get(temp_key) - 1);
        }
        global_map.put(key, value);
        if(global_count.containsKey(value)) {
            global_count.put(value, global_count.get(value) + 1);
        } else {
            global_count.put(value, 1);
        }
    }
    
    public void setcount(String key, int count) {
        global_count.put(key, count);
    }
    
}
