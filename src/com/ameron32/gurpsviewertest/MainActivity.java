package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
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
import com.ameron32.testing.ImportTesting;
import com.ameron32.testing.TestingTools;

public class MainActivity extends SherlockActivity implements OnChildClickListener, OnClickListener, OnKeyListener {

    private ImportTesting it;
    private static final String downloadDir = 
    		"https://dl.dropboxusercontent.com/u/949753/GURPS/GURPSBuilder/" 
    		+ ImportTesting.getVERSION() + "/";
    private static final String sdDir = Environment.getExternalStorageDirectory()
            .getPath() + "/ameron32projects/GURPSBattleFlow/";
    public static String getSDDir() {
    	return sdDir;
    }

	private ExpandableListView elv;
	private Button bDownload, bUpdate, bLoad; 
	private SearchView svQuery;
	private TextView tvInstructions, tvSearchOf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main);

		elv = (ExpandableListView) findViewById(R.id.expandableListView1);
		bDownload = (Button) findViewById(R.id.bDownload);
		bUpdate = (Button) findViewById(R.id.bUpdate);
		bLoad = (Button) findViewById(R.id.bLoad);
		tvInstructions = (TextView) findViewById(R.id.tvInstructions);
		tvSearchOf = (TextView) findViewById(R.id.tvSearchOf);
		
		init();
	}
	
