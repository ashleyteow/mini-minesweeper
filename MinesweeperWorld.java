import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.*;


public class MinesweeperWorld extends World {
  public static int WIDTH = 1200;
  public static int HEIGHT = 640;
  public static int MINES = 99;

  ArrayList<ArrayList<Cell>> grid;
  int numMinesFound;

  // for testing purposes
  MinesweeperWorld(ArrayList<ArrayList<Cell>> cells) {
    this.grid = cells;
    this.numMinesFound = 0;
  }

  // to play the game
  MinesweeperWorld() {
    this.grid = this.buildGrid(16, 30);
    this.buildNeighborsList();
    this.populateMines();
    this.numMinesFound = 0;
  }

  // builds the array list of array lists of cells that represents the game board grid
  public ArrayList<ArrayList<Cell>> buildGrid(int rows, int cols) {
    ArrayList<ArrayList<Cell>> grid = new ArrayList<ArrayList<Cell>>();
    for (int rowCounter = 0; rowCounter < rows; rowCounter++) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int colCounter = 0; colCounter < cols; colCounter++) {
        row.add(new Cell());
      }
      grid.add(row);
    }
    return grid;
  }

  // builds each cell's neighbors list based on the index of their position on the grid
  public void buildNeighborsList() {
    Cell c;
    ArrayList<Cell> neighbors;
    for (int rowCounter = 0; rowCounter < grid.size(); rowCounter++) {
      for (int colCounter = 0; colCounter < grid.get(0).size(); colCounter++) {
        c = grid.get(rowCounter).get(colCounter);
        neighbors = this.buildNeighborsListHelp(rowCounter, colCounter);
        for (Cell neighbor : neighbors) {
          c.updateNeighbors(neighbor);
        }
      }
    }
  }

  // depending on which index a cell is in the game board grid
  // , this function returns the neighbors list accordingly
  public ArrayList<Cell> buildNeighborsListHelp(int rowIdx, int colIdx) {
    // top left corner
    if (rowIdx == 0 && colIdx == 0) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx).get(colIdx + 1),
          this.grid.get(rowIdx + 1).get(colIdx), this.grid.get(rowIdx + 1).get(colIdx + 1)));
    }
    // top right corner
    else if (rowIdx == 0 && colIdx == this.grid.get(rowIdx).size() - 1) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx).get(colIdx - 1),
          this.grid.get(rowIdx + 1).get(colIdx - 1), this.grid.get(rowIdx + 1).get(colIdx)));
    }
    // bottom left corner
    else if (rowIdx == this.grid.size() - 1 && colIdx == 0) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx - 1).get(colIdx),
          this.grid.get(rowIdx - 1).get(colIdx + 1), this.grid.get(rowIdx).get(colIdx + 1)));
    }
    // bottom right corner
    else if (rowIdx == this.grid.size() - 1 && colIdx == this.grid.get(rowIdx).size() - 1) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx - 1).get(colIdx),
          this.grid.get(rowIdx - 1).get(colIdx - 1), this.grid.get(rowIdx).get(colIdx - 1)));
    }
    // top middle
    else if (rowIdx == 0 && (colIdx > 0 && colIdx < this.grid.get(rowIdx).size())) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx).get(colIdx - 1),
          this.grid.get(rowIdx + 1).get(colIdx - 1), this.grid.get(rowIdx + 1).get(colIdx),
          this.grid.get(rowIdx + 1).get(colIdx + 1), this.grid.get(rowIdx).get(colIdx + 1)));
    }
    // left middle
    else if ((rowIdx > 0 && rowIdx < this.grid.size()) && colIdx == 0) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx - 1).get(colIdx),
          this.grid.get(rowIdx).get(colIdx + 1), this.grid.get(rowIdx + 1).get(colIdx + 1),
          this.grid.get(rowIdx + 1).get(colIdx), this.grid.get(rowIdx - 1).get(colIdx + 1)));
    }
    // right middle
    else if ((rowIdx > 0 && rowIdx < this.grid.size())
        && colIdx == this.grid.get(rowIdx).size() - 1) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx - 1).get(colIdx),
          this.grid.get(rowIdx - 1).get(colIdx - 1), this.grid.get(rowIdx).get(colIdx - 1),
          this.grid.get(rowIdx + 1).get(colIdx - 1), this.grid.get(rowIdx - 1).get(colIdx)));
    }
    // bottom middle
    else if (rowIdx == this.grid.size() - 1 && (colIdx > 0
        && colIdx < this.grid.get(rowIdx).size())) {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx).get(colIdx - 1),
          this.grid.get(rowIdx - 1).get(colIdx - 1), this.grid.get(rowIdx - 1).get(colIdx),
          this.grid.get(rowIdx - 1).get(colIdx + 1), this.grid.get(rowIdx).get(colIdx + 1)));
    }
    // any cell in the middle
    else {
      return new ArrayList<Cell>(Arrays.asList(this.grid.get(rowIdx).get(colIdx - 1),
          this.grid.get(rowIdx - 1).get(colIdx - 1), this.grid.get(rowIdx - 1).get(colIdx),
          this.grid.get(rowIdx - 1).get(colIdx + 1), this.grid.get(rowIdx).get(colIdx + 1),
          this.grid.get(rowIdx + 1).get(colIdx + 1), this.grid.get(rowIdx + 1).get(colIdx),
          this.grid.get(rowIdx + 1).get(colIdx - 1)));
    }
  }

  // randomly populates bombs on various cells on the game board grid
  public void populateMines() {
    Random col = new Random();
    Random row = new Random();
    int minesSoFar = 0;
    while (minesSoFar < MinesweeperWorld.MINES + 1) {
      int rowIdx = row.nextInt(grid.size());
      int colIdx = col.nextInt(grid.get(0).size());

      if (!grid.get(rowIdx).get(colIdx).mine) {
        grid.get(rowIdx).get(colIdx).updateMine();
        minesSoFar ++;
      }
    }
  }


  // sets the canvas to draw WorldImages on each clock tick
  public WorldScene makeScene() {
    // loop through grid to place image
    WorldScene game = new WorldScene(WIDTH, HEIGHT);
    for (int rowIdx = 0; rowIdx < grid.size(); rowIdx++) {
      for (int colIdx = 0; colIdx < grid.get(0).size(); colIdx++) {
        game.placeImageXY(this.grid.get(rowIdx).get(colIdx).draw(),
            colIdx * 40 + 20, rowIdx * 40 + 20);
      }
    }
    return game;
  }

  // renders a last scene when the world stops
  public WorldScene lastScene(String msg) {
    WorldScene game = new WorldScene(WIDTH, HEIGHT);
    for (int rowIdx = 0; rowIdx < grid.size(); rowIdx++) {
      for (int colIdx = 0; colIdx < grid.get(0).size(); colIdx++) {
        game.placeImageXY(this.grid.get(rowIdx).get(colIdx).draw(),
            colIdx * 40 + 20, rowIdx * 40 + 20);
      }
    }
    if (msg.equals("GAME OVER!")) {
      game.placeImageXY(new TextImage("GAME OVER", 100, FontStyle.BOLD, Color.BLUE),
          MinesweeperWorld.WIDTH / 2, MinesweeperWorld.HEIGHT / 2);
    }
    else {
      game.placeImageXY(new TextImage("YOU WON", 100, FontStyle.BOLD, Color.BLUE),
          MinesweeperWorld.WIDTH / 2, MinesweeperWorld.HEIGHT / 2);
    }
    return game;
  }


  // handles mouse clicks (left / right)
  public void onMouseClicked(Posn pos, String button) {
    if (button.equals("LeftButton") && this.getCellClicked(pos).mine) {
      this.revealAllMines();
      this.endOfWorld("GAME OVER!");
    }
    else if (numMinesFound == MinesweeperWorld.MINES) {
      this.endOfWorld("YOU WON!");
    }
    else if (button.equals("LeftButton")) {
      this.leftClick(this.getCellClicked(pos));
    }
    else if (button.equals("RightButton")) {
      this.rightClick(this.getCellClicked(pos));
    }
  }

  // EFFECT: reveals all cells with mines
  public void revealAllMines() {
    for (int rowIdx = 0; rowIdx < grid.size(); rowIdx ++) {
      for (int colIdx = 0; colIdx < grid.get(0).size(); colIdx++) {
        if (grid.get(rowIdx).get(colIdx).mine) {
          grid.get(rowIdx).get(colIdx).updateRevealed();
        }
      }
    }
  }

  // retrieves the cell that the given pos represents on the grid
  public Cell getCellClicked(Posn pos) {
    Cell c;
    if (pos.x <= 40 && pos.y <= 40) {
      c = grid.get(0).get(0);
    }
    else {
      c = grid.get(pos.y / 40).get(pos.x / 40);
    }
    return c;
  }

  // EFFECT: updates this cell's mine, flagged or revealed fields based on its current state
  // and which button was clicked
  public void leftClick(Cell c) {
    if (!c.revealed && c.countNeighboringMines() != 0) {
      c.updateRevealed();
    }
    else if (!c.revealed && c.countNeighboringMines() == 0) {
      c.updateRevealed();
      for (Cell neighbor : c.neighbors) {
        this.leftClick(neighbor);
      }
    }
  }

  // EFFECT: updates how many mines have been flagged so far
  public void updateMinesFound() {
    this.numMinesFound++;
  }

  // EFFECT: updates this cell's mine, flagged or revealed fields based on its current state
  // and which button was clicked
  public void rightClick(Cell c) {
    if (!c.revealed && !c.flagged && c.mine) {
      c.updateFlagged(true);
      this.updateMinesFound();
    }
    else if (!c.revealed && !c.flagged && !c.mine) {
      c.updateFlagged(true);
    }
    else {
      c.updateFlagged(false);
    }
  }
}


