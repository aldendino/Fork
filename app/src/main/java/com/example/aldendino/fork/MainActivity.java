package com.example.aldendino.fork;

import java.util.ArrayList;
import java.util.Scanner;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Parsable, IOable {

    //private AutoCompleteTextView commandInput ;
    private TextView textViewBanner;
    private ListView listView ;
    //private Button enterButton ;
    //private Button addButton ;
    private EditText addEditText ;

    public ListTree root ;
    public ListTree current ;
    public ListTree previous ;

    private Scanner scanner ;
    private Parser parser ;
    private String baseTitle ;
    private final int titleLimit = 15 ;

    private ArrayList<Integer> currentPath ;

    private SharedPreferences preferences ;
    //private final String COUNT_KEY = "count" ;
    //private final int COUNT_DEFAULT = 0 ;*/

    private ClipboardManager clipboard ;
    private String clipboardString = "" ;

    private IOManager io ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Stop the soft keyboard from coming up automatically
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) ;

        io = new IOManager(this) ;
        parser = new Parser(this) ;
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this) ;

        textViewBanner = (TextView) findViewById(R.id.textViewBanner);
        //commandInput = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1) ;
        listView = (ListView) findViewById(R.id.listView1) ;
        //enterButton = (Button) findViewById(R.id.button1) ;
        //addButton = (Button) findViewById(R.id.button2) ;

        io.importData() ;
        //setAutoCompleteOptions() ;

        baseTitle = getTitle().toString() ;//getString(R.string.app_name) ;
        populateListView() ;

        /*enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner = new Scanner(commandInput.getText().toString()) ;
                while(scanner.hasNextLine())
                {
                    String line = scanner.nextLine() ;
                    parser.parseString(line) ;
                }
                cleanUp() ;
            }
        }) ;*/

        /*addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog() ;
            }
        }) ;*/

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrent(getCurrent().list.get(position)) ;
                getCurrentPath().add(position) ;
                //clearInput() ;
                //setAutoCompleteOptions() ;
                cleanUp() ;
                //Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
            }
        }) ;

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                createOptions(position);
                //openEditor(current.list.get(position)) ;
                //Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
                return true ;
            }
        }) ;

        //Experimental way to enter to perform command on AutoComplete
		/*commandInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				enterButton.performClick() ;
			}
		}) ;*/
    }

    @Override
    public void onBackPressed() {
        if(current.parent == null) super.onBackPressed() ;
        else {
            setPrevious(getCurrent()) ;
            setCurrent(getCurrent().parent) ;
            getCurrentPath().remove(getCurrentPath().size() - 1) ;
            //clearInput() ;
            //setAutoCompleteOptions() ;
            cleanUp() ;
            scrollToList(previous);
        }
        //return;
    }

    @Override
    public void onResume()
    {
        super.onResume() ;
        updateTitle() ;
    }

    @Override
    public void onPause()
    {
        super.onPause() ;
        io.saveFiles() ;
        //savePreferences() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add: {
                openAddDialog();
                break;
            }
            case R.id.action_remove: {
                openRemoveDialog();
                break;
            }
            case R.id.action_share: {
                startEmail("Fork Backup", getClipboardString());
                break;
            }
        	/*case R.id.action_settings: {
        		Intent i = new Intent(this, SettingsActivity.class);
            	startActivity(i);
            	break ;
        	}*/
            /*case R.id.action_about: {
                Intent i = new Intent(this, AboutActivity.class) ;
                startActivity(i) ;
                break ;
            }*/
        }
        return true ;
    }

    //Setters and Getters

    public void setCurrent(ListTree current)
    {
        this.current = current ;
    }

    public ListTree getCurrent()
    {
        return current ;
    }

    public void setRoot(ListTree root)
    {
        this.root = root ;
    }

    public ListTree getRoot()
    {
        return root ;
    }

    public void setPrevious(ListTree previous) {
        this.previous = previous ;
    }

    public ListTree getPrevious() {
        return previous ;
    }

    public void setCurrentPath(ArrayList<Integer> currentPath)
    {
        this.currentPath = currentPath ;
    }

    public ArrayList<Integer> getCurrentPath()
    {
        return currentPath ;
    }

    //Update GUI

    private void cleanUp()
    {
        populateListView() ;
        updateTitle() ;
        io.saveFiles() ;
        //savePreferences() ;
    }

    private void updateTitle()
    {
        if(current != root && getLevel(current) != 0)
        {
            String currentItemString = "" ;
            if(current.toString().length() > titleLimit) {
                currentItemString = current.toString().substring(0, titleLimit) + " ..." ;
            }
            else {
                currentItemString = current.toString();
            }
            String finalTitle = baseTitle + " - " + currentItemString + " (" + getLevel(current) + ")";
            //getActionBar().setTitle(finalTitle);
            textViewBanner.setText(getPathString());

        }
        else
        {
            //getActionBar().setTitle(baseTitle) ;
            textViewBanner.setText(getPathString());
        }
    }

    public void clearInput()
    {
        //commandInput.setText("") ;
    }

    private void populateListView()
    {
        ListTree[] items = current.getListArray() ;
        /*String[] strings  = null ;
        if(items == null)
        {
            //items = new ListTree[0] ;
            strings = new String[0] ;
        }
        else
        {
            strings = new String[items.length] ;
            for(int i = 0 ; i < items.length ; i++)
            {
                String itemString = "" + (i + 1) + ".  " + items[i].toString() ;
                if(items[i].isList() && !items[i].list.isEmpty()) itemString += "  (" + items[i].list.size() + ")" ;
                strings[i] = itemString ;
            }
        }*/

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
        //listView.setAdapter(adapter);
        ListAdapter listAdapter = new ListAdapter(this, items);
        listView.setAdapter(listAdapter) ;

    }


    public void savePreferences()
    {
        SharedPreferences.Editor editor = preferences.edit();
        //editor.putInt(COUNT_KEY, levelCount) ;
        //editor.commit();
    }


    public void showErrorToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show() ;
    }

    //List Operations

    private int getLevel(ListTree currentItem)
    {
        int levelCount = 0 ;
        while(currentItem.parent != null)
        {
            currentItem = currentItem.parent ;
            levelCount++ ;
        }
        return levelCount ;
    }

    public void emptyList()
    {
        if(current.isList())
        {
            current.removeAll();
            if(current.list.isEmpty()) current.list = null ;
            //clearInput() ;
            //setAutoCompleteOptions() ;
        }
    }

    //List output

    public String getListAsString(ListTree rootTree)
    {
        if(rootTree != null)
        {
            String output = "" ;
            if(rootTree.isList())
            {
                int count = 0 ;
                for(ListTree list : rootTree.list)
                {
                    String listSize = "" ;
                    if(list.isList())
                    {
                        if(!list.list.isEmpty())
                        {
                            listSize = "  (" + list.list.size() + ")" ;
                        }
                    }
                    String offset = "" ;
                    count++ ;
                    if(count < 10) offset = "      " ;
                    else if(count < 100) offset = "    " ;
                    else if(count < 1000) offset = "  " ;
                    output += count + ". " + offset + list + listSize + "\n" ;
                }
            }
            return output ;

        }
        else
        {
            showErrorToast("List is null") ;
            return "" ;
        }
    }

    public void saveTextToClipboard(String text)
    {
        //setClip() ;
        ClipData clip = ClipData.newPlainText(baseTitle + " data", text) ;
        if(clipboard != null)
        {
            clipboard.setPrimaryClip(clip) ;
            showErrorToast("Saved to clipboard") ;
        }
        else
        {
            showErrorToast("Error saving to clipboard") ;
        }
    }

    public String getClipboardString()
    {
        setClip() ;
        return clipboardString ;
    }

    private void setClip()
    {
        clipboardString = "" ;
        setClipRecurse(root, "");
        //System.out.println(clipboardString) ;
    }

    private void setClipRecurse(ListTree temp, String white_space)
    {
        clipboardString += white_space + temp + "\n" ;
        white_space += "    ";

        if(temp.list != null)
        {
            for(ListTree list : temp.list)
            {
                setClipRecurse(list, white_space);
            }
        }
    }



    /*  
     * Note the potential problem that multiple occurrences show up in order, 
     * but all will be removed on selecting either of them... awkward.
     */
    public void setAutoCompleteOptions()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getAutoCompleteOptions()) ;
        //commandInput.setAdapter(adapter) ;
    }

    private String[] getAutoCompleteOptions()
    {
        String[] options ;
        ArrayList<String> optionList = new ArrayList<String>() ;
        //
        //optionList.add(Parser.ADD + " ") ; //not complete
        if(current.parent != null)
        {
            optionList.add(Parser.CHANGE_DIR + " " + Parser.ROOT) ;
            optionList.add(Parser.CHANGE_DIR + " " + Parser.PARENT) ;
        }
        optionList.add(Parser.CLIP) ;
        optionList.add(Parser.CLIP_ALL) ;
        optionList.add(Parser.EMAIL);
        //optionList.add(Parser.MOVE + " ") ; //not complete
        if(current.list != null) optionList.add("rm") ;
        //optionList.add(Parser.SWAP + " ") ; //not complete
        if(current.isList())
        {
            for(ListTree item : current.list)
            {
                optionList.add(Parser.CHANGE_DIR + " " + item) ;
                optionList.add(Parser.REMOVE + " " + item) ;
            }
            //Option to add index removal to AutoComplete list
    		/*for(int i = 0 ; i < current.list.size() ; i++)
    		{
    			optionList.add(Parser.REMOVE + " " + (i+1)) ;
    			optionList.add(Parser.CHANGE_DIR + " " + (i+1)) ;
    			//add swp option ?
    			//add mv option ?
    		}*/
        }
        //
        options = optionList.toArray(new String[optionList.size()]) ;
        return options ;
    }

    public void openRemoveDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this) ;
        //alertDialogBuilder.setTitle(this.getTitle()) ;
        alertDialogBuilder.setTitle("Remove All?") ;
        alertDialogBuilder.setMessage("Would you like to\nremove all items?") ;
        //
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                emptyList() ;
                cleanUp() ;
                Toast.makeText(getApplicationContext(), "List emptied", Toast.LENGTH_SHORT).show() ;
            }
        }) ;
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel() ;
            }
        }) ;
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    public void openRemoveAlert(int position)
    {
        final int pos = position;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this) ;
        //alertDialogBuilder.setTitle(this.getTitle()) ;
        alertDialogBuilder.setTitle("Remove") ;
        alertDialogBuilder.setMessage("Remove this item?\n\"" + current.list.get(pos).name + "\"") ;
        //
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                current.removeListAt(pos + 1);
                cleanUp();
                //scrollToIndex(pos);
            }
        }) ;
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel() ;
            }
        }) ;
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    public void openEditor(ListTree list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View editView = inflater.inflate(R.layout.dialog_add, null) ;
        final EditText editText = (EditText) editView.findViewById(R.id.addEditText) ;
        editText.setText(list.name) ;
        editText.setSelection(list.name.length()) ;
        builder.setTitle("Edit") ;
        builder.setView(editView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel() ;
            }
        });
        final ListTree theList = list;
        builder.setView(editView).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String input = editText.getText().toString();
                if(!input.equals("")) {
                    changeListText(theList, input) ;
                    cleanUp() ;
                    //setAutoCompleteOptions() ;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid list name", Toast.LENGTH_SHORT).show() ;
                }
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
        ad.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void changeListText(ListTree list, String text) {
        list.name = text ;
    }

    public void openAddDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle("Add") ;
        final View editView = inflater.inflate(R.layout.dialog_add, null) ;

        builder.setView(editView)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addEditText = (EditText) editView.findViewById(R.id.addEditText) ;

                        String input = addEditText.getText().toString();
                        if(!input.equals("")) {
                            current.addLeafFirst(input) ;
                            cleanUp() ;
                            //setAutoCompleteOptions() ;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Invalid list name", Toast.LENGTH_SHORT).show() ;
                        }
                        //Toast.makeText(getApplicationContext(), addEditText.getText(), Toast.LENGTH_SHORT).show() ;
                        //if(addEditText == null) Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show() ;


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog ad = builder.create();
        ad.show();
        ad.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        	
        	/*EditText addEditText2 = (EditText) findViewById(R.id.addEditText) ;
        	String damn = "zack : The idea that it is null is " + (addEditText2 == null);
        	Log.d("Zack", damn);*/
        //Toast.makeText(getApplicationContext(), damn, Toast.LENGTH_SHORT).show() ;
    }

    public void scrollToBottom()
    {
        if(listView.getChildCount() == 0) return;
        //int count = listView.getChildCount();
        listView.setSelection(current.list.size());
    }

    public void scrollToList(ListTree child) {
        if(child == null) return;
        listView.setSelection(child.getIndex());
    }

    public void scrollToIndex(int index) {
        if(current.isInRange(index)) listView.setSelection(index);
    }

    public void startEmail(String subject, String body) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);

        String uriText = "mailto:" + Uri.encode("") +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body);
        Uri uri = Uri.parse(uriText);
        emailIntent.setData(uri);
        startActivity(Intent.createChooser(emailIntent, "Email Backup"));

        /*emailIntent.setType("text/plain");
    	emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);*/
        //clearInput();
    }

    public void createOptions(int position) {
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] options = new String[2];
        options[0] = "Edit";
        options[1] = "Remove";
        builder.setTitle("Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        //final int pos = position;
                        if(which == 0) openEditor(current.list.get(pos));
                        else if(which == 1) {
                            openRemoveAlert(pos);
                        }
                    }
                });
        builder.create().show();
    }

    public Activity getActivity() {
        return this;
    }

    public String getPathString() {
        String content = getCurrent().name;
        if(getCurrent().parent == null) return content;
        return getPathStringRecurse(content, " > ", getCurrent());
    }

    public String getPathStringRecurse(String content, String divider, ListTree location) {
        if(location.parent == null) return content;
        content = location.parent.name + divider + content;
        return getPathStringRecurse(content, divider, location.parent);
    }
}

