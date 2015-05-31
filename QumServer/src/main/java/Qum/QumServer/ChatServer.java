package Qum.QumServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import Qum.Mes.Mess;

public class ChatServer {

    public static final Logger MyLogger = Logger.getLogger(ChatServer.class);
    public static List<Mess> MessList = new CopyOnWriteArrayList<Mess>();
    public static Map<String, ClientUnit> ClientThreads = new ConcurrentHashMap<String, ClientUnit>();
    private static int tempNuberOfClientThtead = 0;
    private static String tempNameOfClientThtead; //временное имя потока под которым мы его покладем в мапу, когда клиент на нем авторизируется то он заменится на его Ник.

    public static void main(String[] args) {
	
	okno.main(args);
	ServerSocket SS; // tt
	
	try {
	    SS = new ServerSocket(9090, 350);
	    while (true) {
		tempNuberOfClientThtead++;  // генерим циферку для временного имени очередного клиента.
		tempNameOfClientThtead = "Client Thread number "
			+ tempNuberOfClientThtead;
		Socket S = SS.accept();
		MyLogger.info("New connect nubmer - " + tempNuberOfClientThtead);
		ClientUnit ClientUnitTh = new ClientUnit(
			tempNameOfClientThtead, S);
		ClientThreads.put(tempNameOfClientThtead, ClientUnitTh);
		ClientUnitTh.start();
		MyLogger.info("Start ClientUnitTh " + tempNuberOfClientThtead);
	    }
	} catch (IOException e) {
	    MyLogger.warn("ChatServer throw IOException on "
		    + tempNameOfClientThtead);
	    e.printStackTrace();
	}
    }
}
