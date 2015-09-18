
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class ValueChangeListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent event) {
		if (((JMenuItem) event.getSource()).getActionCommand().equals(Chess.depth.getActionCommand())) {
			String answer = JOptionPane.showInputDialog(Chess.f,
					"Please enter a value for search depth, messageType");
			System.out.println("Depth" + answer);
			Chess.searchDepth = Integer.parseInt(answer);
		} else {
			int messageType = JOptionPane.INFORMATION_MESSAGE;
			String answer = JOptionPane.showInputDialog(Chess.f,
					"Please enter a value for " + event.getActionCommand(), "Weight", messageType);
			System.out.println("Weight" + answer);
			Integer value = Integer.parseInt(answer);
			updatePieceValue(event.getActionCommand(), value);
		}
	}

	private void updatePieceValue(String actionCommand, Integer value) {
		if (actionCommand.equals("Queen")) {
			Rating.pieceValue.put("Q", value);
		} else if (actionCommand.equals("King")) {
			Rating.pieceValue.put("A", value);
		} else if (actionCommand.equals("Pawn")) {
			Rating.pieceValue.put("P", value);
		} else if (actionCommand.equals("Rook")) {
			Rating.pieceValue.put("R", value);
		} else if (actionCommand.equals("Knight")) {
			Rating.pieceValue.put("K2", value);
		} else if (actionCommand.equals("Bishop")) {
			Rating.pieceValue.put("B2", value);
		} else if (actionCommand.equals("One Knight")) {
			Rating.pieceValue.put("K", value);
		} else if (actionCommand.equals("One Bishop")) {
			Rating.pieceValue.put("B", value);
		}
		System.out.println("Rating:" + Rating.pieceValue);
	}
}
