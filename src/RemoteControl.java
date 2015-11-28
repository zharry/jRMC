import javax.swing.JOptionPane;


public class RemoteControl {
	
	public static final String VERSION = "0.01 Alpha";

	public static void main(String[] args) {
		
		Object[] welcomeOptions = { "Remote Control", "Share Screen" };
		int select = JOptionPane.showOptionDialog(null, "Share Screen: Host a session for others to join\nRemote Control: Join a session",
				"Remote Control v." + VERSION, JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, welcomeOptions, welcomeOptions[0]);

		Connection con = new Connection();
		if (select == 1) {
			con.isHost = true;
			con.connect = JOptionPane.showInputDialog(null, "Listen on Port:", "Host Screen", JOptionPane.QUESTION_MESSAGE);
			con.Connect();
		} else if (select == 0)  {
			con.isHost = false;
			con.connect = JOptionPane.showInputDialog(null, "Host IP:Port", "Connect to...", JOptionPane.QUESTION_MESSAGE);
			con.Connect();
		} else {
			System.exit(0);
		}

	}

}
