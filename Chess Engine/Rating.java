import java.util.HashMap;
import java.util.Map;

public class Rating {

	static int pawnBoard[][] = {// attribute to
			// http://chessprogramming.wikispaces.com/Simplified+evaluation+function
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 50, 50, 50, 50, 50, 50, 50, 50 }, { 10, 10, 20, 30, 30, 20, 10, 10 },
			{ 5, 5, 10, 25, 25, 10, 5, 5 }, { 0, 0, 0, 20, 20, 0, 0, 0 }, { 5, -5, -10, 0, 0, -10, -5, 5 },
			{ 5, 10, 10, -20, -20, 10, 10, 5 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };
	static int rookBoard[][] = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 5, 10, 10, 10, 10, 10, 10, 5 },
			{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 },
			{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 }, { 0, 0, 0, 5, 5, 0, 0, 0 } };
	static int knightBoard[][] = { { -50, -40, -30, -30, -30, -30, -40, -50 }, { -40, -20, 0, 0, 0, 0, -20, -40 },
			{ -30, 0, 10, 15, 15, 10, 0, -30 }, { -30, 5, 15, 20, 20, 15, 5, -30 }, { -30, 0, 15, 20, 20, 15, 0, -30 },
			{ -30, 5, 10, 15, 15, 10, 5, -30 }, { -40, -20, 0, 5, 5, 0, -20, -40 },
			{ -50, -40, -30, -30, -30, -30, -40, -50 } };
	static int bishopBoard[][] = { { -20, -10, -10, -10, -10, -10, -10, -20 }, { -10, 0, 0, 0, 0, 0, 0, -10 },
			{ -10, 0, 5, 10, 10, 5, 0, -10 }, { -10, 5, 5, 10, 10, 5, 5, -10 }, { -10, 0, 10, 10, 10, 10, 0, -10 },
			{ -10, 10, 10, 10, 10, 10, 10, -10 }, { -10, 5, 0, 0, 0, 0, 5, -10 },
			{ -20, -10, -10, -10, -10, -10, -10, -20 } };
	static int queenBoard[][] = { { -20, -10, -10, -5, -5, -10, -10, -20 }, { -10, 0, 0, 0, 0, 0, 0, -10 },
			{ -10, 0, 5, 5, 5, 5, 0, -10 }, { -5, 0, 5, 5, 5, 5, 0, -5 }, { 0, 0, 5, 5, 5, 5, 0, -5 },
			{ -10, 5, 5, 5, 5, 5, 0, -10 }, { -10, 0, 5, 0, 0, 0, 0, -10 }, { -20, -10, -10, -5, -5, -10, -10, -20 } };
	static int kingMidBoard[][] = { { -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 }, { -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 }, { -20, -30, -30, -40, -40, -30, -30, -20 },
			{ -10, -20, -20, -20, -20, -20, -20, -10 }, { 20, 20, 0, 0, 0, 0, 20, 20 },
			{ 20, 30, 10, 0, 0, 10, 30, 20 } };
	static int kingEndBoard[][] = { { -50, -40, -30, -20, -20, -30, -40, -50 }, { -30, -20, -10, 0, 0, -10, -20, -30 },
			{ -30, -10, 20, 30, 30, 20, -10, -30 }, { -30, -10, 30, 40, 40, 30, -10, -30 },
			{ -30, -10, 30, 40, 40, 30, -10, -30 }, { -30, -10, 20, 30, 30, 20, -10, -30 },
			{ -30, -30, 0, 0, 0, 0, -30, -30 }, { -50, -30, -30, -30, -30, -30, -30, -50 } };

	static Map<String, Integer> pieceValue = new HashMap<String, Integer>();

	static {
		pieceValue.put("A", 20000);
		pieceValue.put("Q", 900);
		pieceValue.put("R", 500);
		pieceValue.put("P", 100);
		pieceValue.put("B2", 330);
		pieceValue.put("B", 250);
		pieceValue.put("K2", 320);
		pieceValue.put("K", 200);
	}

	public static int rating(int list, int depth) {
		int counter = 0, material = rateMaterial();
		counter += rateAttack();
		counter += material;
		counter += rateMoveablitly(list, depth, material);
		counter += ratePositional(material);
		Chess.flipBoard();
		material = rateMaterial();
		counter -= rateAttack();
		counter -= material;
		counter -= rateMoveablitly(list, depth, material);
		counter -= ratePositional(material);
		Chess.flipBoard();
		return -(counter + depth * 50);
	}

	public static int rateAttack() {
		int counter = 0;
		int tempPositionC = Chess.kingPositionC;
		for (int i = 0; i < 64; i++) {
			switch (Chess.chessBoard[i / 8][i % 8]) {
			case "P": {
				Chess.kingPositionC = i;
				if (!Moves.isKingSafe(Chess.chessBoard)) {
					counter -= 64;
				}
			}
				break;
			case "R": {
				Chess.kingPositionC = i;
				if (!Moves.isKingSafe(Chess.chessBoard)) {
					counter -= 500;
				}
			}
				break;
			case "K": {
				Chess.kingPositionC = i;
				if (!Moves.isKingSafe(Chess.chessBoard)) {
					counter -= 300;
				}
			}
				break;
			case "B": {
				Chess.kingPositionC = i;
				if (!Moves.isKingSafe(Chess.chessBoard)) {
					counter -= 300;
				}
			}
				break;
			case "Q": {
				Chess.kingPositionC = i;
				if (!Moves.isKingSafe(Chess.chessBoard)) {
					counter -= 900;
				}
			}
				break;
			}
		}
		Chess.kingPositionC = tempPositionC;
		if (!Moves.isKingSafe(Chess.chessBoard)) {
			counter -= 200;
		}
		return counter / 2;
	}

	public static int rateMaterial() {
		int counter = 0, bishopCounter = 0, knightCounter = 0;
		for (int i = 0; i < 64; i++) {
			switch (Chess.chessBoard[i / 8][i % 8]) {
			case "P":
				counter += pieceValue.get("P");
				break;
			case "R":
				counter += pieceValue.get("R");
				break;
			case "K":
				counter += pieceValue.get("K");
				break;
			case "B":
				bishopCounter += 1;
				break;
			case "Q":
				knightCounter += 1;
				break;
			}
		}
		if (bishopCounter >= 2) {
			counter += pieceValue.get("B2") * bishopCounter;
		} else {
			if (bishopCounter == 1) {
				counter += pieceValue.get("B");
			}
		}
		if (knightCounter >= 2) {
			counter += pieceValue.get("K2") * knightCounter;
		} else {
			if (knightCounter == 1) {
				counter += pieceValue.get("K");
			}
		}
		return counter;
	}

	public static int rateMoveablitly(int listLength, int depth, int material) {
		int counter = 0;
		counter += listLength;// 5 pointer per valid move
		if (listLength == 0) {// current side is in checkmate or stalemate
			if (!Moves.isKingSafe(Chess.chessBoard)) {// if checkmate
				counter += -200000 * depth;
			} else {// if stalemate
				counter += -150000 * depth;
			}
		}
		return counter;
	}

	public static int ratePositional(int material) {
		int counter = 0;
		for (int i = 0; i < 64; i++) {
			switch (Chess.chessBoard[i / 8][i % 8]) {
			case "P":
				counter += pawnBoard[i / 8][i % 8];
				break;
			case "R":
				counter += rookBoard[i / 8][i % 8];
				break;
			case "K":
				counter += knightBoard[i / 8][i % 8];
				break;
			case "B":
				counter += bishopBoard[i / 8][i % 8];
				break;
			case "Q":
				counter += queenBoard[i / 8][i % 8];
				break;
			case "A":
				if (material >= 1750) {
					counter += kingMidBoard[i / 8][i % 8];
					counter += Moves.possibleKingMoves(Chess.chessBoard, Chess.kingPositionC).length() * 10;
				} else {
					counter += kingEndBoard[i / 8][i % 8];
					counter += Moves.possibleKingMoves(Chess.chessBoard, Chess.kingPositionC).length() * 30;
				}
				break;
			}
		}
		return counter;
	}
}