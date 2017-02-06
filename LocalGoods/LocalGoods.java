
package LocalGoods ;

import com.esri.mo2.cs.geom.*;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.map.draw.* ;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.dlg.*;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;


// CLASS: LocalGoods
// DESCRIPTION: Main class for application.

// LocalGoods is an application that connects the user to locally grown and/or raised food in San Diego County. This map
// allows the user to see not only where to purchase food, but where the farms are located within the county.

public class LocalGoods extends JFrame {

	String s1 = "C:/LocalGoods/data/SDCounty.shp";
	String s2 = "C:/LocalGoods/data/Farms.shp";
	String s3 = "C:/LocalGoods/data/Farmers Markets.shp";
	String s5 = "C:/LocalGoods/data/California.shp";
	String sproduce = "C:/LocalGoods/data/Produce.shp";
	String schickens = "C:/LocalGoods/data/Chickens.shp";
	String scows = "C:/LocalGoods/data/Cows.shp";
	String shoney = "C:/LocalGoods/data/honey.shp";
	String datapathname = "";
	String legendname = "";
	private ArrayList helpText = new ArrayList(3);
	static HelpTool helptool = new HelpTool() ;
	
	static boolean fullMap = true;
	static boolean helpToolOn = true;
	double distance;
	int activeLayerIndex;
	
	// Listeners
	ActionListener lis;
	ActionListener layerlis;
	ActionListener layercontrollis;
	ComponentListener complistener;

	// Layers
	static Map map = new Map();
	Legend legend;
	Legend legend2;
	Layer layer2 = new Layer();
	Layer layer = new Layer();
	Layer layer3 = new Layer();
	Layer layer5 = new Layer();
	Layer layerProduce = new Layer();
	Layer layerChickens = new Layer();
	Layer layerCows = new Layer();
	Layer layerHoney = new Layer();
	Layer newlayer = new Layer() ;
	static com.esri.mo2.map.dpy.Layer layer4;	
	static AcetateLayer acetLayer;
	com.esri.mo2.map.dpy.Layer activeLayer;
	com.esri.mo2.cs.geom.Point initPoint, endPoint;
	Toc toc = new Toc();
	TocAdapter mytocadapter;
	static Envelope env;

	// Menu items
	JMenuBar mbar = new JMenuBar();
	JMenu file = new JMenu("File");
	JMenu theme = new JMenu("Theme");
	JMenu layercontrol = new JMenu("Layer Control");
	JMenu help = new JMenu("Help");
	JMenu helpTopics = new JMenu("Help Topics");

	// ImageIcons
	
	ImageIcon chelp = new ImageIcon("C:/LocalGoods/img/icons/help.png");	
	ImageIcon caddlayer = new ImageIcon("C:/LocalGoods/img/icons/addtheme.png") ;	
	ImageIcon ctable = new ImageIcon("C:/LocalGoods/img/icons/tableview.png") ;
	ImageIcon cdemote = new ImageIcon("C:/LocalGoods/img/icons/demote.png") ;
	ImageIcon cprint = new ImageIcon("C:/LocalGoods/img/icons/print.png") ;
	ImageIcon clegend = new ImageIcon("C:/LocalGoods/img/icons/properties.png") ;
	ImageIcon cdelete = new ImageIcon("C:/LocalGoods/img/icons/delete.png") ;
	ImageIcon cpoly = new ImageIcon("C:/LocalGoods/img/icons/poly.png");
	ImageIcon cpoint = new ImageIcon("C:/LocalGoods/img/icons/point.png");
	
	static ImageIcon cpromo = new ImageIcon("C:/LocalGoods/img/icons/promote.png") ;

	// JMenuIcons
	JMenuItem jmtable = new JMenuItem("Open Attribute Table", ctable );
	JMenuItem jmpoly  = new JMenuItem("Create Polygon Layer From Selection", cpoly );
	JMenuItem jmpolypt  = new JMenuItem("Create Point Layer From Selection", cpoint );
	JMenuItem jmdemote = new JMenuItem("Demote Selected Layer", cdemote );
	JMenuItem jmprint = new JMenuItem("Print", cprint );
	JMenuItem jmaddlayer = new JMenuItem("Add Layer", caddlayer );
	JMenuItem jmdelete = new JMenuItem("Remove Layer", cdelete );
	JMenuItem jmlegend = new JMenuItem("Legend Editor", clegend );
	static JMenuItem jmpromo = new JMenuItem("Promote Selected Layer", cpromo );
	JMenuItem jmhelp = new JMenuItem("About", chelp );
	JMenuItem jmcontact = new JMenuItem("Contact Us", chelp );
	JMenuItem jmhelptool = new JMenuItem("Help Tool", chelp );
	JMenuItem jmtochelp = new JMenuItem("Table of Contents Help", chelp);
	JMenuItem jmleghelp = new JMenuItem("Legend Editor Help", chelp);
	JMenuItem jmconhelp = new JMenuItem("Layer Control Help", chelp);

	// Toolbars
	ZoomPanToolBar zptb = new ZoomPanToolBar();
	static SelectionToolBar stb = new SelectionToolBar();
	JToolBar jtb = new JToolBar();

	// Statusbar
	JLabel statusLabel = new JLabel("status bar    LOC");
	static JLabel milesLabel = new JLabel("   DIST:  0 mi    ");
	static JLabel kmLabel = new JLabel("  0 km    ");
	java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");

	// CustomToolbar
	JPanel myjp = new JPanel();
	JPanel myjp2 = new JPanel();
	JButton prtjb = new JButton(new ImageIcon("C:/LocalGoods/img/icons/print.png"));
	JButton addlyrjb = new JButton(new ImageIcon("C:/LocalGoods/img/icons/addtheme.png"));
	JButton ptrjb = new JButton(new ImageIcon("C:/LocalGoods/img/icons/arrow.png"));
	JButton distjb = new JButton(new ImageIcon("C:/LocalGoods/img/icons/measure.png"));
	JButton XYjb = new JButton("XY");
	JButton hotjb = new JButton(new ImageIcon("C:/LocalGoods/img/icons/hotlinkGloveSmall.png"));
	JButton helptooljb = new JButton(new ImageIcon("C:/LocalGoods/img/icons/help.png"));

