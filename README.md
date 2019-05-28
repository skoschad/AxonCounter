# AxonCounter

## Installation
To install this macro, simply download the file AxonCounter_.jar to the plugins folder, or subfolder, within your ImageJ folder. Restart ImageJ and there will be a new AxonCounter command in the Plugins menu or submenu.

Make sure you also have ImageJ's Cell Counter plugin installed (should be included in the default installation). It is available from https://imagej.nih.gov/ij/plugins/cell-counter.html. 

## Usage
To use this tool as intended in Koschade et al., 2019:

1. Open a digital microscopy image in ImageJ. 

This should be a high-resolution image of the entire optic nerve. Make sure the image's scale is set correctly (pixels need to correspond to a physical unit of length). Either calibrate manually using Analyze > Set Scale, or try opening the image using Fiji's Bio-Formats Importer, which is very good at doing this automatically for you. 

2. Trace the outline of the optic nerve using ImageJ's Polygon Selection tool. Make sure to close the selection by left-clicking on the initial node when done. Add this outline to the ROI manager (Edit > Selection > Add to Manager, or press Ctrl-T).

Note: In cases of regional injury or when information regarding different areas of the optic nerve are required, also use the Polygon Selection tool to create  different, non-overlapping strata. Then proceed with the process below sequentially for each stratum. The axon number estimates from the different strata will need to be added together to arrive at the estimate of the total optic nerve axon number.  

3. With the outline selected within the ROI manager and active, start the AxonCounter plugin. Chose the desired sampling method and sampling parameters; then, click OK.

Note: The publication's recommendation are to use a sampling fraction of 5—10% and a counting frame size of about 4x4 µm.

4. The AxonCounter plugin draws unbiased counting frames onto the selected image according to the chosen sampling method and sampling parameters.

5. Start ImageJ's Cell Counter plugin and click 'Initialize'. This re-opens the current window to initialize it for counting axons using Cell Counter. Then, select 'Type 1' among the Counters and disable 'Show Numbers' (better graphical performance). 

6. In the image window, zoom in as appropriate and begin counting axons sampled within unbiased counting frames by clicking onto them.

As outlined in the manuscript, unbiased counting frames consist of two exclusion lines (drawn left and bottom) and two inclusion lines (drawn right and upper). An axon that in some way touches the exclusion line must not be counted. An axon that in some way touches the inclusion line is counted, even it mostly falls outside the counting frame. Axons that are within the counting frame without touching either inclusion nor exclusion lines are also counted. The total number of counted axons is displayed in the Cell Counter plugin window.

7. When done: Enter the total number of counted objects in AxonCounter plugin window, under the 'Results' tab. Then, click 'Get estimates'. 

7. Document/save your analysis: Within the AxonCounter plugin window, under the 'Results' tab, click 'Save this sampling scheme'. Within the Cell Counter plugin window, click 'Save Markers' to save counted axons. This will allow you to revisit your analysis at a later time. 

8. To revisit or continue your analysis at a later time: First, open the original image (the file name must remain the same). Then, start the AxonCounter plugin and select the Results tab. Click 'Load previous sampling scheme' and open the saved .ser file. To display the marked-up counted axons, start the Cell Counter plugin, click 'Load Markers' and select the saved .xml file. 

## History
2019-02-06: first version

2019-05-28: some clarifications
