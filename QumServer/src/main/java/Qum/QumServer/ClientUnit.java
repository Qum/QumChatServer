package Qum.QumServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.apache.log4j.Logger;

import Qum.Mes.Mess;

public class ClientUnit extends Thread {

    public static final Logger MyLogger = Logger.getLogger(ClientUnit.class);

    static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    static Date date = new Date();

    public Socket Sock;
    public ObjectInputStream Oin;
    public ObjectOutputStream Oou;
    public OutputStream StreamFileRec;
    private Thread OutpMesDemon;
    public Mess BuffMess;
    private String myTempName;
    private String userName;
    private Connection con;
    private boolean onlineStatus, ygeSoobwilProVuhod;

    final static int AUTH_REQUEST = 1;
    final static int REGISTER_REQUEST = 2;
    final static int AUTH_FAIL = 3;
    final static int SUCCESS_AUTH_ALREADY_ONLINE = 4;
    final static int SUCCESS_AUTH_SUCCESS_ONLINE = 5;
    final static int REGISTER_SUCCESS = 6;
    final static int REGISTER_FAIL = 7;
    final static int FILE_REQUEST = 8;
    final static int FILE_REQUEST_SUCCESS = 9;
    final static int FILE_REQUEST_FAIL = 10;
    final static int LOGOUT = 11;
    final static int CHANGE_NAME = 12;
    final static int CHANGE_NAME_SUCCESS = 13;
    final static int CHANGE_NAME_FAIL = 14;

    private final String RegisterQuery = "INSERT INTO users(login,pass,acc_lvl,last_ip,email) VALUES(?,?,?,?,?)";
    private final String CheckUserExist = "SELECT * FROM users WHERE login=(?)";

    ClientUnit(String tempName, Socket s) throws IOException {
	myTempName = tempName;
	Sock = s;
	Oou = new ObjectOutputStream(s.getOutputStream());
	Oin = new ObjectInputStream(s.getInputStream());
	MyLogger.debug("ClientUnit "
		+ (userName == null ? myTempName : userName)
		+ " srart with IP " + Sock.getInetAddress().getHostAddress());
    }

    public void run() {
	startOutputMessThread();
	try {
	    while (true) {
		BuffMess = (Mess) Oin.readObject();
		if (BuffMess.getServiceCode() > 0) {
		    MyLogger.debug("ClientUnit "
			    + (userName == null ? myTempName : userName) + " "
			    + BuffMess.getServiceCode());
		    if (BuffMess.getServiceCode() == AUTH_REQUEST) {
			doAuth();
		    } else if (BuffMess.getServiceCode() == REGISTER_REQUEST) {
			doRegister();
		    } else if (BuffMess.getServiceCode() == FILE_REQUEST) {
			iWantSendFile(BuffMess.getValue1());
		    } else if (BuffMess.getServiceCode() == FILE_REQUEST_SUCCESS) {
			doNotifySander(BuffMess.getValue1(), true);
		    } else if (BuffMess.getServiceCode() == FILE_REQUEST_FAIL) {
			doNotifySander(BuffMess.getValue1(), false);
		    } else if (BuffMess.getServiceCode() == CHANGE_NAME) {
			// TO DO 
		    } else if (BuffMess.getServiceCode() == CHANGE_NAME_SUCCESS) {
			// TO DO
		    } else if (BuffMess.getServiceCode() == CHANGE_NAME_FAIL) {
			// TO DO
		    } else if (BuffMess.getServiceCode() == LOGOUT) {
			doLogout();
		    }
		} else if (onlineStatus) {
		    BuffMess.setValue1(dateFormat.format(date) + " " + userName);
		    ChatServer.MessList.add(BuffMess);
		}
	    }
	} catch (SocketException ex) {
	    MyLogger.warn("ClientUnit "
		    + (userName == null ? myTempName : userName)
		    + " throw SocketException" + ex);
	} catch (ClassNotFoundException e) {
	    MyLogger.warn("Clientunit "
		    + (userName == null ? myTempName : userName)
		    + " throw ClassNotFoundException" + e);
	    e.printStackTrace();
	} catch (IOException e) {
	    MyLogger.warn("Clientunit "
		    + (userName == null ? myTempName : userName)
		    + " throw IOException " + e);
	    e.printStackTrace();
	} finally {
	    MyLogger.info("Clientunit "
		    + (userName == null ? myTempName : userName)
		    + " enter in finally block");
	    if (onlineStatus && !ygeSoobwilProVuhod) {
		ChatServer.MessList
			.add(new Mess(dateFormat.format(date) + " " + "SYS ",
				">>>>>>>>>>>> " + userName + " - DISCONNECT"));
		onlineStatus = false;
		ygeSoobwilProVuhod = true;
	    }
	    ChatServer.ClientThreads.remove(userName);
	    try {
		if (Sock != null && Sock.isConnected()) {
		    Sock.close();
		}
	    } catch (IOException e) {
	    }
	}
    }

