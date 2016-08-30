package HomeMade.Tools;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.IJ;
import ij.measure.ResultsTable;
import java.util.Arrays;

public class Stack_Measure implements PlugIn {
int [] Dim;
ResultsTable rt = ResultsTable.getResultsTable();
ResultsTable ResT= ResultsTable.getResultsTable();
double [][] Res;
int Slices,a,b,c,d,e,f,sliceN,numobj,counter;
Roi cRoi;
RoiManager rm;
ImagePlus imp;
	public void run(String arg) {
		if (arg==""){
			arg="Default ResultTable";
		}
		if (imp==null){
			imp = IJ.getImage(); //get image
		}
		Dim=imp.getDimensions(); 	//get dimensions
		Slices=Dim[2]*Dim[3]*Dim[4];	//calculate number of images Dim[2]=channels, Dim[3]=stacks, Dim[4]=frames
		if (rm==null){
			rm = RoiManager.getInstance(); //get Roimanager
		}
		if (rm!=null){
			numobj=rm.getCount();		//get number of rois in roimanager
		} else {
			cRoi = imp.getRoi();
			if (cRoi!=null){
				numobj=1;
			} else {
				numobj=1;
			}
		}
		Res=new double[numobj][Slices];	//create result array
		IJ.run("Set Measurements...", "mean redirect=None decimal=2"); // set measurments
		counter=0;
		for (c=0;c<Dim[2];c++){ //loop trough channels
			for (a=0;a<Dim[4];a++){ //loop through time
				for (b=0;b<Dim[3];b++){ //loop through stack
					for (d=0;d<numobj;d++){ //loop through d
						IJ.run("Clear Results", "");
						sliceN= (b*Dim[2]+a*Dim[2]*Dim[3]+c+1);//determine slice number to be measured
						if (rm!=null){
							rm.select(d); //needs to go before set slice otherwise the slice where the roi was set will be measured
						} else if (cRoi!=null){
							imp.setRoi(cRoi);
						} else {
							IJ.run(imp, "Select All", "");
						}
						imp.setSlice(sliceN);//set slice
						IJ.run(imp, "Measure", "");//measure
						Res[d][counter]=rt.getValue("Mean",0); //get result in resultarray
					}
				counter=counter+1;
				}
			}
		}
		ResT=new ResultsTable();
		for (f=0;f<Slices;f++){
			ResT.incrementCounter();
			for (e=0;e<numobj;e++){
				ResT.addValue(""+e,Res[e][f]);
				ResT.show(arg);
			}
		}
	}

	public Stack_Measure(ImagePlus imp, RoiManager roiM){
		this.imp =imp;
		this.rm=roiM;
		run("Roi Manager results");
	}

	public Stack_Measure(ImagePlus imp){
		this.imp =imp;
		run("Image input");
	}

	public Stack_Measure(){ // needed to start the run in stand alone version
		
	}
}
