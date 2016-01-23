package qum.chatServer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import qum.messageClass.Mess;

public class Client implements Runnable {

    public static Socket Sock;
    public static ObjectInputStream Oin;
    public static ObjectOutputStream Oou;
    public static BufferedOutputStream Bou;
    public static InputStream ProstIn;
    static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    static Date date = new Date();
    public Mess BuffMesObj;
    public static ArrayList<Mess> In_Mess = new ArrayList<Mess>();
    static int posled, posledOtpr = 0;
    public final static String newline = "\n";

    public void run() {

	System.out.println(Thread.currentThread()
		+ "SockManager : sockStart enter in run");
	GetSocket();
	GetStreams();
	Work();
    }

    private void GetStreams() {
	try {
	    Oou = new ObjectOutputStream(Sock.getOutputStream());
	    Oin = new ObjectInputStream(Sock.getInputStream());
	    Bou = new BufferedOutputStream(Sock.getOutputStream());
	    ProstIn = Sock.getInputStream();
	} catch (IOException e) {

	    e.printStackTrace();
	}
    }

    private void Work() {
	try {
	    Oou.writeObject(new Mess("root", "K95NM567FBEKL3278S5N3",
		    ClientUnit.AUTH_REQUEST));
	    while (true) {
		BuffMesObj = (Mess) Oin.readObject();
		UserWindow.Ta.append(BuffMesObj.getValue1() + " : "
			+ BuffMesObj.getValue2() + newline);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (Sock != null)
		    Sock.close();
	    } catch (IOException e) {

		e.printStackTrace();
	    }
	}
    }

    private Socket GetSocket() {
		try {
			Sock = new Socket(InetAddress.getByName("109.87.60.178"), 9090);
			return Sock;
		} catch (IOException e) {
			UserWindow.Ta.append(("SYS : connection lost !!"));
			try {
				Sock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (NullPointerException e1) {
				
			}
			e.printStackTrace();
		}
		return null;
	}
}