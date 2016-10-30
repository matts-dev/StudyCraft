package StudyCraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is a quick project I am making over two weeks. It will not have much
 * scalable code I am trying to write it so I can use it immediately. There are
 * some design decisions I made simply for the sake of getting this program
 * running. What I am trying to say is that this program does not accurately
 * represent my ability as a programmer; instead it represents the kind of work
 * I can brute force program.
 * 
 * Edit: I revisted this project to a class, module, and file system.
 * 
 * StudyCraft is a program I made to assist with memorization and studying. It
 * reads .txt files the user creates and quizzes the user on the contents of the
 * file. It uses tabs and indentation to digest the file into modules, which can
 * be randomized.
 * 
 * @author Matt Stone
 * @version 5-27-2016
 *
 */
public class GUI {
	// TODO fix access modifiers to all private
	private JFrame frmStudycraft;
	private StudyModule studyModule;
	private String defaultFile = "StudyCraftSettings.txt";
	private boolean quizLoaded = false;
	private boolean firstLine = true;
	private JTextField submitTextField;
	private JTextArea textboxForAnswer;
	private JScrollPane scrollPaneAnswer;
	private JButton btnOpenfile;
	private JLabel imageLabel = null;

	// Quiz Related Fields
	protected StringBuilder quizSB = new StringBuilder();
	protected Queue<String> nextStringQueue = new LinkedList<>();
	protected JTextArea answerBox;
	protected boolean firstQuizRequest = true;
	protected boolean firstHyphen = true;
	protected int currentProgress = 1;
	protected boolean createQuizAsRandom = false;
	protected boolean typingQuizMode = false;

	// File chooser
	protected JFileChooser fchoose = new JFileChooser();
	protected FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("TXT file", "txt");
	protected boolean fileExplorerSet = false;

	// image related
	protected JPanel graphicsPanel;
	protected StringBuilder imgsb = new StringBuilder();

	// Settings
	protected String lastDirectory = null;
	protected String lastFile = null;
	protected JLabel lblStudycraft;

	// Key binds
	protected KeyboardHandler kb = new KeyboardHandler();

	// Recent Files List
	protected JPanel recentFilesPanel;
	protected DefaultListModel<String> listModel = new DefaultListModel<>();
	protected JList<String> recentFileList;
	protected HashMap<String, String> recentHashMap = new HashMap<>();

	// Course, Module, and File Interface
	protected DefaultListModel<String> listModelCourses = new DefaultListModel<>();
	protected DefaultListModel<String> listModelModules = new DefaultListModel<>();
	protected DefaultListModel<String> listModelFiles = new DefaultListModel<>();
	private JList<String> courseList;
	private JList<String> moduleList;
	private JList<String> fileList;

	// Quiz progress bar
	private JProgressBar progressBarForQuiz;
	private JLabel lblQuizProgress;
	private JTextField classModuleFileTextBox;
	
	//Type quiz variables
	private int numberCorrectTypes = 0;
	private int totalNumberAttemptedTypes = 0;
	private double percentageCorrectTypes = 1;
	
	//Other variables
	protected BufferedImage image;
	private JButton btnRestartQuiz;
	private JButton btnAddClass;
	private JButton removeFileBtn;
	private JButton loadFileToScreenBtn;
	private JButton btnFileAdd;
	private JRadioButton rdbtnLinearQuizMode;
	private JRadioButton rdbtnRandomQuizMode;
	private JRadioButton rdbtntypeQuizMode;
	private JLabel typeCorrectPercentageLabel;
	private JProgressBar progressBarForCorrect;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmStudycraft.setVisible(true);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		// init GUI
		initialize();

		// set variables for quiz methods
		setVariables();

		// More GUI logic (non-window builder)
		initializeNonWindowBuilder();

		// Load Default Quiz
		loadDefaultQuiz();