    private void doLogout() {
	if (onlineStatus) {
	    ChatServer.MessList.add(new Mess(dateFormat.format(date) + " "
		    + "SYS ", ">>>>>>>>>>>> " + userName + " - вышел из чата"));
	    onlineStatus = false;
	    if (ChatServer.ClientThreads.remove(userName) != null) {
		MyLogger.debug(userName + " вышел logut");
	    } else {
		MyLogger.error("!!!!!!!!!!!не удален из мапы DO OUT METHOD!!!!!!!!!!!!");
	    }
	}
	ygeSoobwilProVuhod = true;
    }

    private void doNotifySander(String senderNick, boolean reciverAnswer)
	    throws IOException {
	if (reciverAnswer == true) {
	    ChatServer.ClientThreads.get(senderNick).reciverDecision(userName,
		    Sock.getInetAddress().getHostAddress(), reciverAnswer);
	} else {
	    ChatServer.ClientThreads.get(senderNick).reciverDecision(userName,
		    senderNick, reciverAnswer);
	}

    }

    public void doFileRecivRequest(String sender, String fileName, long size,
	    String Ip) throws IOException {

	Oou.writeObject(new Mess(sender, fileName, size, Ip, FILE_REQUEST));
    }

    public void iWantSendFile(String reciverNick) throws IOException {

	ChatServer.ClientThreads.get(reciverNick).doFileRecivRequest(
		this.userName, BuffMess.getValue2(), BuffMess.getFileSize(),
		Sock.getInetAddress().getHostAddress());
    }

    private void reciverDecision(String reciverName, String ip, boolean answer)
	    throws IOException {
	if (answer == true) {
	    Oou.writeObject(new Mess(reciverName, "", FILE_REQUEST_SUCCESS));
	} else if (answer == false) {
	    Oou.writeObject(new Mess(reciverName, "", FILE_REQUEST_FAIL));
	}
    }

