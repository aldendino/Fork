package com.example.aldendino.fork;

import java.util.ArrayList;

public interface Parsable {

    public void clearInput() ;
    public void setAutoCompleteOptions() ;
    public void showErrorToast(String errorMessage) ;
    public void openRemoveDialog() ;
    //
    public ListTree getCurrent() ;
    public ListTree getRoot() ;
    public void setCurrent(ListTree listTree) ;
    public ArrayList<Integer> getCurrentPath() ;
    public String getListAsString(ListTree rootTree) ;
    public void saveTextToClipboard(String text) ;
    public String getClipboardString() ;
    public void setPrevious(ListTree previous);
    public void startEmail(String subject, String body);
}
