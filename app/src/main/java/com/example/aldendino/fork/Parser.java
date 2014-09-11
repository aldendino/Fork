package com.example.aldendino.fork;

import java.util.StringTokenizer;

public class Parser {

    private Parsable parsee ;

    public static final String ADD = "add" ;
    public static final String REMOVE = "rm" ;
    public static final String CHANGE_DIR = "cd" ;
    public static final String ROOT = "." ;
    public static final String PARENT = ".." ;
    public static final String MOVE = "mv" ;
    public static final String SWAP = "swp" ;
    public static final String COPY = "cpy" ;
    public static final String CUT = "cut" ;
    public static final String CLIP = "clp" ;
    public static final String CLIP_ALL = "clpall" ;
    public static final String EMAIL = "eml" ;

    public static final String REMOVE_DUP = "rmdup" ;

    private final String NULL_ROOT_ERROR = "Root is null" ;
    private final String INVALID_COMMAND_ERROR = "Invalid command" ;
    private final String INDEX_BOUNDS_ERROR = "Index out of bounds" ;

    public Parser(Parsable parsee)
    {
        this.parsee = parsee ;
    }

    public void parseString(String command)
    {
        if(parsee != null)
        {
            StringTokenizer st = new StringTokenizer(command) ;
            if(st.hasMoreTokens())
            {
                String par1 = st.nextToken();
                if(par1.equals(CHANGE_DIR))
                {
                    changeDir(st) ;
                }
                else if(par1.equals(ADD))
                {
                    add(st) ;
                }
                else if(par1.equals(REMOVE))
                {
                    remove(st) ;
                }
                else if(par1.equals(MOVE))
                {
                    move(st) ;
                }
                else if(par1.equals(SWAP))
                {
                    swap(st) ;
                }
                else if(par1.equals(CLIP))
                {
                    clip(st) ;
                }
                else if(par1.equals(CLIP_ALL))
                {
                    clipAll(st) ;
                }
                else if(par1.equals(EMAIL)) {
                    email(st) ;
                }
				/*else if(par1.equals(REMOVE_DUP))
				{
					//cleanUniq() ;
				}*/
                else
                {
                    parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
                }
            }
            else
            {
                parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
            }
        }
    }

    private void email(StringTokenizer st) {
        if(!st.hasMoreTokens()) {
            parsee.startEmail("Fork Backup", parsee.getClipboardString());
        }

    }

    private void changeDir(StringTokenizer st)
    {
        if(st.hasMoreTokens())
        {
            String par2 = st.nextToken();
            if(par2.equals(ROOT) && !st.hasMoreTokens())
            {
                toRoot() ;
            }
            else if(par2.equals(PARENT) && !st.hasMoreTokens())
            {
                if(parsee.getCurrent().parent != null)
                {
                    //parsee.setPrevious(parsee.getCurrent());
                    parsee.setCurrent(parsee.getCurrent().parent) ;
                    parsee.getCurrentPath().remove(parsee.getCurrentPath().size() - 1) ;
                    parsee.clearInput() ;
                    parsee.setAutoCompleteOptions() ;
                }
            }
            else if(isInteger(par2) && !st.hasMoreTokens())
            {
                int index = Integer.parseInt(par2) ;
                try
                {
                    parsee.setCurrent(parsee.getCurrent().list.get(index - 1)) ;
                    parsee.getCurrentPath().add(index - 1) ;
                    parsee.clearInput() ;
                    parsee.setAutoCompleteOptions() ;
                }
                catch(Exception e)
                {
                    parsee.showErrorToast(INDEX_BOUNDS_ERROR) ;
                }
            }
            else
            {
                while(st.hasMoreTokens())
                {
                    par2 += " " + st.nextToken() ;
                }
                if(parsee.getCurrent().isList())
                {
                    ListTree item = parsee.getCurrent().getList(par2) ;
                    if(item != null)
                    {
                        parsee.getCurrentPath().add(parsee.getCurrent().list.indexOf(item)) ;
                        parsee.setCurrent(item) ;
                        parsee.clearInput() ;
                        parsee.setAutoCompleteOptions() ;
                    }
                }
            }
        }
        else
        {
            toRoot() ;
        }
    }

    private void add(StringTokenizer st)
    {
        if(st.hasMoreTokens())
        {
            String par2 = st.nextToken() ;
            while(st.hasMoreTokens())
            {
                par2 += " " + st.nextToken() ;
            }
            parsee.getCurrent().addLeafFirst(par2) ;
            parsee.clearInput() ;
            parsee.setAutoCompleteOptions() ;
        }
    }

    private void addUniq(StringTokenizer st)
    {
        if(st.hasMoreTokens())
        {
            String par2 = st.nextToken() ;
            while(st.hasMoreTokens())
            {
                par2 += " " + st.nextToken() ;
            }
            if(parsee.getCurrent().isList())
            {
                if(!parsee.getCurrent().list.contains(par2))
                {
                    parsee.getCurrent().addLeaf(par2) ;
                    parsee.clearInput() ;
                    parsee.setAutoCompleteOptions() ;
                }
                else
                {
                    parsee.showErrorToast("List already exists") ;
                }
            }

        }
    }
	