	// Hotlink
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image bolt = tk.getImage("C:/LocalGoods/img/icons/hotlinkGlove.png");
	java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,
										new java.awt.Point(6,30),"bolt");

	// PickAdapter 
	MyPickAdapter picklis = new MyPickAdapter();
	Identify hotlink = new Identify();

	class MyPickAdapter implements PickListener {
		MouseEvent me ;
		int x ;
		int y ;

		public void beginPick(PickEvent pe){
			System.out.println("begin pick");
			me = pe.getMouseEvent() ;
			x = me.getX() ;
			y = me.getY() ;
		}

		public void foundData(PickEvent pe){

			String fname = "" ;
			String fsvalue ;
			int fivalue = 0;

			System.out.println("x,y = " + x + "," + y);
			com.esri.mo2.cs.geom.Point point = map.transformPixelToWorld(x,y);
			com.esri.mo2.data.feat.Cursor cursor = pe.getCursor();

			// Get feature attribute information
			Row row = null ;
			while (cursor.hasMore()) {
				row = (com.esri.mo2.data.feat.Row)cursor.next();
				Fields fields = row.getFields() ;
				Field field = fields.getField(1) ;
				fname = field.getName() ;
				fsvalue = row.getDisplayValue(1) ;
				fivalue = Integer.parseInt(fsvalue);
			}

			// Skip SD County and CA
			String actlayername = map.getLayer(activeLayerIndex).getName();
			if ( ! new String("SDCounty").equals(actlayername) && ! new String("California").equals(actlayername)) {

				try {
					HotPick hotpick = new HotPick(fivalue, row,
											      activeLayerIndex,
											      map);
					hotpick.setVisible(true);
				} catch(Exception e){
					System.err.println(e);
					e.printStackTrace();
				}
			}
		}


		public void endPick(PickEvent pe){
			System.out.println("end pick");
			hotlink.setCursor(java.awt.Cursor.getPredefinedCursor(
													java.awt.Cursor.DEFAULT_CURSOR));
			map.setSelectedTool(null);
		}
	} ;


	public LocalGoods() {

		super("Local Goods");
		helpToolOn = false ;
		this.setBounds(50,50,900,900);
		zptb.setMap(map);
		stb.setMap(map);
		setJMenuBar(mbar);

		ActionListener lisZoom = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = false;
			}
		};

		ActionListener lisFullExt = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = true;
			}
		};

		// Zoomtoolbar button 
		JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
		JButton zoomFullExtentButton =
				(JButton)zptb.getActionComponent("ZoomToFullExtent");
		JButton zoomToSelectedLayerButton =
				(JButton)zptb.getActionComponent("ZoomToSelectedLayer");
		zoomInButton.addActionListener(lisZoom);
		zoomFullExtentButton.addActionListener(lisFullExt);
		zoomToSelectedLayerButton.addActionListener(lisZoom);

		complistener = new ComponentAdapter () {
			public void componentResized(ComponentEvent ce) {
				if(fullMap) {
					map.setExtent(env);
					map.zoom(1.0);
					map.redraw();
				}
			}
		};
		addComponentListener(complistener);

	    MouseAdapter lishelptoolprt = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(9)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    prtjb.addMouseListener(lishelptoolprt);

	    // Add Layer
	    MouseAdapter lishelptooladdlyr = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(10)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    addlyrjb.addMouseListener(lishelptooladdlyr);

	    // Arrow Button
	    MouseAdapter lishelptoolptr = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(11)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    ptrjb.addMouseListener(lishelptoolptr);

	    // Distance Tool Button
	    MouseAdapter lishelptooldist = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(5)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    distjb.addMouseListener(lishelptooldist);

	    // XY Tool Button
	    MouseAdapter lishelptoolxy = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(7)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    XYjb.addMouseListener(lishelptoolxy);

	    // HotLink Tool Button
	    MouseAdapter lishelptoolhot = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(8)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    hotjb.addMouseListener(lishelptoolhot);

	    // HelpTool  Button
	    MouseAdapter lishelptoolhelp = new MouseAdapter() {
	    	public void mousePressed(MouseEvent me) {
	    		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	    			try {
	    				HelpDialog hd = new HelpDialog((String)helpText.get(3)) ;
	    				hd.setVisible(true);
	    			} catch(IOException e) {
	    				System.err.println(e);
						e.printStackTrace();
	    			}
	    		}
	    	}
	    };
	    helptooljb.addMouseListener(lishelptoolhelp);

		// Custom Tool Bar case statement next...
		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == prtjb || source instanceof JMenuItem ) {
					com.esri.mo2.ui.bean.Print mapPrint =
							new com.esri.mo2.ui.bean.Print();
					mapPrint.setMap(map);
					mapPrint.doPrint();
				} else if (source == ptrjb) {
					Arrow arrow = new Arrow();
					map.setSelectedTool(arrow);
				} else if (source == distjb) {
					DistanceTool distanceTool = new DistanceTool();
					map.setSelectedTool(distanceTool);
				} else if (source == XYjb) {
					try {
						AddXYtheme addXYtheme = new AddXYtheme();
						addXYtheme.setMap(map);
						addXYtheme.setVisible(false);
						map.redraw();
					} catch (IOException e) { }
				} else if (source == hotjb) {
					hotlink.setCursor(boltCursor);
					map.setSelectedTool(hotlink);
				} else if (source == helptooljb){
					helpToolOn = true ;
					map.setSelectedTool(helptool);
				} else {
					try {
						AddLyrDialog aldlg = new AddLyrDialog();
						aldlg.setMap(map);
						aldlg.setVisible(true);
					} catch(IOException e) { }
				}
			}
		};
	    hotlink.setPickWidth(10);
		hotlink.addPickListener(picklis);
	    XYjb.addActionListener(lis);
	    hotjb.addActionListener(lis);
	    prtjb.addActionListener(lis);
	    ptrjb.addActionListener(lis);
	    distjb.addActionListener(lis);
	    addlyrjb.addActionListener(lis);
	    jmprint.addActionListener(lis);
	    helptooljb.addActionListener(lis);

		// Create and configure the promote and demote listener now...
		layercontrollis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String source = ae.getActionCommand();
				System.out.println(activeLayerIndex+" active index");
				if (source == "Promote Selected Layer") {
					map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
				} else {
					map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
					enableDisableButtons();
					map.redraw();
				}
			}
		};
	    jmpromo.addActionListener(layercontrollis);
	    jmdemote.addActionListener(layercontrollis);

		layerlis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source instanceof JMenuItem) {
					String arg = ae.getActionCommand();
					if(arg == "Add Layer") {
						try {
							AddLyrDialog aldlg = new AddLyrDialog();
							aldlg.setMap(map);
							aldlg.setVisible(true);
						} catch(IOException e){}
					} else if(arg == "About") {
						AboutBox ab = new AboutBox();
						ab.setProductName("LocalGoods");
						ab.setProductVersion("1.0");
						ab.setVisible(true) ;
					} else if (arg == "Contact Us"){
						try {
							String s = "\n\nInquiries should be addressed to: " ;
							s = s + "LocalGoods@rohan.sdsu.edu." ;
							HelpDialog hd = new HelpDialog(s) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Help Tool"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(3)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Table of Contents Help"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(0)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Legend Editor Help"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(1)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if (arg == "Layer Control Help"){
						try {
							HelpDialog hd = new HelpDialog((String)helpText.get(2)) ;
							hd.setVisible(true);
						} catch (IOException e) {}
					} else if(arg == "Remove Layer") {
						try {
							com.esri.mo2.map.dpy.Layer dpylayer = legend.getLayer();
							map.getLayerset().removeLayer(dpylayer);
							map.redraw();
							jmdelete.setEnabled(false);
							jmlegend.setEnabled(false);
							jmtable.setEnabled(false);
							jmpromo.setEnabled(false);
							jmdemote.setEnabled(false);
							stb.setSelectedLayer(null);
							stb.setSelectedLayers(null);
							zptb.setSelectedLayer(null);
						} catch(Exception e) {}
					} else if(arg == "Legend Editor") {
						LayerProperties lp = new LayerProperties();
						lp.setLegend(legend);
						lp.setSelectedTabIndex(0);
						lp.setVisible(true);
					} else if (arg == "Open Attribute Table") {
						try {
							layer4 = legend.getLayer();
							AttrTab attrtab = new AttrTab();
							attrtab.setVisible(true);
						} catch(IOException ioe){}
					} else if (arg=="Create Polygon Layer From Selection" ||
						arg=="Create Point Layer From Selection" ) {

						int layertype ;
						com.esri.mo2.map.draw.BaseSimpleRenderer sbr = new
								com.esri.mo2.map.draw.BaseSimpleRenderer();

						// Polygons
					    com.esri.mo2.map.draw.SimpleFillSymbol simplepolysymbol = new
				            	com.esri.mo2.map.draw.SimpleFillSymbol();
						simplepolysymbol.setSymbolColor(new Color(255,255,0));
			            simplepolysymbol.setType(
			            	com.esri.mo2.map.draw.SimpleFillSymbol.FILLTYPE_SOLID);
			            simplepolysymbol.setBoundary(true);

						// Points
			            com.esri.mo2.map.draw.SimpleMarkerSymbol simplepointsymbol =
			            		new com.esri.mo2.map.draw.SimpleMarkerSymbol();
			            simplepointsymbol.setType( SimpleMarkerSymbol.CROSS_MARKER );
						simplepointsymbol.setSymbolColor(new Color(255,0,0));
			            simplepointsymbol.setWidth(12);

						if ( arg =="Create Polygon Layer From Selection") {
							layertype = 2;
						} else {
							layertype = 0;
						}

						layer4 = legend.getLayer();
						FeatureLayer flayer2 = (FeatureLayer)layer4;
						System.out.println("has selected " + flayer2.hasSelection());
						if (flayer2.hasSelection()) {
							SelectionSet selectset = flayer2.getSelectionSet();
							FeatureLayer selectedlayer =
									flayer2.createSelectionLayer(selectset);
							sbr.setLayer(selectedlayer);
							if ( layertype == 2 ) {
								sbr.setSymbol(simplepolysymbol);
							} else {
								sbr.setSymbol(simplepointsymbol);
							}
							selectedlayer.setRenderer(sbr);
							Layerset layerset = map.getLayerset();

							layerset.addLayer(selectedlayer);

							if(stb.getSelectedLayers() != null) {
								jmpromo.setEnabled(true);
			            	}
							try {
								legend2 = toc.findLegend(selectedlayer);
			            	} catch (Exception e) {}

							CreateShapeDialog csd =
									new CreateShapeDialog(selectedlayer,layertype);
							csd.setVisible(true);
							Flash flash = new Flash(legend2);
							flash.start();
							map.redraw();
						}
					}
				}
			}
		};
	    jmaddlayer.addActionListener(layerlis);
	    jmlegend.addActionListener(layerlis);
	    jmtable.addActionListener(layerlis);
	    jmpoly.addActionListener(layerlis);
	    jmdelete.addActionListener(layerlis);
	    jmpolypt.addActionListener(layerlis);
	    jmhelp.addActionListener(layerlis);
	    jmcontact.addActionListener(layerlis);
	    jmhelptool.addActionListener(layerlis);
	    jmtochelp.addActionListener(layerlis);
	    jmleghelp.addActionListener(layerlis);
	    jmconhelp.addActionListener(layerlis);

		// TOC Listener to set active layer
		toc.setMap(map);
		mytocadapter = new TocAdapter() {
			public void click(TocEvent e) {
				System.out.println(activeLayerIndex + " active layer index");
				legend = e.getLegend();
				activeLayer = legend.getLayer();
				stb.setSelectedLayer(activeLayer);
				zptb.setSelectedLayer(activeLayer);
				activeLayerIndex = map.getLayerset().indexOf(activeLayer);
				com.esri.mo2.map.dpy.Layer[] layers = {activeLayer};
				hotlink.setSelectedLayers(layers);

				System.out.println(activeLayerIndex + " active index");
				jmdelete.setEnabled(true);
				jmlegend.setEnabled(true);
				jmtable.setEnabled(true);
				enableDisableButtons();
			}
		};
		toc.addTocListener(mytocadapter);

		map.addMouseMotionListener(
			new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent me) {
					com.esri.mo2.cs.geom.Point worldPoint = null;
					if (map.getLayerCount() > 0) {
						worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
						String s = "X:"+df.format(worldPoint.getX())+" "+
							"Y:"+df.format(worldPoint.getY());
						statusLabel.setText(s);
					} else {
						statusLabel.setText("X:0.000 Y:0.000");
					}
				}
			}
		);

		// Default application properties 
		jmdelete.setEnabled(false);
		jmlegend.setEnabled(false);
		jmtable.setEnabled(false);
		jmpromo.setEnabled(false);
	    jmdemote.setEnabled(false);

	    // Build the File menu 
	    file.add(jmaddlayer);
	    file.add(jmprint);
	    file.add(jmdelete);
	    file.add(jmlegend);

	    // Build the Theme menu 
	    theme.add(jmtable);
	    theme.add(jmpoly);
	    theme.add(jmpolypt);

	    // Build the Layer Control menu 
	    layercontrol.add(jmpromo);
	    layercontrol.add(jmdemote);

	    // Build the Help menu 
	    setuphelpText();
	    helpTopics.add(jmtochelp);
	    helpTopics.add(jmleghelp);
	    helpTopics.add(jmconhelp);
	    help.add(helpTopics);
	    help.add(jmhelptool);
	    help.add(jmcontact);
	    help.add(jmhelp);

	    // Add menus to the menubar 
	    mbar.add(file);
	    mbar.add(theme);
	    mbar.add(layercontrol);
	    mbar.add(help);

	    // Add tool tips 
	    prtjb.setToolTipText("Print: click to print map");
	    addlyrjb.setToolTipText("Add layer: click to add a layer to the map");
	    ptrjb.setToolTipText("Arrow tool: press to reset your cursor");
	    distjb.setToolTipText("Distance tool: press, drag and release to measure distance");
	    XYjb.setToolTipText("add a layer of points from a file");
	    hotjb.setToolTipText("Hotlink: See attribute data and photos");
	    helptooljb.setToolTipText("Click this button, "
	    		+ "then right click any toolbar item for help");

	    // Attach buttons to the tool bar
	    jtb.add(prtjb);
	    jtb.add(addlyrjb);
	    jtb.add(ptrjb);
	    jtb.add(distjb);
	    jtb.add(XYjb);
	    jtb.add(hotjb);
	    jtb.add(helptooljb);

	    // Add objects to the jpanels
	    myjp.add(jtb);
	    myjp.add(zptb);
	    myjp.add(stb);
	    myjp2.add(statusLabel);
	    myjp2.add(milesLabel);
	    myjp2.add(kmLabel);

	    // Add a window splitter
	    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,toc,map) ;
	    splitPane.setOneTouchExpandable(true) ;

	    // Configure the layout for the jpanel and then  add the maps...
	    getContentPane().add(map, BorderLayout.CENTER);
	    getContentPane().add(myjp,BorderLayout.NORTH);
	    getContentPane().add(myjp2,BorderLayout.SOUTH);
		
		//CA
		addShapefileToMap(layer5,s5);
		//SD County
	    addShapefileToMap(layer,s1);
		//Farms
	    addShapefileToMap(layer2,s2);
		//Farmer's Markets
	    addShapefileToMap(layer3,s3);
		//Chickens
		addShapefileToMap(layerChickens,schickens);
		//Produce
		addShapefileToMap(layerProduce,sproduce);
		//Cows
		addShapefileToMap(layerCows,scows);
		//Honey
		addShapefileToMap(layerHoney,shoney);

		java.util.List list = toc.getAllLegends();
		
		
		
		
		//California
		com.esri.mo2.map.dpy.Layer lay3 = ((Legend)list.get(7)).getLayer();
	    FeatureLayer flayer3 = (FeatureLayer)lay3;
	    BaseSimpleRenderer bsr3 = (BaseSimpleRenderer)flayer3.getRenderer();
	    SimplePolygonSymbol sym3 = (SimplePolygonSymbol)bsr3.getSymbol();
	    sym3.setPaint(
	    		AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL,
	    		                     new java.awt.Color(119,176,49)));
	    bsr3.setSymbol(sym3);
		
	    //SD County
	    com.esri.mo2.map.dpy.Layer lay2 = ((Legend)list.get(6)).getLayer();
	    FeatureLayer flayer2 = (FeatureLayer)lay2;
	    BaseSimpleRenderer bsr2 = (BaseSimpleRenderer)flayer2.getRenderer();
	    SimplePolygonSymbol sym2 = (SimplePolygonSymbol)bsr2.getSymbol();
	    sym2.setPaint(
	    		AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL,
	    		                     new java.awt.Color(161,222,87)));
	    bsr2.setSymbol(sym2);
		
	    //Farms
	    com.esri.mo2.map.dpy.Layer lay1 = ((Legend)list.get(5)).getLayer();
	    FeatureLayer flayer1 = (FeatureLayer)lay1;
		com.esri.mo2.map.draw.RasterMarkerSymbol rms = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		rms.setSizeX(24);
		rms.setSizeY(24);
		rms.setImageString("C:/LocalGood/img/icons/farmbg.png");	
	    BaseSimpleRenderer bsr1 = (BaseSimpleRenderer)flayer1.getRenderer();
		bsr1.setSymbol(rms) ;
		
		//Farmer's Markets
	    com.esri.mo2.map.dpy.Layer lay0 = ((Legend)list.get(4)).getLayer();
	    FeatureLayer flayer0 = (FeatureLayer)lay0;
		com.esri.mo2.map.draw.RasterMarkerSymbol rms0 = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		rms0.setSizeX(24);
		rms0.setSizeY(24);
		rms0.setImageString("C:/LocalGood/img/icons/farmersmarketsbg.png");	
	    BaseSimpleRenderer bsr0 = (BaseSimpleRenderer)flayer0.getRenderer();
		bsr0.setSymbol(rms0) ;		
		
		
		//Chickens
	    com.esri.mo2.map.dpy.Layer lchicken = ((Legend)list.get(3)).getLayer();
	    FeatureLayer flayerchicken = (FeatureLayer)lchicken;
		com.esri.mo2.map.draw.RasterMarkerSymbol rmschicken = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		rmschicken.setSizeX(24);
		rmschicken.setSizeY(24);
		rmschicken.setImageString("C:/LocalGood/img/icons/chickenbg.png");	
	    BaseSimpleRenderer bsrchicken = (BaseSimpleRenderer)flayerchicken.getRenderer();
		bsrchicken.setSymbol(rmschicken) ;	
		
		//Produce
	    com.esri.mo2.map.dpy.Layer lproduce = ((Legend)list.get(2)).getLayer();
	    FeatureLayer flayerproduce = (FeatureLayer)lproduce;
		com.esri.mo2.map.draw.RasterMarkerSymbol rmsproduce = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		rmsproduce.setSizeX(24);
		rmsproduce.setSizeY(24);
		rmsproduce.setImageString("C:/LocalGood/img/icons/producebg.png");	
	    BaseSimpleRenderer bsrproduce = (BaseSimpleRenderer)flayerproduce.getRenderer();
		bsrproduce.setSymbol(rmsproduce) ;		
		
		//Cows
	    com.esri.mo2.map.dpy.Layer lcow = ((Legend)list.get(1)).getLayer();
	    FeatureLayer flayercow = (FeatureLayer)lcow;
		com.esri.mo2.map.draw.RasterMarkerSymbol rmscow = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		rmscow.setSizeX(24);
		rmscow.setSizeY(24);
		rmscow.setImageString("C:/LocalGood/img/icons/cowbg.png");	
	    BaseSimpleRenderer bsrcow = (BaseSimpleRenderer)flayercow.getRenderer();
		bsrcow.setSymbol(rmscow) ;	
		
		// Honey
		com.esri.mo2.map.dpy.Layer lbee = ((Legend)list.get(0)).getLayer();
	    FeatureLayer flayerbee = (FeatureLayer)lbee;
		com.esri.mo2.map.draw.RasterMarkerSymbol rmsbee = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		rmsbee.setSizeX(24);
		rmsbee.setSizeY(24);
		rmsbee.setImageString("C:/LocalGood/img/icons/bee.png");	
	    BaseSimpleRenderer brsbee = (BaseSimpleRenderer)flayerbee.getRenderer();
		brsbee.setSymbol(rmsbee) ;	
		

	    getContentPane().add(splitPane, BorderLayout.WEST);
	    map.setExtent(env);
		map.zoom(1.0);
		map.redraw();
	}


	private void addShapefileToMap(Layer layer, String s) {
		String datapath = s;
		layer.setDataset("0;"+datapath);
		map.add(layer);
	}


	private void enableDisableButtons() {
		int layerCount = map.getLayerset().getSize();
		if (layerCount < 2) {
			jmpromo.setEnabled(false);
			jmdemote.setEnabled(false);
		} else if (activeLayerIndex == 0) {
			jmdemote.setEnabled(false);
			jmpromo.setEnabled(true);
		} else if (activeLayerIndex == layerCount - 1) {
			jmpromo.setEnabled(false);
			jmdemote.setEnabled(true);
		} else {
			jmpromo.setEnabled(true);
			jmdemote.setEnabled(true);
		}
	}


	private void setuphelpText() {
		String s0 = "The Table of Contents, otherwise known as the toc, is the the left menu.\n"
	    + "Each entry is called a layer. The layers in the toc can be activated by clicking them.";

		helpText.add(s0);
		String s1 = "The Legend Editor is a menu item found under the File menu.\n"
	    + "Clicking on Legend Editor will open a window will give you\n"
	    + " choices about how to display that layer.";
		
		helpText.add(s1);
		String s2 = "Layer Control is a Menu on the menu bar.  If you have selected\n"
		+ " a layer by clicking on a legend in the toc (table of contents) to the\n"
		+ " left of the map, you will be able to promote or demote that layer.";
		
		helpText.add(s2);
		String s3 = "You may use the help tool to learn about other tools.\n"
	    + "You may right click on other items in the toolbar to learn about them.";
		
		helpText.add(s3);
		String s4 = "The zoom in tool will allow you to enlarge parts of the map"
		+ "\n in order to see the map in greater detail.";
		
		helpText.add(s4);
		String s5 = "You must have a selected a layer to use the Zoom to Active"
		+ "Layer tool.\n"
	    + "If you then click on Zoom to Active Layer, you will be able to see\n"
	    + " only the features in the layer you have selected.";

		helpText.add(s5);
		String s6 = "The Measuring tool allows you to click and release in order to\n"
		+ "measure distances in km on the map.";
		
	    helpText.add(s6);
	    String s7 = "The XY tool allows the user to add a CSV file to the map as\n"
		+ " a feature layer. Simply choose the csv file you wish to add and it will\n"
		+ " be generated as a feature layer.";
		
		helpText.add(s7);
		String s8 = "The Hotlink tool allows you to see images and data for the \n "
		+ "point you select.";

		helpText.add(s8);
		String s9 = "Print allows you to print the map.";
		
		helpText.add(s9);
		String s10 = "Add Layer allows you to add a shape file to your map. It brings\n"
		+ " up a file selection tool that allows you to select the shape fil you want\n"
		+ " from your computer." ;
		
		helpText.add(s10);
		String s11 = "The arrow tool resets the mouse to its default setting.";
		helpText.add(s11);
	}



	// FUNCTION: main()
	public static void main(String[] args) {
		System.out.println("Launching program");
		LocalGoods qstart = new LocalGoods();
		qstart.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
		qstart.setVisible(true);
		env = map.getExtent();
	}
}


