/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sebastian
 */

import java.util.Arrays;
import org.uncommons.maths.random.MersenneTwisterRNG;

public class AlignedSurs {
    private final int dim_x, dim_y, cf_x, cf_y, dx, dy;
    private final int max_n_cfs;
    private int n_cfs;
    private final float exclusion_line_multiplier;
    private final byte[] area_mask;
    private SingleCountingFrame[] all_cfs;
    private java.awt.geom.GeneralPath overlay;    
    
    
    public AlignedSurs(int dim_x_, int dim_y_, int cf_x_, int cf_y_, int dx_, int dy_,
            float exclusion_line_multiplier_, byte[] roi) {
        dim_x = dim_x_;
        dim_y = dim_y_;
        cf_x = cf_x_;
        cf_y = cf_y_;
        dx = dx_;
        dy = dy_;
        max_n_cfs = (int) Math.ceil(Math.ceil(dim_x / (cf_x + dx)) *
                Math.ceil(dim_y / (cf_y + dy)));  
        exclusion_line_multiplier = exclusion_line_multiplier_;        
        area_mask = roi;
        overlay = new java.awt.geom.GeneralPath();
        createCfs();
    }

    
    private void createCfs() {
        all_cfs = new SingleCountingFrame[max_n_cfs];
        int ro_x; int ro_y;
        MersenneTwisterRNG RNG = new MersenneTwisterRNG();
        // nextInt(n) returns a pseudorandom, uniformly distributed int value
        // between 0 (inclusive) and the specified value n (exclusive).
        // -> uniform distribution of integers e [0, cfx+dx-1] and
        //                                       [0, cfy+dy-1]
        ro_x = RNG.nextInt(cf_x + dx);
        ro_y = RNG.nextInt(cf_y + dy);
        int origin_x, origin_y;
        int trunc_cf_x, trunc_cf_y;
        int current_x, current_y, pxl;
        int current_cf_x, current_cf_y;
        int i_curr_pattern_in_x, i_curr_pattern_in_y;
        int cf_sampling_area, N;

        // are there truncated cfs?
        trunc_cf_x = ro_x - dx;
        trunc_cf_y = ro_y - dy;

        /* do the work */
        N = 0;
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
                cf_sampling_area = 0;
                for (current_y = origin_y; (current_y - origin_y) < current_cf_y && current_y < dim_y;
                        ++current_y) {
                    pxl = current_y * dim_x + origin_x;
                    for (current_x = origin_x; (current_x - origin_x) < current_cf_x && current_x < dim_x;
                            ++current_x) {
                        if ((area_mask[pxl] & 0xff) == 255) {
                            cf_sampling_area++;
                        }
                        pxl++;
                    }
                }
                // this cf has now been sampled completely
                if (cf_sampling_area > 0) {
                    all_cfs[N] = new SingleCountingFrame(origin_x, origin_x + current_cf_x,
                                                         origin_y, origin_y + current_cf_y,
                                                         cf_sampling_area);
                    N++;
                }
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
        n_cfs = N;
    }
   
    
    public SingleCountingFrame[] getCfs() {
        SingleCountingFrame[] cfs = Arrays.copyOfRange(all_cfs, 0, n_cfs);
        return cfs;
    }
   
    
    public double getSamplingFraction() {
        double empirical_sf = 0;
        int n_PixelsInAreaMask = 0;
        int n_sampledPixels = 0;
        // area (pixel count) of roi
        for (int i=0; i<dim_x*dim_y; i++) {
            if ((area_mask[i] & 0xff) == 255) {
                n_PixelsInAreaMask++;
            }
        }
        // area (pixel count) of counting frames within roi
        for (int i = 0; i < n_cfs; i++) {
            n_sampledPixels += all_cfs[i].sampling_area;
        }
        // print to log for documentation
        System.err.println(String.valueOf(n_PixelsInAreaMask));
        System.err.println(String.valueOf(n_sampledPixels));
        if (n_PixelsInAreaMask > 0) {
            empirical_sf = (double)n_sampledPixels / n_PixelsInAreaMask;
        } else {
            System.err.println("Error: n_PixelsInAreaMask=0");
        }
        return empirical_sf;
    }   
}
