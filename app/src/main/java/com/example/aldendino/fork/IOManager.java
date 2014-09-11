package com.example.aldendino.fork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;

public class IOManager {

    private IOAble ioable ;

    private String rootName = "ROOT" ;
    private String dataFilename = "data" ;
    private String currentFilename = "current" ;

    public IOManager(IOAble ioable) {
        this.ioable = ioable ;
    }

    public void importData()
    {
        try
        {
            ioable.setRoot(readRoot()) ;
            if(ioable.getRoot() == null) throw new NullPointerException()  ;
        }
        catch(Exception e)
        {
            ioable.setRoot(new ListTree(rootName, null)) ;
        }

        try
        {
            ioable.setCurrentPath(readCurrent()) ;
            if(ioable.getCurrentPath() == null) throw new NullPointerException() ;
            ioable.setCurrent(ioable.getRoot()) ;
            for(int i = 0 ; i < ioable.getCurrentPath().size() ; i++)
            {
                if(ioable.getCurrent().isList())
                {
                    ioable.setCurrent(ioable.getCurrent().list.get(ioable.getCurrentPath().get(i))) ;
                }
            }
        }
        catch(Exception e)
        {
            ioable.showErrorToast("Current path exception") ;
            ioable.setCurrentPath(new ArrayList<Integer>()) ;
            ioable.setCurrent(ioable.getRoot()) ;
        }
    }

    public ListTree readRoot() throws ClassNotFoundException, IOException
    {
        FileInputStream fis = ioable.openFileInput(dataFilename) ;
        ObjectInputStream ois = new ObjectInputStream(fis) ;
        ListTree temp = (ListTree) ois.readObject() ;
        ois.close();
        fis.close() ;
        return temp ;
    }

    public ArrayList<Integer> readCurrent() throws ClassNotFoundException, IOException
    {
        FileInputStream fis = ioable.openFileInput(currentFilename) ;
        ObjectInputStream ois = new ObjectInputStream(fis) ;
        @SuppressWarnings("unchecked") //Unsafe casting
                ArrayList<Integer> temp = (ArrayList<Integer>) ois.readObject() ;
        ois.close();
        fis.close() ;
        return temp ;
    }

    public void saveFiles()
    {
        try
        {
            FileOutputStream fos = ioable.openFileOutput(dataFilename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos) ;
            oos.writeObject(ioable.getRoot());
            oos.close();
            fos.close();
        }
        catch(IOException ioe) {
            ioable.showErrorToast("Could not save \'" + dataFilename + "\'");
        }
        try
        {
            FileOutputStream fos = ioable.openFileOutput(currentFilename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos) ;
            oos.writeObject(ioable.getCurrentPath());
            oos.close();
            fos.close();
        }
        catch(IOException ioe) {
            ioable.showErrorToast("Could not save \'" + currentFilename + "\'");
        }
    }
}
