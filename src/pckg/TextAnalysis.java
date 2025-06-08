package pckg;

import java.nio.file.Files;
import java.util.*;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TextAnalysis {
	
	/* DO NOT MODIFY MAIN */
	public static void main (String [] args) throws IOException, 
	                                         InvocationTargetException, InterruptedException {
		
		final File textFileToProcess;
		File binFile = new File("BinFile.dat");
		File textFile = new File("textOutput.text");
		JFileChooser fileChooser = new JFileChooser(".");
		final TreeMap<Integer, List<String>> lineContents, lc;
		final TreeMap<String, Integer> wordCount, wc;
		
		EventQueue.invokeAndWait(()->{
			fileChooser.setDialogTitle("Select Text file to process");
			fileChooser.setFileFilter(new FileNameExtensionFilter("Text files","txt"));
			fileChooser.showOpenDialog(null);
			fileChooser.getSelectedFile();}
		);
		textFileToProcess = fileChooser.getSelectedFile();
		

		// process the text file. First stage: line number -> List of words
		lineContents = processTextFile(textFileToProcess);
		
		// Second stage: word -> number of appearances
		wordCount = countWords(lineContents);
		
		// save both maps. One in a binary file, the other in a text file
		mapToBinFile(lineContents, binFile);
		mapToTextFile(wordCount, textFile);
		
		// inform...
		EventQueue.invokeAndWait(()->
			JOptionPane.showMessageDialog(null,
					"Text processed. Results saved.\nPress OK to retrieve and check")
		);
		
		// retrieve information back from the files
		lc = binFileToMap(binFile);
		wc = textFileToMap(textFile);
		
		// show the results in a cute JFrame
		EventQueue.invokeAndWait(()-> {
			JFrame frame = new JFrame("Results of text processing. File: "+textFileToProcess.getName());
			JTextArea textAreaOne = new JTextArea();
			JScrollPane scrollOne = new JScrollPane(textAreaOne);
			JTextArea textAreaTwo = new JTextArea();
			JScrollPane scrollTwo = new JScrollPane(textAreaTwo);
			JPanel contentPane = new JPanel(new GridLayout(1,2,3,3));
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			frame.setContentPane(contentPane);
			textAreaOne.setForeground(Color.BLUE);
			textAreaTwo.setForeground(Color.RED);
			textAreaOne.setEditable(false);
			textAreaTwo.setEditable(false);
			scrollOne.setBorder(new TitledBorder("SELECTED WORDS IN LINES"));
			scrollTwo.setBorder(new TitledBorder("REPETITIONS OF SELECTED WORDS"));
	
			
			frame.getContentPane().add(scrollOne);
			frame.getContentPane().add(scrollTwo);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			textAreaOne.append("\n");
			for (int ln: lc.keySet()) {
				textAreaOne.append(" line "+ln+" => "+lineContents.get(ln)+"\n");
			}
			textAreaOne.setCaretPosition(0);
			
			textAreaTwo.append("\n");
			for (String w: wc.keySet()) {
				textAreaTwo.append(" "+w+": "+wordCount.get(w)+"\n");
			}
			
			frame.setSize(600, 800);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
	
	
	//---------- Text processing  procedures -----------------
	
	private static TreeMap<Integer, List<String>> processTextFile (File f) 
			       throws IOException {
		/* this procedure processes the given text file and produces a map
		   that binds each line number to the list of all all-uppercase words
		   appearing in that line.
		   Lines are numbered from 1 onwards
		   Those lines that do not contain any all-uppercase word DO NOT
		   appear in the map */
		
		
		/* COMPLETE 1 */

		TreeMap<Integer, List<String>> resultat = new TreeMap<>();
		List<String> lines = Files.readAllLines(f.toPath());

		for (int i=0;i<lines.size(); i++){
			String frase = lines.get(i);
			String[] paraules = frase.split("[\\s!?\"',;:.-]+");

			List<String> majuscules = new ArrayList<>();

			for (String p : paraules){
				if(p.length() > 2 && p.matches("[A-Z]+")){
					majuscules.add(p);
				}
			}

			if(!majuscules.isEmpty()){
				resultat.put(i+1,majuscules);
			}

		}
		
		return resultat; //Change appropriately
		
	}
	
	private static TreeMap<String, Integer> countWords (TreeMap<?, List<String>> lc) {
		/* This procedure produces a map that binds words to the number of times they
		   appear in a text. The only parameter is another map the values of which
		   are lists containing the words to be counted */
		
		/* COMPLETE 2 */
		TreeMap<String, Integer> resultat = new TreeMap<>();

		for(List<String> paraules : lc.values()){
			for(String p : paraules) {
				resultat.put(p,resultat.getOrDefault(p,0)+1);
			}
		}

		return resultat; //Change appropriately
	}


	
	// -------------- SAVE procedures ---------
	
	/* DO NOT MODIFY THIS PROCEDURE */
	private static void mapToBinFile (TreeMap<Integer, List<String>> map, File f) throws IOException {
		/* This procedure saves into a binary file (second parameter 
		 * contains its path) the contents of the given map (first
		 * parameter).
		 * The format of this file is "compatible" with the format
		 * of the file expected by procedure binFileToMap
		 */
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
		
		for (int num : map.keySet()) {
			dos.writeInt(num);
			dos.writeInt(map.get(num).size());
			for (String s : map.get(num)) {
				dos.writeUTF(s);
			}
		}
		dos.close();
	}
	
	private static void mapToTextFile (TreeMap<String, Integer> map, File f) throws IOException {
		/* This procedure saves into a text file (second parameter 
		   contains its path) the contents of the given map (first
		   parameter).
		   The format of this file has to be compatible with the format
		   of the file expected by procedure textFileToMap */
		
		/* COMPLETE 3 */
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		for (Map.Entry<String, Integer> entrada : map.entrySet()) {
			bw.write(entrada.getKey());
			bw.newLine();
			bw.write(Integer.toString(entrada.getValue()));
			bw.newLine();
		}

		bw.close();
	}
	
	
	// ------------------ RETRIEVE procedures --------------
	
	/* DO NOT MODIFY THIS PROCEDURE */
	private static TreeMap<String, Integer> textFileToMap (File f) throws IOException {
		TreeMap<String, Integer> result = new TreeMap<>();
		BufferedReader bur = new BufferedReader(new FileReader(f));
		
		String word = bur.readLine();
		String num;
		
		while (word!=null) {
			num = bur.readLine();
			result.put(word, Integer.valueOf(num)); //Updated code
			word = bur.readLine();
		}
		bur.close();
		
		return result;
	}
	
	
	private static TreeMap<Integer, List<String>> binFileToMap (File f) throws IOException {
		/* This procedure retrieves a map, from a bin file produced by
		 * mapToBinFile. Actually the map is created empty and then populated
		 * with the information retrieved from the file */
		
		/* COMPLETE 4 */
		TreeMap<Integer, List<String>> resultat = new TreeMap<>();
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

		while (dis.available() > 0) {
			int lineNumber = dis.readInt();
			int wordCount = dis.readInt();
			List<String> paraules = new ArrayList<>();
			for (int i = 0; i < wordCount; i++) {
				paraules.add(dis.readUTF());
			}
			resultat.put(lineNumber, paraules);
		}

		dis.close();
		return resultat;

	}
	
}
