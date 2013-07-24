package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ameron32.libgurps.impl.GURPSObject;

import android.content.Context;
import android.util.Log;
import android.widget.SimpleExpandableListAdapter;

public class GURPSLibraryAdapter extends SimpleExpandableListAdapter {

	/**
	 * The first method run to convert the data from <String, GURPSObject> to <String, String>
	 * 
	 * @param unconverted List<List<Map<S,GO>>>
	 * @return completed conversion
	 */
	private static 
	final ArrayList<ArrayList<HashMap<String, String>>> convertChildData(
			final ArrayList<ArrayList<HashMap<String, GURPSObject>>> feedData) {
		revisedChildData.clear();
		for (final ArrayList<HashMap<String, GURPSObject>> a : feedData) {
			ArrayList<HashMap<String, String>> revisedA = new ArrayList<HashMap<String, String>>();
			revisedChildData.add(revisedA);
			for (final HashMap<String, GURPSObject> b : a) {
				HashMap<String, String> revisedB = new HashMap<String, String>();
				revisedA.add(revisedB);
				for (final GURPSObject go : b.values()) {
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
			ArrayList<HashMap<String, String>> loadGroupData,
			int expandedGroupLayout,
			String[] groupFrom, 
			int[] groupTo,
			ArrayList<ArrayList<HashMap<String, GURPSObject>>> loadChildData,
			int childLayout, 
			String[] childFrom,
			int[] childTo) {
		super(context, groupData, expandedGroupLayout,
				groupFrom, groupTo, revisedChildData, childLayout, childFrom, childTo);
		groupData.addAll(loadGroupData);
		childData.addAll(loadChildData);
	}

	private static final ArrayList<HashMap<String, String>> groupData =
			new ArrayList<HashMap<String, String>>();
	private static final ArrayList<ArrayList<HashMap<String, GURPSObject>>> childData =
			new ArrayList<ArrayList<HashMap<String, GURPSObject>>>();
	private static final ArrayList<ArrayList<HashMap<String, String>>> revisedChildData =
			new ArrayList<ArrayList<HashMap<String, String>>>();


//	private void addGroup() {
//		revisedChildData.add(new ArrayList<HashMap<String, String>>());
//		childData.add(new ArrayList<HashMap<String, GURPSObject>>());
//	}
	
	public void addGroup(Class<?> group) {
		HashMap<String, String> groupMap = new HashMap<String, String>();
		groupMap.put("Group Item", group.getSimpleName());
		groupData.add(groupMap);
		revisedChildData.add(new ArrayList<HashMap<String, String>>());
		childData.add(new ArrayList<HashMap<String, GURPSObject>>());
	}
	
	public void removeGroup(int groupPosition) {
		groupData.remove(groupPosition);
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
	
	private static final ArrayList<HashMap<String, String>> removeRGD = new ArrayList<HashMap<String, String>>();
	private static final ArrayList<ArrayList<HashMap<String, String>>> removeRCD = new ArrayList<ArrayList<HashMap<String, String>>>();
	private static final ArrayList<ArrayList<HashMap<String, GURPSObject>>> removeCD = new ArrayList<ArrayList<HashMap<String, GURPSObject>>>();
	public void removeEmptyGroups() {
		removeRGD.clear();
		removeRCD.clear();
		removeCD.clear();
		int counter = 0;
		
		for (ArrayList<HashMap<String, String>> group : revisedChildData) {
			if (group.isEmpty()) {
				try {
					removeRGD.add(groupData.get(counter));
					removeRCD.add(revisedChildData.get(counter));
					removeCD.add(childData.get(counter));
//					Log.e("Successful", "c:" + counter);
//					Log.e("Successful", removeRGD.size() + " in removeRGD");
				} catch (IndexOutOfBoundsException e) {
					Log.e("IOOBE", "groupData.size() [" + groupData.size() + "]");
					Log.e("IOOBE", "childData.size() [" + childData.size() + "]");
					Log.e("IOOBE", "revisedChildData.size() [" + revisedChildData.size() + "]");
					Log.e("IOOBE", "c:[" + counter + "]");
					Log.e("IOOBE", "removeRGD.size() [" + removeRGD.size() + "]");
				}
			}
			counter++;
		}
		for (HashMap<String, String> a : removeRGD) {
			groupData.remove(a);
		}
		for (ArrayList<HashMap<String, String>> b : removeRCD) {
			revisedChildData.remove(b);
		}
		for (ArrayList<HashMap<String, GURPSObject>> c : removeCD) {
			childData.remove(c);
		}
	}
	
	public GURPSObject getChildData(int groupPosition, int childPosition) {
		return childData.get(groupPosition).get(childPosition).get("Sub Item");
	}
	
}
