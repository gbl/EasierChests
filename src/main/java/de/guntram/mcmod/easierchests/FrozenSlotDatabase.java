/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.easierchests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gbl
 */
public class FrozenSlotDatabase {
    
    private final static int PLAYER_SLOTS=36;
    private static File configFile;
    private static boolean frozenSlots[];
    
    public static void init (File configDir) {
        frozenSlots=new boolean[PLAYER_SLOTS];
        configFile=new File(configDir, "easierChests.slots.txt");
        if (!loadConfigFile()) {
            for (int i=0; i<5; i++)
                frozenSlots[i]=true;
            for (int i=5; i<PLAYER_SLOTS; i++)
                frozenSlots[i]=false;
        }
    }

    private static boolean loadConfigFile() {
        BufferedReader reader=null;
        Pattern pattern=Pattern.compile("frozen\\.(\\d+)=(.*)");
        System.out.println("trying to read frozen slots from "+configFile.getAbsolutePath());
        try {
            reader=new BufferedReader(new FileReader(configFile));
            String line;
            while ((line=reader.readLine())!=null) {
                Matcher matcher=pattern.matcher(line);
                if (matcher.matches()) {
                    try {
                        int index=Integer.parseInt(matcher.group(1));
                        String value=matcher.group(2);
                        if (index>=0 && index<PLAYER_SLOTS)
                            frozenSlots[index]=value.startsWith("t");
                    } catch (NumberFormatException ex) {
                        ;
                    }
                }
            }
            reader.close();
            return true;
        } catch (IOException ex) {
            System.out.println(ex);
            try {
                if (reader!=null)
                    reader.close();
            } catch (IOException ex1) {
            }
            return false;
        }
    }
    
    private static void saveConfigFile() {
        PrintWriter writer=null;
        System.out.println("trying to save frozen slots to "+configFile.getAbsolutePath());
        try {
            writer=new PrintWriter(new FileWriter(configFile));
            for (int i=0; i<PLAYER_SLOTS; i++)
                writer.println("frozen."+i+"="+(frozenSlots[i] ? "true" : "false"));
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex);
            if (writer!=null)
                writer.close();
        }
    }
    
    public static boolean isSlotFrozen(int slot) {
        if (slot>=0 && slot<PLAYER_SLOTS)
            return frozenSlots[slot];
        else
            return false;
    }
    
    public static void setSlotFrozen(int slot, boolean b) {
        if (slot>=0 && slot<PLAYER_SLOTS && frozenSlots[slot]!=b) {
            frozenSlots[slot]=b;
            saveConfigFile();
        }
    }
}
