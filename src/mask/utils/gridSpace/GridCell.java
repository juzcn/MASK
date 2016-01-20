/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.utils.gridSpace;

import java.io.Serializable;

/**
 *
 * @author zj
 */
public class GridCell<T extends Serializable> implements Serializable {

    private final int row, column;
    private final T value;

    public GridCell(int row, int column, T value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return value;
    }
}
