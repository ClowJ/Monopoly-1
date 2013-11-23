/**
 * CSCI 205 - Software Design and Engineering
 * Name: Arjuna Kankipati
 * Semester: Fall 2013
 * Work: FinalProject
 * Created: Nov 15, 2013, 10:13:41 AM
 */
package gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.Border;

/**
 * @author ajrk001
 * Class that will represent the main frame of the board. 
 */
public class MMainFrame extends JFrame
{
	private MBoardPanel theBoard;
	private MControlPanel control;
	Border empty = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	
	/**
	 * Constructor for the main frame, will initalize the frame and set attributes
	 */
	public MMainFrame()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Monopoly");
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		((JComponent) getContentPane()).setBorder(empty);
		
		MBoardPanel board = new MBoardPanel();
		this.getContentPane().add(board, BorderLayout.CENTER);
		this.theBoard = board;
		
		MControlPanel controlT = new MControlPanel();
		this.getContentPane().add(controlT, BorderLayout.WEST);
		this.control = controlT;
		
		this.pack();
		
	}
}