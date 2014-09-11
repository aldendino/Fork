package com.example.aldendino.fork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;

public interface IOable {
    public FileInputStream openFileInput(String filename) throws FileNotFoundException ;
    public FileOutputStream openFileOutput(String filename, int contextMode) throws FileNotFoundException ;
    //
    public ListTree getRoot() ;
    public void setRoot(ListTree root) ;
    public ListTree getCurrent() ;
    public void setCurrent(ListTree current) ;
    //
    public ArrayList<Integer> getCurrentPath() ;
    public void setCurrentPath(ArrayList<Integer> currentPath) ;
    public void showErrorToast(String string) ;
}