		// TODO remove Testing
		// testing();

	}

	public void testing() {
		for (int i = 0; i < 20; ++i) {
			recentHashMap.put("default", "StudyCraftTestFile.txt");
			listModel.addElement("default");
			recentHashMap.put("String" + i, "String" + i + "full");
			listModel.addElement("String" + i);
		}

	}

	protected void setupImage() {
		try {
			/*
			 * String directory = System.getProperty("user.dir"); directory +=
			 * "\\" + "test.png"; System.out.println(directory);
			 */
			// image = loadImage("test.png");
			loadImage("StudyCraft.png");

		} catch (IOException e) {
			System.out.println("Failed to default image");
		} catch (Exception other) {
			other.printStackTrace();
		}

	}

	protected void loadImage(String imgName) throws IOException {

		// load image with the unscaled file
		image = ImageIO.read(new File(imgName));

		// set size of image
		int squareValue = 320;
		int width = squareValue;
		int height = squareValue;

		// create a scaled image from the unscaled image
		BufferedImage imageResized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graph2d = imageResized.createGraphics();
		graph2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graph2d.drawImage(image, 0, 0, width, height, null);
		graph2d.dispose();

		// set field to newly sized image
		image = imageResized;

		// put image in label
		ImageIcon imageIcon = new ImageIcon(image);
		graphicsPanel.setLayout(new BorderLayout(0, 0));
		if (imageLabel == null) {
			imageLabel = new JLabel(imageIcon);
		} else {
			imageLabel.setIcon(imageIcon);
		}
		clearGraphicsPanelContents();
		graphicsPanel.add(imageLabel);
	}

	protected void clearGraphicsPanelContents() {
		Component[] currentComponents = graphicsPanel.getComponents();
		if (currentComponents != null) {
			for (int i = 0; i < currentComponents.length; ++i) {
				graphicsPanel.remove(currentComponents[i]);
			}
		}
	}

	protected void loadDefaultQuiz() {
		if (lastFile != null) {
			createQuiz(lastFile);
		} else {
			createQuiz(defaultFile);
		}
	}

	protected void initializeNonWindowBuilder() {
		loadCourseModuleFileSettings();
		loadOrcreateSettingFile();
		setupKeyBinding();
		setupImage();
		setupScrollBars();

	}

	protected void setupScrollBars() {
		// Get bars
		JScrollBar vScrollBar = scrollPaneAnswer.getVerticalScrollBar();
		JScrollBar hScrollBar = scrollPaneAnswer.getHorizontalScrollBar();

		vScrollBar.setBackground(Color.BLACK);
		hScrollBar.setBackground(Color.BLACK);

		// TODO: this doesn't change the thumb color
		vScrollBar.setForeground(Color.LIGHT_GRAY);
	}

	private void setupKeyBinding() {
		kb.setOpenFileButton(btnOpenfile);
		kb.setGui(this);
		frmStudycraft.addKeyListener(kb);
		// submitTextField.addKeyListener(kb); //TODO add keyboard shortcut that
		// will put cursor in text field, and make enter textfield to submit
		// regardless of focus
		answerBox.addKeyListener(kb);
	}

	/**
	 * Untested.
	 * 
	 * @param comp
	 *            component
	 * @param kb
	 *            the keyListener
	 */
	protected void addKeyListenerToAllComponents(JComponent comp, KeyListener kb) {
		comp.addKeyListener(kb);
		Component[] compArray = comp.getComponents();
		for (int i = 0; i < compArray.length; ++i) {
			if (comp instanceof JComponent) {
				addKeyListenerToAllComponents((JComponent) comp, kb);
			}
		}
	}

	protected void loadOrcreateSettingFile() {
		// check if file exists
		try (FileReader fReader = new FileReader("StudyCraftSettings.txt"); Scanner fScan = new Scanner(fReader)) {
			// open file if file exists
			loadSettingsFile(fScan);
		} catch (FileNotFoundException fnfe) {
			// create file if no file exists
			createSettingsFile();
		} catch (IOException ioe) {
			// Error reporting
			ioe.printStackTrace();
			answerBox.setText(ioe.getMessage() + "\n" + ioe.getLocalizedMessage());
		}

	}

	protected void loadSettingsFile(Scanner fScan) {
		loadDirectoryPath(fScan);
		loadFilePath(fScan);
		// LoadPathInto(lastDirectory, fScan);
	}

	private void loadDirectoryPath(Scanner fScan) {
		if (fScan.hasNext()) {
			String directoryPath = fScan.nextLine();
			for (int i = 0; i < directoryPath.length(); ++i) {
				// Find : and make sure there is a character after it
				if (directoryPath.charAt(i) == ':' && i < directoryPath.length() - 1) {
					directoryPath = directoryPath.substring(i + 1, directoryPath.length());
					break;
				}
			}
			// If the path did not save "null"
			if (!directoryPath.equals("null")) {
				lastDirectory = directoryPath;
			}
		}

	}

	private void loadFilePath(Scanner fScan) {
		if (fScan.hasNext()) {
			String filePathStr = fScan.nextLine();
			for (int i = 0; i < filePathStr.length(); ++i) {
				// Find : and make sure there is a character after it
				if (filePathStr.charAt(i) == ':' && i < filePathStr.length() - 1) {
					filePathStr = filePathStr.substring(i + 1, filePathStr.length());
					break;
				}
			}
			// If the path did not save "null"
			if (!filePathStr.equals("null")) {
				lastFile = filePathStr;
			}
		}

	}

	protected void saveUpdatedSettings() {
		createSettingsFile();
	}

	protected void createSettingsFile() {
		try (FileWriter fWrite = new FileWriter("StudyCraftSettings.txt", false);
				PrintWriter pWriter = new PrintWriter(fWrite)) {
			updateSettingsFile(pWriter);
		} catch (IOException e) {
			answerBox.setText(
					"Error in creating StudyCraftSettings file" + e.toString() + '\n' + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	protected void updateSettingsFile(PrintWriter pWriter) {
		// Save the last directory that a file was loaded from.
		pWriter.println("Last File Directory:" + lastDirectory);
		pWriter.println("Last Loaded File:" + lastFile);
	}

	protected void setVariables() {
		// answerBox = textboxForAnswer;
		answerBox = textboxForAnswer;
	}

	public void createQuiz(String filePath) {
		studyModule = new StudyModule();
		try {
			studyModule.createModuleFromFile(filePath);
			studyModule.startQuiz();
			answerBox.setText("Quiz Loaded");
			quizSB.setLength(0);
			nextStringQueue.clear();
			quizLoaded = true;
			firstQuizRequest = true;
			firstHyphen = true;
			firstLine = true;
			studyModule.setRandom(createQuizAsRandom);
			

			// Reset the progress bar
			currentProgress = 1;
			updateProgressBar("");
			
			// Reset the typing bar
			numberCorrectTypes = 0;
			totalNumberAttemptedTypes = 0;
			percentageCorrectTypes = 1;
			updatePercentBar();

			// reset the image
			loadImage("StudyCraft.png");
		} catch (IOException e) {
			// e.printStackTrace();
			answerBox.setText("Error in loading file: " + filePath + "\n" + e.getMessage());
			
		}
	}

	/**
	 * Determines if the gui has a quiz loaded that is ready to be used.
	 * 
	 * @return if thee quiz has tokens remaining to be returned.
	 */
	public boolean quizLoadedAndReady() {
		if (quizLoaded) {
			if (studyModule.quizFinished()) {
				quizLoaded = false;
			}
		}
		return quizLoaded;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	protected void initialize() {
		// GUI
		frmStudycraft = new JFrame();
		frmStudycraft.setExtendedState(Frame.MAXIMIZED_BOTH);
		frmStudycraft.setTitle("StudyCraft");
		frmStudycraft.getContentPane().setBackground(Color.BLACK);

		JPanel submitPanel = new JPanel();
		submitPanel.setBackground(Color.DARK_GRAY);

		JPanel answerPanel = new JPanel();
		answerPanel.setBackground(Color.DARK_GRAY);

		scrollPaneAnswer = new JScrollPane();
		scrollPaneAnswer.setViewportBorder(null);
		scrollPaneAnswer.setBackground(Color.WHITE);
		scrollPaneAnswer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		lblStudycraft = new JLabel("StudyCraft");
		lblStudycraft.setHorizontalAlignment(SwingConstants.CENTER);
		lblStudycraft.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblStudycraft.setForeground(Color.LIGHT_GRAY);
		lblStudycraft.setBackground(Color.DARK_GRAY);

		graphicsPanel = new JPanel();
		graphicsPanel.setBackground(Color.BLACK);

		JLabel labelClassView = new JLabel("Class");
		labelClassView.setHorizontalAlignment(SwingConstants.CENTER);
		labelClassView.setForeground(Color.LIGHT_GRAY);
		labelClassView.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		JLabel moduleLabe = new JLabel("Module");
		moduleLabe.setHorizontalAlignment(SwingConstants.CENTER);
		moduleLabe.setForeground(Color.LIGHT_GRAY);
		moduleLabe.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		JLabel lblFile = new JLabel("File");
		lblFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblFile.setForeground(Color.LIGHT_GRAY);
		lblFile.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		btnAddClass = new JButton("Class Add");
		btnAddClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String classText = classModuleFileTextBox.getText();
				if (classText != null && classText.length() > 0) {
					addCourseModule(classText);
				} else {
					answerBox.setText("could not create a new class");
				}
			}
		});
		btnAddClass.setForeground(Color.LIGHT_GRAY);
		btnAddClass.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		btnAddClass.setBackground(Color.BLACK);

		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String classText = classModuleFileTextBox.getText();
				if (classText != null && classText.length() > 0 || courseList.getSelectedValue() != null) {
					removeCourse(classText);
					classModuleFileTextBox.setText("");
					refreshFileList();
				} else {
					answerBox.setText(
							"could not remove; no selection, empty text string, or null string. \nType name to remove in the textbox under \"files\"");
				}
			}
		});
		btnRemove.setForeground(Color.LIGHT_GRAY);
		btnRemove.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		btnRemove.setBackground(Color.BLACK);

		JButton btnLoadClass = new JButton("Load");
		btnLoadClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCourse();
			}
		});
		btnLoadClass.setForeground(Color.LIGHT_GRAY);
		btnLoadClass.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		btnLoadClass.setBackground(Color.BLACK);

		JButton btnAdd = new JButton("Mod Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String moduleName = classModuleFileTextBox.getText();
				if (moduleName != null && moduleName.length() > 0) {
					createModule(moduleName);
				}
			}
		});
		btnAdd.setForeground(Color.LIGHT_GRAY);
		btnAdd.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		btnAdd.setBackground(Color.BLACK);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// updateModuleList();

				// update the module list, or update module list and file list
				// (if selection present)
				updateModuleListSelectionCheckWrapper();
			}

		});
		btnUpdate.setForeground(Color.LIGHT_GRAY);
		btnUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		btnUpdate.setBackground(Color.BLACK);

		JButton button_3 = new JButton("Remove");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeModule();
			}
		});
		button_3.setForeground(Color.LIGHT_GRAY);
		button_3.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		button_3.setBackground(Color.BLACK);

		btnFileAdd = new JButton("File Add");
		btnFileAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createFileInModule();
			}
		});
		btnFileAdd.setForeground(Color.LIGHT_GRAY);
		btnFileAdd.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		btnFileAdd.setBackground(Color.BLACK);

		loadFileToScreenBtn = new JButton("Open");
		loadFileToScreenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// refreshFileList();
				loadFileFromFileList();
			}
		});
		loadFileToScreenBtn.setForeground(Color.LIGHT_GRAY);
		loadFileToScreenBtn.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		loadFileToScreenBtn.setBackground(Color.BLACK);

		removeFileBtn = new JButton("Remove");
		removeFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeFileInModule();
			}
		});
		removeFileBtn.setForeground(Color.LIGHT_GRAY);
		removeFileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 8));
		removeFileBtn.setBackground(Color.BLACK);

		classModuleFileTextBox = new JTextField();
		classModuleFileTextBox.setToolTipText("Type the name for your new class, module, or file!");
		classModuleFileTextBox.setForeground(Color.LIGHT_GRAY);
		classModuleFileTextBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		classModuleFileTextBox.setColumns(10);
		classModuleFileTextBox.setCaretColor(Color.LIGHT_GRAY);
		classModuleFileTextBox.setBackground(Color.BLACK);

		JScrollPane scrollPane_1 = new JScrollPane();

		JScrollPane scrollPane_2 = new JScrollPane();

		JScrollPane scrollPane_3 = new JScrollPane();
		GroupLayout gl_answerPanel = new GroupLayout(answerPanel);
		gl_answerPanel.setHorizontalGroup(
			gl_answerPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_answerPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_answerPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_answerPanel.createSequentialGroup()
							.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
							.addGap(17))
						.addGroup(gl_answerPanel.createSequentialGroup()
							.addGroup(gl_answerPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_answerPanel.createSequentialGroup()
									.addGroup(gl_answerPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(classModuleFileTextBox, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
										.addGroup(gl_answerPanel.createSequentialGroup()
											.addComponent(btnFileAdd, GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(loadFileToScreenBtn, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(removeFileBtn, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)))
									.addGap(18))
								.addGroup(gl_answerPanel.createSequentialGroup()
									.addGroup(gl_answerPanel.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblFile, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
										.addComponent(moduleLabe, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
										.addComponent(labelClassView, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
										.addGroup(gl_answerPanel.createSequentialGroup()
											.addComponent(btnAddClass, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnLoadClass, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnRemove, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
										.addGroup(gl_answerPanel.createSequentialGroup()
											.addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnUpdate, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(button_3, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
										.addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
									.addGap(18)))
							.addGap(0))
						.addGroup(gl_answerPanel.createSequentialGroup()
							.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
							.addGap(18)))
					.addGroup(gl_answerPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_answerPanel.createSequentialGroup()
							.addComponent(scrollPaneAnswer, GroupLayout.DEFAULT_SIZE, 735, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(graphicsPanel, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
							.addGap(4))
						.addGroup(gl_answerPanel.createSequentialGroup()
							.addComponent(lblStudycraft, GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
							.addGap(347)))
					.addGap(0))
		);
		gl_answerPanel.setVerticalGroup(
			gl_answerPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_answerPanel.createSequentialGroup()
					.addGroup(gl_answerPanel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblStudycraft, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addComponent(labelClassView))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_answerPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPaneAnswer, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
						.addGroup(gl_answerPanel.createSequentialGroup()
							.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
							.addGap(8)
							.addGroup(gl_answerPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnRemove, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnLoadClass, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnAddClass, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(moduleLabe, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
							.addGap(11)
							.addGroup(gl_answerPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(button_3, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
								.addComponent(btnUpdate, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
								.addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblFile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
							.addGap(15)
							.addGroup(gl_answerPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnFileAdd, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
								.addComponent(loadFileToScreenBtn, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
								.addComponent(removeFileBtn, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(classModuleFileTextBox, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
							.addGap(2))
						.addComponent(graphicsPanel, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
					.addContainerGap())
		);

		courseList = new JList<String>(listModelCourses);
		scrollPane_3.setViewportView(courseList);
		courseList.setForeground(Color.LIGHT_GRAY);
		courseList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		courseList.setBackground(Color.BLACK);

		moduleList = new JList<String>(listModelModules);
		scrollPane_2.setViewportView(moduleList);
		moduleList.setForeground(Color.LIGHT_GRAY);
		moduleList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		moduleList.setBackground(Color.BLACK);

		fileList = new JList<String>(listModelFiles);
		scrollPane_1.setViewportView(fileList);
		fileList.setForeground(Color.LIGHT_GRAY);
		fileList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		fileList.setBackground(Color.BLACK);

		textboxForAnswer = new JTextArea();
		textboxForAnswer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		textboxForAnswer.setForeground(Color.LIGHT_GRAY);
		textboxForAnswer.setTabSize(2);
		textboxForAnswer.setEditable(false);
		textboxForAnswer.setBackground(Color.BLACK);
		scrollPaneAnswer.setViewportView(textboxForAnswer);
		answerPanel.setLayout(gl_answerPanel);

		recentFilesPanel = new JPanel();
		recentFilesPanel.setBackground(Color.DARK_GRAY);

		JLabel lblRecentFiles = new JLabel("Recent Files");
		lblRecentFiles.setHorizontalAlignment(SwingConstants.CENTER);
		lblRecentFiles.setForeground(Color.LIGHT_GRAY);
		lblRecentFiles.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		JButton btnClearList = new JButton("Clear List");
		btnClearList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listModel.removeAllElements();
			}

		});
		btnClearList.setBackground(Color.BLACK);
		btnClearList.setForeground(Color.LIGHT_GRAY);

		JScrollPane scrollPane = new JScrollPane();

		JButton btnLoadItem = new JButton("Load");
		btnLoadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Action handler for the load button
				String strToLoad = recentFileList.getSelectedValue();

				// use selected string
				if (strToLoad != null) {
					// Get the full path that is stored in the hashmap with the
					// key for the file name
					strToLoad = recentHashMap.get(strToLoad);

					// Use the full path to see if file exists
					File selectedQuiz = new File(strToLoad);
					if (selectedQuiz.exists() && selectedQuiz.isFile()) {
						setLastDirectAndFile(strToLoad);
						saveUpdatedSettings();
						createQuiz(strToLoad);
					} else {
						textboxForAnswer.setText("Cannot find file as specified");
					}
				}
			}
		});
		btnLoadItem.setForeground(Color.LIGHT_GRAY);
		btnLoadItem.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnLoadItem.setBackground(Color.BLACK);
		GroupLayout gl_recentFilesPanel = new GroupLayout(recentFilesPanel);
		gl_recentFilesPanel.setHorizontalGroup(gl_recentFilesPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_recentFilesPanel.createSequentialGroup().addContainerGap()
						.addGroup(gl_recentFilesPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
								.addGroup(gl_recentFilesPanel.createSequentialGroup()
										.addComponent(btnLoadItem, GroupLayout.PREFERRED_SIZE, 79,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(lblRecentFiles, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnClearList)))
						.addContainerGap()));
		gl_recentFilesPanel.setVerticalGroup(gl_recentFilesPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_recentFilesPanel.createSequentialGroup().addContainerGap()
						.addGroup(gl_recentFilesPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblRecentFiles, GroupLayout.PREFERRED_SIZE, 23,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(btnClearList).addComponent(btnLoadItem))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE).addContainerGap()));

		recentFileList = new JList<String>(listModel);
		recentFileList.setForeground(Color.LIGHT_GRAY);
		recentFileList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		recentFileList.setBackground(Color.BLACK);
		scrollPane.setViewportView(recentFileList);
		// My addition to move recentFileList to scroll pane
		recentFilesPanel.setLayout(gl_recentFilesPanel);
		GroupLayout groupLayout = new GroupLayout(frmStudycraft.getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(submitPanel, GroupLayout.DEFAULT_SIZE, 957,
														Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(recentFilesPanel, GroupLayout.PREFERRED_SIZE, 379,
														GroupLayout.PREFERRED_SIZE))
										.addComponent(answerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
								.addContainerGap()));
		groupLayout
				.setVerticalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup().addContainerGap()
								.addComponent(answerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(recentFilesPanel, 0, 0, Short.MAX_VALUE).addComponent(submitPanel,
												GroupLayout.PREFERRED_SIZE, 123, Short.MAX_VALUE))
								.addContainerGap()));

		submitTextField = new JTextField();
		submitTextField.setCaretColor(Color.LIGHT_GRAY);
		submitTextField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		submitTextField.setForeground(Color.LIGHT_GRAY);
		// STONE - actionListener for submit button
		submitTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				quizGUI();
			}
		});

		// END STONE
		submitTextField.setBackground(Color.BLACK);
		submitTextField.setColumns(10);

		rdbtnLinearQuizMode = new JRadioButton("Normal Quiz Mode");
		rdbtnLinearQuizMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// disable random quiz mode button
				rdbtnRandomQuizMode.setSelected(false);
				if (studyModule != null) {
					studyModule.setRandom(false);
					createQuizAsRandom = false;
				} else {
					answerBox.setText("No quiz to set to linear mode");
					rdbtnLinearQuizMode.setSelected(false);
				}
			}
		});
		rdbtnLinearQuizMode.setSelected(true);
		rdbtnLinearQuizMode.setForeground(Color.LIGHT_GRAY);
		rdbtnLinearQuizMode.setBackground(Color.BLACK);
		rdbtnLinearQuizMode.setHorizontalAlignment(SwingConstants.CENTER);
		rdbtnLinearQuizMode.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		rdbtnRandomQuizMode = new JRadioButton("Random Quiz Mode");
		rdbtnRandomQuizMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// disable linear quiz mode button
				rdbtnLinearQuizMode.setSelected(false);
				if (studyModule != null) {
					studyModule.setRandom(true);
					createQuizAsRandom = true;
				} else {
					answerBox.setText("No quiz to make random");
					rdbtnRandomQuizMode.setSelected(false);
				}
			}
		});
		rdbtnRandomQuizMode.setHorizontalAlignment(SwingConstants.CENTER);
		rdbtnRandomQuizMode.setForeground(Color.LIGHT_GRAY);
		rdbtnRandomQuizMode.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		rdbtnRandomQuizMode.setBackground(Color.BLACK);

		btnRestartQuiz = new JButton("Restart Quiz");
		btnRestartQuiz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Restart Button Action Handler
				createQuiz(lastFile);
			}
		});
		btnRestartQuiz.setForeground(Color.LIGHT_GRAY);
		btnRestartQuiz.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnRestartQuiz.setBackground(Color.BLACK);

		rdbtntypeQuizMode = new JRadioButton("Typing Quiz Mode");
		rdbtntypeQuizMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtntypeQuizMode.isSelected()) {
					typingQuizMode = true;
				} else {
					typingQuizMode = false;
				}
			}
		});
		rdbtntypeQuizMode.setHorizontalAlignment(SwingConstants.CENTER);
		rdbtntypeQuizMode.setForeground(Color.LIGHT_GRAY);
		rdbtntypeQuizMode.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		rdbtntypeQuizMode.setBackground(Color.BLACK);

		progressBarForQuiz = new JProgressBar();
		progressBarForQuiz.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		progressBarForQuiz.setBackground(new Color(0, 0, 0));
		progressBarForQuiz.setForeground(new Color(0, 204, 0));

		lblQuizProgress = new JLabel("Progress: 0/0");
		lblQuizProgress.setHorizontalAlignment(SwingConstants.CENTER);
		lblQuizProgress.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblQuizProgress.setForeground(Color.LIGHT_GRAY);
		
		progressBarForCorrect = new JProgressBar();
		progressBarForCorrect.setForeground(new Color(0, 204, 0));
		progressBarForCorrect.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		progressBarForCorrect.setBackground(Color.BLACK);
		
		typeCorrectPercentageLabel = new JLabel("Correct: ");
		typeCorrectPercentageLabel.setToolTipText("In typing quiz mode, the bar below shows the percentage of the correct answers typed");
		typeCorrectPercentageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		typeCorrectPercentageLabel.setForeground(Color.LIGHT_GRAY);
		typeCorrectPercentageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		GroupLayout gl_submitPanel = new GroupLayout(submitPanel);
		gl_submitPanel.setHorizontalGroup(
			gl_submitPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_submitPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_submitPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_submitPanel.createSequentialGroup()
							.addGroup(gl_submitPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(Alignment.TRAILING, gl_submitPanel.createSequentialGroup()
									.addComponent(rdbtnLinearQuizMode, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
									.addGap(18)
									.addComponent(rdbtnRandomQuizMode, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
								.addComponent(progressBarForQuiz, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
								.addComponent(lblQuizProgress, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
							.addGap(18)
							.addGroup(gl_submitPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(typeCorrectPercentageLabel, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
								.addComponent(progressBarForCorrect, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
								.addComponent(rdbtntypeQuizMode, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
							.addGap(18)
							.addComponent(btnRestartQuiz, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
						.addComponent(submitTextField, GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_submitPanel.setVerticalGroup(
			gl_submitPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_submitPanel.createSequentialGroup()
					.addGap(16, 16, Short.MAX_VALUE)
					.addGroup(gl_submitPanel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_submitPanel.createSequentialGroup()
							.addGroup(gl_submitPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnLinearQuizMode)
								.addComponent(rdbtnRandomQuizMode)
								.addComponent(rdbtntypeQuizMode))
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(gl_submitPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblQuizProgress)
								.addComponent(typeCorrectPercentageLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_submitPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(progressBarForQuiz, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
								.addComponent(progressBarForCorrect, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnRestartQuiz, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(submitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(110))
		);
		submitPanel.setLayout(gl_submitPanel);
		frmStudycraft.getContentPane().setLayout(groupLayout);
		frmStudycraft.setBackground(Color.DARK_GRAY);
		frmStudycraft.setBounds(100, 100, 1190, 744);
		frmStudycraft.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.DARK_GRAY);
		menuBar.setFont(new Font("Myriad Web", Font.PLAIN, 12));
		frmStudycraft.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setForeground(Color.LIGHT_GRAY);
		menuBar.add(mnFile);

		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		mnFile.add(panel);

		btnOpenfile = new JButton("Open File...");
		btnOpenfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openFile();
			}
		});
		btnOpenfile.setForeground(Color.LIGHT_GRAY);
		btnOpenfile.setBackground(Color.DARK_GRAY);
		btnOpenfile.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panel.add(btnOpenfile);

	}

	private void setLastDirectAndFile(String strToLoad) {
		if (strToLoad != null && strToLoad.length() > 0) {
			// scan backwards for the first \ in the string
			for (int i = strToLoad.length(); i <= 0; --i) {
				if (strToLoad.charAt(i) == '\\') {
					// ensure that there is enough space in array
					if (i + 1 < strToLoad.length()) {
						lastDirectory = strToLoad.substring(0, i);
						lastFile = strToLoad.substring(i + 1, strToLoad.length());
						return;
					}
				}
			}
		}
	}

	/**
	 * Updates the text box displaying the quiz.
	 * 
	 * *1: The first line has an inherent \n character; creating a blank space
	 * at the start of the file. this \n character is removed, but causes the
	 * first term in the list to have an unnecessary hyphen. The variable below
	 * prevents this first hyphen
	 */
	protected void quizGUI() {
		if (firstLine) {
			firstLine = false;
			while (nextStringQueue.size() == 0) {
				nextStringQueue.add(studyModule.quizNextStr(true));
				String checkStr = nextStringQueue.peek();
				if (checkStr == null || checkStr.equals("")) {
					nextStringQueue.poll();
				}
			}
		}

		if (quizLoadedAndReady()) {
			nextStringQueue.add(studyModule.quizNextStr(true));

			// Clear newline on first quiz term
			if (firstQuizRequest) {
				firstQuizNewlineClear();
			}

			// Clear empty strings
			if (nextStringQueue.peek().equals("") && quizLoadedAndReady()) {
				// clear empty string
				nextStringQueue.poll();
				nextStringQueue.add(studyModule.quizNextStr(true));
			}

			String nextStr = nextStringQueue.poll();

			/*
			 * // check if there should be a hyphen if (nextStr.length() > 0 &&
			 * nextStr.charAt(0) != '\n' && !firstHyphen) { quizSB.append(" - "
			 * ); // checkIfHyphenNeeded(nextStr); } else { // prevents hyphen
			 * on first line - *1 firstHyphen = false; }
			 */
			checkNextStrForImages(nextStr);
			if (typingQuizMode) {
				typingQuizCheck(nextStr);
				updatePercentBar();
			}
			updateProgressBar(nextStr);

			quizSB.append(nextStr);
			checkForPreHyphen(quizSB);

			answerBox.setText(quizSB.toString());
		} else if (nextStringQueue.size() > 0) {
			// Queue may still have string loaded
			if (!nextStringQueue.peek().equals("NULL")) {
				quizSB.append(nextStringQueue.poll());
				answerBox.setText(quizSB.toString());
			}
		}

	}

	private void typingQuizCheck(String nextStr) {
		//make sure string is worth checking
		if(nextStr == null || nextStr.equals(' ') || nextStr.equals('\n')){
			return;
		}
		//get the typed expression
		String testExpression = submitTextField.getText();
		
		//make sure typed Expression is valid (user may skip typing mode by entering nothing)
		if(testExpression == null || testExpression.equals(' ') || testExpression.equals('\n') || testExpression.length() < 3){
			return;
		}
		
		//strings are valid, so remove the string from the textbox
		submitTextField.setText("");
		
		// check if typed expression is  in the string
		int index = QuickRegex.expressionPresent(nextStr, testExpression);
		
		//test if expression is in the line
		if(index >= 0){
			//expression was present
			numberCorrectTypes++;
			totalNumberAttemptedTypes++;
			calculatePercentageCorrectTypes();
		} else {
			// expression was absent 
			totalNumberAttemptedTypes++; 
			calculatePercentageCorrectTypes();
		}
	}
	
	private void calculatePercentageCorrectTypes(){
		if(totalNumberAttemptedTypes != 0){
			percentageCorrectTypes = (double) numberCorrectTypes / totalNumberAttemptedTypes;
		}
	}

	private void checkForPreHyphen(StringBuilder quizSB2) {
		String upNext = nextStringQueue.peek();
		if (upNext != null && upNext.length() > 0 && upNext.charAt(0) != '\n') {
			quizSB2.append('-');
		}

	}

	private void updateProgressBar(String nextStr) {
		if (nextStr != null && nextStr.length() > 0 && nextStr.charAt(0) == '\n') {
			++currentProgress;
		}
		if (studyModule != null) {
			int currentLineNum = studyModule.getLineNumber();
			lblQuizProgress.setText("Progress: " + currentProgress + "/" + currentLineNum);
			progressBarForQuiz.setValue((int) (100 * (double) currentProgress / currentLineNum));
		}
	}
	
	private void updatePercentBar() {
		if (studyModule != null) {
			typeCorrectPercentageLabel.setText("Correct: " + numberCorrectTypes + "/" + totalNumberAttemptedTypes);
			progressBarForCorrect.setValue((int)(100 * percentageCorrectTypes));
		}
		
		//progressBarForCorrect
		//typeCorrectPercentageLabel
	}

	private void checkNextStrForImages(String nextStr) {
		if (nextStr != null && nextStr.length() > 0) {
			for (int i3 = 0; i3 < nextStr.length(); ++i3) {
				if (nextStr.charAt(i3) == '*') {
					int position = QuickRegex.expressionPresent(nextStr, ".png");
					if (position >= 0) {
						String imageName = getImageName(nextStr, position, ".png");
						imgsb.setLength(0);
						imgsb.append(lastDirectory);
						imgsb.append('\\');
						imgsb.append(imageName);
						if (imageName != null) {
							try {
								loadImage(imgsb.toString());
							} catch (IOException e) {
								submitTextField.setText("failed to load called image:" + imageName);
							}
						}
						break;
					}
				}
			}
		}
	}

	private String getImageName(String nextStr, int periodPosition, String imageType) {
		int fileExtensionLength = imageType.length();

		for (int i = nextStr.length() - 1; i >= 0; --i) {
			if (nextStr.charAt(i) == '*') {
				// backtrack i to represent first character
				++i;
				return nextStr.substring(i, periodPosition + fileExtensionLength);
			}
		}
		return null;
	}

	protected void checkIfHyphenNeeded(String nextStr) {
		int startIndex = 0;
		for (int i = 0; i < nextStr.length(); ++i) {
			if (nextStr.charAt(i) != '\t' && nextStr.charAt(i) != ' ') {
				startIndex = i;
				break;
			}
		}
		if (nextStr.charAt(startIndex) != '-') {
			quizSB.append(" - ");
		}
	}

	private void firstQuizNewlineClear() {
		String queuedStr = nextStringQueue.peek();
		if (nextStringQueue.size() < 1) {
			String error = "Non-critial error in firstQuizNewlineClear()";
			answerBox.setText(error);
			System.out.println(error);
			firstQuizRequest = false;
			return;
		}
		if (queuedStr != null && queuedStr.length() > 0 && queuedStr.charAt(0) == '\n') {

			// Remove the string with erroneous \n
			nextStringQueue.poll();

			// remove \n, put at start of new list
			Queue<String> temp = new LinkedList<>();
			temp.add(queuedStr.substring(1, queuedStr.length()));
			// List should be small at this point, so will simply loop through
			while (nextStringQueue.size() > 0) {
				temp.add(nextStringQueue.poll());
			}
			// make the que refer to the corrected temp que
			nextStringQueue = temp;
			// nextStringQueue.add(queuedStr.substring(1, queuedStr.length()));
			// //Old method
			// deque the old version of the string
			// nextStringQueue.poll();
		}
		firstQuizRequest = false;
	}

	public JTextArea getTextboxForAnswer() {
		return textboxForAnswer;
	}

	protected void configureScrollBarColors() {
		// this.thumbColor = Color.GREEN;
		// getScrollPaneAnswer().thumbColor = Color.GRAY;
		scrollPaneAnswer.getVerticalScrollBar();

		// http://stackoverflow.com/questions/23037433/changing-the-thumb-color-and-background-color-of-a-jscrollpane
	}

	protected JScrollPane getScrollPaneAnswer() {
		return scrollPaneAnswer;
	}

	protected JButton getBtnOpenfile() {
		return btnOpenfile;
	}

	protected void openFile() {
		// Set up the file explorer if it is not set
		if (!fileExplorerSet)
			setupFileExplorer();

		// set last directory as start directory
		if (lastDirectory != null) {
			changeWorkingDirectoryOfFileChooser();
		}

		// If the openDialog opens the dialog
		if (fchoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			defaultFile = fchoose.getSelectedFile().getPath();
			lastDirectory = fchoose.getCurrentDirectory().toString();
			lastFile = defaultFile;

			// Populate recent files menu
			updateRecentList();

			// Save new working directory
			saveUpdatedSettings();

			// create a quiz from the loaded file
			createQuiz(defaultFile);
		}
	}

	protected String openFileToFileList() {
		// Set up the file explorer if it is not set
		if (!fileExplorerSet)
			setupFileExplorer();

		// set last directory as start directory
		if (lastDirectory != null) {
			changeWorkingDirectoryOfFileChooser();
		}

		// If the openDialog opens the dialog
		if (fchoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			// get string from selected
			String resultString = fchoose.getSelectedFile().getPath();

			// update the last directory visited
			lastDirectory = fchoose.getCurrentDirectory().toString();

			// Populate recent files menu
			updateRecentList();

			// Save new working directory
			saveUpdatedSettings();

			// return string
			return resultString;
		}
		return null;
	}

	private void updateRecentList() {
		String lastFileNameOnly = null;
		if (lastFile != null) {
			int len = lastFile.length();
			// ensures i + 1 is valid

			// loop in reverse to find \ character
			for (int i = len - 1; i >= 0; --i) {
				if (lastFile.charAt(i) == '\\' && (i + 1) < len) {
					lastFileNameOnly = lastFile.substring(i + 1, len);

					// check if file is already on list
					for (int j = 0; j < listModel.size(); ++j) {
						if (lastFileNameOnly.equals(listModel.get(j))) {
							return;
						}
					}

					recentHashMap.put(lastFileNameOnly, lastDirectory + '\\' + lastFileNameOnly);
					listModel.addElement(lastFileNameOnly);
					return;
				}
			}
		}
	}

	private void changeWorkingDirectoryOfFileChooser() {
		fchoose.setCurrentDirectory(new File(lastDirectory));
	}

	private void changeWorkingDirectoryOfFileChooserToCourses() {
		String courseDir = System.getProperty("user.dir") + "\\courses";
		fchoose.setCurrentDirectory(new File(courseDir));
	}

	protected void setupFileExplorer() {
		fchoose.setFileFilter(fileFilter);
		// change visuals of the FileChooser
	}

	protected JLabel getLblStudycraft() {
		return lblStudycraft;
	}

	protected JPanel getGraphicsPanel() {
		return graphicsPanel;
	}

	public JProgressBar getProgressBarForQuiz() {
		return progressBarForQuiz;
	}

	protected JLabel getLblQuizProgress() {
		return lblQuizProgress;
	}

	public JButton getBtnRestartQuiz() {
		return btnRestartQuiz;
	}

	/**
	 * Creates a new class file in the course directory.
	 * 
	 * @param courseName
	 *            the name of the.
	 */
	private void addCourseModule(String courseName) {
		// course == class

		// class name must be checked before this method is called.
		CourseTopic newCourseFile = new CourseTopic();
		boolean fileAlreadyExists = newCourseFile.checkForExistingFile(courseName);
		if (!fileAlreadyExists) {
			// file doesn't exist, so proceed to make it
			newCourseFile.makeNewCourseFile(courseName);
			listModelCourses.addElement(courseName);
			answerBox.setText("new class created!");
			saveCourseModuleFileSettings();
		} else {
			// file exists already, do not overwrite it
			answerBox.setText("file already exists; load it and remove it first.");
		}
	}

	/**
	 * Method does the work needed to remove a module from a topic, and update
	 * the associated file.
	 * 
	 * @param moduleName
	 */
	private void removeModule() {
		// get index, then get value of item to be removed
		int indexToRemove = moduleList.getSelectedIndex();
		String selectedModuleStr = moduleList.getSelectedValue();

		// ensure that module is in the selected class
		boolean modulePresent = helpVerifyModulePresent(selectedModuleStr, indexToRemove);
		if (!modulePresent) {
			answerBox.setText("Select appropriate class for module to remove");
			return;
		}

		// remove selection from file
		String courseStr = courseList.getSelectedValue();
		if (courseStr == null || courseStr.length() < 1) {
			answerBox.setText("Select appropriate class for module to remove");
			return;
		}

		// load course into ram to be modified
		CourseTopic workingTopic = new CourseTopic();
		workingTopic.createCourseFromFile(courseStr);

		// remove module (autosave happens within the removeModule method)
		if (workingTopic.removeModule(selectedModuleStr)) {
		} else {
			answerBox.setText("failed to remove module");
		}

		// remove selection from the GUI interface
		listModelModules.remove(indexToRemove);
		saveCourseModuleFileSettings();
	}

	private boolean helpVerifyModulePresent(String moduleName, int index) {
		boolean modulePresent = false;
		updateModuleList();
		String strAtIndex = listModelModules.getElementAt(index);

		// check if string at index matches what was passed
		modulePresent = strAtIndex.equals(moduleName);

		return modulePresent;
	}

	/**
	 * This function takes a string and searches the element data of a list; if
	 * the string is found it removes it from the list.
	 * 
	 * @param courseName
	 *            the name of the course to find and remove.
	 */
	private void removeCourse(String courseName) {
		// REMOVE BY SELECTION
		String selectedValue = courseList.getSelectedValue();

		// Check if selection is valid
		if (selectedValue != null && selectedValue.length() > 0) {
			// removes what was passed by text field with what was selected
			courseName = selectedValue;
		}

		// loop through all elements
		for (int i = 0; i < listModelCourses.size(); ++i) {
			String element = listModelCourses.getElementAt(i);
			if (element.equals(courseName)) {
				listModelCourses.remove(i);
				saveCourseModuleFileSettings();
				return;
			}
		}
		answerBox.setText("could not find class " + courseName + " to remove.");
	}

	/**
	 * Load a course file from the course directory into the course List.
	 * 
	 * @param courseName
	 *            the name of the file to load into the course list.
	 */
	public void loadCourse() {
		// Set up the file explorer if it is not set
		if (!fileExplorerSet)
			setupFileExplorer();

		// set last directory as start directory
		changeWorkingDirectoryOfFileChooserToCourses();

		// If the openDialog opens the dialog
		if (fchoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			// get filename
			String courseFile = fchoose.getSelectedFile().getPath();

			// make course in RAM
			CourseTopic loadedTopic = new CourseTopic();

			// load file into RAM
			loadedTopic.createCourseFromRawFilePath(courseFile);

			// check success of loading
			boolean loadSucessful = loadedTopic.getLoadSuccess();
			if (loadSucessful) {
				// load worked, now to populate list with name
				listModelCourses.addElement(loadedTopic.getCourseName());
				updateModuleList();
				saveCourseModuleFileSettings();
			} else {
				answerBox.setText("failure loading class");
			}
		}
	}

	private void createModule(String newModuleName) {
		// get the currently selected course
		String selectedCourse = courseList.getSelectedValue(); // classModuleFileTextBox.getText();

		// make sure the selected value is usable
		if (selectedCourse != null && selectedCourse.length() > 0) {

			// convert selected course to a file path name
			selectedCourse = CourseTopic.convertClassNameToFileName(selectedCourse);

			// create a course in Ram to use
			CourseTopic course = new CourseTopic();

			// load data into course from file path
			course.createCourseFromRawFilePath(selectedCourse);

			// if load is successful, create a new module (which will update the
			// file)
			if (course.getLoadSuccess()) {
				boolean makeModSuccess = course.makeNewModule(newModuleName);

				// update the module list in the GUI
				if (makeModSuccess) {
					updateModuleList();
					saveCourseModuleFileSettings();
				}

			} else {
				answerBox.setText("internal failure in createModule; failed to load class file to add to.");
			}
		} else {
			answerBox.setText("Failure in getting selected class in class list.");
		}
	}

	/**
	 * Adds a new file to the selected module. Method assumes that gui module
	 * indexing matches CourseTopic indexing.
	 * 
	 * @param newFileName
	 *            the name of the new file to be added; this is a file path to a
	 *            .txt file.
	 */
	private void createFileInModule() {
		// open file explorer and get string representation
		String newFileName = openFileToFileList();
		if (newFileName == null) {
			answerBox.setText("error, no selected file from file explorer");
			return;
		}

		// get course name
		String selectedCourse = courseList.getSelectedValue();
		// int courseIndex = courseList.getSelectedIndex();

		// get selected module
		String selectedModule = moduleList.getSelectedValue();
		int moduleIndex = moduleList.getSelectedIndex();

		// refresh module list
		// updateModuleListNoDefaultSelection();
		refreshFileList();

		// make a working topic
		CourseTopic workingTopic = new CourseTopic();
		workingTopic.createCourseFromFile(selectedCourse);

		// ensure that module name corresponds with the index
		String indexStr = workingTopic.getModuleStrAt(moduleIndex);
		if (indexStr != null && indexStr.equals(selectedModule)) {
			// add file if string is valid
			workingTopic.makeNewFile(moduleIndex, newFileName);
			saveCourseModuleFileSettings();
		} else {
			answerBox.setText("error in adding file -- module selection mismatch; reselect module and try again.");
		}

		// refresh the results of the file add
		refreshFileList();
		saveCourseModuleFileSettings();
	}

	private void removeFileInModule() {
		// get course name
		String selectedCourse = courseList.getSelectedValue();
		// int courseIndex = courseList.getSelectedIndex();

		// get selected module
		String selectedModule = moduleList.getSelectedValue();
		int moduleIndex = moduleList.getSelectedIndex();

		// get selected file to remove
		String selectedFile = fileList.getSelectedValue();
		int fileIndex = fileList.getSelectedIndex();

		// refresh lists
		refreshFileList();

		// make a working topic
		CourseTopic workingTopic = new CourseTopic();
		workingTopic.createCourseFromFile(selectedCourse);

		// ensure that module name corresponds with the index
		String indexStr = workingTopic.getModuleStrAt(moduleIndex);
		if (indexStr != null && indexStr.equals(selectedModule)) {
			// remove file if string is valid
			workingTopic.removeFile(moduleIndex, fileIndex, selectedFile);
		} else {
			answerBox.setText("error in removing file -- module selection mismatch; reselect module and try again.");
		}

		// refresh the results of the file add
		refreshFileList();
		saveCourseModuleFileSettings();
	}

	private void loadFileFromFileList() {
		// get course name
		String selectedCourse = courseList.getSelectedValue();

		// get selected module
		int moduleIndex = moduleList.getSelectedIndex();
		// String selectedModule = moduleList.getSelectedValue();

		// get selected file to remove
		int fileIndex = fileList.getSelectedIndex();
		// String selectedFile = fileList.getSelectedValue();

		// refresh lists
		refreshFileList();

		// make a working topic
		CourseTopic workingTopic = new CourseTopic();
		workingTopic.createCourseFromFile(selectedCourse);

		// get file pathname
		String finalFilePath = null;
		// get list of strings that represent files
		ArrayList<String> fileList = workingTopic.getFileListFromModuleAtIndex(moduleIndex);
		if (fileList != null) {
			// string representation of filepath
			finalFilePath = fileList.get(fileIndex);
			if (finalFilePath.length() > 0) {
				while (finalFilePath.charAt(0) == '\t') {
					finalFilePath = finalFilePath.substring(1);
				}
			}

			// load quiz
			if (finalFilePath != null) {
				// setup history settings
				defaultFile = finalFilePath;
				lastFile = defaultFile;

				// Save new working directory
				saveUpdatedSettings();

				// create a quiz from the loaded file
				createQuiz(defaultFile);
			}
		}
		refreshFileList();
		saveCourseModuleFileSettings();
	}

	/**
	 * Updates the file list based on a selection from the module menu (also
	 * refreshes the module list)
	 */
	private void refreshFileList() {
		checkIfCourseListEmpty();
		// get course name
		String selectedCourse = courseList.getSelectedValue();
		// int courseIndex = courseList.getSelectedIndex();

		// get selected module
		// String selectedModule = moduleList.getSelectedValue();
		int moduleIndex = moduleList.getSelectedIndex();

		// get selected file
		int fileIndex = fileList.getSelectedIndex();
		// String selectedFile = fileList.getSelectedValue();

		// make sure the selected course value is usable
		if (selectedCourse != null && selectedCourse.length() > 0) {

			// get file path base on the course name
			selectedCourse = CourseTopic.convertClassNameToFileName(selectedCourse);

			// create course in ram to use
			CourseTopic workingCourse = new CourseTopic();
			workingCourse.createCourseFromRawFilePath(selectedCourse);

			// check if load was successful
			if (workingCourse.getLoadSuccess()) {
				// clear list data and replace with updated modules
				listModelModules.clear();

				// populate module gui list
				for (int i = 0; i < workingCourse.numModules(); ++i) {
					// get the module name
					String moduleName = workingCourse.getModuleStrAt(i);
					if (moduleName != null && moduleName.length() > 0) {
						listModelModules.addElement(moduleName);
					}
				}
				// set the selected module to what was previously selected
				if (moduleIndex < workingCourse.numModules() && moduleIndex >= 0) {
					moduleList.setSelectedIndex(moduleIndex);
				}
				// clear file gui list
				listModelFiles.clear();

				// populate file gui list
				populateFileList(workingCourse, moduleIndex);

				// restore file selection
				attemptToSelectPreviousFile(fileIndex);

			} else {
				answerBox.setText("failure in updateModuleList() file loading");
				listModelModules.clear(); // clear so that user will know there
											// was an error
			}

		}
	}

	private void checkIfCourseListEmpty() {
		if (listModelCourses.size() == 0 || courseList.getSelectedIndex() == -1) {
			listModelModules.clear();
			listModelFiles.clear();
		}
	}

	private void attemptToSelectPreviousFile(int fileIndex) {
		if (fileIndex >= 0 && fileIndex < listModelFiles.size()) {
			fileList.setSelectedIndex(fileIndex);
		}
	}

	private void populateFileList(CourseTopic workingCourse, int moduleIndex) {
		ArrayList<String> fileArrayList = workingCourse.getFileListFromModuleAtIndex(moduleIndex);
		if (fileArrayList != null) {
			for (int i = 0; i < fileArrayList.size(); ++i) {
				// get file from array list provided by module
				String fileName = fileArrayList.get(i);

				// remove directory portion of file path
				fileName = convertFilePathToFileName(fileName);

				// add file to the gui list(listmodel)
				this.listModelFiles.addElement(fileName);
			}
		}
	}

	private void updateModuleListSelectionCheckWrapper() {
		// if a module is selected, then updated module list and update file
		// lits
		if (moduleList.getSelectedValue() != null) {
			refreshFileList();
		} else {
			// no module was selected, so just update the module list
			updateModuleList();
		}
	}

	private String convertFilePathToFileName(String filePath) {
		// find the back slash that ends the directory portion of string
		for (int i = filePath.length() - 1; i >= 0; --i) {
			// start at the end in case file extensions change from .txt
			if (filePath.charAt(i) == '\\' || filePath.charAt(i) == '/') {
				if (i + 1 < filePath.length()) {
					// valid string, will return a name representation
					return filePath.substring(i + 1);
				}
			}
		}
		return filePath;
	}

	/**
	 * @deprecated The usage of this method was replaced with refreshfilelist()
	 * 
	 */
	@SuppressWarnings("unused")
	private void updateModuleListNoDefaultSelection() {
		// get the currently selected course
		String selectedCourse = courseList.getSelectedValue();

		// make sure the selected value is usable
		if (selectedCourse != null && selectedCourse.length() > 0) {

			// get file path base on the course name
			selectedCourse = CourseTopic.convertClassNameToFileName(selectedCourse);

			// create course in ram to use
			CourseTopic workingCourse = new CourseTopic();
			workingCourse.createCourseFromRawFilePath(selectedCourse);

			// check if load was successful
			if (workingCourse.getLoadSuccess()) {
				// clear list data and replace with updated modules
				listModelModules.clear();

				// populate module gui list
				for (int i = 0; i < workingCourse.numModules(); ++i) {
					// get the module name
					String moduleName = workingCourse.getModuleStrAt(i);
					if (moduleName != null && moduleName.length() > 0) {
						listModelModules.addElement(moduleName);
					}
				}
			} else {
				answerBox.setText("failure in updateModuleList() file loading");
				listModelModules.clear(); // clear so that user will know there
											// was an error
			}
		}
	}

	private void updateModuleList() {
		// get the currently selected course
		String selectedCourse = courseList.getSelectedValue();

		// if no course was selected, attempt to select the first in the list
		if (selectedCourse == null && listModelCourses.size() > 0) {
			courseList.setSelectedIndex(0);
		}

		// make sure the selected value is usable
		if (selectedCourse != null && selectedCourse.length() > 0) {

			// get file path base on the course name
			selectedCourse = CourseTopic.convertClassNameToFileName(selectedCourse);

			// create course in ram to use
			CourseTopic workingCourse = new CourseTopic();
			workingCourse.createCourseFromRawFilePath(selectedCourse);

			// check if load was successful
			if (workingCourse.getLoadSuccess()) {
				// clear list data and replace with updated modules
				listModelModules.clear();

				// populate module gui list
				for (int i = 0; i < workingCourse.numModules(); ++i) {
					// get the module name
					String moduleName = workingCourse.getModuleStrAt(i);
					if (moduleName != null && moduleName.length() > 0) {
						listModelModules.addElement(moduleName);
						saveCourseModuleFileSettings();
					}
				}
			} else {
				answerBox.setText("failure in updateModuleList() file loading");
				listModelModules.clear(); // clear so that user will know there
											// was an error
			}

		}

	}

	private void saveCourseModuleFileSettings() {
		// ensure save directory
		makeCoursesDirectory();
		String fileName = "courses\\settings.file";

		// make file
		try {
			// resource leak if exception is thrown - evaluated at minimal risk
			FileWriter fwriter = new FileWriter(fileName, false); // overwrites
			PrintWriter pwriter = new PrintWriter(fwriter);

			// save courses in list
			helperSaveCourses(pwriter);

			// save selected module index
			helperSaveModuleIndex(pwriter);

			// save selected file index
			helperSaveFileIndex(pwriter);

			pwriter.close();
			fwriter.close();
		} catch (Exception all) {
			System.out.println("failed to write file course, module, and file history");
			System.out.println(all.getMessage());
		}
	}

	/**
	 * Upon laying on the structure of this method, I realize that the a while
	 * loop wasn't the most efficient approach; but it the difference is
	 * negligible in this application
	 */
	private void loadCourseModuleFileSettings() {
		// reset lists
		refreshFileList();

		// setup input files
		String fileName = "courses\\settings.file";
		try {
			Scanner scanIn = new Scanner(Paths.get(fileName));
			int courseIndex = -1;
			int moduleIndex = -1;
			int fileIndex = -1;

			while (scanIn.hasNextLine()) {
				String line = scanIn.nextLine();
				// load courses
				if (line.length() >= 7 && line.substring(0, 7).equals("courses")) {
					loadCoursesFromStr(line);
					continue;
				}
				// load course index
				if (line.length() >= 10 && line.substring(0, 10).equals("course sel")) {
					courseIndex = getCourseIndexFromStr(line);
					continue;
				}

				// load module index
				if (line.length() >= 6 && line.substring(0, 6).equals("Module")) {
					moduleIndex = loadModuleIndexFromStr(line);
					continue;
				}

				// load file index
				if (line.length() >= 4 && line.substring(0, 4).equals("File")) {
					fileIndex = loadFileIndexFromStr(line);
					continue;
				}
			}
			scanIn.close();

			// select course (courses should be loaded at this point)
			if (courseIndex > -1 && courseIndex < listModelCourses.size()) {
				courseList.setSelectedIndex(courseIndex);
			}

			// update modules
			refreshFileList();
			// select module
			if (moduleIndex > -1 && moduleIndex < listModelModules.size()) {
				moduleList.setSelectedIndex(moduleIndex);
			}

			// update files
			refreshFileList();
			// select file
			if (fileIndex > -1 && fileIndex < listModelFiles.size()) {
				fileList.setSelectedIndex(fileIndex);
			}

		} catch (Exception all) {
			answerBox.setText("error in loading left sidebar settings; ie class, course, and file settings");
			System.out.println(all.getMessage());
		}
	}

	private int getCourseIndexFromStr(String line) {
		for (int i = line.length() - 1; i >= 0; --i) {
			// if char is a space, then it is before the number
			if (line.charAt(i) == ' ') {
				line = line.substring(i + 1);
				try {
					int index = Integer.parseInt(line);
					if (index >= 0) {
						return index;
					} else {
						return -1;
					}
				} catch (NumberFormatException e) {
					answerBox.setText("error loading previous course index from settings.file");
					System.out.println("error loading previous course index from settings.file");
					return -1;
				}
			}
		}
		// error in extracting int
		return -1;
	}

	private int loadFileIndexFromStr(String line) {
		for (int i = line.length() - 1; i >= 0; --i) {
			// if char is a space, then it is before the number
			if (line.charAt(i) == ' ') {
				line = line.substring(i + 1);
				try {
					int index = Integer.parseInt(line);
					if (index >= 0) {
						return index;
					} else {
						return -1;
					}
				} catch (NumberFormatException e) {
					answerBox.setText("error loading previous file index from settings.file");
					System.out.println("error loading previous file index from settings.file");
					return -1;
				}
			}
		}
		return -1;
	}

	/**
	 * Populates the module list using the string: "ModuleIndex: x"
	 * 
	 * @param line
	 *            where the index can be parsed
	 */
	private int loadModuleIndexFromStr(String line) {
		for (int i = line.length() - 1; i >= 0; --i) {
			// if char is a space, then it is before the number
			if (line.charAt(i) == ' ') {
				line = line.substring(i + 1);
				try {
					int index = Integer.parseInt(line);
					if (index >= 0) {
						return index;
					} else {
						return -1;
					}
				} catch (NumberFormatException e) {
					answerBox.setText("error loading previous module index from settings.file");
					System.out.println("error loading previous module index from settings.file");
					return -1;
				}
			}
		}
		return -1;
	}

	private void loadCoursesFromStr(String line) {
		// e.g. of how a line will look when passed below
		// courses\class X-CourseFile.txt
		if (line != null) {
			// remove "courses\" from the string
			line = line.substring(8);
			int endingPhraseStartIndex = QuickRegex.expressionPresent(line, "-CourseFile.txt");
			if (endingPhraseStartIndex > -1) {
				// trim the end (-CourseFile.txt) from the course string
				line = line.substring(0, endingPhraseStartIndex);
				// line has been trimed to course name, so add it to the list
				listModelCourses.addElement(line);
			}
		}
	}

	private void helperSaveFileIndex(PrintWriter pwriter) {
		int fileIndex = fileList.getSelectedIndex();
		pwriter.println("FileIndex: " + fileIndex);
	}

	private void helperSaveModuleIndex(PrintWriter pwriter) {
		int modIndex = moduleList.getSelectedIndex();
		pwriter.println("ModuleIndex: " + modIndex);
	}

	private void helperSaveCourses(PrintWriter pwriter) {
		// save course names
		for (int i = 0; i < listModelCourses.size(); ++i) {
			// save individual course names that should appear on the list
			String courseName = listModelCourses.getElementAt(i);
			courseName = "courses\\" + courseName + "-CourseFile.txt";
			pwriter.println(courseName);
		}
		// save course selection index
		int courseIndex = courseList.getSelectedIndex();
		pwriter.println("course selection index: " + courseIndex);

	}

	/**
	 * Will ensure that a courses directory exists in the executable's working
	 * directory.
	 * 
	 * @return
	 */
	private boolean makeCoursesDirectory() {
		File newDir = new File("courses");
		return newDir.mkdir();
	}

	public JList<?> getRecentFileList() {
		return recentFileList;
	}

	public JList<?> getListCourses() {
		return courseList;
	}

	public JList<?> getModuleList() {
		return moduleList;
	}

	public JList<?> getFileList() {
		return fileList;
	}

	public JButton getBtnAddClass() {
		return btnAddClass;
	}

	public JButton getRemoveFileBtn() {
		return removeFileBtn;
	}

	public JButton getFreshFileList() {
		return loadFileToScreenBtn;
	}

	public JButton getBtnFileAdd() {
		return btnFileAdd;
	}

	public JRadioButton getRdbtnLinearQuizMode() {
		return rdbtnLinearQuizMode;
	}

	public JRadioButton getRdbtnRandomQuizMode() {
		return rdbtnRandomQuizMode;
	}

	public JRadioButton getRdbtntypeQuizMode() {
		return rdbtntypeQuizMode;
	}
	public JLabel getTypeCorrectPercentageLabel() {
		return typeCorrectPercentageLabel;
	}
	public JProgressBar getProgressBarForCorrect() {
		return progressBarForCorrect;
	}
}