// representing each block on the game board grid
class Cell {
  ArrayList<Cell> neighbors;
  boolean mine;
  boolean flagged;
  boolean revealed;

  // testing constructor
  Cell(ArrayList<Cell> neighbors, boolean hasMine) {
    this.neighbors = neighbors;
    this.mine = hasMine;
    for (Cell c : neighbors) {
      c.neighbors.add(this);
    }
  }

  // constructor
  Cell() {
    this.neighbors = new ArrayList<Cell>();
    this.mine = false;
    this.flagged = false;
    this.revealed = false;
  }

  // EFFECT: updates this cell to have a mine
  void updateMine() {
    this.mine = true;
  }

  // EFFECT: updates this cell to show that it has been clicked and revealed
  void updateRevealed() {
    this.revealed = true;
  }

  // EFFECT: updates this cell's neighbors' list with the given cell
  void updateNeighbors(Cell c) {
    this.neighbors.add(c);
  }

  // EFFECT: updates whether or not this cell has been flagged
  void updateFlagged(boolean bool) {
    this.flagged = bool;
  }

  // counts the number of mines that is surrounding this cell
  int countNeighboringMines() {
    int numMines = 0;
    for (int i = 0; i < this.neighbors.size(); i++) {
      if (this.neighbors.get(i).mine) {
        numMines++;
      }
    }
    return numMines;
  }


