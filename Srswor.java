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

public class Srswor {
    private int dim_x, dim_y;
    private int cf_x, cf_y;
    private double sf;
    private int n_cfs, max_n_cfs;
    private final float exclusion_line_multiplier;
    private SingleCountingFrame[] chosen_cfs;
    private final byte[] area_mask;
    private java.awt.geom.GeneralPath overlay;

    
    /** constructor method */
    public Srswor(int dim_x_, int dim_y_, int cf_x_, int cf_y_, double sf_,
            float exclusion_line_multiplier_, byte[] roi) {
        dim_x = dim_x_;
        dim_y = dim_y_;
        cf_x = cf_x_;
        cf_y = cf_y_;
        sf = sf_;
        max_n_cfs = (int) Math.ceil(Math.ceil(dim_x / cf_x) *
                Math.ceil(dim_y / cf_y));
        exclusion_line_multiplier = exclusion_line_multiplier_;
        
        area_mask = roi;
        overlay = new java.awt.geom.GeneralPath();
        
        createCfs();
    }
    
    
    private void createCfs() {
        int origin_x, origin_y;
        int current_x, current_y;
        int current_cf_x, current_cf_y;
        int pxl;
        int sampling_area;
        // for SRSWOR
        int N, m, t;
        double u;
        SingleCountingFrame this_cf;
        SingleCountingFrame[] eligible_cfs = new SingleCountingFrame[max_n_cfs];
        N = 0;
        origin_y = 0;
        
        int ro_x; int ro_y;
        MersenneTwisterRNG RNG = new MersenneTwisterRNG();
        // nextInt(n) returns a pseudorandom, uniformly distributed int value
        // between 0 (inclusive) and the specified value n (exclusive).
        // -> uniform distribution of integers e [0, cfx-1] and
        //                                       [0, cfy-1]
        ro_x = RNG.nextInt(cf_x);
        ro_y = RNG.nextInt(cf_y);        
        // create all possible counting frames
        current_cf_y = cf_y - ro_y;  // truncated to this size
        while (origin_y < dim_y) {
            origin_x = 0;
            current_cf_x = cf_x - ro_x;  // truncated to this size
            while (origin_x < dim_x) {
                // inside a cf
                sampling_area = 0;
                for (current_y = origin_y;
                        ((current_y-origin_y) < current_cf_y) && (current_y < dim_y);
                        ++current_y) {
                    pxl = current_y * dim_x + origin_x;
                    for (current_x = origin_x;
                            ((current_x-origin_x) < current_cf_x) && (current_x < dim_x);
                            ++current_x) {
                        if ((area_mask[pxl] & 0xff) == 255) {
                            // this cf has at least 1 pxl overlap with area mask
                            sampling_area++;
                        }
                        pxl++;
                    }
                }   
                if (sampling_area >= 1) {
                    eligible_cfs[N] = new SingleCountingFrame(origin_x, origin_x+current_cf_x,
                                                              origin_y, origin_y+current_cf_y,
                                                              sampling_area);
                    N++;
                }
                origin_x += current_cf_x;
                // switch back to full cf_x size
                current_cf_x = cf_x;
            }
            origin_y += current_cf_y;
            current_cf_y = cf_y;
        }

        // sample n_cfs of N
        n_cfs = (int) Math.round((double) N * sf);
        chosen_cfs = new SingleCountingFrame[n_cfs];
        m = 0;
        t = 0;
        while (m < n_cfs) {
            // Returns the next pseudorandom, uniformly distributed double value
            // between 0.0 and 1.0 (0.0 included, 1.0 excluded) 
            u = RNG.nextDouble();
            if ((N - t) * u >= n_cfs - m) {  // skip
                ++t;
            } else {  // choose this one
                chosen_cfs[m] = eligible_cfs[t];
                ++t;
                ++m;
            }
        }   
    }
    
    
    public SingleCountingFrame[] getCfs() {
        return chosen_cfs;
    }    
   
    
    public double getSamplingFraction() {
        double empirical_sf = 0;
        int n_PixelsInAreaMask = 0;
        int n_sampledPixels = 0;
        for (int i=0; i<dim_x*dim_y; i++) {
            if ((area_mask[i] & 0xff) == 255) {
                n_PixelsInAreaMask++;
            }
        }
        for (int i = 0; i < n_cfs; i++) {
            n_sampledPixels += chosen_cfs[i].sampling_area;
        }
        // System.err.println(String.valueOf(n_PixelsInAreaMask));
        // System.err.println(String.valueOf(n_sampledPixels));
        if (n_PixelsInAreaMask > 0) {
            empirical_sf = (double)n_sampledPixels / n_PixelsInAreaMask;
        } else {
            System.err.println("Error: n_PixelsInAreaMask=0");
        }
        return empirical_sf;
    } 
}
