import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class Chess {
	static String chessBoard[][] = { { "r", "k", "b", "q", "a", "b", "k", "r" },
			{ "p", "p", "p", "p", "p", "p", "p", "p" }, { " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " }, { " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " }, { "P", "P", "P", "P", "P", "P", "P", "P" },
			{ "R", "K", "B", "Q", "A", "B", "K", "R" } };
	static int kingPositionC, kingPositionL;
	static int humanAsWhite = -1;// 1=human as white, 0=human as black
	static int searchDepth = 3;
	static final JFrame f = new JFrame("Chess Tutorial");
	static int noOfMoves = 0, rookMoves = 0;
	static JCheckBoxMenuItem tenMovesRook, sortAlphaBeta;
	static JMenuItem depth;
	static Boolean sort = true;

	public static void main(String[] args) {
		while (!"A".equals(chessBoard[kingPositionC / 8][kingPositionC % 8])) {
			kingPositionC++;
		}// get King's location
		while (!"a".equals(chessBoard[kingPositionL / 8][kingPositionL % 8])) {
			kingPositionL++;
		}

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menubar = new JMenuBar();
		JMenu options = new JMenu("Options");

		JMenu changePieceValue = new JMenu("Change PieceValue");

		JMenuItem kingValue = new JMenuItem("King");
		kingValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(kingValue);

		JMenuItem queenValue = new JMenuItem("Queen");
		queenValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(queenValue);

		JMenuItem knightValue = new JMenuItem("Knight");
		knightValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(knightValue);

		JMenuItem bishopValue = new JMenuItem("Bishop");
		bishopValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(bishopValue);

		JMenuItem rookValue = new JMenuItem("Rook");
		rookValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(rookValue);

		JMenuItem pawnValue = new JMenuItem("Pawn");
		pawnValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(pawnValue);

		changePieceValue.addSeparator();

		JMenuItem oneKnightValue = new JMenuItem("One Knight");
		oneKnightValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(oneKnightValue);

		JMenuItem oneBishopValue = new JMenuItem("One Bishop");
		oneBishopValue.addActionListener(new ValueChangeListener());
		changePieceValue.add(oneBishopValue);

		options.add(changePieceValue);

		JMenu exit = new JMenu("Exit");
		JMenuItem exitItem = new JMenuItem("Exit");

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		exit.add(exitItem);

		JMenu heuristics = new JMenu("Heuristics");

		depth = new JMenuItem("Search Depth");
		depth.addActionListener(new ValueChangeListener());
		heuristics.add(depth);

		sortAlphaBeta = new JCheckBoxMenuItem("Sort Alpha Beta for pruning");
		sortAlphaBeta.setState(true);
		sortAlphaBeta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (Chess.sortAlphaBeta.isSelected()) {
					System.out.println("Sorting for pruning in Alpha Beta");
					Chess.sort = true;
				} else if (Chess.sortAlphaBeta.isSelected() == false) {
					System.out.println("Not Sorting for pruning in Alpha Beta");
					Chess.sort = false;
				}
			}
		});
		heuristics.add(sortAlphaBeta);

		tenMovesRook = new JCheckBoxMenuItem("Save Rook for ten moves");
		tenMovesRook.setState(false);
		tenMovesRook.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (tenMovesRook.isSelected()) {
					System.out.println("Yay..." + Chess.tenMovesRook.isSelected());
					Rating.pieceValue.put("R", 10000);
					Chess.rookMoves = Chess.noOfMoves;

				} else if (tenMovesRook.isSelected() == false) {
					System.out.println("Your rook will no longer be protected");
					JOptionPane.showMessageDialog(Chess.f, "Your rook will no longer be protected");
					Chess.tenMovesRook.setSelected(false);
					Rating.pieceValue.put("R", 500);
				}

			}
		});
		heuristics.add(tenMovesRook);

		menubar.add(options);
		menubar.add(heuristics);
		menubar.add(Box.createHorizontalGlue());
		menubar.add(exit);

		f.setJMenuBar(menubar);
		GraphicalUserInterface ui = new GraphicalUserInterface();
		f.add(ui);
		f.setSize(656, 699);
		f.setVisible(true);

		/*
		 * int messageType = JOptionPane.INFORMATION_MESSAGE; String answer =
		 * JOptionPane.showInputDialog(f, "Please enter a global search depth",
		 * "Search Depth", messageType); System.out.println("Depth: " + answer);
		 * searchDepth = Integer.parseInt(answer);
		 */

		// System.out.println(sortMoves(posibleMoves()));
		Object[] option = { "Computer", "Human" };
		humanAsWhite = JOptionPane.showOptionDialog(f, "Who should play as white?", "Choose first player",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
		if (humanAsWhite == 0) {
			long startTime = System.currentTimeMillis();
			makeMove(alphaBetaEvaluation(searchDepth, 1000000, -1000000, "", 0));
			long endTime = System.currentTimeMillis();
			System.out.println("That took " + (endTime - startTime) + " milliseconds");
			flipBoard();
			f.repaint();
		}
		// makeMove("7655 ");
		// undoMove("7655 ");
		/*
		 * for (int i = 0; i < 8; i++) {
		 * System.out.println(Arrays.toString(chessBoard[i])); }
		 */
	}

	public static String alphaBetaEvaluation(int depth, int beta, int alpha, String move, int player) {
		String list = possibleMoves();
		if (depth == 0 || list.length() == 0) {
			return move + (Rating.rating(list.length(), depth) * (player * 2 - 1));
		}
		if (sort) {
			list = sortMoves(list);
		}
		player = 1 - player;// either 1 or 0
		for (int i = 0; i < list.length(); i += 5) {
			makeMove(list.substring(i, i + 5));
			flipBoard();
			String returnString = alphaBetaEvaluation(depth - 1, beta, alpha, list.substring(i, i + 5), player);
			int value = Integer.valueOf(returnString.substring(5));
			flipBoard();
			undoMove(list.substring(i, i + 5));
			if (player == 0) {
				if (value <= beta) {
					beta = value;
					if (depth == searchDepth) {
						move = returnString.substring(0, 5);
					}
				}
			} else {
				if (value > alpha) {
					alpha = value;
					if (depth == searchDepth) {
						move = returnString.substring(0, 5);
					}
				}
			}
			if (alpha >= beta) {
				if (player == 0) {
					return move + beta;
				} else {
					return move + alpha;
				}
			}
		}
		if (player == 0) {
			return move + beta;
		} else {
			return move + alpha;
		}
	}

	public static void flipBoard() {
		String temp;
		for (int i = 0; i < 32; i++) {
			int r = i / 8, c = i % 8;
			if (Character.isUpperCase(chessBoard[r][c].charAt(0))) {
				temp = chessBoard[r][c].toLowerCase();
			} else {
				temp = chessBoard[r][c].toUpperCase();
			}
			if (Character.isUpperCase(chessBoard[7 - r][7 - c].charAt(0))) {
				chessBoard[r][c] = chessBoard[7 - r][7 - c].toLowerCase();
			} else {
				chessBoard[r][c] = chessBoard[7 - r][7 - c].toUpperCase();
			}
			chessBoard[7 - r][7 - c] = temp;
		}
		int kingTemp = kingPositionC;
		kingPositionC = 63 - kingPositionL;
		kingPositionL = 63 - kingTemp;
	}

	public static void makeMove(String move) {
		if (move.charAt(4) != 'P') {
			chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))] = chessBoard[Character
					.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))];
			chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))] = " ";
			if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move
					.charAt(3))])) {
				kingPositionC = 8 * Character.getNumericValue(move.charAt(2))
						+ Character.getNumericValue(move.charAt(3));
			}
		} else {
			// if pawn promotion
			chessBoard[1][Character.getNumericValue(move.charAt(0))] = " ";
			chessBoard[0][Character.getNumericValue(move.charAt(1))] = String.valueOf(move.charAt(3));
		}
	}

	public static void undoMove(String move) {
		if (move.charAt(4) != 'P') {
			chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move.charAt(1))] = chessBoard[Character
					.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))];
			chessBoard[Character.getNumericValue(move.charAt(2))][Character.getNumericValue(move.charAt(3))] = String
					.valueOf(move.charAt(4));
			if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(0))][Character.getNumericValue(move
					.charAt(1))])) {
				kingPositionC = 8 * Character.getNumericValue(move.charAt(0))
						+ Character.getNumericValue(move.charAt(1));
			}
		} else {
			// if pawn promotion
			chessBoard[1][Character.getNumericValue(move.charAt(0))] = "P";
			chessBoard[0][Character.getNumericValue(move.charAt(1))] = String.valueOf(move.charAt(2));
		}
	}

	public static String possibleMoves() {
		String list = "";
		for (int i = 0; i < 64; i++) {
			switch (chessBoard[i / 8][i % 8]) {
			case "P":
				list += Moves.possiblePawnMoves(chessBoard, i);
				break;
			case "R":
				list += Moves.possibleRookMoves(chessBoard, i);
				break;
			case "K":
				list += Moves.possibleKnightMoves(chessBoard, i);
				break;
			case "B":
				list += Moves.possibleBishopMoves(chessBoard, i);
				break;
			case "Q":
				list += Moves.possibleQueenMoves(chessBoard, i);
				break;
			case "A":
				list += Moves.possibleKingMoves(chessBoard, i);
				break;
			}
		}
		return list;// x1,y1,x2,y2,captured piece
	}

	public static String sortMoves(String list) {
		int[] score = new int[list.length() / 5];
		for (int i = 0; i < list.length(); i += 5) {
			makeMove(list.substring(i, i + 5));
			score[i / 5] = -Rating.rating(-1, 0);
			undoMove(list.substring(i, i + 5));
		}
		String newListA = "", newListB = list;
		for (int i = 0; i < Math.min(6, list.length() / 5); i++) {// first few
																	// moves
																	// only
			int max = -1000000, maxLocation = 0;
			for (int j = 0; j < list.length() / 5; j++) {
				if (score[j] > max) {
					max = score[j];
					maxLocation = j;
				}
			}
			score[maxLocation] = -1000000;
			newListA += list.substring(maxLocation * 5, maxLocation * 5 + 5);
			newListB = newListB.replace(list.substring(maxLocation * 5, maxLocation * 5 + 5), "");
		}
		return newListA + newListB;
	}

}