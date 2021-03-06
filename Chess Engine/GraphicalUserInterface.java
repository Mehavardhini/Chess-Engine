import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GraphicalUserInterface extends JPanel implements MouseListener, MouseMotionListener {
	static int mouseX, mouseY, newMouseX, newMouseY;
	static int squareSize = 80;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.yellow);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		for (int i = 0; i < 64; i += 2) {
			g.setColor(Color.white);
			g.fillRect((i % 8 + (i / 8) % 2) * squareSize, (i / 8) * squareSize, squareSize, squareSize);
			g.setColor(Color.gray);
			g.fillRect(((i + 1) % 8 - ((i + 1) / 8) % 2) * squareSize, ((i + 1) / 8) * squareSize, squareSize,
					squareSize);
		}
		Image chessPiecesImage;
		chessPiecesImage = new ImageIcon("ChessPieces.png").getImage();
		for (int i = 0; i < 64; i++) {
			int j = -1, k = -1;
			switch (Chess.chessBoard[i / 8][i % 8]) {
			case "P":
				j = 5;
				k = 0;
				break;
			case "p":
				j = 5;
				k = 1;
				break;
			case "R":
				j = 2;
				k = 0;
				break;
			case "r":
				j = 2;
				k = 1;
				break;
			case "K":
				j = 4;
				k = 0;
				break;
			case "k":
				j = 4;
				k = 1;
				break;
			case "B":
				j = 3;
				k = 0;
				break;
			case "b":
				j = 3;
				k = 1;
				break;
			case "Q":
				j = 1;
				k = 0;
				break;
			case "q":
				j = 1;
				k = 1;
				break;
			case "A":
				j = 0;
				k = 0;
				break;
			case "a":
				j = 0;
				k = 1;
				break;
			}
			if (j != -1 && k != -1) {
				g.drawImage(chessPiecesImage, (i % 8) * squareSize, (i / 8) * squareSize, (i % 8 + 1) * squareSize,
						(i / 8 + 1) * squareSize, j * 64, k * 64, (j + 1) * 64, (k + 1) * 64, this);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getX() < 8 * squareSize && e.getY() < 8 * squareSize) {
			// if inside the board
			mouseX = e.getX();
			mouseY = e.getY();
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getX() < 8 * squareSize && e.getY() < 8 * squareSize) {
			// if inside the board
			newMouseX = e.getX();
			newMouseY = e.getY();
			if (e.getButton() == MouseEvent.BUTTON1) {
				String dragMove;
				if (newMouseY / squareSize == 0 && mouseY / squareSize == 1
						&& "P".equals(Chess.chessBoard[mouseY / squareSize][mouseX / squareSize])) {
					// pawn promotion
					dragMove = "" + mouseX / squareSize + newMouseX / squareSize
							+ Chess.chessBoard[newMouseY / squareSize][newMouseX / squareSize] + "QP";
				} else {
					// regular move
					dragMove = "" + mouseY / squareSize + mouseX / squareSize + newMouseY / squareSize + newMouseX
							/ squareSize + Chess.chessBoard[newMouseY / squareSize][newMouseX / squareSize];
				}
				String userPosibilities = Chess.possibleMoves();
				if (userPosibilities.replaceAll(dragMove, "").length() < userPosibilities.length()) {
					// if valid move
					repaint();
					Chess.makeMove(dragMove);
					Chess.flipBoard();
					Chess.makeMove(Chess.alphaBetaEvaluation(Chess.searchDepth, 1000000, -1000000,
							"", 0));
					Chess.noOfMoves++;
					if (Chess.tenMovesRook.isSelected() && Chess.noOfMoves == Chess.rookMoves + 10) {
						System.out.println("Your rook will no longer be protected");
						JOptionPane.showMessageDialog(Chess.f, "Your rook will no longer be protected");
						Chess.tenMovesRook.setSelected(false);
						Rating.pieceValue.put("R", 500);
					}
					Chess.flipBoard();
					// repaint();
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}