  // renders the cell as an image
  public WorldImage draw() {
    // rendering a mine as a black circle
    if (this.revealed && this.mine) {
      return new OverlayImage(new CircleImage(15, OutlineMode.SOLID, Color.BLACK),
          new OverlayImage(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.WHITE),
              new RectangleImage(40, 40, OutlineMode.SOLID, Color.GRAY)));
    }
    // rendering a cell with a number representing the number of mines neighboring this cell
    else if (this.revealed && this.countNeighboringMines() != 0) {
      return new OverlayImage(new TextImage(Integer.toString(this.countNeighboringMines()),
          15.0, FontStyle.BOLD, Color.RED),
          new OverlayImage(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.GRAY),
              new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE)));
    }
    // rendering a flag represented as a green triangle
    else if (!this.revealed && this.flagged) {
      return new OverlayImage(new EquilateralTriangleImage(
          15, OutlineMode.SOLID, Color.GREEN),
          new OverlayImage(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.WHITE),
              new RectangleImage(40, 40, OutlineMode.SOLID, Color.GRAY)));
    }
    // rendering a cell when there are no neighboring mines
    else if (this.revealed && this.countNeighboringMines() == 0) {
      return new OverlayImage(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.GRAY),
          new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE));
    }
    // default hidden cell
    else {
      return new OverlayImage(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.WHITE),
          new RectangleImage(40, 40, OutlineMode.SOLID, Color.GRAY));
    }
  }
}

// Examples class
class ExamplesMinesweeperWorld {
  MinesweeperWorld w1;
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  ArrayList<Cell> row0;
  ArrayList<Cell> row1;
  MinesweeperWorld w2;

