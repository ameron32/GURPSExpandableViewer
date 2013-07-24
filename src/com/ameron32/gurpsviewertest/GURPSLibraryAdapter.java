package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ameron32.libgurps.impl.GURPSObject;

import android.content.Context;
import android.widget.SimpleExpandableListAdapter;

public class GURPSLibraryAdapter extends SimpleExpandableListAdapter {

	/**
	 * The first method run to convert the data from <String, GURPSObject> to <String, String>
	 * 
	 * @param unconverted List<List<Map<S,GO>>>
	 * @return completed conversion
	 */
	private static ArrayList<ArrayList<HashMap<String, String>>> initializeChildData(ArrayList<ArrayList<HashMap<String, GURPSObject>>> feedData) {
		revisedChildData.clear();
		for (ArrayList<HashMap<String, GURPSObject>> a : feedData) {
			ArrayList<HashMap<String, String>> revisedA = new ArrayList<HashMap<String, String>>();
			revisedChildData.add(revisedA);
			for (HashMap<String, GURPSObject> b : a) {
				HashMap<String, String> revisedB = new HashMap<String, String>();
				revisedA.add(revisedB);
				for (GURPSObject go : b.values()) {
					revisedB.put("Sub Item", go.getName());
				}
				if (revisedB.isEmpty()) revisedA.remove(revisedB); 
			}
			if (revisedA.isEmpty()) revisedChildData.remove(revisedA);
		}
		return revisedChildData;
	}
	
	
	public GURPSLibraryAdapter(
			Context context,
			ArrayList<HashMap<String, String>> groupData,
			int expandedGroupLayout,
			String[] groupFrom, 
			int[] groupTo,
			ArrayList<ArrayList<HashMap<String, GURPSObject>>> childData,
			int childLayout, 
			String[] childFrom,
			int[] childTo) {
		super(context, groupData, expandedGroupLayout,
				groupFrom, groupTo, initializeChildData(childData), childLayout, childFrom, childTo);
		groupData.addAll(groupData);
		childData.addAll(childData);
	}

	
	private static final ArrayList<HashMap<String, String>> groupData =
			new ArrayList<HashMap<String, String>>();
	private static final ArrayList<ArrayList<HashMap<String, GURPSObject>>> childData =
			new ArrayList<ArrayList<HashMap<String, GURPSObject>>>();
	private static final ArrayList<ArrayList<HashMap<String, String>>> revisedChildData =
				new ArrayList<ArrayList<HashMap<String, String>>>();


	public void addGroup() {
		revisedChildData.add(new ArrayList<HashMap<String, String>>());
		childData.add(new ArrayList<HashMap<String, GURPSObject>>());
	}
	
	public void addGroup(Class<?> group) {
		HashMap<String, String> groupMap = new HashMap<String, String>();
		groupMap.put("Group Item", group.getSimpleName());
		groupData.add(groupMap);
	}
	
	public void removeGroup(int groupPosition) {
		revisedChildData.remove(groupPosition);
		childData.remove(groupPosition);
	}
	
	public void addChild(int groupPosition, final GURPSObject go) {
		HashMap<String, GURPSObject> goObject = new HashMap<String, GURPSObject>();
		goObject.put("Sub Item", go);
		childData.get(groupPosition).add(goObject);
		
		HashMap<String, String> goName = new HashMap<String, String>();
		goName.put("Sub Item", go.getName());
		revisedChildData.get(groupPosition).add(goName);
	}
	
	public void clear() {
		groupData.clear();
		revisedChildData.clear();
		childData.clear();
	}
	
	private static final ArrayList<ArrayList<HashMap<String, String>>> removeRCD = new ArrayList<ArrayList<HashMap<String, String>>>();
	private static final ArrayList<ArrayList<HashMap<String, GURPSObject>>> removeCD = new ArrayList<ArrayList<HashMap<String, GURPSObject>>>();
	public void removeEmptyChilds() {
		removeRCD.clear();
		removeCD.clear();
		
		int counter = 0;
		for (ArrayList<HashMap<String, String>> group : revisedChildData) {
			if (group.isEmpty()) {
				removeRCD.add(revisedChildData.get(counter));
				removeCD.add(childData.get(counter));
			}
			counter++;
		}
		Object r;
		for (ArrayList<HashMap<String, String>> a : removeRCD) {
			revisedChildData.remove(a);
		}
		for (ArrayList<HashMap<String, GURPSObject>> b : removeCD) {
			childData.remove(b);
		}
	}
	
	public GURPSObject getChildData(int groupPosition, int childPosition) {
		return childData.get(groupPosition).get(childPosition).get("Sub Item");
	}
	
}
