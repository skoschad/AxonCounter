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

public class StratifiedRandomSampling {
    private final int dim_x, dim_y, cf_x, cf_y, dx, dy;
    private int n_cfs;
    private final int max_n_cfs;
    private final float exclusion_line_multiplier;
    private final byte[] area_mask;
    private SingleCountingFrame[] all_cfs;
    private java.awt.geom.GeneralPath overlay;
    
    public StratifiedRandomSampling(int dim_x_, int dim_y_, int cf_x_, int cf_y_,
            int dx_, int dy_, float exclusion_line_multiplier_, byte[] roi) {
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
        MersenneTwisterRNG RNG = new MersenneTwisterRNG();
        
        int k_x = (cf_x + dx) / cf_x;
        int k_y = (cf_y + dy) / cf_y;
        int grid_cell_x = cf_x + dx;
        int grid_cell_y = cf_y + dy;
        int origin_x, origin_y;
        int current_x, current_y;
        int pxl;
        int cf_sampling_area, N;
        
        int ro_k_x, ro_k_y;
  
        N = 0;
        for (origin_y = 0; origin_y < dim_y; origin_y += grid_cell_y) {
            for (origin_x = 0; origin_x < dim_x; origin_x += grid_cell_x) {
                // inside a grid cell
                // uniform distribution of integers e [0, k_x-1] & [0, k_y - 1]
                ro_k_x = RNG.nextInt(k_x);
                ro_k_y = RNG.nextInt(k_y);
                // sample this one cf placed within this grid cell
                cf_sampling_area = 0;
                for (current_y = origin_y + ro_k_y * cf_y;
                        (current_y < (origin_y + (ro_k_y + 1) * cf_y)) && current_y < dim_y; ++current_y) {
                    pxl = current_y * dim_x + origin_x + ro_k_x * cf_x;
                    for (current_x = origin_x + ro_k_x * cf_x;
                            (current_x < (origin_x + (ro_k_x + 1) * cf_x)) && current_x < dim_x; ++current_x) {
                          // sample
                        if ((area_mask[pxl] & 0xff) == 255) {
                            cf_sampling_area++;
                        }
                        ++pxl;
                    }
                }
                // this cf has now been sampled completely
                if (cf_sampling_area > 0) {
                    all_cfs[N] = new SingleCountingFrame(origin_x + ro_k_x * cf_x,
                                                         origin_x + (ro_k_x + 1) * cf_x,
                                                         origin_y + ro_k_y * cf_y,
                                                         origin_y + (ro_k_y + 1) * cf_y,
                                                         cf_sampling_area);
                    N++;
                }
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
        for (int i=0; i<dim_x*dim_y; i++) {
            if ((area_mask[i] & 0xff) == 255) {
                n_PixelsInAreaMask++;
            }
        }
        for (int i = 0; i < n_cfs; i++) {
            n_sampledPixels += all_cfs[i].sampling_area;
        }
        System.err.println(String.valueOf(n_PixelsInAreaMask));
        System.err.println(String.valueOf(n_sampledPixels));
        if (n_PixelsInAreaMask > 0) {
            empirical_sf = (double)n_sampledPixels / n_PixelsInAreaMask;
        } else {
            System.err.println("Error: n_PixelsInAreaMask=0");
        }
        return empirical_sf;
    }
    
    
    public double getSamplingVariance(int[][] points) {
        int n_points = points.length;
        int[] curr_point;
        double shortest_distance;
        int index_nearest_cf;
        for (int i = 0; i < n_points; i++) {
            curr_point = points[i];
            // find nearest cf (naive nearest neighbour search)
            shortest_distance = all_cfs[0].getEuclideanDistance(curr_point);
            index_nearest_cf = 0;
            for (int j = 1; j < n_cfs; j++) {
                if (all_cfs[j].getEuclideanDistance(curr_point) < shortest_distance) {
                    shortest_distance = all_cfs[j].getEuclideanDistance(curr_point);
                    index_nearest_cf = j;
                }
            }
            // add point to nearest cf
            all_cfs[index_nearest_cf].addElement();
        } 
        return 0;
    }
}
  
