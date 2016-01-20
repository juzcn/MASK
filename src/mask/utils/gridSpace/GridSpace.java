/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.utils.gridSpace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zj
 */
public class GridSpace<T extends Serializable> implements Serializable {

    private final Object[][] grid;
    private final int rowSize, columnSize;

    public GridSpace(int rowSize, int columnSize) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        grid = new Object[rowSize][columnSize];
    }

    public synchronized List<GridCell<T>> getAllCellNeighbs(int row, int column) {
        List<GridCell<T>> cellList = new ArrayList<>();
        if (row > 0) { // top
            cellList.add(new GridCell(row - 1, column, (T) grid[row - 1][column]));
        }
        if (row > 0 && column > 0) { // top left
            cellList.add(new GridCell(row - 1, column - 1, (T) grid[row - 1][column - 1]));
        }
        if (row > 0 && column < columnSize - 1) { // top rigth
            cellList.add(new GridCell(row - 1, column + 1, (T) grid[row - 1][column + 1]));
        }
        if (row < rowSize - 1) { // bottom
            cellList.add(new GridCell(row + 1, column, (T) grid[row + 1][column]));
        }
        if (row < rowSize - 1 && column > 0) { // bottom left
            cellList.add(new GridCell(row + 1, column - 1, (T) grid[row + 1][column - 1]));
        }
        if (row < rowSize - 1 && column < columnSize - 1) { // bottom rigth
            cellList.add(new GridCell(row + 1, column + 1, (T) grid[row + 1][column + 1]));
        }
        if (column > 0) { // left
            cellList.add(new GridCell(row, column - 1, (T) grid[row][column - 1]));
        }
        if (column < columnSize - 1) { // right
            cellList.add(new GridCell(row, column + 1, (T) grid[row][column + 1]));
        }
        return cellList;
    }

    public synchronized List<T> getAllNeighbs(int row, int column) {
        List<T> cellList = new ArrayList<>();
        if (row > 0) { // top
            cellList.add((T) grid[row - 1][column]);
        }
        if (row > 0 && column > 0) { // top left
            cellList.add((T) grid[row - 1][column - 1]);
        }
        if (row > 0 && column < columnSize - 1) { // top rigth
            cellList.add((T) grid[row - 1][column + 1]);
        }
        if (row < rowSize - 1) { // bottom
            cellList.add((T) grid[row + 1][column]);
        }
        if (row < rowSize - 1 && column > 0) { // bottom left
            cellList.add((T) grid[row + 1][column - 1]);
        }
        if (row < rowSize - 1 && column < columnSize - 1) { // bottom rigth
            cellList.add((T) grid[row + 1][column + 1]);
        }
        if (column > 0) { // left
            cellList.add((T) grid[row][column - 1]);
        }
        if (column < columnSize - 1) { // right
            cellList.add((T) grid[row][column + 1]);
        }
        return cellList;
    }

    public boolean isFree(int row, int column) {
        return (grid[row][column] == null);
    }

    public boolean isOccupied(int row, int column) {
        return (grid[row][column] != null);
    }

    public void setValue(int row, int column, T value) {
        grid[row][column] = value;
    }

    public T getValue(int row, int column) {
        return (T) grid[row][column];
    }

    public void clear() {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                grid[i][j] = null;
            }
        }
    }

    /**
     * @return the rowSize
     */
    public int getRowSize() {
        return rowSize;
    }

    /**
     * @return the columnSize
     */
    public int getColumnSize() {
        return columnSize;
    }

}
