package com.github.godofacceptance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ReadWriteWizard {

    private static final String READ_SRC = "src/storage/";
    private static final String WRITE_SRC = READ_SRC;

    public static void saveObject(Map<?,?> map, String name){

        try (FileOutputStream fstream = new FileOutputStream(READ_SRC + name + ".ser");
                ObjectOutputStream ostream = new ObjectOutputStream(fstream);){
            ostream.writeObject(map);
            System.out.println("@ReadWriteWizard:: saveObject:: Object has been serialized, and stored as " + name + ".ser" + " in " + WRITE_SRC);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Object readObjectFromFile(String name) throws IOException{
        try (FileInputStream fstream = new FileInputStream(READ_SRC + name);
                ObjectInputStream ostream = new ObjectInputStream(fstream);){
            Object obj = ostream.readObject();
            System.out.println("@ReadWriteWizard: readObjectFromFile:: Object has been deserialized");
            return obj;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new UnknownError("@ReadWriteWizard: readObjectFromFile:: An unknown error has occurred.");
    }
}
