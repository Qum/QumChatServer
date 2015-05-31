package Qum.QumServer;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DeamonOutMess implements Runnable {
	int posled = 0;
	private ObjectOutputStream Oou;

	DeamonOutMess(ObjectOutputStream O) throws IOException {
		Oou = O;
		// new ObjectOutputStream(s.getOutputStream());
	}

	public void run() {
		if (ChatServer.MessList.size() > 10) {
			posled = ChatServer.MessList.size() - 10;
		}
		posled = ChatServer.MessList.size();
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (posled < ChatServer.MessList.size()
					&& (0 == ChatServer.MessList.get(posled).getServiceCode())) {

				// Mess Me = Serv.MessList.get(posled);
				// if (Me.Meseg.contains(" >>>>>>>>> "))
				// Me.Meseg += " " + getIP TO DO ;
				try {
					Oou.writeObject(ChatServer.MessList.get(posled));
				} catch (IOException ex) {
					// System.out.println("DemIn " + Me.Nick +
					// " - Трабла с исходящими");
				}
				posled++;
			}

		}

	}
}
