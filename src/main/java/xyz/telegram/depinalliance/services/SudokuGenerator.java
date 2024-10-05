package xyz.telegram.depinalliance.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author holden on 04-Oct-2024
 */
public class SudokuGenerator {
  private static final int SIZE = 9;
  private static final int SUBGRID = 3;

  public int[][] generateSudoku() {
    int[][] board = new int[SIZE][SIZE];
    fillDiagonalRegions(board);
    fillRemaining(board, 0, SUBGRID);
    return board;
  }

  private void fillDiagonalRegions(int[][] board) {
    for (int i = 0; i < SIZE; i += SUBGRID) {
      fillSubgrid(board, i, i);
    }
  }

  private void fillSubgrid(int[][] board, int row, int col) {
    List<Integer> numbers = getRandomNumbers();
    for (int i = 0; i < SUBGRID; i++) {
      for (int j = 0; j < SUBGRID; j++) {
        board[row + i][col + j] = numbers.remove(0);
      }
    }
  }

  private List<Integer> getRandomNumbers() {
    List<Integer> numbers = new ArrayList<>();
    for (int i = 1; i <= SIZE; i++) {
      numbers.add(i);
    }
    Collections.shuffle(numbers);
    return numbers;
  }

  private boolean fillRemaining(int[][] board, int row, int col) {
    if (row >= SIZE && col >= SIZE)
      return true;

    if (col >= SIZE) {
      row++;
      col = 0;
    }

    if (row < SUBGRID) {
      if (col < SUBGRID) {
        col = SUBGRID;
      }
    } else if (row < SIZE - SUBGRID) {
      if (col == (row / SUBGRID) * SUBGRID) {
        col += SUBGRID;
      }
    } else {
      if (col == SIZE - SUBGRID) {
        row++;
        col = 0;
        if (row >= SIZE)
          return true;
      }
    }

    for (int num = 1; num <= SIZE; num++) {
      if (isSafe(board, row, col, num)) {
        board[row][col] = num;
        if (fillRemaining(board, row, col + 1)) {
          return true;
        }
        board[row][col] = 0;
      }
    }
    return false;
  }

  private boolean isSafe(int[][] board, int row, int col, int num) {
    return !usedInRow(board, row, num) && !usedInCol(board, col, num) && !usedInBox(board, row - row % SUBGRID,
      col - col % SUBGRID, num);
  }

  private boolean usedInRow(int[][] board, int row, int num) {
    for (int col = 0; col < SIZE; col++) {
      if (board[row][col] == num) {
        return true;
      }
    }
    return false;
  }

  private boolean usedInCol(int[][] board, int col, int num) {
    for (int row = 0; row < SIZE; row++) {
      if (board[row][col] == num) {
        return true;
      }
    }
    return false;
  }

  private boolean usedInBox(int[][] board, int boxStartRow, int boxStartCol, int num) {
    for (int row = 0; row < SUBGRID; row++) {
      for (int col = 0; col < SUBGRID; col++) {
        if (board[boxStartRow + row][boxStartCol + col] == num) {
          return true;
        }
      }
    }
    return false;
  }

  public void removeDigits(int[][] board, int numToRemove) {
    Random random = new Random();
    while (numToRemove > 0) {
      int row = random.nextInt(SIZE);
      int col = random.nextInt(SIZE);
      if (board[row][col] != 0) {
        board[row][col] = 0;
        numToRemove--;
      }
    }
  }

  public List<String> printBoard(int[][] board) {
    List<String> lst = new ArrayList<>();
    for (int row = 0; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        lst.add(board[row][col] + "");
      }
    }
    return lst;
  }

  public static void main(String[] args) {

    for (int i = 1; i <= 1000; i++) {
      SudokuGenerator generator = new SudokuGenerator();
      int[][] board = generator.generateSudoku();
      String insert = "(" + i + ",";
      List<String> lst = generator.printBoard(board);
      insert += "'" + String.join("", lst) + "',";
      generator.removeDigits(board, 40);
      lst = generator.printBoard(board);
      insert += "'" + String.join("", lst) + "'";
      insert += "),";
      System.out.println(insert);
    }
  }
}