class AddLyrDialog extends JDialog {
	Map map;
	ActionListener lis;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JPanel panel1 = new JPanel();
	com.esri.mo2.ui.bean.CustomDatasetEditor cus =
			new com.esri.mo2.ui.bean.CustomDatasetEditor();

	AddLyrDialog() throws IOException {

		setBounds(50,50,520,430);
		setTitle("Select a theme/layer");
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		);

		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == cancel) {
					setVisible(false);
				} else {
					try {
						setVisible(false);
						map.getLayerset().addLayer(cus.getLayer());
						map.redraw();
						if (LocalGoods.stb.getSelectedLayers() != null) {
							LocalGoods.jmpromo.setEnabled(true);
						}
					} catch(IOException e){}
				}
			}
		};

		ok.addActionListener(lis);
		cancel.addActionListener(lis);
		getContentPane().add(cus,BorderLayout.CENTER);
		panel1.add(ok);
		panel1.add(cancel);
		getContentPane().add(panel1,BorderLayout.SOUTH);
	}


	public void setMap(com.esri.mo2.ui.bean.Map map1){
		map = map1;
	}
}


class AddXYtheme extends JDialog {
	Map map;
	Vector s2 = new Vector();
	Vector s3 = new Vector();
	Vector s4 = new Vector();
	Vector s5 = new Vector();
	Vector s6 = new Vector();
	Vector s7 = new Vector();
	Vector s8 = new Vector();
	Vector s9 = new Vector();
	Vector s10 = new Vector();
	JFileChooser jfc = new JFileChooser();
	BasePointsArray bpa = new BasePointsArray();
	AddXYtheme() throws IOException {
		setBounds(50,50,520,430);
		jfc.showOpenDialog(this);
		try {
			File file  = jfc.getSelectedFile();
			FileReader fred = new FileReader(file);
			BufferedReader in = new BufferedReader(fred);
			String s;
			double x,y;
			int n = 0;
			while ((s = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s,",");
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				bpa.insertPoint(n,new com.esri.mo2.cs.geom.Point(x,y));
				s2.addElement(st.nextToken());
				s3.addElement(st.nextToken());
				s4.addElement(st.nextToken());
				s5.addElement(st.nextToken());
				s6.addElement(st.nextToken());
				s7.addElement(st.nextToken());
				s8.addElement(st.nextToken());
				s9.addElement(st.nextToken());
				s10.addElement(st.nextToken());
				n++ ;
			}
		} catch (IOException e){}
		XYfeatureLayer xyfl =
				new XYfeatureLayer(bpa,map,s2,s3,s4,s5,s6,s7,s8,s9,s10);
		xyfl.setVisible(true);
		map = LocalGoods.map;
		map.getLayerset().addLayer(xyfl);
		map.redraw();
	}

