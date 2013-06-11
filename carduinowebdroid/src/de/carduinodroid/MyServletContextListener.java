package de.carduinodroid;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.carduinodroid.desktop.Model.GPSTrack;
import de.carduinodroid.utilities.CarControllerWrapper;
import de.carduinodroid.utilities.Config;
import de.carduinodroid.utilities.Config.Options;
import de.carduinodroid.utilities.DBConnector;
import de.carduinodroid.utilities.LogNG;
import de.carduinodroid.shared.activeSession;

@WebListener
public class MyServletContextListener implements ServletContextListener {

	private ServletContext context;
	private LogNG log;

	/*This method is invoked when the Web Application has been removed 
	and is no longer able to accept requests
	*/

	public void contextDestroyed(ServletContextEvent event)
	{
		log.writelogfile("contextDestroyed.");
		this.context = null;
		
		DBConnector db = (DBConnector)event.getServletContext().getAttribute("database");
		db.shutDown();
	}


	//This method is invoked when the Web Application
	//is ready to service requests

	public void contextInitialized(ServletContextEvent event)
	{
		context = event.getServletContext();
		
		// initialize everything		
		// new log
		log = new LogNG();
		log.writelogfile("logNG instanciated.");
		context.setAttribute("log", log);
		
		// config & options
		Config config = new Config(log, context.getRealPath("/WEB-INF/"));
		config.readOptions();
		Options options = config.getOptions();
		if(options.dbAddress == null) {
			config.setDefault(false);
			options = config.getOptions();
		}
		log.writelogfile("Options loaded");
		context.setAttribute("options", options);
			
		//main
		activeSession.init();
		de.carduinodroid.shared.Warteschlange.initWarteschlange();
		
		// GPS		
		GPSTrack gps = new GPSTrack();
		log.writelogfile("GPSTracker instanciated");
		
		// controller
		//Controller_Computer controller = new Controller_Computer(log.getOldLog(), gps);	
		CarControllerWrapper controller = new CarControllerWrapper(log, gps);
		log.writelogfile("Controller_Computer instanciated.");
		context.setAttribute("controller", controller);	
				
		// database
		DBConnector db = new DBConnector(log, options);
		log.writelogfile("DBConnector instanciated.");
		context.setAttribute("database", db);
		
		// send options and db to logNG 
		log.setOptions(options);
		log.setDB(db);
		
		de.carduinodroid.Main.main(options,db);

		//db.dbTest();		
	}
}
