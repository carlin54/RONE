package rone.plugins;

import java.util.ArrayList;

import org.pf4j.Extension;
import org.pf4j.PluginWrapper;

public class TargetMineSearchPlugin extends SearchPlugin {

	public TargetMineSearchPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Extension
	public static class TargetMineSearchExtension implements SearchExtension {

		public String getTitle() {
			return "TargetMine";
		}

		public String[] getColumnIdentifers() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getSearchSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getThreadPoolSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getSearchTimeoutDuration() {
			// TODO Auto-generated method stub
			return 0;
		}

		public ArrayList<Object[]> search(String[] columnIdentifers, ArrayList<Object[]> tableSelection) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
    @Override
    public void start() {
    	System.out.println("TargetMineSearchPlugin:start():-Hello World!");
        // This method is called by the application when the plugin is started.
    }

    @Override
    public void stop() {
    	System.out.println("TargetMineSearchPlugin:stop():-Hello World!");
        // This method is called by the application when the plugin is stopped.
    }

    @Override
    public void delete() {
    	System.out.println("TargetMineSearchPlugin:delete():-Hello World!");
        // This method is called by the application when the plugin is deleted.
    }

}
