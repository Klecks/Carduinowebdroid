package de.carduinodroid;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.carduinodroid.shared.*;
import de.carduinodroid.utilities.*;
import de.carduinodroid.utilities.Config.Options;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * Servlet implementation class Main
 */
@WebServlet(loadOnStartup=1, value = "/main")

/**
 * \brief This Class is used to handle user-timeouts and to log GPS in a given interval and to handle the waiting queue
 * @author Alexander Rose
 *
 */

public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	private static Timer caretaker;
	private static Timer GPSLog;
	private static TimerTask GPSLogger;
	private static TimerTask Session;
	private static TimerTask action;
	private static ArrayList<String> aliveSessions;
	static LogNG log;
	static int driveID;
	static int Fahrzeit, gpsLogInterval;
	static Options opt;
	static boolean flag;
	static String aktSessionID;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
        super();
        // TODO Auto-generated constructor stub
        System.out.println("Main");
    }
    
    /*
     * what is get and what is post?
     * 
     * get:
     * 
     * post:
     * 	- login
     * 	- enqueue
     * 	- dequeue
     * 	- watchDriver
     * 	-
     * 
     * NO REDIRECT FROM HERE!
     */

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doGet");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doPost");
		
		if(request instanceof HttpServletRequest) {			
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();			

			Map<String, String[]> m = req.getParameterMap();
//			Iterator<Entry<String, String[]>> entries = m.entrySet().iterator();
//			while (entries.hasNext()) {
//			    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) entries.next();
//			    String key = (String)entry.getKey();
//			    String[] value = (String[])entry.getValue();
//			    System.out.println("Key = " + key + ", Value = " + value[0]);
//			}
			///TODO \todo Sessions sind wirklich Strings werden aber später nach int gecastet
			if(m.size() > 0 && m.containsKey("action")) {
				String SessionID = session.getId();
				String ipAdress = req.getRemoteAddr();
				DBConnector db = (DBConnector)request.getServletContext().getAttribute("database");
				
				switch((String)m.get("action")[0])  {				
				case "login":
					if(!m.containsKey("loginName") || !m.containsKey("password"))
						break;
					
					String userID, pw;
					userID = (String)m.get("loginName")[0];
					pw = (String)m.get("password")[0];
					User u = db.loginUser(userID, pw);
					
					if(u == null)
						break;
					
					///TODO \todo session attribute (Rechte der user)
					session.setAttribute("isAdmin", u.isAdmin());
					session.setAttribute("isUser", u.isUser());
					session.setAttribute("nickName", u.getNickname());
					session.setAttribute("userId", u.getUserID());
					System.out.println("user " + u.getNickname() + " has logged in");
					activeSession.insertSession(SessionID, ipAdress, userID);
					break;
				case "enqueue":					
					User user = db.getUserBySession(activeSession.getSessionInt(SessionID));
					if (user == null){
						System.out.println("User nicht gefunden");
						break;
					}
					if (user.isGuest() == true) return;
					waitingqueue.insertUser(SessionID);
					log.logQueue(user.getUserID(), activeSession.getSessionInt(SessionID));
					break;
				case "dequeue":
					waitingqueue.deleteTicket(SessionID);
					break;
				case "NextUser":
					//String nextUserID = waitingqueue.getNextUser();
					///TODO \todo wohin soll der übergeben werden
					break;
				case "watchDriver":
					userID = "guest" + System.currentTimeMillis();
					activeSession.insertSession(SessionID, ipAdress, userID);
					session.setAttribute("isAdmin", false);
					session.setAttribute("isUser", false);
					session.setAttribute("nickname", userID);
					session.setAttribute("userId", userID);
					///TODO \todo user objekt anlegen wie bei login
					break;
//				case "toMainPage":
//					//request.getServletContext().getRequestDispatcher("/WEB-INF/main.jsp").forward(request, response);
//					break;
//				case "toAdminPage":
//					if ((boolean)session.getAttribute("isAdmin"))
//						request.getServletContext().getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);					
//					break;
//					break;
				case "logout":
					activeSession.deleteSession(SessionID);
					waitingqueue.deleteTicket(SessionID);
					session.removeAttribute("nickName");
					///TODO \todo logout = ich lösche ein paar sachen und das wars? session? rechte? zurück zum index?
					break;
				}
			}
		}
	}

	/** 
	 * \brief refreshes the options needed in this class (Fahrzeit and GPS interval)
	 * @param opt Options object
	 */
	
	public static void refresh(Options opt){
    	Fahrzeit = opt.fahrZeit;
    	gpsLogInterval = opt.logGPSInterval;
    	flag = true;
    }
	
	/** 
	 * \brief deletes all Timers and TimerTasks and also deletes all active sessions
	 */
	
	public static void shutDown(){
		System.out.println("Shut-Down main");
		//Session.cancel();
		action.cancel();
		GPSLogger.cancel();
		GPSLog.cancel();
		activeSession.deleteAll();
	}
    
    public static void main(Options opt, DBConnector db, LogNG logng){
    	
    	log = logng;
    	aliveSessions = new ArrayList<String>();
    	
//    	Session = new TimerTask(){
//			public void run(){
//							
//				for (int i = 0; i < aliveSessions.size(); i++){
//					activeSession.deleteSession(aliveSessions.get(i));
//					waitingqueue.deleteTicket(aliveSessions.get(i));
//				}
//				
//				String[] Sessions = new String[activeSession.getAllSessions().length];
//				Sessions = activeSession.getAllSessions();
//			
//				for(int i = 0; i < Sessions.length; i++){
//					aliveSessions.add(Sessions[i]);
//					///TODO \todo sende Nachricht an user und versuche diese wieder zu Empfangen
//				}
//			
//				///TODO \todo wenn Nachrichten ankommen entferne User aus aliveSessions
//			}
//		};
//    	
//		Timer Sessionhandle = new Timer();
//		Sessionhandle.schedule(Session, 10, 5000);
		Fahrzeit = opt.fahrZeit;
		gpsLogInterval = opt.logGPSInterval;
		System.out.println("Main-function");
		
		try {
			action = new TimerTask() {
				DBConnector db = new DBConnector();
				public void run() {
			    	if(flag){
			    		caretaker.cancel();
						caretaker = new Timer();
			    		caretaker.schedule(new de.carduinodroid.Dummy(action), 60000*Fahrzeit, 60000*Fahrzeit);
			    		flag = false;
			    	}
					
					if (waitingqueue.isEmpty() == true){
						caretaker.cancel();
						caretaker = new Timer();
						caretaker.schedule(new de.carduinodroid.Dummy(action), 1000, 60000*Fahrzeit);				
						return;
					}
					else{
						String aktSessionID = waitingqueue.getNextUser();
						driveID = db.startDrive(db.getUserIdBySession(activeSession.getSessionInt(aktSessionID)));
						///TODO \todo Fahrrechte;

						}
					}
			    
			};
		} catch (Exception e) {
			log.writelogfile(e.getMessage());
			e.printStackTrace();
		}
	
		
		
		caretaker = new Timer();
		caretaker.schedule(action, 100, 60000*Fahrzeit);  
		
		try {
			GPSLogger = new TimerTask(){		
				public void run(){
					String longitude = CarControllerWrapper.getLongitude();
					String latitude = CarControllerWrapper.getLatitude();
					if (longitude == null || latitude == null){
						//System.out.println("GPS: N/A");
					} else
						log.logGPS(driveID, longitude, latitude);
				}
			};
		} catch (Exception e) {
			log.writelogfile(e.getMessage());
			e.printStackTrace();
		}
		GPSLog = new Timer();
		GPSLog.schedule(GPSLogger, 10, gpsLogInterval * 1000);
    }   
    
}