//	private void redirect() {
//		Intent runOther = new Intent(MainActivity.this, Tabs.class);
//		startActivity(runOther);
//	}
	
	private void init() {
		elv.setOnChildClickListener(this);
		bDownload.setOnClickListener(this);
		bUpdate.setOnClickListener(this);
		bLoad.setOnClickListener(this);
		tvInstructions.setText("Steps:\n1. If this is the first time you\'ve run this app, "
						+ "choose [Download] to pull the most recent copies of the documents and resources. "
						+ "If you\'ve already done this, you do not need to [Download] unless informed by your "
						+ "GM of updated documents.\n2. The downloaded files stored on the SD card must be "
						+ "loaded into memory. Press [Update] to complete this process. If you have recently "
						+ "run this app, you might not have to do this again.\n3. Press [Load] to populate "
						+ "the list and begin using this library each time you open/re-open this app. \n\nEach "
						+ "of these buttons has a corresponding menu option, if needed. \n\nMost common problem: "
						+ "If the library populates with no information, just the group headers, you must "
						+ "[Update] again and then [Load] from the menu or button bar. This will resolve this issue.");
		tvSearchOf.setVisibility(View.GONE);
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
		
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	SubMenu primary = menu.addSubMenu("Actions");

        MenuItem subMenu1Item = primary.getItem();
        subMenu1Item.setIcon(R.drawable.ic_title_share_default);
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        MenuInflater inf = getSupportMenuInflater();
        inf.inflate(R.menu.main, primary);
        
        svQuery = new SearchView(getSupportActionBar().getThemedContext());
//		svQuery.setOnClickListener(this);
		svQuery.setQueryHint("Search");
		svQuery.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		svQuery.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		svQuery.setOnQueryTextListener(qtl);
		
        menu.add("Search")
        .setIcon(
//        		isLight ? R.drawable.ic_search_inverse : 
        			R.drawable.abs__ic_search)
        .setActionView(svQuery)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        
		return super.onCreateOptionsMenu(menu);
	}
	
    private void start() {
        it = new ImportTesting(new String[] { sdDir });
//        importThenLoad2.execute();
    }
    
    private void download() {
        String[][] fileNames = ImportTesting.getAllFilenames();
        
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
        	if (update.equalsIgnoreCase("false")) {
        		noUpdateFileNames.add(fileName);
        		noUpdateDownloadLocations.add(downloadLocation);
        	} else if (update.equalsIgnoreCase("true")){
        		updateFileNames.add(fileName);
        		updateDownloadLocations.add(downloadLocation);
        	} else {
        		Log.e("UpdateStatusUnknown","Could not determine update status. Defaulting to YES.");
        		updateFileNames.add(fileName);
        		updateDownloadLocations.add(downloadLocation);
        	}
        }

        // pack runnable so that updates will run then new
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
        
        // prepare a placeholder for each group
		for (int i = 0; i < include.length; i++) {
			result.add(new ArrayList<HashMap<String, GURPSObject>>());
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
						entry.put("Sub Item", go);

						result.get(x).add(entry);
					}
				}
			}

		}
        
        return result;
    }
    
    private void addToGroupList(ArrayList<HashMap<String, String>> group) {
		for (int i = 0; i < include.length; i++) {
			boolean exclude = false;
			
			if (!exclude) {
				HashMap<String, String> m = new HashMap<String, String>();
				m.put("Group Item", include[i].getSimpleName());
				group.add(m);
				
				expListAdapter.addGroup(include[i]);
			}
		}
    }
    
    private void removeGroup(int groupNumber) {
    	expListAdapter.removeGroup(groupNumber);
    }
    
	private final ArrayList<ArrayList<HashMap<String, GURPSObject>>> removeThese = new ArrayList<ArrayList<HashMap<String, GURPSObject>>>();
    private void addToChildList(String query, ArrayList<ArrayList<HashMap<String, GURPSObject>>> child) {
    	// prepare a placeholder for each group
		for (int i = 0; i < include.length; i++) {
			expListAdapter.addGroup(include[i]);
			child.add(new ArrayList<HashMap<String, GURPSObject>>());
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
						entry.put("Sub Item", go);

						child.get(x).add(entry);
						
						expListAdapter.addChild(x, go);
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
		
		expListAdapter.removeEmptyGroups();
    }

    /* This function is called on each child click */
    public boolean onChildClick( ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    	
    	final GURPSObject go = GURPSObject.findGURPSObjectById(expListAdapter.getChildData(groupPosition, childPosition).getObjectId());

    	// generate and display the dialog box for THAT child/GURPSObject
    	final InformationDialog inf = new InformationDialog(MainActivity.this, this);
    	inf.set(go);
    	inf.show();
    	
        return true;
    }
 
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
			update();
			break;
		case R.id.bLoad:
			load();
			break;
		}
	}

	private void update() {
		importOnly.run();
	}
	
	private void load() {
		updateText.run();
		
		/* dup */
		bLoad.setVisibility(View.GONE);
		bDownload.setVisibility(View.GONE);
		bUpdate.setVisibility(View.GONE);
		tvInstructions.setVisibility(View.GONE);
		
		MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		Toast.makeText(MainActivity.this, "rotation unlocked", Toast.LENGTH_LONG).show();
		
		if (expListAdapter != null) expListAdapter.clear();
		
		refineData(svQuery.getQuery().toString());
		/* end dup*/
	}

	Runnable importOnly = new Runnable() {
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
	
	AsyncTask<String, Integer, String> importThenLoad2 = new AsyncTask<String, Integer, String>() {
		@Override
		protected void onPreExecute() {
			updateText.run();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			importOnly.run();
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			/* dup */
			updateText.run();
			
			bLoad.setVisibility(View.GONE);
			bDownload.setVisibility(View.GONE);
			bUpdate.setVisibility(View.GONE);
			tvInstructions.setVisibility(View.GONE);
			
			MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			Toast.makeText(MainActivity.this, "rotation unlocked", Toast.LENGTH_LONG).show();
			
			if (expListAdapter != null) expListAdapter.clear();
			
			refineData(svQuery.getQuery().toString());
			/* end dup */
			
			super.onPostExecute(result);
		}
	};
	Runnable importThenLoad = new Runnable() {
		@Override
		public void run() {
			new ProgressMonitor(MainActivity.this, it, updateText).execute();
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
	}
	
	private void refineData(String s) {
		// reset info
		if (expListAdapter != null) {
			expListAdapter.clear();

			// get query
			String query = s.trim();

			if (query.equals("")) {
				tvSearchOf.setText("");
				tvSearchOf.setVisibility(View.GONE);
			} else {
				tvSearchOf.setText("Results with titles containing: [" + query
						+ "]");
				tvSearchOf.setVisibility(View.VISIBLE);
			}

			// process query
			addToChildList(query, createChildList());
			addToGroupList(createGroupList());

			expListAdapter.removeEmptyGroups();

			// update view
			expListAdapter.notifyDataSetChanged();
		} else {
			// do nothing
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
	
	OnQueryTextListener qtl = new OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String queryText) {
			refineData(queryText);
			return false;
		}
		
		@Override
		public boolean onQueryTextChange(String queryText) {
			refineData(queryText);
			return false;
		}
	};
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.i_menu_download:
			download();
			break;
		case R.id.i_menu_update:
			importOnly.run();
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

	@Override
	public boolean onSearchRequested() {
		if (svQuery.getVisibility() == View.VISIBLE) {
				svQuery.requestFocus();
		}
		return super.onSearchRequested();
	}
	
}
