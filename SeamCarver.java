/**
 * @author ZhangJie
 */
 // package edu.princeton.cs.algs4;

import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    // private final Picture scp;
    // a flag to register the last call between Horizontal or Vertical seam find
    // private boolean transposed;
    private int[][] colormap;
    private int width;
    private int height;
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new NullPointerException("a picture is required.");
        // this.scp = picture;
        // this.transposed = false;
        
        // initialize colormap
        this.width = picture.width();
        this.height = picture.height();
        colormap = new int[height][width];
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                colormap[row][col] = picture.get(col, row).getRGB();
            }
        }        
        
    }
    
     // current picture
    public Picture picture() {
        // int width = colormap[0].length;
        // int height = colormap.length;
        Picture p = new Picture(width, height);
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(colormap[row][col]);
                p.set(col, row, c);
            }
        }
        return p;
    }
    
     // width of current picture
    public int width() {
        return width;
        // return colormap[0].length;
    }
    
     // height of current picture
    public int height() {
        return height;
       // return colormap.length;
    }
    
    // energy of pixel at column x and row y
    public  double energy(int x, int y) {
        // int width = colormap[0].length;
        // int height = colormap.length;
        if (x < 0 || x > width - 1) 
            throw new IndexOutOfBoundsException("the first argument should be nonnegative and less than width");
        if (y < 0 || y > height - 1) 
            throw new IndexOutOfBoundsException("the first argument should be nonnegative and less than height");
        if (x == 0 || x == width - 1) return 1000.0;
        if (y == 0 || y == height - 1) return 1000.0;
        // get color
        int left = colormap[y][x - 1];
        int right = colormap[y][x + 1];
        int up = colormap[y - 1][x];
        int down = colormap[y + 1][x];
        // get color RGB
        int lR, lG, lB;
        int rR, rG, rB;
        int upR, upG, upB;
        int dR, dG, dB;
        lR = (left & 0x00ff0000) >> 16;
        lG = (left & 0x0000ff00) >> 8;
        lB = left & 0x000000ff;
        rR = (right & 0x00ff0000) >> 16;
        rG = (right & 0x0000ff00) >> 8;
        rB = right & 0x000000ff;
        upR = (up & 0x00ff0000) >> 16;
        upG = (up & 0x0000ff00) >> 8;
        upB = up & 0x000000ff;
        dR = (down & 0x00ff0000) >> 16;
        dG = (down & 0x0000ff00) >> 8;
        dB = down & 0x000000ff;
        double deltaXX = (rR - lR)*(rR - lR) + (rG - lG)*(rG - lG) + (rB - lB)*(rB - lB);
        double deltaYY = (dR - upR)*(dR - upR) + (dG - upG)*(dG - upG) + (dB - upB)*(dB - upB);
        
        return Math.sqrt(deltaXX + deltaYY);
    }
    
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // if (!transposed) {
        //     transpose();
        //     transposed = true;
        // }
        transpose();

        int[] hseam;       
        hseam = findVerticalSeam();

        transpose();
        // transposed = false;
        return hseam;
    }
    
     // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // int width = colormap[0].length;
        // int height = colormap.length;
        int[][] edgeTo;
        double[][] distTo;
        edgeTo = new int[height][width];
        distTo = new double[height][width];
        
        if (width == 1) {
            int[] vseam = new int[height];
            for (int row = 0; row < height; row++) {
                vseam[row] = 0;
            }
            return vseam;
        }
        
        // calculate row 0 for distTo and edgeTo
        for (int col = 0; col < width; col++) {
            distTo[0][col] = energy(col, 0);
            edgeTo[0][col] = col;
        }
        for (int row = 1; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (col == 0) {
                    if (distTo[row - 1][0] < distTo[row - 1][1]) {
                        distTo[row][0] = distTo[row - 1][0] + energy(0, row);
                        edgeTo[row][0] = width * (row - 1);
                    } else {
                        distTo[row][0] = distTo[row - 1][1] + energy(0, row);
                        edgeTo[row][0] = width * (row - 1) + 1;
                    }
                } else if (col == width - 1) {
                    if (distTo[row - 1][col] < distTo[row - 1][col - 1]) {
                        distTo[row][col] = distTo[row - 1][col] + energy(col, row);
                        edgeTo[row][col] = width * (row - 1) + col;
                    } else {
                        distTo[row][col] = distTo[row - 1][col - 1] + energy(col, row);
                        edgeTo[row][col] = width * (row - 1) + col - 1;
                    }
                } else {
                    if (distTo[row - 1][col - 1] < distTo[row - 1][col] && 
                        distTo[row - 1][col - 1] < distTo[row - 1][col + 1]) {
                        distTo[row][col] = distTo[row - 1][col - 1] + energy(col, row);
                        edgeTo[row][col] = width * (row - 1) + col - 1;
                    } else if (distTo[row - 1][col] < distTo[row - 1][col + 1]) {
                        distTo[row][col] = distTo[row - 1][col] + energy(col, row);
                        edgeTo[row][col] = width * (row - 1) + col;
                    } else {
                        distTo[row][col] = distTo[row - 1][col + 1] + energy(col, row);
                        edgeTo[row][col] = width * (row - 1) + col + 1;
                    }
                }
            }
        }
        // find the minimum column in the last row
        int colmin = 0;
        double min = Double.MAX_VALUE;
        for (int col = 0; col < width; col++) {
            if (distTo[height - 1][col] < min) {
                min = distTo[height - 1][col];
                colmin = col;
            }
        }
        // construct the vertical seam
        int[] vseam;
        vseam = new int[height];
        int index = width * (height - 1) + colmin;
        for (int row = height - 1; row >= 0; row--) {
            int col = index % width;            
            vseam[row] = col;
            index = edgeTo[row][col];
        }
        return vseam;
    }
    
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException("an argument is required");
        // if (!transposed) {
        //     transpose();
        //     transposed = true;
        // }
        
        // int height = colormap.length;
        // int width = colormap[0].length;
        if (seam.length != width)
            throw new IllegalArgumentException("the length of array is not ok");
        // validate seam
        for (int i = 0; i < width; i++) {
            if (seam[i] < 0 || seam[i] > height - 1) 
                throw new IllegalArgumentException("the elements should be between 0 and W - 1.");
        }
        for (int i = 0; i < width - 1; i++) {
            if (seam[i + 1] - seam[i] > 1 || seam[i] - seam[i + 1] > 1) 
                throw new IllegalArgumentException("invalid seam.");
        }
        transpose();
        removeVerticalSeam(seam);
        transpose();
        // transposed = false;
    }
    
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException("an argument is required");
        // the length of vertical seam should be equal to the height of colormap   
        // int height = colormap.length;
        // int width = colormap[0].length;
        if (seam.length != height) 
            throw new IllegalArgumentException("the length of array is not ok");
        // validate seam
        for (int i = 0; i < height; i++) {
            if (seam[i] < 0 || seam[i] > width - 1) 
                throw new IllegalArgumentException("the elements should be between 0 and W - 1.");
        }
        for (int i = 0; i < height - 1; i++) {
            if (seam[i + 1] - seam[i] > 1 || seam[i] - seam[i + 1] > 1) 
                throw new IllegalArgumentException("invalid seam.");
        }        
       
        // remove seam from colormap      
        int[][] colorTemp;
        colorTemp = new int[height][width - 1];
        for (int row = 0; row < height; row++) {
            System.arraycopy(colormap[row], 0, colorTemp[row], 0, seam[row]);
            System.arraycopy(colormap[row], seam[row] + 1, colorTemp[row], seam[row], width - 1 - seam[row]);
        }
        colormap = colorTemp;
        // decrease width by 1
        width -= 1;
    }
    
    private void transpose() {
        // int width = colormap[0].length;
        // int height = colormap.length;       
        
        int[][] colorTrans;
        colorTrans = new int[width][height];

        // transpose color map
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                colorTrans[row][col] = colormap[col][row];
            }
        }
        colormap = colorTrans;
        // swap width and height
        int widthTemp = width;
        width = height;
        height = widthTemp;
    }
}