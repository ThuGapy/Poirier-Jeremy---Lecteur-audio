import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class TP2 extends Application {
	public static int WindowWidth = 800;
	public static int WindowHeight = 600;
	
	public static List<String> extensionsDossier = Arrays.asList("mp3");
	
	public static List<Piste> Playlist = new ArrayList<Piste>();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("TP2 - Lecteur audio");
		
		StackPane root = new StackPane();
		
		CreerStripMenu(primaryStage, root);
		
		Button button = new Button();
		button.setMaxSize(130, 40);
		button.setText("Print playlist");
		
		button.setOnAction(e -> {
			for(Piste piste : Playlist) {
				System.out.println(piste.getTitle());
				System.out.println(piste.getPosition());
			}
		});
		
		StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
		
		root.getChildren().add(button);
		
		primaryStage.setScene(new Scene(root, WindowWidth, WindowHeight));
		primaryStage.show();
	}
	
	public static void CreerStripMenu(Stage primaryStage, StackPane root) {
		MenuBar menuBar = new MenuBar();
		StackPane.setAlignment(menuBar, Pos.TOP_CENTER);
		
		Menu menuFichier = new Menu("Fichier");
		
		MenuItem ouvrirFichier = new MenuItem("Ouvrir un fichier...");
		
		ouvrirFichier.setOnAction(e -> {
			FileChooser selectionFichier = new FileChooser();
			selectionFichier.setTitle("Selection de fichier(s)");
			selectionFichier.getExtensionFilters().add(new ExtensionFilter("Fichiers MP3", "*.mp3"));
			List<File> fichiers = selectionFichier.showOpenMultipleDialog(primaryStage);
			
			OuvrirFichiers(fichiers);
		});
		
		MenuItem ouvrirDossier = new MenuItem("Ouvrir un dossier...");
		
		ouvrirDossier.setOnAction(e -> {
			DirectoryChooser selectionDossier = new DirectoryChooser();
			selectionDossier.setTitle("Selection de dossier");
			File dossier = selectionDossier.showDialog(primaryStage);
			
			if(dossier != null) {
				List<File> fichiersMusique = new ArrayList<File>();
				
				for(File fichier : dossier.listFiles()) {
					if(extensionsDossier.contains(ObtenirExtension(fichier))) {
						fichiersMusique.add(fichier);
					}
				}
				
				OuvrirFichiers(fichiersMusique);
			}
		});
		
		MenuItem quitterProgramme = new MenuItem("Quitter");
		
		quitterProgramme.setOnAction(e -> {
			System.exit(0);
		});
		
		SeparatorMenuItem separateur = new SeparatorMenuItem();
		
		menuFichier.getItems().addAll(ouvrirFichier, ouvrirDossier, separateur, quitterProgramme);
		
		menuBar.getMenus().add(menuFichier);
		
		root.getChildren().add(menuBar);
	}
	
	public static void OuvrirFichiers(List<File> fichiers) {
		for(File fichier : fichiers) {
			Playlist.add(new Piste(fichier));
		}
	}
	
	public static String ObtenirExtension(File fichier) {
		String nomFichier = fichier.getName();
		return nomFichier.substring(nomFichier.lastIndexOf('.') + 1);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
