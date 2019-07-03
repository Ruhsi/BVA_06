  start = System.currentTimeMillis();
  roiManagerTest(true);
  roiManagerTest(false);
  compositeImageTests(true);
  compositeImageTests(false);
  metadataTests();
  thresholdTests()

  //print("time="+(System.currentTimeMillis()-start)+"ms");

  function roiManagerTest(headless) {
      msg = "FAIL: roiManagerTest"+(headless?" (headless)":"")+" #";
      imp = IJ.openImage("http://imagej.nih.gov/ij/images/blobs.gif");
      if (!headless) imp.show();
      IJ.setAutoThreshold(imp, "Default");
      IJ.run("Set Measurements...", "area mean redirect=None decimal=3");
      manager = null;
      if (headless) {
         manager = new RoiManager(true);
         ParticleAnalyzer.setRoiManager(manager);
      }
      IJ.run(imp, "Analyze Particles...", "exclude clear add");
      if (manager==null)
         manager = RoiManager.getInstance();
      rois = manager.getRoisAsArray();
      n = rois.length;
      if (n!=46) print(msg+1);
      stats = [];
      for (i=0; i<n; i++) {
          imp.setRoi(rois[i]);
           stats[i] = imp.getStatistics()
      }
      WindowManager.setTempCurrentImage(imp);
      ran = new Random();
      for (i=0; i<500; i++) {
          index = Math.round(ran.nextDouble()*(n-1));
          manager.select(index);
           stats2 = imp.getStatistics();
           //print(i+" "+index+"  "+stats2.area);
           if (stats2.area!=stats[index].area) {
              print(msg+2);
              break;
           }   
      }
      manager.setSelectedIndexes([1,2,4,8,20]);
      manager.runCommand("Combine");
      stats2 = imp.getStatistics();
      if (stats2.area!=1853)
         print(msg+3);
      manager.setSelectedIndexes([45,44,33,5,0]);
      manager.runCommand("Combine");
      stats2 = imp.getStatistics();
      if (stats2.area!=1601)
         print(msg+4);
      if (!headless) {
         imp.close();
         manager.close();
      }
  }

  function compositeImageTests(headless) {
     msg = "FAIL: CompositeImageTest"+(headless?" (headless)":"")+" #";
     imp = IJ.openImage("http://imagej.nih.gov/ij/images/Rat_Hippocampal_Neuron.zip");
     if (!headless) imp.show();
     ip = imp.getProcessor();
     if (ip.getMin()!=472|| ip.getMax()!=2436) print(msg+1);
     stats = ip.getStatistics();
     if (stats.min!=472||stats.max!=8583) print(msg+2);

     imp.setC(3);
     ip = imp.getProcessor();
     if (ip.getMin()!=504|| ip.getMax()!=942) print(msg+3);
     stats = ip.getStatistics();
     if (stats.min!=484||stats.max!=5821) print(msg+4);
     if (!headless) imp.close();

     imp = IJ.openImage("http://imagej.nih.gov/ij/images/Spindly-GFP.zip");
     if (!headless) imp.show();
     ip = imp.getProcessor();
     if (ip.getMin()!=1582|| ip.getMax()!=6440) print(msg+5);
     stats = ip.getStatistics();
     if (stats.min!=1601||stats.max!=6796) print(msg+6);

     imp.setPosition(2, 4, 20);
     ip = imp.getProcessor();
     if (ip.getMin()!=1614|| ip.getMax()!=15787) print(msg+7);
     stats = ip.getStatistics();
     if (stats.min!=1634||stats.max!=16172) print(msg+8+" "+stats);
     //print("Mitosis (2-4-20): "+stats.min+"-"+stats.max);

     imp.setPosition(1, 2, 3);
     ip = imp.getProcessor();
     if (ip.getMin()!=1582|| ip.getMax()!=6440) print(msg+9);
     stats = ip.getStatistics();
     if (stats.min!=1606||stats.max!=18946) print(msg+10);
     if (!headless) imp.close();
  }

  function metadataTests() {
     msg = "FAIL: MetadataTests #";
     size = 256;
     info = "info-metadata";
     label = "label";
     for (n=1; n<10; n++) {
        imp = IJ.createImage("A","16-bit",size,size,n);
        imp.setProperty("Info", info+n);
        stack = imp.getStack();
        for (i=1; i<=stack.size(); i++)
           stack.setSliceLabel(label+i, i);
        imp2 = imp.duplicate();
        path = IJ.getDir("temp")+"untitled.tif";
        IJ.saveAs(imp2, "tif", path);
        imp3 = IJ.openImage(path);
        if (imp.getProperty("Info")!=info+n) print(msg+1);
        stack = imp3.getStack();
        for (i=1; i<=stack.size(); i++) {
           if (stack.getSliceLabel(i)!=label+i) print(msg+2);
        }
        imp3.setSlice(imp3.getStackSize());
        while (imp3.getStackSize()>1)
            IJ.run(imp3, "Delete Slice", "");
        if (imp3.getStackSize()!=1) print(msg+3);
        if (imp3.getStack().getSliceLabel(1)!=label+1) print(msg+4);;
     }
     imp = IJ.createImage("B","16-bit",size,size,1);
     stack = imp.getStack();
     stack.setSliceLabel(label,1);
     stack.addSlice(new FloatProcessor(size,size));
     ip = imp.getProcessor().duplicate();
     ip.add(12345);
     imp.setProcessor(ip);
     if (imp.getStatistics().mean!=12345) print(msg+5);
     imp2 = imp.duplicate();
     imp3 = imp2.duplicate();
     stack = imp3.getStack();
     if (stack.getSliceLabel(1)!=label) print(msg+6);
     stack = new ImageStack(size,size);
     stack.addSlice(label,new ByteProcessor(size,size));
     if (stack.getSliceLabel(1)!=label) print(msg+7);
     imp = new ImagePlus("",stack);
     if (imp.getStack().getSliceLabel(1)!=label) print(msg+8);
     imp = IJ.createImage("C","32-bit",size,size,1);
     imp.setStack(stack);
     if (imp.getStack().getSliceLabel(1)!=label) print(msg+9);  
  }

  function thresholdTests() {
     msg = "FAIL: ThresholdTests #";
     img = IJ.createImage("Untitled", "8-bit ramp", 256, 256, 1);
     IJ.setRawThreshold(img, 100, 200, null);
     Prefs.blackBackground = true;
     IJ.run(img, "Convert to Mask", "");
     stats = img.getStatistics();
     if (stats.histogram[255]!=25856) print(msg+1);
     img = IJ.createImage("Untitled", "16-bit ramp", 256, 256, 1);
     IJ.setRawThreshold(img, 16000, 45000, null);
     IJ.run(img, "Convert to Mask", "");
     stats = img.getStatistics();
     if (stats.histogram[255]!=28928) print(msg+2);
     img = IJ.createImage("Untitled", "32-bit ramp", 256, 256, 1);
     IJ.setRawThreshold(img, 0.25, 0.75, null);
     IJ.run(img, "Convert to Mask", "");
     stats = img.getStatistics();
     if (stats.histogram[255]!=33024) print(msg+3);
  }

