package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ameron32.gurpsviewertest.Downloader;
import com.ameron32.gurpsviewertest.MainActivity;
import com.ameron32.gurpsviewertest.ProgressMonitor;
import com.ameron32.testing.ImportTesting;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class MainActivity extends Activity {

    ImportTesting it;
    private final String downloadDir = "https://dl.dropboxusercontent.com/u/949753/GURPS/GURPSBuilder/156/";
    private final String sdDir = Environment.getExternalStorageDirectory()
            .getPath() + "/ameron32projects/GURPSBattleFlow/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
//		start();
		createELA();
	}

	ExpandableListView elv;
	private void init() {
		elv = (ExpandableListView) findViewById(R.id.expandableListView1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    private void start() {
        it = new ImportTesting(new String[] { sdDir });
        String[] fileNames = ImportTesting.getAllFilenames();
        String[] downloadLocations = fileNames.clone();
        for (int i = 0; i < downloadLocations.length; i++) {
            downloadLocations[i] = downloadDir + downloadLocations[i];
        }
        downloadAssets(null, fileNames, true, downloadLocations);
    }
    
    Runnable updateText = new Runnable() {
		@Override
		public void run() {
//			tvMain.setText(ImportTesting.getSB());
		}
	};
	Runnable importAndLoad = new Runnable() {
		@Override
		public void run() {
        	new ProgressMonitor(MainActivity.this, it, updateText).execute();
		}
	};
	
    private void downloadAssets(String dlDir, String[] fileNames,
            boolean update, String[] sUrl) {
        // execute this when the downloader must be fired
        final Downloader d = new Downloader(MainActivity.this, importAndLoad);
        if (dlDir != null)
            d.setDlDir(dlDir);
        d.setDlFiles(fileNames);
        if (update)
            d.setUpdate(update);
        d.execute(sUrl);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    private void createELA() {
//    @SuppressWarnings("unchecked")
//    public void onCreate(Bundle savedInstanceState) {
//        try{
//             super.onCreate(savedInstanceState);
//             setContentView(R.layout.main);
 
        SimpleExpandableListAdapter expListAdapter =
//            new SimpleExpandableListAdapter(
//                    this,
//                    createGroupList(),              // Creating group List.
//                    R.layout.group_row,             // Group item layout XML.
//                    new String[] { "Group Item" },  // the key of group item.
//                    new int[] { R.id.row_name },    // ID of each group item.-Data under the key goes into this TextView.
//                    createChildList(),              // childData describes second-level entries.
//                    R.layout.child_row,             // Layout for sub-level entries(second level).
//                    new String[] {"Sub Item"},      // Keys in childData maps to display.
//                    new int[] { R.id.grp_child}     // Data under the keys above go into these TextViews.
//                );
//            setListAdapter( expListAdapter );       // setting the adapter in the list.
        		new SimpleExpandableListAdapter(this, 
        				createGroupList(),
        				R.layout.group_row,
        				new String[] { "Group Item" },
        				new int[] { R.id.row_name },
        				createChildList(),
        				R.layout.child_row,
        				new String[] { "Sub Item" },
        				new int[] { R.id.grp_child }
        				);
        elv.setAdapter(expListAdapter);
        
//        } catch(Exception e){
//            System.out.println("Errrr +++ " + e.getMessage());
//        }
    }
 
    /* Creating the Hashmap for the row */
//    @SuppressWarnings("unchecked")
    private List createGroupList() {
          ArrayList result = new ArrayList();
          for( int i = 0 ; i < 15 ; ++i ) { // 15 groups........
            HashMap m = new HashMap();
            m.put( "Group Item","Group Item " + i ); // the key and it's value.
            result.add( m );
          }
          return (List)result;
    }
 
    /* creatin the HashMap for the children */
//    @SuppressWarnings("unchecked")
    private List createChildList() {
 
        ArrayList result = new ArrayList();
        for( int i = 0 ; i < 15 ; ++i ) { // this -15 is the number of groups(Here it's fifteen)
          /* each group need each HashMap-Here for each group we have 3 subgroups */
          ArrayList secList = new ArrayList();
          for( int n = 0 ; n < 3 ; n++ ) {
            HashMap child = new HashMap();
            child.put( "Sub Item", "Sub Item " + n );
            secList.add( child );
          }
         result.add( secList );
        }
        return result;
    }
    public void  onContentChanged  () {
        System.out.println("onContentChanged");
        super.onContentChanged();
    }
    /* This function is called on each child click */
    public boolean onChildClick( ExpandableListView parent, View v, int groupPosition,int childPosition,long id) {
        System.out.println("Inside onChildClick at groupPosition = " + groupPosition +" Child clicked at position " + childPosition);
        return true;
    }
 
    /* This function is called on expansion of the group */
    public void  onGroupExpand  (int groupPosition) {
        try{
             System.out.println("Group exapanding Listener => groupPosition = " + groupPosition);
        }catch(Exception e){
            System.out.println(" groupPosition Errrr +++ " + e.getMessage());
        }
    }
}