  // initial data
  void initData() {
    this.c1 = new Cell(new ArrayList<Cell>(), false);
    this.c2 = new Cell(new ArrayList<Cell>(Arrays.asList(this.c1)), true);
    this.c3 = new Cell(new ArrayList<Cell>(Arrays.asList(this.c1, this.c2)), true);
    this.c4 = new Cell(new ArrayList<Cell>(Arrays.asList(this.c1, this.c2, this.c3)), false);
    this.row0 = new ArrayList<Cell>(Arrays.asList(this.c1, this.c2));
    this.row1 = new ArrayList<Cell>(Arrays.asList(this.c3, this.c4));
    this.w2 = new MinesweeperWorld(
        new ArrayList<ArrayList<Cell>>(Arrays.asList(this.row0, this.row1)));
    this.w1 = new MinesweeperWorld();
  }

  // testing buildGrid function in MinesweeperWorld class
  void testBuildGrid(Tester t) {
    this.initData();
    t.checkExpect(w1.buildGrid(16, 30).size(), 16);
    t.checkExpect(w1.grid.get(0).size(), 30);
  }

  // testing buildNeighborsList function in MinesweeperWorld class
  void testBuildNeighborsList(Tester t) {
    this.initData();
    t.checkExpect(this.w1.grid.get(0).get(0).neighbors.size(), 3);
    t.checkExpect(this.w1.grid.get(0).get(1).neighbors.size(), 5);
    t.checkExpect(this.w1.grid.get(0).get(2).neighbors.size(), 5);
    t.checkExpect(this.w1.grid.get(1).get(0).neighbors.size(), 5);
    t.checkExpect(this.w1.grid.get(1).get(3).neighbors.size(), 8);
    t.checkExpect(this.w1.grid.get(14).get(21).neighbors.size(), 8);
    t.checkExpect(this.w1.grid.get(12).get(0).neighbors.size(), 5);
    t.checkExpect(this.w1.grid.get(15).get(11).neighbors.size(), 5);
    t.checkExpect(this.w1.grid.get(11).get(29).neighbors.size(), 5);
    t.checkExpect(this.w1.grid.get(w1.grid.size() - 1)
        .get(w1.grid.get(0).size() - 1).neighbors.size(), 3);
    t.checkExpect(this.w1.grid.get(0).get(18).neighbors.size(), 5);
    t.checkExpect(this.w1.grid.get(15).get(0).neighbors.size(), 3);
    t.checkExpect(this.w1.grid.get(0).get(29).neighbors.size(), 3);
    t.checkExpect(this.w1.grid.get(9).get(9).neighbors.size(), 8);
    t.checkExpect(this.w1.grid.get(1).get(2).neighbors.size(), 8);
  }


  // testing buildNeighborsList function in MinesweeperWorld class
  void testNeighbors(Tester t) {
    this.initData();
    t.checkExpect(this.c1.neighbors, new ArrayList<Cell>(Arrays.asList(this.c2,
        this.c3, this.c4)));
    t.checkExpect(this.c2.neighbors, new ArrayList<Cell>(Arrays.asList(this.c1,
        this.c3, this.c4)));
    t.checkExpect(this.c3.neighbors, new ArrayList<Cell>(Arrays.asList(this.c1,
        this.c2, this.c4)));
    t.checkExpect(this.c4.neighbors, new ArrayList<Cell>(Arrays.asList(this.c1,
        this.c2, this.c3)));
  }


  // testing revealAllMines function in the MinesweeperWorld class
  void testRevealAllMines(Tester t) {
    this.initData();
    t.checkExpect(this.w2.grid.get(0).get(1).revealed, false);
    t.checkExpect(this.w2.grid.get(1).get(0).revealed, false);
    this.w2.revealAllMines();
    t.checkExpect(this.w2.grid.get(0).get(1).revealed, true);
    t.checkExpect(this.w2.grid.get(1).get(0).revealed, true);

  }

  // testing mouse handling functions
  void testOnMouseClicked(Tester t) {
    this.initData();
    t.checkExpect(this.w2.grid.get(0).get(0).revealed, false);
    t.checkExpect(this.w2.grid.get(0).get(1).flagged, false);
    this.w2.onMouseClicked(new Posn(30, 30), "LeftButton");
    this.w2.onMouseClicked(new Posn(50, 30), "RightButton");
    t.checkExpect(this.w2.grid.get(0).get(1).flagged, true);
    t.checkExpect(this.w2.grid.get(0).get(0).revealed, true);
  }