	public void setMap(com.esri.mo2.ui.bean.Map map1){
		map = map1;
	}
}



class XYfeatureLayer extends BaseFeatureLayer {
	BaseFields fields;
	private java.util.Vector featureVector;


	public XYfeatureLayer(BasePointsArray bpa, Map map, Vector s2, Vector s3,
			Vector s4, Vector s5, Vector s6, Vector s7, Vector s8, Vector s9,
			Vector s10) {

		createFeaturesAndFields(bpa,map,s2,s3,s4,s5,s6,s7,s8,s9,s10);
		BaseFeatureClass bfc = getFeatureClass("New Layer",bpa);
		setFeatureClass(bfc);
		BaseSimpleRenderer srd = new BaseSimpleRenderer();
		
		
		com.esri.mo2.map.draw.RasterMarkerSymbol rms5 = new com.esri.mo2.map.draw.RasterMarkerSymbol();
		rms5.setSizeX(16);
		rms5.setSizeY(16);
		rms5.setImageString("C:/LocalGoods/img/icons/newlayer.png");	
		srd.setSymbol(rms5) ;	
		
		setRenderer(srd);
		XYLayerCapabilities lc = new XYLayerCapabilities();
		setCapabilities(lc);
	}

	private void createFeaturesAndFields(BasePointsArray bpa,Map map,Vector s2,
			Vector s3, Vector s4, Vector s5, Vector s6, Vector s7, Vector s8,
			Vector s9, Vector s10) {
		featureVector = new java.util.Vector();
		fields = new BaseFields();
		createDbfFields();
		for(int i=0;i<bpa.size();i++) {
			BaseFeature feature = new BaseFeature();
			feature.setFields(fields);
			com.esri.mo2.cs.geom.Point p =
					new com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
			feature.setValue(0,p);
			feature.setValue(1,new Integer(i));
			feature.setValue(2,(String)s2.elementAt(i));
			feature.setValue(3,(String)s3.elementAt(i));
			feature.setValue(4,(String)s4.elementAt(i));
			feature.setValue(5,(String)s5.elementAt(i));
			feature.setValue(6,(String)s6.elementAt(i));
			feature.setValue(7,(String)s7.elementAt(i));
			feature.setValue(8,(String)s8.elementAt(i));
			feature.setValue(9,(String)s9.elementAt(i));
			feature.setDataID(new BaseDataID("New Layer",i));
			featureVector.addElement(feature);
		}
	}