	/*
	 * Not quite removing the right stuff just yet...
	 */
	/*private void cleanUniq()
	{
		if(parsee.getCurrent().isList())
		{
			ArrayList<ListTree> toRemove = new ArrayList<ListTree>() ;
			for(int i = 0 ; i < parsee.getCurrent().list.size() - 1 ; i++)
			{
				for(int j = i + 1 ; i < parsee.getCurrent().list.size() ; i++)
				{
					if(parsee.getCurrent().list.get(i).name.equals(parsee.getCurrent().list.get(j).name)) toRemove.add(parsee.getCurrent().list.get(j)) ;
				}
			}
			for(int i  = 0 ; i < parsee.getCurrent().list.size() ; i++)
			{
				parsee.getCurrent().list.remove(i) ;
			}
		}
	}*/

    private void remove(StringTokenizer st)
    {
        if(st.hasMoreTokens())
        {
            String par2 = st.nextToken();
            if(isInteger(par2))
            {
                if(!st.hasMoreTokens())
                {
                    int index = Integer.parseInt(par2) ;
                    if(parsee.getCurrent().removeListAt(index))
                    {
                        if(parsee.getCurrent().list.isEmpty()) parsee.getCurrent().list = null ;
                        parsee.clearInput() ;
                        parsee.setAutoCompleteOptions() ;
                    }
                    else parsee.showErrorToast(INDEX_BOUNDS_ERROR) ;
                }
                else
                {
                    String par3 = st.nextToken();
                    if(isInteger(par3) && !st.hasMoreTokens())
                    {
                        int fromIndex = Integer.parseInt(par2) ;
                        int toIndex = Integer.parseInt(par3) ;
                        if(parsee.getCurrent().removeRange(fromIndex - 1, toIndex - 1))
                        {
                            if(parsee.getCurrent().list.isEmpty()) parsee.getCurrent().list = null ;
                            parsee.clearInput() ;
                            parsee.setAutoCompleteOptions() ;
                        }
                        else parsee.showErrorToast(INDEX_BOUNDS_ERROR) ;
                    }
                }

            }
            else
            {
                while(st.hasMoreTokens())
                {
                    par2 += " " + st.nextToken() ;
                }
                parsee.getCurrent().removeList(par2) ;
                if(parsee.getCurrent().list.isEmpty()) parsee.getCurrent().list = null ;
                parsee.clearInput() ;
                parsee.setAutoCompleteOptions() ;
            }
        }
        else
        {
            if(parsee.getCurrent().isList())
            {
                parsee.openRemoveAllDialog() ;
            }
        }
    }

    private void move(StringTokenizer st)
    {
        if(st.hasMoreTokens())
        {
            String par2 = st.nextToken();
            if(isInteger(par2) && st.hasMoreTokens())
            {
                int fromIndex = Integer.parseInt(par2) ;
                String par3 = st.nextToken();
                if(isInteger(par2) && !st.hasMoreTokens())
                {
                    int toIndex = Integer.parseInt(par3) ;
                    if(parsee.getCurrent().move(fromIndex - 1, toIndex - 1))
                    {
                        parsee.clearInput() ;
                        parsee.setAutoCompleteOptions() ;
                    }
                    else parsee.showErrorToast(INDEX_BOUNDS_ERROR) ;
                }
                else
                {
                    parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
                }
            }
            else
            {
                parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
            }
        }
        else
        {
            parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
        }
    }

    private void swap(StringTokenizer st)
    {
        if(st.hasMoreTokens())
        {
            String par2 = st.nextToken();
            if(isInteger(par2) && st.hasMoreTokens())
            {
                int fromIndex = Integer.parseInt(par2) ;
                String par3 = st.nextToken();
                if(isInteger(par2) && !st.hasMoreTokens())
                {
                    int toIndex = Integer.parseInt(par3) ;
                    if(parsee.getCurrent().swap(fromIndex - 1, toIndex - 1))
                    {
                        parsee.clearInput() ;
                        parsee.setAutoCompleteOptions() ;
                    }
                    else parsee.showErrorToast(INDEX_BOUNDS_ERROR) ;
                }
                else
                {
                    parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
                }
            }
            else
            {
                parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
            }
        }
        else
        {
            parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
        }
    }

    private void clip(StringTokenizer st)
    {
        if(!st.hasMoreTokens())
        {
            parsee.saveTextToClipboard(parsee.getCurrent() + "\n" + parsee.getListAsString(parsee.getCurrent())) ;
            parsee.clearInput() ;
        }
        else
        {
            String par2 = st.nextToken();
            if(isInteger(par2) && !st.hasMoreTokens()) {
                try {
                    int index = Integer.parseInt(par2) ;
                    parsee.saveTextToClipboard(parsee.getCurrent().list.get(index - 1).name) ;
                    parsee.clearInput() ;
                }
                catch(Exception e) {
                    parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
                }
            }
            else parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
        }
    }

    private void clipAll(StringTokenizer st)
    {
        if(!st.hasMoreTokens())
        {
            parsee.saveTextToClipboard(parsee.getClipboardString()) ;
            parsee.clearInput() ;
        }
        else
        {
            parsee.showErrorToast(INVALID_COMMAND_ERROR) ;
        }

    }

    private void toRoot()
    {
        if(parsee.getRoot() != null)
        {
            parsee.setCurrent(parsee.getRoot()) ;
            parsee.getCurrentPath().clear();
            parsee.clearInput() ;
            parsee.setAutoCompleteOptions() ;
        }
        else
        {
            parsee.showErrorToast(NULL_ROOT_ERROR) ;
        }
    }

    private boolean isInteger(String token) {
        try
        {
            Integer.parseInt(token);
        }
        catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean endsWithChar(String s, char c)
    {
        if(s.charAt(s.length() - 1) == c) return true ;
        return false ;
    }
}