  //  // testing cellClicked function in MinesweeperWorld class
  void testGetCellClicked(Tester t) {
    this.initData();
    t.checkExpect(this.w1.getCellClicked(new Posn(30, 30)), this.w1.grid.get(0).get(0));
    t.checkExpect(this.w1.getCellClicked(new Posn(45, 30)), this.w1.grid.get(0).get(1));
    t.checkExpect(this.w1.getCellClicked(new Posn(30, 45)), this.w1.grid.get(1).get(0));
    t.checkExpect(this.w1.getCellClicked(new Posn(1180, 635)), this.w1.grid.get(15).get(29));
    t.checkExpect(this.w1.getCellClicked(new Posn(30, 635)), this.w1.grid.get(15).get(0));
    t.checkExpect(this.w1.getCellClicked(new Posn(1180, 30)), this.w1.grid.get(0).get(29));
  }

  // testing updateMine function in Cell class
  void testUpdateMine(Tester t) {
    this.initData();
    t.checkExpect(this.w2.grid.get(1).get(1).mine, false);
    t.checkExpect(this.w2.grid.get(0).get(0).mine, false);
    this.w2.grid.get(1).get(1).updateMine();
    this.w2.grid.get(0).get(0).updateMine();
    t.checkExpect(this.w2.grid.get(1).get(1).mine, true);
    t.checkExpect(this.w2.grid.get(0).get(0).mine, true);
  }

  // testing updateRevealed function in Cell class
  void testUpdateRevealed(Tester t) {
    this.initData();
    t.checkExpect(this.w2.grid.get(1).get(1).revealed, false);
    t.checkExpect(this.w2.grid.get(0).get(0).revealed, false);
    this.w2.grid.get(1).get(1).updateRevealed();
    this.w2.grid.get(0).get(0).updateRevealed();
    t.checkExpect(this.w2.grid.get(1).get(1).revealed, true);
    t.checkExpect(this.w2.grid.get(0).get(0).revealed, true);
  }

  // testing updateFlagged function in Cell class
  void testUpdateFlagged(Tester t) {
    this.initData();
    t.checkExpect(this.w2.grid.get(1).get(1).flagged, false);
    t.checkExpect(this.w2.grid.get(0).get(0).flagged, false);
    this.w2.grid.get(1).get(1).updateFlagged(true);
    this.w2.grid.get(0).get(0).updateFlagged(true);
    t.checkExpect(this.w2.grid.get(1).get(1).flagged, true);
    t.checkExpect(this.w2.grid.get(0).get(0).flagged, true);
  }

  // testing draw function in Cell class
  void testDraw(Tester t) {
    this.initData();
    t.checkExpect(this.w2.grid.get(0).get(0).draw(), new OverlayImage(new RectangleImage(40,
        40, OutlineMode.OUTLINE, Color.WHITE),
        new RectangleImage(40, 40, OutlineMode.SOLID, Color.GRAY)));
    this.w2.grid.get(0).get(0).updateFlagged(true);
    t.checkExpect(this.w2.grid.get(0).get(0).draw(), new OverlayImage(new EquilateralTriangleImage(
        15, OutlineMode.SOLID, Color.GREEN),
        new OverlayImage(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.WHITE),
            new RectangleImage(40, 40, OutlineMode.SOLID, Color.GRAY))));
    this.w2.grid.get(0).get(0).updateFlagged(false);
    this.w2.grid.get(0).get(0).updateMine();
    this.w2.grid.get(0).get(0).updateRevealed();
    t.checkExpect(this.w2.grid.get(0).get(0).draw(), new OverlayImage(new CircleImage(15,
        OutlineMode.SOLID, Color.BLACK),
        new OverlayImage(new RectangleImage(40, 40, OutlineMode.OUTLINE, Color.WHITE),
            new RectangleImage(40, 40, OutlineMode.SOLID, Color.GRAY))));
  }

  // testing countNeighbors function in Cell class
  void testCountNeighbors(Tester t) {
    this.initData();
    t.checkExpect(this.w2.grid.get(0).get(0).countNeighboringMines(), 2);
    t.checkExpect(this.w2.grid.get(1).get(1).countNeighboringMines(), 2);
  }

  // testing populateMines function in MinesweeperWorld class
  void populateMines(Tester t) {
    this.initData();
    int numMines = 0;
    for (int row = 0; row < w1.grid.size(); row++) {
      for (int col = 0; col < w1.grid.get(0).size(); col++) {
        if (w1.grid.get(row).get(col).mine) {
          numMines++;
        }
      }
    }
    t.checkExpect(numMines, w1.MINES);
  }

  // main function
  void testBigBang(Tester t) {
    this.initData();
    w1.bigBang(MinesweeperWorld.WIDTH, MinesweeperWorld.HEIGHT, 0.00);
  }
}