	private void createDbfFields() {
		fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
		fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
		fields.addField(new BaseField("Name",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Street",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("City",java.sql.Types.VARCHAR,32,0));
		fields.addField(new BaseField("State",java.sql.Types.VARCHAR,8,0));
		fields.addField(new BaseField("ZipCode",java.sql.Types.VARCHAR,16,0));
		fields.addField(new BaseField("Phone",java.sql.Types.VARCHAR,16,0));
		fields.addField(new BaseField("URL",java.sql.Types.VARCHAR,64,0));
		fields.addField(new BaseField("Hours",java.sql.Types.VARCHAR,64,0));

	}

	public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa){
		com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
		try {
			featClass =
					new com.esri.mo2.map.mem.MemoryFeatureClass(
							MapDataset.POINT,fields);
		} catch (IllegalArgumentException iae) {}
		featClass.setName(name);
		for (int i=0;i<bpa.size();i++) {
			featClass.addFeature((Feature) featureVector.elementAt(i));
		}
		return featClass;
	}


	private final class XYLayerCapabilities
		extends com.esri.mo2.map.dpy.LayerCapabilities {

		XYLayerCapabilities() {
			for (int i=0;i<this.size(); i++) {
				setAvailable(this.getCapabilityName(i),true);
				setEnablingAllowed(this.getCapabilityName(i),true);
				getCapability(i).setEnabled(true);
			}
		}
	}
}



class AttrTab extends JDialog {
	JPanel panel1 = new JPanel();
	com.esri.mo2.map.dpy.Layer layer = LocalGoods.layer4;
	JTable jtable = new JTable(new MyTableModel());
	JScrollPane scroll = new JScrollPane(jtable);


