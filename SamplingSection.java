/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sebastian
 * 
 */

public class SamplingSection implements java.io.Serializable {
    // increment serialVersionUID if further class versions are not backwards
    // compatible
    private static final long serialVersionUID = 1L;
    private final String filename;
    private String method;
    private SingleCountingFrame[] all_cfs;
    private double empirical_sf;
    
    SamplingSection(String filename, String method) {
        this.filename = filename;
        setMethod(method);
    }
    
    
    public void srswor(int dim_x, int dim_y, int cf_x, int cf_y, double sf,
            float exclusion_line_multiplier, byte[] roi) {
        Srswor srswor = new Srswor(dim_x, dim_y, cf_x, cf_y, sf,
            exclusion_line_multiplier, roi);
        this.all_cfs = srswor.getCfs();
        this.empirical_sf = srswor.getSamplingFraction();
    }
    
    
    public void surs(int dim_x, int dim_y, int cf_x, int cf_y, int dx, int dy,
            float exclusion_line_multiplier, byte[] roi) {
        AlignedSurs surs = new AlignedSurs(dim_x, dim_y, cf_x, cf_y, dx, dy,
                exclusion_line_multiplier, roi);
        this.all_cfs = surs.getCfs();
        this.empirical_sf = surs.getSamplingFraction();
    }
    
    
    public void stratrs(int dim_x, int dim_y, int cf_x, int cf_y, int dx, int dy,
            float exclusion_line_multiplier, byte[] roi) {
        StratifiedRandomSampling stratrs = new StratifiedRandomSampling(dim_x, dim_y,
            cf_x, cf_y, dx, dy, exclusion_line_multiplier, roi);
        this.all_cfs = stratrs.getCfs();
        this.empirical_sf = stratrs.getSamplingFraction();
    }
    
    private void setMethod(String method) {
        if (method.equals("srswor") || method.equals("surs") ||
                method.equals("stratrs")) {
            this.method = method;
        }
    }
    
    
    public String getMethod() {
        return this.method;
    }
    
    
    private void setEmpiricalSf(double sf) {
        this.empirical_sf = sf;
    }
    
    
    public double getEmpiricalSf() {
        return this.empirical_sf;
    }
    
    
    public java.awt.geom.GeneralPath getOverlay(float exclusion_line_multiplier) {
        java.awt.geom.GeneralPath overlay;
        overlay = new java.awt.geom.GeneralPath();
        int n_cfs = all_cfs.length;
        for (int i = 0; i < n_cfs; i++) {
            all_cfs[i].drawSingleCountingFrameOnOverlay(overlay, exclusion_line_multiplier);
        }
        return overlay;
    }
    
    
    public String getFilename() {
        return this.filename;
    }
}
