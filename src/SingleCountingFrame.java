/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sebastian
 */

public class SingleCountingFrame implements java.io.Serializable {
    public final int x_left;
    public final int x_right;
    public final int y_upper;
    public final int y_lower;
    private final int cf_x;
    private final int cf_y; 
    public final int sampling_area;
    private int n_elements;
    private final double center_x, center_y;
    
    public SingleCountingFrame (int x_left, int x_right, int y_upper, int y_lower,
            int sampling_area) {
        this.x_left = x_left;
        this.x_right = x_right;
        this.y_upper = y_upper;
        this.y_lower = y_lower;
        this.cf_x = x_right - x_left;
        this.cf_y = y_lower - y_upper;
        this.sampling_area = sampling_area;
        this.n_elements = 0;
        this.center_x = x_left + (x_right - x_left) / 2;
        this.center_y = y_upper + (y_lower - y_upper) / 2;
    }
    
    private void drawDashedLine(java.awt.geom.GeneralPath path, int from_x, int from_y,
            int to_x, int to_y, float number_of_dashes) {
        float dx_ = (to_x - from_x) / (2 * number_of_dashes);
        float dy_ = (to_y - from_y) / (2 * number_of_dashes);
        float current_x = from_x;
        float current_y = from_y;
        for (int i = 1; i <= number_of_dashes; i++) {
            path.moveTo(current_x, current_y);
            path.lineTo(current_x + dx_, current_y + dy_);
            current_x += 2 * dx_;
            current_y += 2 * dy_;
        }
    }
    
    
    public void drawSingleCountingFrameOnOverlay(java.awt.geom.GeneralPath path,
            float exclusion_line_multiplier) {
        boolean draw_longer_exclusion_lines = true;
        if (-0.001 < exclusion_line_multiplier && exclusion_line_multiplier < 0.001) {
            // float is approx. 0
            draw_longer_exclusion_lines = false;
        }
        
        // upper border
        drawDashedLine(path, this.x_left, this.y_upper, this.x_right, this.y_upper, 10.0f);
        // right border
        drawDashedLine(path, this.x_right, this.y_upper, this.x_right, this.y_lower, 10.0f);

        if (draw_longer_exclusion_lines) {
            //left border
            path.moveTo(this.x_left, this.y_lower);
            path.lineTo(this.x_left, this.y_upper - exclusion_line_multiplier * this.cf_y);
            // lower border
            path.moveTo(this.x_left, this.y_lower);
            path.lineTo(this.x_right, this.y_lower);
            path.lineTo(this.x_right, this.y_lower + exclusion_line_multiplier * this.cf_x);
        } else {
            // left border
            path.moveTo(this.x_left, this.y_lower);
            path.lineTo(this.x_left, this.y_upper);
            // lower border
            path.moveTo(this.x_left, this.y_lower);
            path.lineTo(this.x_right, this.y_lower);
        }
    }
    
    
    public double getEuclideanDistance(int[] point) {
        int point_x = point[0];
        int point_y = point[1];
        double distance = Math.sqrt(Math.pow((point_x - center_x), 2) +
                                    Math.pow((point_y - center_y), 2));
        return distance;
    }
    
    
    public void addElement() {
        this.n_elements += 1;
    }
    
    
    public int getCount() {
        return this.n_elements;
    }
}