	public AttrTab() throws IOException {
		setBounds(70,70,450,350);
		setTitle("Attribute Table");
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		);

		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableColumn tc = null;
		int numCols = jtable.getColumnCount();
		for (int j=0;j<numCols;j++) {
			tc = jtable.getColumnModel().getColumn(j);
			tc.setMinWidth(50);
		}
		getContentPane().add(scroll,BorderLayout.CENTER);
	}
}


class MyTableModel extends AbstractTableModel {
	com.esri.mo2.map.dpy.Layer layer = LocalGoods.layer4;
	MyTableModel() {
		qfilter.setSubFields(fields);
		com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
		while (cursor.hasMore()) {
			ArrayList inner = new ArrayList();
			Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
			inner.add(0,String.valueOf(row));
			for (int j=1;j<fields.getNumFields();j++) {
				inner.add(f.getValue(j).toString());
			}
			data.add(inner);
			row++;
		}
	}
	FeatureLayer flayer = (FeatureLayer) layer;
	FeatureClass fclass = flayer.getFeatureClass();
	String columnNames [] = fclass.getFields().getNames();
	ArrayList data = new ArrayList();
	int row = 0;
	int col = 0;
	BaseQueryFilter qfilter = new BaseQueryFilter();
	Fields fields = fclass.getFields();


