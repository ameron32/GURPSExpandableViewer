package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ameron32.libcharacter.library.PersonalityTrait;
import com.ameron32.libgurps.References;
import com.ameron32.libgurps.attackoptions.MeleeAttackOption;
import com.ameron32.libgurps.attackoptions.ThrownAttackOption;
import com.ameron32.libgurps.character.stats.Advantage;
import com.ameron32.libgurps.character.stats.Skill;
import com.ameron32.libgurps.character.stats.Technique;
import com.ameron32.libgurps.impl.GURPSObject;
import com.ameron32.libgurps.items.library.LibraryAddon;
import com.ameron32.libgurps.items.library.LibraryArmor;
import com.ameron32.libgurps.items.library.LibraryItem;
import com.ameron32.libgurps.items.library.LibraryMeleeWeapon;
import com.ameron32.libgurps.items.library.LibraryRangedWeapon;
import com.ameron32.libgurps.items.library.LibraryRangedWeaponAmmunition;
import com.ameron32.libgurps.items.library.LibraryShield;
import com.ameron32.libgurps.items.library.LibraryThrowableProjectile;
import com.ameron32.libgurps.tools.StringTools;
import com.ameron32.testing.ImportTesting;
import com.ameron32.testing.TestingTools;

public class MainActivity extends Activity implements OnChildClickListener, OnClickListener, OnKeyListener {

