package com.facetorched.iebpt;

import java.util.ArrayList;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.lib.manual.IManualPage;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.ManualInstance.ManualEntry;
import blusunrize.lib.manual.ManualPages;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import scala.actors.threadpool.Arrays;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.immersiveengineering.Manual")
public class Manual {
	@ZenMethod
	public static void removePage(String name, int pageNum) {
		MineTweakerAPI.apply(new RemovePage(name, pageNum));
	}
	@ZenMethod
	public static void removeEntry(String name) {
		MineTweakerAPI.apply(new RemoveEntry(name));
	}
	@ZenMethod
	public static void addTextPage(String name, String text, String category, @Optional int pageNum) {
		MineTweakerAPI.apply(new AddPage(name, text, category, pageNum));
	}
	@ZenMethod
	public static void addTextPage(String name, String text, @Optional int pageNum) {
		MineTweakerAPI.apply(new AddPage(name, text, null, pageNum));
	}
	
	private static class RemoveEntry implements IUndoableAction{
		private final String name;
		ManualEntry manualEntry;

		public RemoveEntry(String name){
			this.name = name;
		}
		
		@Override
		public void apply() {
			ManualInstance manual = ManualHelper.getManual();
			this.manualEntry = manual.getEntry(name);
			if (manualEntry != null) {
				manual.manualContents.remove(manualEntry.getCategory(), manualEntry);
			}
			else {
				MineTweakerAPI.logError("no Engineer's Manual entry with name " + name);
			}
		}
		@Override
		public boolean canUndo() {
			return true;
		}
		@Override
		public String describe() {
			return "removing Engineer's Manual entry" + name;
		}
		@Override
		public String describeUndo() {
			return "re-adding Engineer's Manual entry " + name;
		}
		@Override
		public Object getOverrideKey() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void undo() {
			if (manualEntry != null){
				ManualInstance manual = ManualHelper.getManual();
				manual.addEntry(name, manualEntry.getCategory(), manualEntry.getPages());
			}
		}
	}
	
	private static class RemovePage implements IUndoableAction
	{
		private final String name;
		private final int pageNum;
		IManualPage page;

		public RemovePage(String name, int pageNum){
			this.name = name;
			this.pageNum = pageNum;
		}
		
		@Override
		public void apply() {
			ManualInstance manual = ManualHelper.getManual();
			ManualEntry manualEntry = manual.getEntry(name);
	    	if(manualEntry != null) {
	    		@SuppressWarnings("unchecked")
				ArrayList<IManualPage> mp = new ArrayList<IManualPage>(Arrays.asList(manualEntry.getPages()));
	    		try {
	    			page = mp.remove(pageNum);
	    		}
	    		catch (IndexOutOfBoundsException e) {
	    			MineTweakerAPI.logError("Engineer's Manual entry with name " + name + " does not have a page at index " + pageNum);
	    			return;
	    		}
	    		IManualPage[] mparr = new IManualPage[mp.size()];
	    		manualEntry.setPages(mp.toArray(mparr));
	    	}
	    	else {
	    		MineTweakerAPI.logError("no Engineer's Manual entry with name " + name);
	    	}
		}
		@Override
		public boolean canUndo() {
			return true;
		}
		@Override
		public String describe() {
			return "removing Engineer's Manual page " + pageNum + " from entry " + name;
		}
		@Override
		public String describeUndo() {
			return "re-adding Engineer's Manual page " + pageNum + " to entry " + name;
		}
		@Override
		public Object getOverrideKey() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void undo() {
			ManualInstance manual = ManualHelper.getManual();
			ManualEntry manualEntry = manual.getEntry(name);
	    	if(manualEntry != null && page != null) {
	    		@SuppressWarnings("unchecked")
				ArrayList<IManualPage> mp = new ArrayList<IManualPage>(Arrays.asList(manualEntry.getPages()));
	    		mp.add(pageNum, page);
	    		IManualPage[] mparr = new IManualPage[mp.size()];
	    		manualEntry.setPages(mp.toArray(mparr));
	    	}
		}
	}
	private static class AddPage implements IUndoableAction
	{
		private final String name;
		private final int pageNum;
		private final String text;
		private final String category;
		boolean applied;

		public AddPage(String name, String text, String category, int pageNum){
			this.name = name;
			this.pageNum = pageNum;
			this.text = text;
			this.category = category;
		}
		
		@Override
		public void apply() {
			ManualInstance manual = ManualHelper.getManual();
			ManualEntry manualEntry = manual.getEntry(name);
	    	if(manualEntry != null && (category == null || category.equals(manualEntry.getCategory()))) {
	    		@SuppressWarnings("unchecked")
				ArrayList<IManualPage> mp = new ArrayList<IManualPage>(Arrays.asList(manualEntry.getPages()));
	    		try {
	    			mp.add(pageNum, new ManualPages.Text(manual, text));
	    			applied = true;
	    		}
	    		catch (IndexOutOfBoundsException e) {
	    			MineTweakerAPI.logError("Engineer's Manual entry with name " + name + " cannot add a page at index " + pageNum);
	    			return;
	    		}
	    		IManualPage[] mparr = new IManualPage[mp.size()];
	    		manualEntry.setPages(mp.toArray(mparr));
	    	}
	    	else if (category != null){
	    		if(pageNum != 0) {
	    			MineTweakerAPI.logError("Engineer's Manual entry with name " + name + " cannot add a page at index " + pageNum);
	    			return;
	    		}
	    		manual.addEntry(name, category, new ManualPages.Text(manual, text));
	    		applied = true;
	    	}
		}
		@Override
		public boolean canUndo() {
			return true;
		}
		@Override
		public String describe() {
			return "adding Engineer's Manual page " + pageNum + " to entry " + name;
		}
		@Override
		public String describeUndo() {
			return "removing Engineer's Manual page " + pageNum + " from entry " + name;
		}
		@Override
		public Object getOverrideKey() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void undo() {
			ManualInstance manual = ManualHelper.getManual();
			ManualEntry manualEntry = manual.getEntry(name);
	    	if(manualEntry != null && applied) {
	    		@SuppressWarnings("unchecked")
				ArrayList<IManualPage> mp = new ArrayList<IManualPage>(Arrays.asList(manualEntry.getPages()));
	    		mp.remove(pageNum);
	    		if(mp.size() == 0) {
	    			String cat = manualEntry.getCategory();
	    			manual.manualContents.remove(cat, manualEntry);
	    		}
	    		else {
	    			IManualPage[] mparr = new IManualPage[mp.size()];
	    			manualEntry.setPages(mp.toArray(mparr));
	    		}
	    	}
		}
	}
}
