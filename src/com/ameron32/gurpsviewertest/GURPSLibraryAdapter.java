package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ameron32.libgurps.impl.GURPSObject;

import android.content.Context;
import android.widget.SimpleExpandableListAdapter;

public class GURPSLibraryAdapter extends SimpleExpandableListAdapter {

	public GURPSLibraryAdapter(
			Context context,
			List<? extends Map<String, ?>> groupData,
			int expandedGroupLayout,
			String[] groupFrom, 
			int[] groupTo,
			List<? extends List<? extends Map<String, GURPSObject>>> childData,
			int childLayout, 
			String[] childFrom,
			int[] childTo) {

		super(context, groupData, expandedGroupLayout,
				groupFrom, groupTo, reviseChildData(childData), childLayout, childFrom, childTo);
	}
	
	private static ArrayList<ArrayList<HashMap<String, String>>> reviseChildData(List<? extends List<? extends Map<String, GURPSObject>>> childData) {
		ArrayList<ArrayList<HashMap<String, String>>> revised =
				new ArrayList<ArrayList<HashMap<String, String>>>(); 
		for (List<? extends Map<String, GURPSObject>> a : childData) {
			ArrayList<HashMap<String, String>> revisedA = new ArrayList<HashMap<String, String>>();
			revised.add(revisedA);
			for (Map<String, GURPSObject> b : a) {
				HashMap<String, String> revisedB = new HashMap<String, String>();
				revisedA.add(revisedB);
				for (GURPSObject go : b.values()) {
					revisedB.put("Sub Item", go.getName());
				}
				if (revisedB.isEmpty()) revisedA.remove(revisedB); 
			}
			if (revisedA.isEmpty()) revised.remove(revisedA);
		}
		return revised;
	}

	
	
	
//	new SimpleExpandableListAdapter(
//			this, 
//			groupList,
//			R.layout.group_row,
//			new String[] { "Group Item" },
//			new int[] { R.id.row_name },
//			childList,
//			R.layout.child_row,
//			new String[] { "Sub Item" },
//			new int[] { R.id.grp_child }
//			);
}