	public int getColumnCount() {
		return fclass.getFields().getNumFields();
	}


	public int getRowCount() {
		return data.size();
	}


	public String getColumnName(int colIndx) {
		return columnNames[colIndx];
	}


	public Object getValueAt(int row, int col) {
		ArrayList temp = new ArrayList();
		temp =(ArrayList) data.get(row);
		return temp.get(col);
	}
}



class CreateShapeDialog extends JDialog {
	String name = "";
	String path = "";
	int ltype  ;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JTextField nameField =
			new JTextField("enter layer name here, then hit ENTER",25);
	com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
	JPanel panel1 = new JPanel();
	JLabel centerlabel = new JLabel();

	ActionListener lis = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			Object o = ae.getSource();
			if (o == nameField) {
				name = nameField.getText().trim();
				try {
					path = ((ShapefileFolder)
							(LocalGoods.layer4.getLayerSource())).getPath();
							System.out.println(path);
				} catch ( Exception e ) {
					path = "C:/Temp" ;
				}
				System.out.println(path+"    " + name);
			} else if (o == cancel) {
				setVisible(false);
			} else {
				try {
					ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,ltype);
				} catch(Exception e) {
				      System.err.println("1");
				      System.err.println(e);
				      System.err.println("\n2");
				      System.err.println(e.getMessage());
				      System.err.println("\n3");
				      System.err.println(e.getLocalizedMessage());
				      System.err.println("\n4");
				      System.err.println(e.getCause());
				      e.printStackTrace();
				}
				setVisible(false);
			}
		}
	};


	public CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5,
					          int layertype) {
		selectedlayer = layer5;
		ltype = layertype;
		setBounds(40,350,450,150);
	    setTitle("Create new shapefile?");
	    addWindowListener(
	    	new WindowAdapter() {
	    		public void windowClosing(WindowEvent e) {
	    			setVisible(false);
	    		}
	    	}
	    );
	    nameField.addActionListener(lis);
	    ok.addActionListener(lis);
	    cancel.addActionListener(lis);
	    String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
	      "the new name you want for the layer and click OK.<BR>" +
	      "You can then add it to the map in the usual way.<BR>"+
	      "Click ENTER after replacing the text with your layer name";
	    centerlabel.setHorizontalAlignment(JLabel.CENTER);
	    centerlabel.setText(s);
	    getContentPane().add(centerlabel,BorderLayout.CENTER);
	    panel1.add(nameField);
	    panel1.add(ok);
	    panel1.add(cancel);
	    getContentPane().add(panel1,BorderLayout.SOUTH);
	}
}



class Arrow extends Tool {

	public Arrow() {
		LocalGoods.milesLabel.setText("DIST   0 mi   ");
		LocalGoods.kmLabel.setText("   0 km    ");
		LocalGoods.map.repaint();
		LocalGoods.helpToolOn = false ;
	}
}


class Flash extends Thread {
	Legend legend;
	Flash(Legend legendin) {
		legend = legendin;
	}

	public void run() {
		for (int i=0;i<12;i++) {
			try {
				Thread.sleep(500);
				legend.toggleSelected();
			} catch (Exception e) {}
		}
	}
}


class DistanceTool extends DragTool  {
	int startx,starty,endx,endy,currx,curry;
	com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
	double distance;


	public void mousePressed(MouseEvent me) {
		startx = me.getX(); starty = me.getY();
		initPoint = LocalGoods.map.transformPixelToWorld(me.getX(),me.getY());
	}

	public void mouseReleased(MouseEvent me) {
		endx = me.getX(); endy = me.getY();
		endPoint = LocalGoods.map.transformPixelToWorld(me.getX(),me.getY());
		distance = (69.44 / (2*Math.PI)) * 360 * Math.acos(
				 Math.sin(initPoint.y * 2 * Math.PI / 360)
				 * Math.sin(endPoint.y * 2 * Math.PI / 360)
				 + Math.cos(initPoint.y * 2 * Math.PI / 360)
				 * Math.cos(endPoint.y * 2 * Math.PI / 360)
				 * (Math.abs(initPoint.x - endPoint.x) < 180 ?
                    Math.cos((initPoint.x - endPoint.x)*2*Math.PI/360):
                    Math.cos((360 -
                    		Math.abs(initPoint.x - endPoint.x))*2*Math.PI/360)));
		System.out.println( distance );
		LocalGoods.milesLabel.setText("DIST: " +
									 new Float((float)distance).toString()
									 + " mi  ");
		LocalGoods.kmLabel.setText(new Float((float)(distance*1.6093)).toString()
								  + " km");

		if (LocalGoods.acetLayer != null) {
			LocalGoods.map.remove(LocalGoods.acetLayer);
		}

		LocalGoods.acetLayer = new AcetateLayer() {
			public void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				Line2D.Double line = new Line2D.Double(startx,
							starty,
							endx,
							endy);
				g2d.setColor(new Color(0,0,250));
				g2d.draw(line);
			}
		};

