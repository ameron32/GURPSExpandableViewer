package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

import com.ameron32.libgurps.attackoptions.MeleeAttackOption;
import com.ameron32.libgurps.attackoptions.ThrownAttackOption;
import com.ameron32.libgurps.character.stats.Advantage;
import com.ameron32.libgurps.character.stats.Skill;
import com.ameron32.libgurps.impl.GURPSObject;
import com.ameron32.libgurps.items.library.LibraryAddon;
import com.ameron32.libgurps.items.library.LibraryArmor;
import com.ameron32.libgurps.items.library.LibraryMeleeWeapon;
import com.ameron32.libgurps.items.library.LibraryRangedWeapon;
import com.ameron32.libgurps.items.library.LibraryRangedWeaponAmmunition;
import com.ameron32.libgurps.items.library.LibraryShield;
import com.ameron32.libgurps.items.library.LibraryThrowableProjectile;
import com.ameron32.testing.ImportTesting;

public class MainActivity extends Activity implements OnChildClickListener, OnClickListener {

    ImportTesting it;
    private final String downloadDir = "https://dl.dropboxusercontent.com/u/949753/GURPS/GURPSBuilder/156/";
    private final String sdDir = Environment.getExternalStorageDirectory()
            .getPath() + "/ameron32projects/GURPSBattleFlow/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		start();
	}

	ExpandableListView elv;
	
	private void init() {
		elv = (ExpandableListView) findViewById(R.id.expandableListView1);
		elv.setOnChildClickListener(this);
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
			createELA();
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

    
    
    
    
    
    
    
    
    
    
    
    
    private final ArrayList<HashMap<String, String>> groupList = createGroupList();
    private final ArrayList<ArrayList<HashMap<String, String>>> childList = createChildList();
    private ArrayList<ArrayList<HashMap<String, Long>>> childListLong;
    private SimpleExpandableListAdapter expListAdapter;
    private void createELA() {
//    @SuppressWarnings("unchecked")
//    public void onCreate(Bundle savedInstanceState) {
//        try{
//             super.onCreate(savedInstanceState);
//             setContentView(R.layout.main);
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
 		expListAdapter = new SimpleExpandableListAdapter(this, 
				groupList,
				R.layout.group_row,
				new String[] { "Group Item" },
				new int[] { R.id.row_name },
				childList,
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
    private ArrayList<HashMap<String, String>> createGroupList() {
          ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
//          for( int i = 0 ; i < 15 ; ++i ) { // 15 groups........
//            HashMap m = new HashMap();
//            m.put( "Group Item","Group Item " + i ); // the key and it's value.
//            result.add( m );
//          }
          for (Class<?> c : include) {
        	  HashMap<String, String> m = new HashMap<String, String>();
        	  m.put( "Group Item" , c.getSimpleName());
        	  result.add( m );
          }
          return result;
    }
 
    Class<?>[] include = { 
    		Advantage.class, 
    		LibraryAddon.class, 
    		LibraryArmor.class, MeleeAttackOption.class, LibraryMeleeWeapon.class, 
    		LibraryRangedWeaponAmmunition.class, LibraryRangedWeapon.class, LibraryShield.class,
     		ThrownAttackOption.class, LibraryThrowableProjectile.class,
    		Skill.class 
    		};
    
    /* creatin the HashMap for the children */
//    @SuppressWarnings("unchecked")
    private ArrayList<ArrayList<HashMap<String, String>>> createChildList() {
//      for( int i = 0 ; i < 15 ; ++i ) { // this -15 is the number of groups(Here it's fifteen)
//      /* each group need each HashMap-Here for each group we have 3 subgroups */
//      ArrayList secList = new ArrayList();
//      for( int n = 0 ; n < 3 ; n++ ) {
//        HashMap child = new HashMap();
//        child.put( "Sub Item", "Sub Item " + n );
//        secList.add( child );
//      }
//     result.add( secList );
//    }
        ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<ArrayList<HashMap<String, Long>>> resultLong = new ArrayList<ArrayList<HashMap<String, Long>>>();
		for (Class<?> c : include) {
			ArrayList<HashMap<String, String>> secList = new ArrayList<HashMap<String, String>>();
			ArrayList<HashMap<String, Long>> secListLong = new ArrayList<HashMap<String, Long>>();
			for (GURPSObject go : it.getEverything()) {
				boolean mustExclude = false;
//				boolean hasSearchString = false;
//				if (c.isInstance(go)) {
//					if (go.getName().contains(searchString)) hasSearchString = true;
//				}
				// for (Class<?> c : it.getExcludes()) {
				// if (c.isInstance(go)) {
				// mustExclude = true;
				// }
				// }
				HashMap<String, String> child = new HashMap<String, String>();
				HashMap<String, Long> childLong = new HashMap<String, Long>();
				if (!mustExclude && c.isInstance(go)) {
					child.put("Sub Item", go.getName());
					childLong.put("Sub Item", go.getObjectId());
				}
				secList.add(child);
				secListLong.add(childLong);
			}
			result.add(secList);
			resultLong.add(secListLong);
		}
        
		childListLong = resultLong;
        return result;
    }
    public void  onContentChanged  () {
        System.out.println("onContentChanged");
        super.onContentChanged();
    }
    /* This function is called on each child click */
    public boolean onChildClick( ExpandableListView parent, View v, int groupPosition,int childPosition,long id) {
//        System.out.println("Inside onChildClick at groupPosition = " + groupPosition +" Child clicked at position " + childPosition);
    	GURPSObject go = GURPSObject.findGURPSObjectById(childListLong.get(groupPosition).get(childPosition).get("Sub Item"));
//        Toast.makeText(MainActivity.this, 
//        		go.getName() + ": " + go.getObjectId(), 
//        		Toast.LENGTH_LONG).show();
        
        final AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
        d.setMessage(go.toString());
        d.setOnKeyListener(new Dialog.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) dialog.dismiss();
				return true;
			}
		});
        d.show();
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

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
