package com.example.aldendino.fork;

import java.io.Serializable;
import java.util.ArrayList ;

/**
 * Created by aldendino on 2014-09-07.
 */
public class ListTree implements Serializable {
    private static final long serialVersionUID = -6110776202976443110L;
    public String name ;
    public ArrayList<ListTree> list ;
    public ListTree parent ;

    public ListTree(String name, ListTree parent)
    {
        this.name = name ;
        this.parent = parent ;
    }

    public ListTree(String name, ListTree parent, boolean isList)
    {
        this.name = name ;
        this.parent = parent ;
        if (isList)
        {
            this.list = new ArrayList<ListTree>() ;
        }
    }

    public boolean isList()
    {
        return list != null ;
    }

    public void addLeaf(String name)
    {
        convertToList() ;
        list.add(new ListTree(name, this, false)) ;
    }

    public void addLeafFirst(String name) {
        convertToList() ;
        list.add(0, new ListTree(name, this, false)) ;
    }

    public void addList(String name)
    {
        convertToList() ;
        list.add(new ListTree(name, this, true)) ;
    }

    public void addListAt(String name, int index)
    {
        convertToList() ;
        try
        {
            list.add(index, new ListTree(name, this, false)) ;
        }
        catch(Exception e)
        {

        }
    }

    public ListTree getList(String listName)
    {
        if(isList())
        {
            for(ListTree item : list)
            {
                if(item.name.equals(listName))
                {
                    return item ;
                }
            }
        }
        return null ;
    }

    public void removeList(String listName)
    {
        if(isList())
        {
            ArrayList<ListTree> toRemove = new ArrayList<ListTree>() ;
            for(ListTree item : list)
            {
                if(item.name.equals(listName))
                {
                    toRemove.add(item) ;
                    break; //Only removes the first
                }
            }
            for(ListTree item : toRemove)
            {
                list.remove(item) ;
            }
            if(list.isEmpty()) list = null;
        }
    }

    public boolean removeListAt(int index)
    {
        if(isList())
        {
            try
            {
                list.remove(index - 1) ;
                if(list.isEmpty()) list = null;
                return true ;
            }
            catch(Exception e)
            {
                return false ;
            }
        }
        return false ;
    }

    public void removeAll()
    {
        list.clear();
    }

    public boolean move(int fromIndex, int toIndex)
    {
        if(isInRange(fromIndex) && isInRange(toIndex))
        {
            if(fromIndex == toIndex) return true ;
            ListTree temp = list.get(fromIndex) ;
            list.remove(temp) ;
            list.add(toIndex, temp) ;
            return true ;
        }
        return false ;
    }

    public boolean swap(int fromIndex, int toIndex)
    {
        if(isInRange(fromIndex) && isInRange(toIndex))
        {
            if(fromIndex == toIndex) return true ;
            ListTree temp = list.get(toIndex) ;
            list.set(toIndex, list.get(fromIndex)) ;
            list.set(fromIndex, temp) ;
            return true ;
        }
        return false ;
    }

    public boolean removeRange(int fromIndex, int toIndex)
    {
        if(isInRange(fromIndex) && isInRange(toIndex))
        {
            for(int i = toIndex ; i >= fromIndex ; i--)
            {
                list.remove(i) ;
            }
            return true ;
        }
        return false ;
    }

    public boolean isInRange(int index)
    {
        if(isList()) return index >= 0 && index < list.size();
        return false ;
    }

    public boolean isInRangeTo(int index, int limit)
    {
        if(isList()) return index > 0 && index < limit ;
        return false ;
    }

    public void convertToList()
    {
        if (!isList())
        {
            this.list = new ArrayList<ListTree>() ;
        }
    }

    public ListTree[] getListArray()
    {
        if(isList())
        {
            ListTree[] array = list.toArray(new ListTree[list.size()]) ;
            return array ;
        }
        return new ListTree[0] ;
    }

    public int getIndex() {
        if(parent == null) return -1;
        return parent.list.indexOf(this);
    }

    @Override
    public String toString()
    {
        return name ;
    }
}