    ImportTesting it;
    private static final String downloadDir = 
    		"https://dl.dropboxusercontent.com/u/949753/GURPS/GURPSBuilder/" 
    		+ ImportTesting.getVERSION() + "/";
    private static final String sdDir = Environment.getExternalStorageDirectory()
            .getPath() + "/ameron32projects/GURPSBattleFlow/";
    public static String getSDDir() {
    	return sdDir;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		start();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	ExpandableListView elv;
	Button bDownload, bUpdate, bLoad, bRefine;
	EditText etQuery;
	TextView tvInstructions;
	private void init() {
		elv = (ExpandableListView) findViewById(R.id.expandableListView1);
		elv.setOnChildClickListener(this);
		bDownload = (Button) findViewById(R.id.bDownload);
		bDownload.setOnClickListener(this);
		bUpdate = (Button) findViewById(R.id.bUpdate);
		bUpdate.setOnClickListener(this);
		bLoad = (Button) findViewById(R.id.bLoad);
		bLoad.setOnClickListener(this);
		bRefine = (Button) findViewById(R.id.bRefine);
		bRefine.setOnClickListener(this);
		bRefine.setVisibility(View.INVISIBLE);
		etQuery = (EditText) findViewById(R.id.etQuery);
		etQuery.setOnClickListener(this);
		etQuery.addTextChangedListener(tcl);
		tvInstructions = (TextView) findViewById(R.id.tvInstructions);
		etQuery.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    private void start() {
        it = new ImportTesting(new String[] { sdDir });
    }
    
    private void download() {
        String[][] fileNames = ImportTesting.getAllFilenames();
        
		// cheat to convert the fileNames for references on the file to the
		// right fileName
//        for (int n = 0; n < fileNames.length; n++) {
//        	for (String[] file : References.getReferences())
//        		if (fileNames[n].equalsIgnoreCase(file[2])) {
//        			fileNames[n] = file[3];
//        		}
//        }

        ArrayList<String> updateFileNames = new ArrayList<String>();
        ArrayList<String> noUpdateFileNames = new ArrayList<String>();
        ArrayList<String> updateDownloadLocations = new ArrayList<String>();
        ArrayList<String> noUpdateDownloadLocations = new ArrayList<String>();
        
        for (String[] fileInfo : fileNames) {
    		String fileName = fileInfo[0];
    		String update = fileInfo[1];
        	String downloadLocation = fileInfo[0];
        	// add "http" etc, if needed
        	if (!fileInfo[0].substring(0,3).equalsIgnoreCase("http")) {
        		downloadLocation = downloadDir + fileName;
        	} else {
        		downloadLocation = fileName;
        	}
        	
        	// determine update
        	if (fileInfo[1].equalsIgnoreCase("false")) {
        		noUpdateFileNames.add(fileName);
        		noUpdateDownloadLocations.add(downloadLocation);
        	} else if (fileInfo[1].equalsIgnoreCase("true")){
        		updateFileNames.add(fileName);
        		updateDownloadLocations.add(downloadLocation);
        	} else {
        		Log.e("UpdateStatusUnknown","Could not determine update status. Defaulting to YES.");
        		updateFileNames.add(fileName);
        		updateDownloadLocations.add(downloadLocation);
        	}
        }

//    	// add download directory to standalone filenames, if needed
//      for (int i = 0; i < downloadLocations.length; i++) {
//      	if (!downloadLocations[i][0].substring(0,3).equalsIgnoreCase("http"))
//      		downloadLocations[i][0] = downloadDir + downloadLocations[i][0];
//      }

        Runnable b = packDownloadBatch(null, noUpdateFileNames, false, noUpdateDownloadLocations, null);
        downloadBatch(null, updateFileNames, true, updateDownloadLocations, b);
    }
    
    private Runnable packDownloadBatch(
    		final String dlDir, 
    		final ArrayList<String> fileNames,
            final boolean update, 
            final ArrayList<String> sUrl,
            final Runnable doNext) {
    	return new Runnable() {
			@Override
			public void run() {
		        downloadBatch(dlDir, fileNames, update, sUrl, doNext);
			}
		};
    }
    
    private void downloadBatch(
    		final String dlDir, 
    		final ArrayList<String> fileNames,
            final boolean update, 
            final ArrayList<String> sUrl,
            final Runnable doNext) {
        // execute this when the downloader must be fired
        final Downloader d = new Downloader(MainActivity.this, doNext);
        if (dlDir != null)
            d.setDlDir(dlDir);
        d.setDlFiles(fileNames.toArray(new String[0]));
        if (update)
            d.setUpdate(update);
        d.execute(sUrl.toArray(new String[0]));
    }

    
    
    
//    private ArrayList<HashMap<String, String>> groupList;
//    private ArrayList<ArrayList<HashMap<String, GURPSObject>>> childList;
//    private ArrayList<ArrayList<HashMap<String, Long>>> childListLong;
    private GURPSLibraryAdapter expListAdapter;
    private ArrayList<HashMap<String, String>> createGroupList() {
          ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
          for (Class<?> c : include) {
        	  HashMap<String, String> m = new HashMap<String, String>();
        	  m.put( "Group Item" , c.getSimpleName() + " [" + TestingTools.numOf(c) + "]");
        	  result.add( m );
          }
          return result;
    }
 
    Class<?>[] include = { 
    		Advantage.class, Skill.class, Technique.class, 
    		PersonalityTrait.class,
    		LibraryItem.class, LibraryAddon.class, 
    		LibraryArmor.class, LibraryShield.class, LibraryMeleeWeapon.class, 
    		LibraryRangedWeapon.class, 
    		LibraryRangedWeaponAmmunition.class, LibraryThrowableProjectile.class, 
    		MeleeAttackOption.class,ThrownAttackOption.class, 
    		};
    
    private ArrayList<ArrayList<HashMap<String, GURPSObject>>> createChildList() {
        ArrayList<ArrayList<HashMap<String, GURPSObject>>> result = new ArrayList<ArrayList<HashMap<String, GURPSObject>>>();
//        ArrayList<ArrayList<HashMap<String, Long>>> resultLong = new ArrayList<ArrayList<HashMap<String, Long>>>();
        
        // prepare a placeholder for each group
		for (int i = 0; i < include.length; i++) {
			result.add(new ArrayList<HashMap<String, GURPSObject>>());
//			resultLong.add(new ArrayList<HashMap<String, Long>>());
		}
        
		Class<?>[] excludes = ImportTesting.getExcludes();
		int excludesLength = excludes.length;
        for (final GURPSObject go : ImportTesting.getEverything()) {
			// determine whether to exclude this object
        	boolean exclude = false;
			if (excludesLength != 0) {
				for (Class<?> c2 : excludes) {
					if (c2.isInstance(go))
						exclude = true;
				}
			}
			
			if (!exclude) {
				// put the object into the proper group(s)
				for (int x = 0; x < include.length; x++) {
					// verify that the object is of a special class, and not inheriting that class
					if (include[x].isInstance(go) 
							&& include[x].getSimpleName().equals(go.getClass().getSimpleName())) {
						// add the item to the list
						HashMap<String, GURPSObject> entry = new HashMap<String, GURPSObject>();
//						HashMap<String, Long> entryLong = new HashMap<String, Long>();
						entry.put("Sub Item", go);
//						entryLong.put("Sub Item", go.getObjectId());

						result.get(x).add(entry);
//						resultLong.get(x).add(entryLong);
					}
				}
			}

		}
        
//        childListLong = resultLong;
        return result;
    }
    
    private void clearGroupList() {
//    	groupList.clear();
    	
/**/	expListAdapter.clear();
    }
    
    private void addToGroupList(ArrayList<HashMap<String, String>> group) {
		for (int i = 0; i < include.length; i++) {
			boolean exclude = false;
//			for (Integer x : emptys) {
//				if (x.intValue() == i) {
//					exclude = true;
//				}
//			}
			if (!exclude) {
				HashMap<String, String> m = new HashMap<String, String>();
				m.put("Group Item", include[i].getSimpleName());
				group.add(m);
				
				expListAdapter.addGroup(include[i]);
			}
		}
    }
    
    private void removeGroup(int groupNumber) {
//		emptys.add(groupNumber);
		
/**/	expListAdapter.removeGroup(groupNumber);
    }
    
    private void clearChildList() {
//    	childList.clear();
//    	childListLong.clear();
    	
/**/    expListAdapter.clear();	
    }
    
	private final ArrayList<ArrayList<HashMap<String, GURPSObject>>> removeThese = new ArrayList<ArrayList<HashMap<String, GURPSObject>>>();
    private void addToChildList(String query, ArrayList<ArrayList<HashMap<String, GURPSObject>>> child) {
    	// prepare a placeholder for each group
		for (int i = 0; i < include.length; i++) {
/**/		expListAdapter.addGroup(include[i]);
			child.add(new ArrayList<HashMap<String, GURPSObject>>());
//			childLong.add(new ArrayList<HashMap<String, Long>>());
		}

		Class<?>[] excludes = ImportTesting.getExcludes();
		int excludesLength = excludes.length;
		for (final GURPSObject go : ImportTesting.getEverything()) {
			// determine whether to exclude this object
			boolean exclude = false;
			if (excludesLength != 0) {
				for (Class<?> c2 : excludes) {
					if (c2.isInstance(go))
						exclude = true;
				}
			}
			
			/* TEST searchQuery */
			if (!query.equals("")) {
				String[] queryA = new String[] { query };
				if (query.contains(" ")){
					queryA = query.split(" ",10);
				}
				for (String q : queryA) {
					if (!go.getName().toLowerCase(Locale.ENGLISH)
							.contains(q.toLowerCase(Locale.ENGLISH))) {
						exclude = true;
					}
				}
			}

			if (!exclude) {
				// put the object into the proper group(s)
				for (int x = 0; x < include.length; x++) {
					// verify that the object is of a special class, and not
					// inheriting that class
					if (include[x].isInstance(go)
							&& include[x].getSimpleName().equals(
									go.getClass().getSimpleName())) {
						// add the item to the list
						HashMap<String, GURPSObject> entry = new HashMap<String, GURPSObject>();
//						HashMap<String, Long> entryLong = new HashMap<String, Long>();
						entry.put("Sub Item", go);
//						entryLong.put("Sub Item", go.getObjectId());

						child.get(x).add(entry);
//						childLong.get(x).add(entryLong);
						
/**/					expListAdapter.addChild(x, go);
					}
				}
			}
		}
		
		removeThese.clear();
		for (int j = 0; j < include.length; j++) {
			if (child.get(j).isEmpty()) {
				removeThese.add(child.get(j));
				removeGroup(j);
				

			}
		}
		for (ArrayList<HashMap<String, GURPSObject>> r : removeThese) {
			child.remove(r);
		}
		
/**/	expListAdapter.removeEmptyGroups();
    }

    /* This function is called on each child click */
    public boolean onChildClick( ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    	
    	final GURPSObject go = GURPSObject.findGURPSObjectById(expListAdapter.getChildData(groupPosition, childPosition).getObjectId());
//    	final GURPSObject go = GURPSObject.findGURPSObjectById(childList.get(groupPosition).get(childPosition).get("Sub Item").getObjectId());

    	// generate and display the dialog box for THAT child/GURPSObject
    	final InformationDialog inf = new InformationDialog(R.layout.information_dialog, MainActivity.this, this);
    	inf.set(go);
    	inf.show();
    	
        return true;
    }
 
    /* This function is called on expansion of the group */
    public void  onGroupExpand  (int groupPosition) {
        try {
            System.out.println("Group exapanding Listener => groupPosition = " + groupPosition);
        } catch (Exception e) {
            System.out.println(" groupPosition Errrr +++ " + e.getMessage());
        }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bDownload:
			download();
			break;
		case R.id.bUpdate:
			importAndLoad.run();
			break;
		case R.id.bLoad:
			load();
			break;
		case R.id.bRefine:
			refineData(etQuery.getText());
			break;
		}
	}
	
	private void load() {
		updateText.run();
		
		etQuery.setVisibility(View.VISIBLE);
		bRefine.setVisibility(View.VISIBLE);
		bLoad.setVisibility(View.GONE);
		bDownload.setVisibility(View.GONE);
		bUpdate.setVisibility(View.GONE);
		tvInstructions.setVisibility(View.GONE);
		
		MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		Toast.makeText(MainActivity.this, "rotation unlocked", Toast.LENGTH_LONG).show();
		
		if (expListAdapter != null) expListAdapter.clear();
		
		refineData(etQuery.getText());
	}

	Runnable importAndLoad = new Runnable() {
		@Override
		public void run() {
	    	new ProgressMonitor(MainActivity.this, it, null).execute();
		}
	};
	Runnable updateText = new Runnable() {
		@Override
		public void run() {
			createELA();
		}
	};
	
	private void createELA() { // create 
		ArrayList<HashMap<String, String>> 
			groupList = createGroupList();
		ArrayList<ArrayList<HashMap<String, GURPSObject>>> 
			childList = createChildList();
		
		expListAdapter = new GURPSLibraryAdapter(
				this, 
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
	    //addToGroupList//addToChildList
	}
	
//	private final ArrayList<Integer> emptys = new ArrayList<Integer>();
	private void refineData(Editable e) {
		// reset info
		clearGroupList();
		clearChildList();
//		emptys.clear();
		expListAdapter.clear();
		
		// get query
		String query = e.toString().trim();
		
		// process query
		addToChildList(query, createChildList()); //addToChildList(query, childList);
		addToGroupList(createGroupList()); //addToGroupList(groupList);
		
/**/	expListAdapter.removeEmptyGroups();
		
		// update view
		expListAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			confirmExit();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onBackPressed() {
		confirmExit();
	}
	
	private void confirmExit() {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setMessage("Exit Now?");
		dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.setNegativeButton("Do Not Exit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog a = dialog.create();
		a.show();
	}
	
	TextWatcher tcl = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {
			refineData(s);
		}
	};
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.i_menu_download:
			download();
			break;
		case R.id.i_menu_update:
			importAndLoad.run();
			break;
		case R.id.i_menu_load:
			load();
			break;
		case R.id.i_menu_exit:
			confirmExit();
			break;
		}
		
		return false;
	}

}
