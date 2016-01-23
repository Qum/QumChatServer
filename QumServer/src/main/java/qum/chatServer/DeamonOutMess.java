package qum.chatServer;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DeamonOutMess implements Runnable {
    int posled = 0;
    private ObjectOutputStream Oou;

    DeamonOutMess(ObjectOutputStream O) throws IOException {
	Oou = O;
    }

    public void run() {
	if (ChatServer.incomingMessagesList.size() > 10) {
	    posled = ChatServer.incomingMessagesList.size() - 10;
	} else {
	    posled = ChatServer.incomingMessagesList.size();
	}
	while (true) {
	    try {
		Thread.sleep(50);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    while (posled < ChatServer.incomingMessagesList.size()) {
		try {
		    if (ChatServer.incomingMessagesList.get(posled).getServiceCode() == 0) {
			Oou.writeObject(ChatServer.incomingMessagesList.get(posled));
		    }
		} catch (IOException ex) {
		}
		posled++;
	    }
	}
    }
}