    private void doRegister() throws IOException {
	if (!userExistInDb()) {
	    MyLogger.debug("Clientunit.doRegister in "
		    + (userName == null ? myTempName : userName)
		    + " get conn from jdbc conn pool");
	    try {
		con = DbFactory.getInstance().getCon();
		PreparedStatement ps = con.prepareStatement(RegisterQuery);
		ps.setString(1, BuffMess.getValue1());
		ps.setString(2, cryptPassword(BuffMess.getValue2()));
		ps.setInt(3, 0);
		ps.setString(4, Sock.getInetAddress().getHostAddress());
		ps.setString(5, BuffMess.getValue3());
		ps.execute();
		onlineStatus = true;
		userName = BuffMess.getValue1();
		ChatServer.MessList
			.add(new Mess(dateFormat.format(date) + " " + "SYS ",
				">>>>>>>>>>>>" + userName + " - зашел в чат"));
		Oou.writeObject(new Mess(BuffMess.getValue1(), " ",
			REGISTER_SUCCESS));
	    } catch (SQLException e) {
		MyLogger.warn("Clientunit.doRegister in  "
			+ (userName == null ? myTempName : userName)
			+ " throw SQLException " + e);
		e.printStackTrace();
	    } finally {
		try {
		    con.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	} else {
	    Oou.writeObject(new Mess(" ", " ", REGISTER_FAIL));
	    MyLogger.debug("Clientunit.doRegister in "
		    + (userName == null ? myTempName : userName)
		    + " tried register existing user");
	}
    }

    private void doAuth() throws IOException {
	MyLogger.info("Clientunit.doAuth in "
		+ (userName == null ? myTempName : userName));
	if (userExistInDb()) {
	    if (checkAuthData()) {
		MyLogger.debug("Clientunit.doAuth in "
			+ (userName == null ? myTempName : userName)
			+ " username-pass confirmed");
		if (ChatServer.ClientThreads.containsKey(BuffMess.getValue1())) {
		    Oou.writeObject(new Mess("SYS",
			    "Такой пользователь уже(ещё) активен в чате",
			    SUCCESS_AUTH_ALREADY_ONLINE));
		    MyLogger.debug("Clientunit.doAuth in "
			    + (userName == null ? myTempName : userName)
			    + " is already online");
		} else {
		    MyLogger.debug("Clientunit.doAuth in "
			    + (userName == null ? myTempName : userName)
			    + " is not online");
		    if (ChatServer.ClientThreads.remove(myTempName, this)) {
			ChatServer.ClientThreads
				.put(BuffMess.getValue1(), this);
			this.userName = BuffMess.getValue1();
			onlineStatus = true;
			ChatServer.MessList.add(new Mess(dateFormat
				.format(date) + " " + "SYS ", ">>>>>>>>>>>>"
				+ userName + " - зашел в чат"));
			Oou.writeObject(new Mess("SYS",
				"Авторизация прошла успешно",
				SUCCESS_AUTH_SUCCESS_ONLINE));
			MyLogger.debug("Clientunit.doAuth in "
				+ (userName == null ? myTempName : userName)
				+ " is success get online");
		    } else {
			MyLogger.warn("Clientunit.doAuth in "
				+ (userName == null ? myTempName : userName)
				+ "Do NOT remove OLD CliUnin from ClientThreads!!!");
		    }
		}
	    } else {
		Oou.writeObject(new Mess("SYS", "Неверный Логин,или пароль",
			AUTH_FAIL));
		MyLogger.debug("Clientunit.doAuth in "
			+ (userName == null ? myTempName : userName)
			+ " username-pass not confirmed");
	    }
	} else {
	    Oou.writeObject(new Mess("SYS", "Неверный Логин,или пароль",
		    AUTH_FAIL));
	    MyLogger.debug("Clientunit.doAuth in "
		    + (userName == null ? myTempName : userName)
		    + " username not existing");
	}
    }

    private boolean checkAuthData() {

	String resultPass = null;
	PreparedStatement ps;
	ResultSet rs;

	try {
	    con = DbFactory.getInstance().getCon();
	    MyLogger.debug("Clientunit.checkAuthData in "
		    + (userName == null ? myTempName : userName)
		    + " get conn from jdbc conn pool");
	    ps = con.prepareStatement(CheckUserExist);
	    ps.setString(1, BuffMess.getValue1());
	    ps.execute();
	    rs = ps.getResultSet();
	    while (rs.next()) {
		resultPass = rs.getString(3);
	    }
	} catch (SQLException e) {
	    MyLogger.info("Clientunit.checkAuthData in "
		    + (userName == null ? myTempName : userName)
		    + " FAIL SQL =====***");
	    e.printStackTrace();
	} finally {
	    try {
		con.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}

	return (resultPass.equals(cryptPassword(BuffMess.getValue2())) ? true
		: false);
    }

    private boolean userExistInDb() {

	PreparedStatement ps;
	ResultSet rs;
	String result = null;
	try {
	    con = DbFactory.getInstance().getCon();
	    MyLogger.info("Clientunit.userExistInDb in "
		    + (userName == null ? myTempName : userName)
		    + " get conn from jdbc conn pool");
	    ps = con.prepareStatement(CheckUserExist);
	    ps.setString(1, BuffMess.getValue1());
	    ps.execute();
	    rs = ps.getResultSet();
	    while (rs.next()) {
		result = rs.getString(2);
	    }
	    MyLogger.debug("Clientunit.userExistInDb in "
		    + (userName == null ? myTempName : userName) + " " + result);
	} catch (SQLException e) {
	    MyLogger.info("Clientunit.userExistInDb in "
		    + (userName == null ? myTempName : userName)
		    + " FAIL SQL =====**");
	    e.printStackTrace();
	} finally {
	    try {
		con.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return (result != null ? true : false);
    }

    private void startOutputMessThread() {
	try {
	    OutpMesDemon = new Thread(new DeamonOutMess(Oou));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	OutpMesDemon.setDaemon(true);
	OutpMesDemon.start();
    }

    private String cryptPassword(String password) {
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA");
	    byte[] raw = password.getBytes("UTF-8");
	    byte[] hash = md.digest(raw);
	    return Base64.getEncoder().encodeToString(hash);
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	return null;
    }
}