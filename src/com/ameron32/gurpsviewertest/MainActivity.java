package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

import com.ameron32.libcharacter.library.PersonalityTrait;
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
    private final String downloadDir = 
    		"https://dl.dropboxusercontent.com/u/949753/GURPS/GURPSBuilder/" 
    		+ ImportTesting.getVERSION() + "/";
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
	Button bDownload, bUpdate, bLoad;
	private void init() {
		elv = (ExpandableListView) findViewById(R.id.expandableListView1);
		elv.setOnChildClickListener(this);
		bDownload = (Button) findViewById(R.id.bDownload);
		bDownload.setOnClickListener(this);
		bUpdate = (Button) findViewById(R.id.bUpdate);
		bUpdate.setOnClickListener(this);
		bLoad = (Button) findViewById(R.id.bLoad);
		bLoad.setOnClickListener(this);
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
        	new ProgressMonitor(MainActivity.this, it, null).execute();
		}
	};
	
    private void downloadAssets(String dlDir, String[] fileNames,
            boolean update, String[] sUrl) {
        // execute this when the downloader must be fired
        final Downloader d = new Downloader(MainActivity.this, null);
        if (dlDir != null)
            d.setDlDir(dlDir);
        d.setDlFiles(fileNames);
        if (update)
            d.setUpdate(update);
        d.execute(sUrl);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    private ArrayList<HashMap<String, String>> groupList;
    private ArrayList<ArrayList<HashMap<String, String>>> childList;
    private ArrayList<ArrayList<HashMap<String, Long>>> childListLong;
    private SimpleExpandableListAdapter expListAdapter;
    private void createELA() {
    	groupList = createGroupList();
    	childList = createChildList();
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
    }
 
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
    
    private ArrayList<ArrayList<HashMap<String, String>>> createChildList() {
        ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<ArrayList<HashMap<String, Long>>> resultLong = new ArrayList<ArrayList<HashMap<String, Long>>>();
        
        // prepare a placeholder for each group
		for (int i = 0; i < include.length; i++) {
			result.add(new ArrayList<HashMap<String, String>>());
			resultLong.add(new ArrayList<HashMap<String, Long>>());
		}
        
		Class<?>[] excludes = ImportTesting.getExcludes();
		int excludesLength = excludes.length;
        for (GURPSObject go : ImportTesting.getEverything()) {
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
						HashMap<String, String> entry = new HashMap<String, String>();
						HashMap<String, Long> entryLong = new HashMap<String, Long>();
						entry.put("Sub Item", go.getName());
						entryLong.put("Sub Item", go.getObjectId());

						result.get(x).add(entry);
						resultLong.get(x).add(entryLong);
					}
				}
			}

		}
        
        childListLong = resultLong;
        return result;
    }
    public void onContentChanged  () {
        System.out.println("onContentChanged");
        super.onContentChanged();
    }

    /* This function is called on each child click */
    public boolean onChildClick( ExpandableListView parent, View v, int groupPosition,int childPosition,long id) {
    	final GURPSObject go = GURPSObject.findGURPSObjectById(childListLong.get(groupPosition).get(childPosition).get("Sub Item"));

    	// generate and display the dialog box for THAT child/GURPSObject
    	final InformationDialog inf = new InformationDialog(R.layout.information_dialog, MainActivity.this);
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
	protected void onPause() {
		super.onPause();
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
			updateText.run();
			break;
		}
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
	
}
