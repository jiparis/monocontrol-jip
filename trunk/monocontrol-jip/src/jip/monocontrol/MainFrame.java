package jip.monocontrol;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton saveFileButton;
	private JButton openFileButton;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JComboBox jComboBox1;
	private JComboBox jComboBox0;
	private JPanel jPanel0;
	private JLabel jLabel2;
	private JButton addXFaderButton;
	private JButton addFaderButton;
	private JButton addXYButton;
	private JButton addTracksButton;
	private JButton addLooperButton;
	private JLabel jLabel3;
	
	static ViewManager vm;
	MidiObject midi;
	List<Info> midiInputDevices, midiOutputDevices;
	private JRadioButton pushRadioButton;
	private JRadioButton toggleRadioButton;
	private JLabel jLabel4;
	private JTextField ccTextField;
	private JLabel jLabel5;
	private JTextField channelTextField;
	private ButtonGroup buttonGroup1;
	private JLabel helpLabel;
	private JButton addMatrixButton;
	private JRadioButton noteRadioButton;
	private JButton addButtonButton;
	private JLabel jLabel6;
	private JTextField jTextField0;
	private JLabel jLabel7;
	private JLabel jLabel8;
	private JLabel jLabel9;
	private JLabel jLabel10;
	private JTextField oscInTextField;
	private JLabel jLabel11;
	private JTextField oscOutTextField;
	private JCheckBox gateLoopChockesCheckBox;
	private JLabel jLabel12;
	private JComboBox resolutionComboBox;
	private JLabel jLabel13;
	private JRadioButton size64;
	private JRadioButton size128;
	private JRadioButton size256;
	private JLabel jLabel14;
	private ButtonGroup buttonGroup2;
	private JLabel jLabel15;
	private JComboBox stepsComboBox;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public MainFrame() {
		initComponents();
		vm = new ViewManager(this); // object that manages the diffrent views
		midi = new MidiObject(this);
		midiInputDevices = MidiObject.getAvailibleInputs();
		jComboBox0.addItem("");
		for (Info info: midiInputDevices) {
			jComboBox0.addItem(info);			
		}
		jComboBox0.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem() instanceof Info && e.getStateChange() == ItemEvent.SELECTED)
					midi.setInputDevice((Info)e.getItem());				
			}		
		});
		
		midiOutputDevices = MidiObject.getAvailibleOutputs();
		jComboBox1.addItem("");
		for (Info info: midiOutputDevices) {
			jComboBox1.addItem(info);			
		}
		jComboBox1.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem() instanceof Info && e.getStateChange() == ItemEvent.SELECTED)
					midi.setOutputDevice((Info)e.getItem());				
			}		
		});
		vm.returnToPlayMode();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabel1(), new Constraints(new Leading(170, 12, 12), new Leading(12, 11, 12, 12)));
		add(getJComboBox0(), new Constraints(new Leading(8, 156, 12, 12), new Leading(24, 10, 10)));
		add(getJComboBox1(), new Constraints(new Leading(170, 153, 10, 10), new Leading(24, 12, 12)));
		add(getJLabel0(), new Constraints(new Leading(9, 10, 10), new Leading(11, 11, 11)));
		add(getAddLooperButton(), new Constraints(new Leading(8, 74, 10, 10), new Leading(239, 10, 10)));
		add(getAddTracksButton(), new Constraints(new Leading(8, 74, 10, 10), new Leading(220, 11, 11)));
		add(getAddXYButton(), new Constraints(new Leading(8, 74, 10, 10), new Leading(199, 10, 10)));
		add(getAddFaderButton(), new Constraints(new Leading(8, 74, 10, 10), new Leading(178, 10, 10)));
		add(getAddMatrixButton(), new Constraints(new Leading(8, 74, 10, 10), new Leading(91, 11, 11)));
		add(getJLabel2(), new Constraints(new Leading(10, 10, 10), new Leading(53, 11, 11)));
		add(getAddXFaderButton(), new Constraints(new Leading(8, 74, 10, 10), new Leading(157, 10, 10)));
		add(getAddButtonButton(), new Constraints(new Leading(8, 74, 10, 10), new Leading(69, 10, 10)));
		add(getOpenFileButton(), new Constraints(new Leading(284, 74, 10, 10), new Leading(208, 11, 11)));
		add(getSaveFileButton(), new Constraints(new Leading(284, 74, 10, 10), new Leading(235, 11, 11)));
		add(getJLabel3(), new Constraints(new Leading(235, 123, 10, 10), new Leading(4, 11, 11)));
		add(getHelpLabel(), new Constraints(new Leading(8, 384, 10, 10), new Leading(264, 23, 10, 10)));
		add(getOscInTextField(), new Constraints(new Leading(344, 52, 10, 10), new Leading(57, 11, 11)));
		add(getOscOutTextField(), new Constraints(new Leading(344, 52, 10, 10), new Leading(80, 11, 11)));
		add(getJLabel10(), new Constraints(new Leading(306, 10, 10), new Leading(60, 11, 11)));
		add(getJLabel11(), new Constraints(new Leading(298, 10, 10), new Leading(85, 11, 11)));
		add(getButtonPanel(), new Constraints(new Leading(88, 188, 10, 10), new Leading(53, 208, 11, 11)));
		add(getSize128(), new Constraints(new Leading(313, 10, 10), new Leading(127, 7, 7)));
		add(getSize256(), new Constraints(new Leading(356, 6, 6), new Leading(127, 7, 7)));
		add(getSize64(), new Constraints(new Leading(278, 6, 6), new Leading(127, 7, 7)));
		add(getJLabel14(), new Constraints(new Leading(354, 10, 10), new Leading(113, 10, 10)));
		initButtonGroup1();
		initButtonGroup2();
		setSize(402, 292);
	}

	private JComboBox getStepsComboBox() {
		if (stepsComboBox == null) {
			stepsComboBox = new JComboBox();
			stepsComboBox.setModel(new DefaultComboBoxModel(new Object[] { "8", "16", "32", "64", "128" }));
			stepsComboBox.setSelectedIndex(2);
			stepsComboBox.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					stepsComboBoxActionActionPerformed(event);
				}
			});
		}
		return stepsComboBox;
	}

	private JLabel getJLabel15() {
		if (jLabel15 == null) {
			jLabel15 = new JLabel();
			jLabel15.setText("Pattern recorders steps");
		}
		return jLabel15;
	}

	private void initButtonGroup2() {
		buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(getSize64());
		buttonGroup2.add(getSize128());
		buttonGroup2.add(getSize256());
	}

	private JLabel getJLabel14() {
		if (jLabel14 == null) {
			jLabel14 = new JLabel();
			jLabel14.setText("Monome");
		}
		return jLabel14;
	}

	private JRadioButton getSize256() {
		if (size256 == null) {
			size256 = new JRadioButton();
			size256.setText("256");
			size256.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					size256ActionActionPerformed(event);
				}
			});
		}
		return size256;
	}

	private JRadioButton getSize128() {
		if (size128 == null) {
			size128 = new JRadioButton();
			size128.setText("128");
			size128.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					size128ActionActionPerformed(event);
				}
			});
		}
		return size128;
	}

	private JRadioButton getSize64() {
		if (size64 == null) {
			size64 = new JRadioButton();
			size64.setSelected(true);
			size64.setText("64");
			size64.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					size64ActionActionPerformed(event);
				}
			});
		}
		return size64;
	}

	private JLabel getJLabel13() {
		if (jLabel13 == null) {
			jLabel13 = new JLabel();
			jLabel13.setFont(new Font("Tahoma", Font.PLAIN, 9));
			jLabel13.setText("24 = 1/8, 48 = 1/4 ...");
		}
		return jLabel13;
	}

	private JComboBox getResolutionComboBox() {
		if (resolutionComboBox == null) {
			resolutionComboBox = new JComboBox();
			resolutionComboBox.setModel(new DefaultComboBoxModel(new Object[] { "6", "12", "24", "48", "96" }));
			resolutionComboBox.setSelectedIndex(2);
			resolutionComboBox.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					resolutionComboBoxActionActionPerformed(event);
				}
			});
		}
		return resolutionComboBox;
	}

	private JLabel getJLabel12() {
		if (jLabel12 == null) {
			jLabel12 = new JLabel();
			jLabel12.setText("Quant");
		}
		return jLabel12;
	}

	private JCheckBox getGateLoopChockesCheckBox() {
		if (gateLoopChockesCheckBox == null) {
			gateLoopChockesCheckBox = new JCheckBox();
			gateLoopChockesCheckBox.setText("Gate Loop Chockes");
			gateLoopChockesCheckBox.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					gateLoopChockesCheckBoxActionActionPerformed(event);
				}
			});
		}
		return gateLoopChockesCheckBox;
	}

	private JTextField getOscOutTextField() {
		if (oscOutTextField == null) {
			oscOutTextField = new JTextField();
			oscOutTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			oscOutTextField.setText("8080");
			oscOutTextField.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					oscOutTextFieldActionActionPerformed(event);
				}
			});
		}
		return oscOutTextField;
	}

	private JLabel getJLabel11() {
		if (jLabel11 == null) {
			jLabel11 = new JLabel();
			jLabel11.setText("OSC Out");
		}
		return jLabel11;
	}

	private JTextField getOscInTextField() {
		if (oscInTextField == null) {
			oscInTextField = new JTextField();
			oscInTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			oscInTextField.setText("8000");
			oscInTextField.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					oscInTextFieldActionActionPerformed(event);
				}
			});
		}
		return oscInTextField;
	}

	private JLabel getJLabel10() {
		if (jLabel10 == null) {
			jLabel10 = new JLabel();
			jLabel10.setText("OSC In");
		}
		return jLabel10;
	}

	private JLabel getJLabel9() {
		if (jLabel9 == null) {
			jLabel9 = new JLabel();
			jLabel9.setText("Other parameters");
		}
		return jLabel9;
	}

	private JLabel getJLabel8() {
		if (jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText("Midi parameters");
		}
		return jLabel8;
	}

	private JLabel getJLabel7() {
		if (jLabel7 == null) {
			jLabel7 = new JLabel();
			jLabel7.setText("Button type");
		}
		return jLabel7;
	}

	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
		}
		return jTextField0;
	}

	private JLabel getJLabel6() {
		if (jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setText("CC");
		}
		return jLabel6;
	}

	private JButton getAddButtonButton() {
		if (addButtonButton == null) {
			addButtonButton = new JButton();
			addButtonButton.setText("Button");
			addButtonButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
			addButtonButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					addButtonButtonMouseMouseClicked(event);
				}
			});
		}
		return addButtonButton;
	}

	private JRadioButton getNoteRadioButton() {
		if (noteRadioButton == null) {
			noteRadioButton = new JRadioButton();
			noteRadioButton.setSelected(true);
			noteRadioButton.setText("Note");
			noteRadioButton.addChangeListener(new ChangeListener() {
	
				public void stateChanged(ChangeEvent event) {
					noteRadioButtonChangeStateChanged(event);
				}
			});
		}
		return noteRadioButton;
	}

	private JButton getAddMatrixButton() {
		if (addMatrixButton == null) {
			addMatrixButton = new JButton();
			addMatrixButton.setText("Matrix");
			addMatrixButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
			addMatrixButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					addMatrixButtonMouseMouseClicked(event);
				}
			});
		}
		return addMatrixButton;
	}

	private JLabel getHelpLabel() {
		if (helpLabel == null) {
			helpLabel = new JLabel();
			helpLabel.setText("Welcome");
		}
		return helpLabel;
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getNoteRadioButton());
		buttonGroup1.add(getPushRadioButton());
		buttonGroup1.add(getToggleRadioButton());
	}

	private JTextField getChannelTextField() {
		if (channelTextField == null) {
			channelTextField = new JTextField();
			channelTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			channelTextField.setText("1");
			channelTextField.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					channelTextFieldActionActionPerformed(event);
				}
			});
		}
		return channelTextField;
	}

	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Channel (1-16)");
		}
		return jLabel5;
	}

	private JTextField getCcTextField() {
		if (ccTextField == null) {
			ccTextField = new JTextField();
			ccTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			ccTextField.setText("0");
			ccTextField.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
					ccTextFieldActionActionPerformed(event);
				}
			});
		}
		return ccTextField;
	}

	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("CC/Note (0-127)");
		}
		return jLabel4;
	}

	private JRadioButton getToggleRadioButton() {
		if (toggleRadioButton == null) {
			toggleRadioButton = new JRadioButton();
			toggleRadioButton.setText("Toggle");
			toggleRadioButton.addChangeListener(new ChangeListener() {
	
				public void stateChanged(ChangeEvent event) {
					toggleRadioButtonChangeStateChanged(event);
				}
			});
		}
		return toggleRadioButton;
	}

	private JRadioButton getPushRadioButton() {
		if (pushRadioButton == null) {
			pushRadioButton = new JRadioButton();
			pushRadioButton.setText("Push");
			pushRadioButton.addChangeListener(new ChangeListener() {
	
				public void stateChanged(ChangeEvent event) {
					pushRadioButtonChangeStateChanged(event);
				}
			});
		}
		return pushRadioButton;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel3.setText("jip.monocontrol by Jip");
		}
		return jLabel3;
	}

	private JButton getAddLooperButton() {
		if (addLooperButton == null) {
			addLooperButton = new JButton();
			addLooperButton.setText("Looper");
			addLooperButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
			addLooperButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					addLooperButtonMouseMouseClicked(event);
				}
			});
		}
		return addLooperButton;
	}

	private JButton getAddTracksButton() {
		if (addTracksButton == null) {
			addTracksButton = new JButton();
			addTracksButton.setText("Tracks");
			addTracksButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
			addTracksButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					addTracksButtonMouseMouseClicked(event);
				}
			});
		}
		return addTracksButton;
	}

	private JButton getAddXYButton() {
		if (addXYButton == null) {
			addXYButton = new JButton();
			addXYButton.setText("XY");
			addXYButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
			addXYButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					addXYButtonMouseMouseClicked(event);
				}
			});
		}
		return addXYButton;
	}

	private JButton getAddFaderButton() {
		if (addFaderButton == null) {
			addFaderButton = new JButton();
			addFaderButton.setText("Fader");
			addFaderButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
			addFaderButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton5MouseMouseClicked(event);
				}
			});
		}
		return addFaderButton;
	}

	private JButton getAddXFaderButton() {
		if (addXFaderButton == null) {
			addXFaderButton = new JButton();
			addXFaderButton.setText("CrossFader");
			addXFaderButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
			addXFaderButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					addXFaderButtonMouseMouseClicked(event);
				}
			});
		}
		return addXFaderButton;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Add ...");
		}
		return jLabel2;
	}

	private JPanel getButtonPanel() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setBorder(BorderFactory.createTitledBorder(null, "Parameters for new objects", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font(
					"Tahoma", Font.PLAIN, 11), Color.black));
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getNoteRadioButton(), new Constraints(new Leading(4, 10, 10), new Leading(12, 10, 10)));
			jPanel0.add(getPushRadioButton(), new Constraints(new Leading(55, 6, 6), new Leading(12, 10, 10)));
			jPanel0.add(getToggleRadioButton(), new Constraints(new Leading(108, 6, 6), new Leading(12, 10, 10)));
			jPanel0.add(getJLabel7(), new Constraints(new Leading(8, 10, 10), new Leading(0, 11, 11)));
			jPanel0.add(getJLabel8(), new Constraints(new Leading(6, 10, 10), new Leading(36, 11, 11)));
			jPanel0.add(getJLabel4(), new Constraints(new Leading(8, 10, 10), new Leading(54, 10, 10)));
			jPanel0.add(getCcTextField(), new Constraints(new Leading(94, 58, 10, 10), new Leading(50, 11, 11)));
			jPanel0.add(getJLabel5(), new Constraints(new Leading(8, 10, 10), new Leading(76, 11, 11)));
			jPanel0.add(getChannelTextField(), new Constraints(new Leading(95, 58, 10, 10), new Leading(73, 11, 11)));
			jPanel0.add(getJLabel9(), new Constraints(new Leading(8, 10, 10), new Leading(99, 11, 11)));
			jPanel0.add(getGateLoopChockesCheckBox(), new Constraints(new Leading(6, 6, 6), new Leading(111, 10, 10)));
			jPanel0.add(getJLabel12(), new Constraints(new Leading(10, 10, 10), new Leading(136, 20, 11, 11)));
			jPanel0.add(getJLabel15(), new Constraints(new Leading(10, 10, 10), new Leading(164, 11, 11)));
			jPanel0.add(getStepsComboBox(), new Constraints(new Leading(128, 43, 10, 10), new Leading(158, 11, 11)));
			jPanel0.add(getJLabel13(), new Constraints(new Leading(87, 10, 10), new Leading(141, 11, 11)));
			jPanel0.add(getResolutionComboBox(), new Constraints(new Leading(44, 10, 10), new Leading(136, 11, 11)));
		}
		return jPanel0;
	}

	private JComboBox getJComboBox0(){
	if(jComboBox0==null){
	jComboBox0 = new JComboBox();
	}
	return jComboBox0;
	}

	private JComboBox getJComboBox1() {
		if (jComboBox1 == null) {
			jComboBox1 = new JComboBox();
		}
		return jComboBox1;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Midi Out");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Midi In");
		}
		return jLabel0;
	}

	private JButton getOpenFileButton() {
		if (openFileButton == null) {
			openFileButton = new JButton();
			openFileButton.setText("Open ...");
			openFileButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButton0MouseMouseClicked(event);
				}
			});
		}
		return openFileButton;
	}

	private JButton getSaveFileButton() {
		if (saveFileButton == null) {
			saveFileButton = new JButton();
			saveFileButton.setText("Save ...");
			saveFileButton.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					saveFileButtonMouseMouseClicked(event);
				}
			});
		}
		return saveFileButton;
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}

	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final MainFrame frame = new MainFrame();
				frame.setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
				frame.setTitle("jip.monocon");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.addWindowListener(new WindowListener(){

					public void windowActivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowClosed(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowClosing(WindowEvent e) {
						frame.midi.closeOutputDevice();
						frame.midi.closeInputDevice();
					}

					public void windowDeactivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowDeiconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowIconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowOpened(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
					
				});
				frame.setVisible(true);
				
			}
		});
	}
	
	void setMidiOut(int value) {		
		midi.setOutputDevice(midiOutputDevices.get(value));		
	}

	void setMidiIn(int value) {		
			
	}
	
	private void jButton0MouseMouseClicked(MouseEvent event) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				if (f.getName().toLowerCase().endsWith(".xml")){
					return true;
				}
				else return false;
			}

			@Override
			public String getDescription() {
				
				return "XML Document";
			}
			
		});
		int r = fc.showOpenDialog(this);
		if(r == JFileChooser.APPROVE_OPTION) {
			importVMLayout(fc.getSelectedFile());
			this.setTitle("jip.monocontrol - " + fc.getSelectedFile().getName());
		}

	}
	

	private void setStatusLabel(String string) {
		helpLabel.setText(string);		
	}

	public void importVMLayout(File file) {

		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(file);
			importVMLayout(doc);
		} catch (Exception e) {
			e.printStackTrace();			
		}

	}

	@SuppressWarnings("unchecked")
	public void importVMLayout(Document layout) {

		vm.clearViews();

		int posx, posy, sizex, sizey, channel, cc;
		if (layout.getRootElement().getName().equals("MonoControlLayout")) {
			List<Element> itr = layout.getRootElement().getChildren();

			for (Element view : itr) {
				int viewID = Integer.parseInt(view.getAttributeValue("viewID"));
				for (Element object : (List<Element>) view.getChildren()) {
					channel = Integer.parseInt(object
							.getAttributeValue("channel"));
					sizey = Integer.parseInt(object.getAttributeValue("sizey"));
					sizex = Integer.parseInt(object.getAttributeValue("sizex"));
					posy = Integer.parseInt(object.getAttributeValue("posy"));
					posx = Integer.parseInt(object.getAttributeValue("posx"));
					cc = Integer.parseInt(object.getAttributeValue("cc"));

					ControlObject co = vm.addControlObject(object.getName(),
							viewID, cc, channel, posx, posy, sizex, sizey);
					co.loadJDOMXMLElement(object); // load specific xml data and
													// initialize object
				}
			}

		}
	}
	
	public void exportVMLayout(ViewManager vm, File file) {
		
		Element layout = new Element("MonoControlLayout");
		Element object;
		Element view;
		for (int i = 0; i < vm.views.length; i++) {
			view = new Element("view");
			view.setAttribute("viewID", String.valueOf(i));
			for (int j = 0; j < vm.views[i].objects.size(); j++) {
				ControlObject co = vm.views[i].objects.get(j);
				object = new Element(co.getType());
				object.setAttribute("channel", String.valueOf(co.getChannel()));
				object.setAttribute("sizey", String.valueOf(co.getSizeY()));
				object.setAttribute("sizex", String.valueOf(co.getSizeX()));
				object.setAttribute("posy", String.valueOf(co.getPosY()));
				object.setAttribute("posx", String.valueOf(co.getPosX()));
				object.setAttribute("cc", String.valueOf(co.getCCValue()));
				object = co.toJDOMXMLElement(object);

				view.addContent(object);
			}
			layout.addContent(view);
		}
		org.jdom.output.XMLOutputter fmt = new XMLOutputter();
		try {
			FileWriter fileWriter = new FileWriter(file);
			fmt.output(layout, fileWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void jButton5MouseMouseClicked(MouseEvent event) {
		setStatusLabel("Add Fader: Hold a button, then press a button above the button you hold");
		vm.setMode(2, "fader");
	}

	private void addButtonButtonMouseMouseClicked(MouseEvent event) {
		noteRadioButton.setSelected(false);
		pushRadioButton.setSelected(false);
		toggleRadioButton.setSelected(false);
		setStatusLabel("Please select a button type");

	}

	private void addXFaderButtonMouseMouseClicked(MouseEvent event) {
		setStatusLabel("Add Crossfader: Hold a button, the aress a button on the right of the one you hold");
		vm.setMode(2, "crossfader");
	}

	private void addXYButtonMouseMouseClicked(MouseEvent event) {
		setStatusLabel("Add XYFader: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(5);
	}

	private void addTracksButtonMouseMouseClicked(MouseEvent event) {
		setStatusLabel("Add Ableton Tracks: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(7);
	}

	private void addLooperButtonMouseMouseClicked(MouseEvent event) {
		setStatusLabel("Add Looper: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(6);
	}

	private void saveFileButtonMouseMouseClicked(MouseEvent event) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				if (f.getName().toLowerCase().endsWith(".xml")){
					return true;
				}
				else return false;
			}

			@Override
			public String getDescription() {
				
				return "XML Document";
			}
			
		});
		int r = fc.showSaveDialog(this);
		if(r == JFileChooser.APPROVE_OPTION) {
			exportVMLayout(vm, fc.getSelectedFile());
			this.setTitle("jip.monocontrol - " + fc.getSelectedFile().getName());
		}
	}

	private void addMatrixButtonMouseMouseClicked(MouseEvent event) {
		setStatusLabel("Add ButtonMatrix: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(4);
	}

	private void noteRadioButtonChangeStateChanged(ChangeEvent event) {
		if (((JRadioButton) event.getSource()).isSelected()){
			setStatusLabel("Add NoteButton: Press a button to create a NoteButton");
			vm.setMode(3, "noteButton");
		}
	}

	private void pushRadioButtonChangeStateChanged(ChangeEvent event) {
		if (((JRadioButton) event.getSource()).isSelected()){
			setStatusLabel("Add PushButton: Press a button to create a PushButton");
			vm.setMode(3, "pushButton");
		}
	}

	private void toggleRadioButtonChangeStateChanged(ChangeEvent event) {
		if (((JRadioButton) event.getSource()).isSelected()){
			setStatusLabel("Add ToggleButton: Press a button to create a PushButton");
			vm.setMode(3, "toggleButton");
		}
	}

	private void ccTextFieldActionActionPerformed(ActionEvent event) {
		int ccval = Integer.parseInt(ccTextField.getText());
		if (ccval < 0 || ccval > 127)
			setStatusLabel("Invalid CC Number!");
		else {
			if (vm.selected != null && vm.getMode() == -1)
				vm.selected.setCCValue(ccval);
			else {
				vm.nextObjectsCC = ccval;
				setStatusLabel("Next object created will have CC: "
						+ vm.nextObjectsCC + " Channel: "
						+ vm.nextObjectsChannel);
			}
		}
	}

	private void channelTextFieldActionActionPerformed(ActionEvent event) {
		int cval = Integer.parseInt(channelTextField.getText()) - 1;
		if (cval < 0 || cval > 15)
			setStatusLabel("Invalid Channel Number!");
		else {
			if (vm.selected != null && vm.getMode() == -1)
				vm.selected.setChannel(cval);
			else {
				vm.nextObjectsChannel = cval;
				setStatusLabel("Next object created will have CC: "
						+ vm.nextObjectsCC + " Channel: "
						+ (vm.nextObjectsChannel + 1));
			}
		}
	}

	private void oscOutTextFieldActionActionPerformed(ActionEvent event) {
		MonoControl.OSCOutPort = Integer.parseInt(oscOutTextField.getText());
		vm.m = new Monome(vm);
		setStatusLabel("OSC Out port changed to: " + MonoControl.OSCOutPort);
	}

	private void oscInTextFieldActionActionPerformed(ActionEvent event) {
		MonoControl.OSCInPort = Integer.parseInt(oscInTextField.getText());
		vm.m = new Monome(vm);
		setStatusLabel("OSC In port changed to: " + MonoControl.OSCInPort);

	}

	private void size64ActionActionPerformed(ActionEvent event) {
		MonoControl.monomeSizeX = 8;
		MonoControl.monomeSizeY = 8;
	}
	private void size128ActionActionPerformed(ActionEvent event) {
		MonoControl.monomeSizeX = 16;
		MonoControl.monomeSizeY = 8;
	}

	private void size256ActionActionPerformed(ActionEvent event) {
		MonoControl.monomeSizeX = 16;
		MonoControl.monomeSizeY = 16;
	}

	private void gateLoopChockesCheckBoxActionActionPerformed(ActionEvent event) {
		JCheckBox chk = (JCheckBox) event.getSource();
		MonoControl.gateLoopChockes = chk.isSelected();
	}

	private void resolutionComboBoxActionActionPerformed(ActionEvent event) {
		JComboBox box = (JComboBox) event.getSource();
		MonoControl.RESOLUTION = Integer.parseInt((String) box.getSelectedItem());
	}

	private void stepsComboBoxActionActionPerformed(ActionEvent event) {
		MonoControl.PATTERNSTEPS = Integer.parseInt((String)((JComboBox) event.getSource()).getSelectedItem());
	}

	

}
