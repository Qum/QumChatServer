package Qum.QumServer;

import java.awt.GridBagConstraints;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import Qum.Mes.Mess;

public class okno extends JPanel {
    // implements ActionListener {

    static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    static Date date = new Date();
    private static final long serialVersionUID = 1L;
    public JTextField Tf;
    public static JTextArea Ta;
    public static String MyNick, buffnick = null;
    public static File BuffFile;
    public static FileInputStream sfsfsf;
    public static FileReader FR;
    private static File prop;
    private static Properties sett;
    private FileInputStream in;
    static int win_WIDTH = 110;
    static int win_HEIGHT = 110;

    okno() {
	super(new GridBagLayout());
	String root_papka = System.getProperty("user.home");
	File propdr = new File(root_papka, ".chatik");
	if (!propdr.exists())
	    propdr.mkdir();
	prop = new File(propdr, "chat.ini");
	Properties def_sett = new Properties();
	def_sett.put("Nick", "");
	// def_sett.put("ширина окна", "400");
	// def_sett.put("высота окна", "250");

	sett = new Properties(def_sett);

	if (prop.exists()) {
	    try {
		in = new FileInputStream(prop);
		sett.load(in);
	    } catch (IOException e) {
		System.out.println("Фреймчат : Трабла с загрузкой конфига");
	    } finally {
		try {
		    in.close();
		} catch (IOException e1) {
		    System.out
			    .println("Фреймчат : Трабла с закрытием файлстрима");
		    e1.printStackTrace();
		}
	    }
	}
	MyNick = sett.getProperty("Nick");
	if (sett.getProperty("Nick") == null)
	    greetingQuest();
	// else {
	//
	// }
	if (sett.getProperty("WIDTH") != null) {
	    win_WIDTH = Integer.parseInt(sett.getProperty("WIDTH"));
	    win_HEIGHT = Integer.parseInt(sett.getProperty("HEIGHT"));
	} else {
	    win_WIDTH = 200;
	    win_HEIGHT = 350;
	}
	if (MyNick.isEmpty())
	    greetingQuest();

	Tf = new JTextField(20);
	Ta = new JTextArea(5, 20);

	Tf.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {

		String text = Tf.getText();
		try {
		    Client.Oou.writeObject(new Mess(dateFormat.format(date)
			    + " " + MyNick, text));
		} catch (IOException e1) {
		    System.out.println("Okno Clienta");
		    e1.printStackTrace();
		}
		Tf.selectAll();
		Tf.setText(null);
		Ta.setCaretPosition(Ta.getDocument().getLength());
	    }

	});
	Ta.setEditable(false);
	JScrollPane scrollPane = new JScrollPane(Ta);

	GridBagConstraints c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;

	c.fill = GridBagConstraints.HORIZONTAL;
	add(Tf, c);

	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.weighty = 1.0;
	add(scrollPane, c);

    }

    class NiChengActList implements ActionListener {

	public void actionPerformed(ActionEvent arg0) {

	}
    }

    private static void createAndShowGUI() {

	final JFrame frame = new JFrame("Сервер");

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.add(new okno());
	frame.setVisible(true);
	frame.pack();

	JMenuBar Menubar = new JMenuBar();
	frame.setJMenuBar(Menubar);
	JMenu Menu = new JMenu("Настройка");
	Menubar.add(Menu);
	JMenuItem NickPeron = new JMenuItem("Поменять ник");
	JMenuItem ChangeServer = new JMenuItem("Поменять сервер");
	JMenuItem FileCho = new JMenuItem("Переслать файл");

	Menu.add(NickPeron);
	Menu.addSeparator();
	Menu.add(FileCho);
	Menu.addSeparator();
	Menu.add(ChangeServer);

	NickPeron.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		buffnick = JOptionPane.showInputDialog("Новый ник");
		if (buffnick.equals("Admin") || buffnick.equals("root")) {
		    JOptionPane
			    .showMessageDialog(
				    null,
				    "<html><h1><i>Это технический ник!!</i></h1><hr>&#822;н&#822;е&#822;х&#822;у&#822;й&#822; &#822;м&#822;у&#822;д&#822;р&#822;и&#822;т&#822;ь&#822; </html>",
				    "information",
				    JOptionPane.YES_NO_CANCEL_OPTION);
		    actionPerformed(e);
		} else if (buffnick.isEmpty()) {
		    JOptionPane
			    .showMessageDialog(
				    null,
				    "<html><h1><i>Ник не может быть пустым!</i></h1></html>",
				    "information",
				    JOptionPane.YES_NO_CANCEL_OPTION);
		    actionPerformed(e);
		} else
		    MyNick = buffnick;
	    }
	});
	ChangeServer.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null,
			"у нас только один сервер, пока что", "information",
			JOptionPane.YES_NO_CANCEL_OPTION);
	    }
	});

	FileCho.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		JFileChooser MisterCho;
		MisterCho = new JFileChooser();
		MisterCho.setCurrentDirectory(new File("."));
		int result = MisterCho.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    String name = MisterCho.getSelectedFile().getPath();

		    try {
			FR = new FileReader(name);
		    } catch (FileNotFoundException e1) {

			e1.printStackTrace();
		    }
		}
	    }
	});

	frame.addWindowListener(new WindowAdapter() { // сейвим пропы по
	    // закрытию окна
	    public void windowClosing(WindowEvent event) {
		sett.put("WIDTH", "" + frame.getWidth());
		sett.put("HEIGHT", "" + frame.getHeight());
		sett.put("Nick", MyNick);

		try {
		    FileOutputStream out = new FileOutputStream(prop);
		    sett.store(out, "prog options");
		} catch (IOException e) {
		    System.out
			    .println("Фреймчат : какаето хуйня с сейвом пропов");
		}
	    }

	});
	frame.setSize(win_WIDTH, win_HEIGHT);
    }

    public static void greetingQuest() {
	System.out.println("frame : Gree in");
	if (MyNick == null) {
	    System.out.println("frame : MyNick == null");
	    buffnick = JOptionPane.showInputDialog("Придумай ник");
	    if (buffnick.equals("root")) {
		System.out.println("frame : buffnick.equals root");
		JOptionPane
			.showMessageDialog(
				null,
				"<html><h1><i>Это технический ник!!</i></h1><hr>&#822;н&#822;е&#822;х&#822;у&#822;й&#822; &#822;м&#822;у&#822;д&#822;р&#822;и&#822;т&#822;ь&#822; </html>",
				"information", JOptionPane.YES_NO_CANCEL_OPTION);
		greetingQuest();
	    } else if (buffnick.isEmpty()) {
		System.out.println("frame : buffnick==null");
		JOptionPane
			.showMessageDialog(
				null,
				"<html><h1><i>Ник не может быть пустым!</i></h1></html>",
				"information", JOptionPane.YES_NO_CANCEL_OPTION);
		greetingQuest();
	    } else {
		MyNick = buffnick;
	    }
	}
    }

    public static void main(String[] args) {
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		createAndShowGUI();
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		new Thread(new Client()).start();
	    }
	});
    }
}