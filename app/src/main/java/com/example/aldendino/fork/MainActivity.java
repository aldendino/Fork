package com.example.aldendino.fork;

import java.util.ArrayList;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IOAble {

    private enum AddPos {TOP, BOTTOM}
    private TextView textViewBanner;
    private ListView listView ;
    private ImageButton addButton ;
    private EditText addEditText ;

    public ListTree root ;
    public ListTree current ;
    public ListTree previous ;
    public ListTree copied;

    private String baseTitle ;

    private ArrayList<Integer> currentPath ;

    private SharedPreferences preferences ;
    //private final String COUNT_KEY = "count" ;
    //private final int COUNT_DEFAULT = 0 ;*/

    private ClipboardManager clipboardManager ;
    private String clipboardString = "" ;

    private IOManager io ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Stop the soft keyboard from coming up automatically
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) ;

        io = new IOManager(this) ;
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this) ;
        copied = null;

        textViewBanner = (TextView) findViewById(R.id.textViewBanner);
        listView = (ListView) findViewById(R.id.listView1) ;

        View footerView = getLayoutInflater().inflate(R.layout.footer_button, null);
        listView.addFooterView(footerView);
        addButton = (ImageButton) footerView.findViewById(R.id.button2) ;

        io.importData() ;

        baseTitle = getTitle().toString() ;//getString(R.string.app_name) ;
        populateListView() ;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog(AddPos.BOTTOM) ;
            }
        }) ;

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCurrent(getCurrent().list.get(position)) ;
                getCurrentPath().add(position) ; // May cause exception
                cleanUp() ;
            }
        }) ;

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                createOptions(position);
                return true ;
            }
        }) ;
    }

    @Override
    public void onBackPressed() {
        if(current.parent == null) super.onBackPressed() ;
        else {
            setPrevious(getCurrent()) ;
            setCurrent(getCurrent().parent) ;
            getCurrentPath().remove(getCurrentPath().size() - 1) ; // May cause exception
            cleanUp() ;
            scrollToList(getPrevious());
        }
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
                openAddDialog(AddPos.TOP);
                break;
            }
            case R.id.action_remove: {
                openRemoveAllDialog();
                break;
            }
            case R.id.action_share: {
                startEmail("Fork Backup", getClipboardString());
                break;
            }
            case R.id.action_clip: {
                setClipboard(current.name, getListAsString(current));
                break;
            }
            case R.id.action_copy: {
                copyList(current);
                break;
            }
            case R.id.action_paste: {
                pasteList(current);
                break;
            }
            case R.id.action_move: {
                moveList(current);
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
        textViewBanner.setText(getPathString());
    }

    private void populateListView()
    {
        ListTree[] items = current.getListArray() ;
        ListAdapter listAdapter = new ListAdapter(this, items);
        listView.setAdapter(listAdapter) ;
    }

    public void savePreferences()
    {
        SharedPreferences.Editor editor = preferences.edit();
        //editor.putInt(COUNT_KEY, levelCount) ;
        //editor.commit();
    }

    public void showToast(String message)
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
        }
    }

    //List output

    public String getListAsString(ListTree rootTree)
    {
        if(rootTree != null)
        {
            String output = rootTree.name ;
            if(rootTree.isList())
            {
                String indent = "    ";
                for(ListTree item : rootTree.list)
                {
                    output += "\n" + indent + item.name;
                }
            }
            return output ;
        }
        else
        {
            showToast("List is null") ;
            return "" ;
        }
    }

    public void setClipboard(String label, String text) {
        ClipData clip = ClipData.newPlainText(label, text);
        if(clipboardManager != null) {
            clipboardManager.setPrimaryClip(clip);
            showToast("List saved to clipboard");
        } else {
            showToast("Error saving to clipboard");
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

    public void openRemoveAllDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this) ;
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

    public void openRemoveDialog(int position)
    {
        final int pos = position;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this) ;
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
                    theList.name = input;
                    cleanUp() ;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid list name", Toast.LENGTH_SHORT).show() ;
                }
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
        ad.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void openAddDialog(final AddPos location) {
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
                            if(location == AddPos.TOP) current.addLeafFirst(input);
                            if(location == AddPos.BOTTOM) current.addLeaf(input);
                            cleanUp() ;
                            //if(location == AddPos.BOTTOM) scrollToBottom(); //Not working :/
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Invalid list name", Toast.LENGTH_SHORT).show() ;
                        }

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
    }

    public void createOptions(int position) {
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] options = new String[4];
        options[0] = "Clip";
        options[1] = "Edit";
        options[2] = "Copy";
        options[3] = "Remove";
        builder.setTitle("Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) setClipboard("Fork List", current.list.get(pos).name);
                        else if (which == 1) openEditor(current.list.get(pos));
                        else if (which == 2) copyList(current.list.get(pos));
                        else openRemoveDialog(pos);
                    }
                });
        builder.create().show();
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

    public void copyList(ListTree source) {
        copied = source;
    }

    public void pasteList(ListTree destination) {
        if(copied != null) {
            listCopyRecurse(copied, destination);
            copied = null;
            cleanUp();
        }
    }

    public void moveList(ListTree destination) {
        if(copied != null) {
            if(copied.parent == null) {
                showToast("Cannot move ROOT");
            }
            else {
                if(listMoveCheck(copied, destination)) {
                    listCopyRecurse(copied, destination);
                    copied.parent.removeListAt(copied.parent.list.indexOf(copied) + 1);
                    copied = null;
                    cleanUp();
                }
            }
        }
    }

    public void listCopyRecurse(ListTree source, ListTree destination) {
        ListTree item = new ListTree(source.name, destination);
        destination.addListLast(item);
        if(source.isList()) {
            for(ListTree sourceItem : source.list) {
                listCopyRecurse(sourceItem, item);
            }
        }
    }

    public boolean listMoveCheck(ListTree source, ListTree destination) {
        if(source == destination) return false;
        if(destination.parent == null) return true;
        return listMoveCheck(source, destination.parent);
    }
}