		Graphics g = super.getGraphics();
		LocalGoods.map.add(LocalGoods.acetLayer);
		LocalGoods.map.redraw();
	}

	public void cancel() {};
}


class HotPick extends JDialog {

	// Photos taken of farmer's markets

			String[][] farmersMarkets = new String[][] {
			{ "C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4702.JPG",  "C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4703.JPG",
			"C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4704.JPG",  "C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4705.JPG",
			"C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4706.JPG",  "C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4710.JPG",
			"C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4708.JPG",  "C:/LocalGoods/img/pictures/farmersmarkets/littleitaly/IMAG4709.JPG"},
			{"C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4745.JPG",	"C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4747.JPG",
			"C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4748.JPG","C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4749.JPG",
			"C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4750.JPG","C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4751.JPG",
			"C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4752.JPG","C:/LocalGoods/img/pictures/farmersmarkets/pacificbeach/IMAG4753.JPG"}
			} ;
		
			String[][] farmPics = new String[][] {
			{ "C:/LocalGoods/img/pictures/farms/SuzysFarm.JPG",  "C:/LocalGoods/img/pictures/farms/SuzysFarm2.JPG"},
			{ "C:/LocalGoods/img/pictures/farms/Hillikers.JPG",  "C:/LocalGoods/img/pictures/farms/Hillikers3.JPG"},
			{ "C:/LocalGoods/img/pictures/farms/Campo.JPG",  "C:/LocalGoods/img/pictures/farms/Campo2.JPG"},
			{ "C:/LocalGoods/img/pictures/farms/Sungrown.JPG",  "C:/LocalGoods/img/pictures/farms/Sungrown2.JPG"},
			{ "C:/LocalGoods/img/pictures/farms/Honeymoon.JPG"},
			{ "C:/LocalGoods/img/pictures/farms/Heritage.JPG",  "C:/LocalGoods/img/pictures/farms/Heritage2.JPG"},
			{ "C:/LocalGoods/img/pictures/farms/Jacy.JPG"},
			{ "C:/LocalGoods/img/pictures/farms/Bates.JPG",  "C:/LocalGoods/img/pictures/farms/Bates2.JPG",
				"C:/LocalGoods/img/pictures/farms/Bates3.JPG",  "C:/LocalGoods/img/pictures/farms/Bates4.JPG"}
			} ;
			
		

	public HotPick(int id, Row row, int activeLayerIndex, Map map)
		throws IOException {

		ActionListener jblis ;
		final String os = System.getProperty("os.name");
		final JButton jb = new JButton("Click Here To Open Website",
				new ImageIcon("C:/LocalGoods/img/icons/globe.png"));

		Container container = getContentPane();

		JPanel top = new JPanel(new BorderLayout());

		setTitle(row.getDisplayValue(2));

		JPanel info = new JPanel(new GridLayout(1,2));

		JPanel infotitlePanel = new JPanel() ;
		infotitlePanel.setLayout( new FlowLayout( FlowLayout.LEADING ) );
		JLabel infotitle = new JLabel(row.getDisplayValue(2)) ;
		infotitlePanel.add(infotitle);
		info.add(infotitlePanel) ;

		String info = "<HTML>";
		JPanel infodetailsPanel = new JPanel() ;
		infodetailsPanel.setLayout( new FlowLayout( FlowLayout.LEADING ) );

		final String url = row.getDisplayValue(8) ;

		int numfieldsinrow = row.getFields().size() ;
		String[] fieldnames = row.getFields().getNames() ;
		for ( int i=3; i<numfieldsinrow; ++i ) {

			if ( i != 8 ) {
				info += fieldnames[i] + ": " +
						row.getDisplayValue(i) + "<BR>" ;
			}
		}
		JLabel infodetails = new JLabel(info) ;
		infodetailsPanel.add(infodetails) ;
		info.add(infodetailsPanel);
		top.add(info, BorderLayout.NORTH);

		JPanel pics = new JPanel(new GridLayout(1,10,10,10));
		String curLayerName = map.getLayer(activeLayerIndex).getName();
		System.out.println("LayerName = [" + curLayerName + "]" );
		
		if ( new String("Farmers Markets").equals(curLayerName) ) {
			setBounds(20,20,1200,800);

			for ( int i=0 ; i<farmersMarkets[id].length; ++i) {
				JLabel label = new JLabel(new ImageIcon(farmersMarkets[id][i]));
				pics.add(label) ;
			}
			//The pictures will likely need a horizontal scroll bar...
			JScrollPane jp = new JScrollPane( pics,
		            				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		            				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			top.add(jp, BorderLayout.CENTER);
		} else if ( new String("Farms").equals(curLayerName) ) {
			setBounds(20,20,1200,800);

			for ( int i=0 ; i<farmPics[id].length; ++i) {
				JLabel label = new JLabel(new ImageIcon(farmPics[id][i]));
				pics.add(label) ;
			}
			JScrollPane jp = new JScrollPane( pics,
		            				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		            				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			top.add(jp, BorderLayout.CENTER);
			
		}else{
			setBounds(50,50,500,275);
		}


		jblis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();

				String b = "C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE";
				b = b + " " + url ;

				if (source == jb) {
					try {
						if (os.indexOf("Windows") != -1) {
							Runtime.getRuntime().exec(b);
						} else if (os.indexOf("Mac") != -1) {
							b = "open" + "-a" + "Safari" + url ;
							Runtime.getRuntime().exec(b);
							}
					} catch (Exception ex) {
						System.out.println("cannot execute command. " + ex);
						ex.printStackTrace();
					}
				}
			}
		} ;


		jb.addActionListener(jblis);
		top.add(jb, BorderLayout.SOUTH);

		container.add(top) ;

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		);
	}
}


class HelpDialog extends JDialog {
	JTextArea ha ;


	public HelpDialog(String input) throws IOException {
		setBounds(70,70,450,250) ;
		setTitle("Help");
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			}
		) ;
		ha = new JTextArea(input,7,40);
		JScrollPane sp = new JScrollPane(ha);
		ha.setEditable(false);
		getContentPane().add(sp,"Center");
	}
}


class HelpTool extends Tool {
}