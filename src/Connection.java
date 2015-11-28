import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Connection {

	public boolean isHost;
	public String connect;

	private int port;
	private String ip;

	public void Connect() {
		if (!isHost) {
			String[] proc = connect.split(":");
			if (proc.length == 1) {
				ip = proc[0];
				port = 782;
			} else {
				ip = proc[0];
				port = Integer.parseInt(proc[1]);
			}

			// CLIENT
			try {
				Socket socket = new Socket(ip, port);
				int i = 0;
				DataInputStream in = new DataInputStream(
						socket.getInputStream());
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				JFrame frame = new JFrame("Connected to " + ip);
				Canvas c = new Canvas();
				c.setPreferredSize(new Dimension(1280, 720));
				frame.add(c);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				
				while (true) {
					int size = 0;
					try {
						size = in.readInt();
					} catch (SocketException se) {
						System.out.println("Out");
						JOptionPane.showMessageDialog(null, "Host refused connection. Closing...", "Disconnected from " + ip, JOptionPane.WARNING_MESSAGE);
						System.exit(0);
					}
					byte[] rec = new byte[size];
					in.readFully(rec, 0, size);
					ByteArrayInputStream is = new ByteArrayInputStream(rec);
					img = ImageIO.read(is);
					frame.repaint();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			// END CLIENT

		} else {
			if (connect.equals("")) {
				port = 782;
			} else {
				port = Integer.parseInt(connect);
			}
			int gui = JOptionPane.showOptionDialog(null, "Enable GUI?",
					"", JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, new Object[] {"Yes", "No"}, (Object) "Yes");
			if (gui == 0) {
				JFrame frame = new JFrame("Remote Control | Listening on port " + port);
				frame.setSize(new Dimension(425, 0));
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				
			}
			// SERVER
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				while (true) {
					Socket socket = serverSocket.accept();
					System.out.println("Client: " + socket.getInetAddress()
							+ " has connected.");
					ServerThread st = new ServerThread(socket);
					st.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			// END SERVER

		}
	}

	BufferedImage img;

	public class Canvas extends JPanel {

		public void paintComponent(Graphics g) {

			super.paintComponent(g); // Dont Know
			
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			if (img != null) {
				int height = img.getHeight();
				int width = img.getWidth();
				double ratio = (width * 1.0)/(height * 1.0);
				int panelHeight = this.getHeight();
				int panelWidth = this.getWidth();
				int finalHeight = 0;
				int finalWidth = 0;
				finalWidth = panelWidth;
				finalHeight = (int) (panelWidth / ratio);
				if (((panelWidth * 1.0) / (panelHeight * 1.0)) > ratio) {
					finalHeight = panelHeight;
					finalWidth = (int) (panelHeight * ratio);
				}
				int x = (panelWidth / 2) - (finalWidth / 2);
				int y = (panelHeight / 2) - (finalHeight / 2);
				g.drawImage(img, x, y, finalWidth, finalHeight, this);
			}

		}

	}

	public class ServerThread extends Thread {

		public Socket socket;

		public ServerThread(Socket s) {
			this.socket = s;
		}

		public void run() {
			try {
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				DataInputStream in = new DataInputStream(
						socket.getInputStream());
				Robot r = new Robot();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				while (true) {
					BufferedImage screen = r.createScreenCapture(new Rectangle(
							Toolkit.getDefaultToolkit().getScreenSize()));
					os.reset();
					ImageIO.write(screen, "JPEG", os);
					byte[] send = os.toByteArray();
					out.writeInt(send.length);
					out.write(send);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
