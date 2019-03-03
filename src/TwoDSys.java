/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sebastian
 */
import org.uncommons.maths.random.MersenneTwisterRNG;

public class TwoDSys {
    private int dim_x, dim_y;
    private int cf_x, cf_y;
    private int dx, dy;
    private int ro_x, ro_y;
    
    float exclusion_line_multiplier;
    private byte[] area_mask;
    private boolean[] frames_mask;
    private java.awt.geom.GeneralPath frames;
   
    
    /** constructor method */
    public TwoDSys(int dim_x_, int dim_y_, int cf_x_, int cf_y_, int dx_, int dy_,
            float exclusion_line_multiplier_, byte[] roi) {
        dim_x = dim_x_;
        dim_y = dim_y_;
        cf_x = cf_x_;
        cf_y = cf_y_;
        dx = dx_;
        dy = dy_;
        exclusion_line_multiplier = exclusion_line_multiplier_;
        
        area_mask = roi;
        frames_mask = new boolean[dim_x_ * dim_y_]; // initialized to false by default
        frames = new java.awt.geom.GeneralPath();
        
        MersenneTwisterRNG RNG = new MersenneTwisterRNG();
        // nextInt(n) returns a pseudorandom, uniformly distributed int value
        // between 0 (inclusive) and the specified value n (exclusive).
        // -> uniform distribution of integers e [0, cfx+dx-1] and
        //                                       [0, cfy+dy-1]
        ro_x = RNG.nextInt(cf_x + dx);
        ro_y = RNG.nextInt(cf_y + dy);
        
        setFramesMask();
        // drawCountingFrames();
    }
    
    private void setFramesMask() {
        int origin_x, origin_y;
        int trunc_cf_x, trunc_cf_y;
        int current_x, current_y, pxl;
        int current_cf_x, current_cf_y;
        int i_curr_pattern_in_x, i_curr_pattern_in_y;

        // are there truncated cfs?
        trunc_cf_x = ro_x - dx;
        trunc_cf_y = ro_y - dy;

        /* do the work */
        if (trunc_cf_y > 0) {
            origin_y = 0;
            current_cf_y = trunc_cf_y;
        } else {
            origin_y = ro_y;
            current_cf_y = cf_y;
        }
        i_curr_pattern_in_y = 0;
        while (origin_y < dim_y) {
            if (trunc_cf_x > 0) {
                origin_x = 0;
                current_cf_x = trunc_cf_x;
            } else {
                origin_x = ro_x;
                current_cf_x = cf_x;
            }
            i_curr_pattern_in_x = 0;

            while (origin_x < dim_x) {
                // sample cf, state vars are current_y, current_x
                for (current_y = origin_y; (current_y - origin_y) < current_cf_y && current_y < dim_y;
                        ++current_y) {
                    pxl = current_y * dim_x + origin_x;
                    for (current_x = origin_x; (current_x - origin_x) < current_cf_x && current_x < dim_x;
                            ++current_x) {
                        // add to frames mask
                        frames_mask[pxl] = true;
                        ++pxl;
                    }
                }
                // this cf has now been sampled completely
                // move origin_x along
                origin_x += current_cf_x + dx;
                // switch back to full cf_x size
                if (current_cf_x != cf_x) {
                    current_cf_x = cf_x;
                }
            }
            // move origin_y along
            origin_y += current_cf_y + dy;
            // switch back to full cf_y size
            if (current_cf_y != cf_y) {
                current_cf_y = cf_y;
            }
        }
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

    private void drawCountingFrames() {
        boolean draw_longer_exclusion_lines = true;
        int trunc_cf_x, trunc_cf_y;
        int current_x, current_y;
        int rect_x_left, rect_x_right, rect_y_upper, rect_y_lower;
        if (-0.001 < exclusion_line_multiplier && exclusion_line_multiplier < 0.001) {
            // float is approx. 0
            draw_longer_exclusion_lines = false;
        }

        // are there truncated cfs?
        trunc_cf_x = ro_x - dx;
        trunc_cf_y = ro_y - dy;

        /* let's go */
        if (trunc_cf_y > 0) {
            current_y = trunc_cf_y * (-1);
        } else {
            current_y = ro_y;
        }
        while (true) {
            if (current_y >= dim_y) {
                break;
            }
            if (trunc_cf_x > 0) {
                current_x = trunc_cf_x * (-1);
            } else {
                current_x = ro_x;
            }
            while (true) {
                if (current_x >= dim_x) {
                    break;
                }
                rect_x_left = current_x;
                rect_x_right = current_x + cf_x;
                rect_y_upper = current_y;
                rect_y_lower = current_y + cf_y;
                // upper border
                drawDashedLine(frames, rect_x_left, rect_y_upper, rect_x_right, rect_y_upper, 10.0f);
                // right border
                drawDashedLine(frames, rect_x_right, rect_y_upper, rect_x_right, rect_y_lower, 10.0f);

                if (draw_longer_exclusion_lines) {
                    //left border
                    frames.moveTo(rect_x_left, rect_y_lower);
                    frames.lineTo(rect_x_left, rect_y_upper - exclusion_line_multiplier * cf_y);
                    // lower border
                    frames.moveTo(rect_x_left, rect_y_lower);
                    frames.lineTo(rect_x_right, rect_y_lower);
                    frames.lineTo(rect_x_right, rect_y_lower + exclusion_line_multiplier * cf_x);
                } else {
                    // left border
                    frames.moveTo(rect_x_left, rect_y_lower);
                    frames.lineTo(rect_x_left, rect_y_upper);
                    // lower border
                    frames.moveTo(rect_x_left, rect_y_lower);
                    frames.lineTo(rect_x_right, rect_y_lower);
                }

                current_x = current_x + cf_x + dx;
            }
            current_y = current_y + cf_y + dy;
        }
    }

    public java.awt.geom.GeneralPath getOverlay() {
        drawCountingFrames();
        return frames;
    }
    
    public double getSamplingFraction() {
        double sf = 0;
        int n_PixelsInAreaMask = 0;
        int n_sampledPixels = 0;
        for (int i=0; i<dim_x*dim_y; i++) {
            //System.err.println(String.valueOf(area_mask[i] & 0xff));
            if ((area_mask[i] & 0xff) == 255) {
                n_PixelsInAreaMask++;
                if (frames_mask[i] == true) {
                    n_sampledPixels++;
                }
            }
        }
        System.err.println(String.valueOf(n_PixelsInAreaMask));
        System.err.println(String.valueOf(n_sampledPixels));
        if (n_PixelsInAreaMask > 0) {
            sf = (double)n_sampledPixels / n_PixelsInAreaMask;
        } else {
            System.err.println("Error: n_PixelsInAreaMask=0");
        }
        return sf;
    }
}
