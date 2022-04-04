import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.stage.Stage;

public class TP2 extends Application {
	public static int WindowWidth = 800;
	public static int WindowHeight = 600;
	
	public static boolean isPlaying = false;
	public static boolean isPaused = false;
	public static int playingPosition = 0;
	
	public static String playingArtist;
	public static String playingTitle;
	public static int playingDuration;
	
	public static List<String> extensionsDossier = Arrays.asList("mp3");
	
	public ObservableList<Piste> Playlist = FXCollections.observableArrayList();
	public static int PlaylistSize = 0;
	
	@SuppressWarnings("rawtypes")
	public static TableView playlistTable;
	
	public static Label labelArtiste;
	public static Label labelTitre;
	public static Label labelProgres;
	public static Label labelDuree;
	public static Label labelPosition;
	
	public static ProgressBar progresLecture;
	
	public static Button boutonJouer;
	public static Button boutonPause;
	
	public static MediaPlayer mediaPlayer;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("TP2 - Lecteur audio");
		primaryStage.setMinHeight(WindowHeight);
		primaryStage.setMinWidth(WindowWidth);
		
		StackPane root = new StackPane();
		
		CreerStripMenu(primaryStage, root);
		
		playlistTable = CreeTableView(primaryStage);
		
		CreerBouttonLecture(primaryStage, root);
		
		CreerInformationLecture(primaryStage, root);
		
		StackPane.setMargin(playlistTable, new Insets(30, 0, 70, 0));
		
		root.getChildren().add(playlistTable);
		
		Timeline uiUpdate = new Timeline(new KeyFrame(Duration.millis(100), e -> {
			if(isPaused || isPlaying) {
				MontrerInformationLecture(true);
				
				labelArtiste.setText(playingArtist);
				labelTitre.setText(playingTitle);
				
				labelProgres.setText(Piste.formatDuration(mediaPlayer.getCurrentTime().toSeconds()));
				labelDuree.setText(Piste.formatDuration(playingDuration));
				
				labelPosition.setText("Position: #" + (playingPosition + 1));
				
				double progressionLecture = mediaPlayer.getCurrentTime().toSeconds() / playingDuration;
				progresLecture.setProgress(progressionLecture);
				
				if(progressionLecture >= 1) {
					ProchaineMusique();
				}
			} else {
				MontrerInformationLecture(false);
			}
		}));
		
		uiUpdate.setCycleCount(Animation.INDEFINITE);
		uiUpdate.play();
		
		primaryStage.setScene(new Scene(root, WindowWidth, WindowHeight));
		primaryStage.show();
	}
	
	public void CreerBouttonLecture(Stage primaryStage, StackPane root) {		
		boutonJouer = new Button();
		boutonJouer.setMaxSize(80, 35);
		boutonJouer.setMinSize(80, 35);
		boutonJouer.setText("Jouer");
		
		boutonJouer.setOnAction(e -> {	
			if(Playlist.size() <= 0) {
				return;
			}
			
			if(!isPaused) {
				if(playlistTable.getSelectionModel().getSelectedItem() == null) {
					JouerMusique(0);
				} else {
					Piste pisteSelectionne = (Piste) playlistTable.getSelectionModel().getSelectedItem();
					JouerMusique(pisteSelectionne.getPosition() - 1);
				}
			} else {
				JouerMusique(playingPosition);
			}
			
			boutonJouer.setVisible(false);
			boutonPause.setVisible(true);
		});
		
		StackPane.setAlignment(boutonJouer, Pos.BOTTOM_CENTER);
		StackPane.setMargin(boutonJouer, new Insets(0, 0, 30, 0));
		
		boutonPause = new Button();
		boutonPause.setMaxSize(80, 35);
		boutonPause.setMinSize(80, 35);
		boutonPause.setText("Pause");
		
		boutonPause.setOnAction(e -> {
			isPaused = true;
			
			mediaPlayer.pause();
			
			boutonPause.setVisible(false);
			boutonJouer.setVisible(true);
			boutonJouer.requestFocus();
		});
		
		boutonPause.setVisible(false);
		
		StackPane.setAlignment(boutonPause, Pos.BOTTOM_CENTER);
		StackPane.setMargin(boutonPause, new Insets(0, 0, 30, 0));
		
		Button nextSong = new Button();
		nextSong.setMaxSize(120, 35);
		nextSong.setMinSize(120, 35);
		nextSong.setText("Prochaine piste");
		
		nextSong.setOnAction(e -> {
			ProchaineMusique();
		});
		
		StackPane.setAlignment(nextSong, Pos.BOTTOM_CENTER);
		StackPane.setMargin(nextSong, new Insets(0, 0, 30, 220));
		
		Button previousSong = new Button();
		previousSong.setMaxSize(120, 35);
		previousSong.setMinSize(120, 35);
		previousSong.setText("Piste précédente");
		
		previousSong.setOnAction(e -> {
			MusiquePrecedente();
		});
		
		StackPane.setAlignment(previousSong, Pos.BOTTOM_CENTER);
		StackPane.setMargin(previousSong, new Insets(0, 220, 30, 0));
		
		progresLecture = new ProgressBar();
		progresLecture.setMinWidth(400);
		progresLecture.setMaxWidth(400);
		
		StackPane.setAlignment(progresLecture, Pos.BOTTOM_CENTER);
		StackPane.setMargin(progresLecture, new Insets(0, 0, 5, 0));
		
		root.getChildren().addAll(boutonJouer, boutonPause, nextSong, previousSong, progresLecture);
	}
	
	public void ProchaineMusique() {
		if(isPlaying) {
			isPaused = false;
			isPlaying = false;
			
			mediaPlayer.stop();
			
			if(playingPosition + 1 >= Playlist.size()) {
				playingPosition = 0;
			} else {
				playingPosition++;
			}
			
			boutonJouer.setVisible(false);
			boutonPause.setVisible(true);
			
			JouerMusique(playingPosition);
		}
	}
	
	public void MusiquePrecedente() {
		if(isPlaying) {
			isPaused = false;
			isPlaying = false;
			
			mediaPlayer.stop();
			
			if(playingPosition - 1 < 0) {
				playingPosition = Playlist.size() - 1;
			} else {
				playingPosition--;
			}
			
			boutonJouer.setVisible(false);
			boutonPause.setVisible(true);
			
			JouerMusique(playingPosition);
		}
	}
	
	public void JouerMusique(int PlaylistPosition) {
		if(!isPaused) {
			mediaPlayer = new MediaPlayer(Playlist.get(PlaylistPosition).getAudio());
		}
		
		mediaPlayer.play();
		
		isPlaying = true;
		isPaused = false;
		playingPosition = PlaylistPosition;
		
		SelectPlayingMusic();
		
		playingArtist = Playlist.get(PlaylistPosition).getArtist();
		playingTitle = Playlist.get(PlaylistPosition).getTitle();
		playingDuration = (int) Playlist.get(PlaylistPosition).getDuration();
	}
	
	public void SelectPlayingMusic() {
		int i = 0;
		for(Object row : playlistTable.getItems()) {
			Piste piste = (Piste) row;
			
			if(piste.getPosition() == playingPosition + 1) {
				playlistTable.requestFocus();
				playlistTable.getSelectionModel().select(i);
				playlistTable.getFocusModel().focus(i);
				return;
			}
			
			i++;
		}
	}
	
	public void CreerInformationLecture(Stage primaryStage, StackPane root) {
		labelTitre = new Label();
		labelTitre.setText("I Got Bitches");
		
		StackPane.setAlignment(labelTitre, Pos.BOTTOM_LEFT);
		StackPane.setMargin(labelTitre, new Insets(0, 0, 35, 15));
		
		labelArtiste = new Label();
		labelArtiste.setText("A2M");
		
		StackPane.setAlignment(labelArtiste, Pos.BOTTOM_LEFT);
		StackPane.setMargin(labelArtiste, new Insets(0, 0, 20, 15));
		
		labelProgres = new Label();
		labelProgres.setText(Piste.formatDuration(0));
		labelProgres.setTextAlignment(TextAlignment.RIGHT);
		
		StackPane.setAlignment(labelProgres, Pos.BOTTOM_CENTER);
		StackPane.setMargin(labelProgres, new Insets(0, 440, 7, 0));
		
		labelDuree = new Label();
		labelDuree.setText(Piste.formatDuration(0));
		labelDuree.setTextAlignment(TextAlignment.LEFT);
		
		StackPane.setAlignment(labelDuree, Pos.BOTTOM_CENTER);
		StackPane.setMargin(labelDuree, new Insets(0, 0, 7, 440));
		
		labelPosition = new Label();
		labelPosition.setText("Position: #1");
		labelPosition.setTextAlignment(TextAlignment.RIGHT);
		
		StackPane.setAlignment(labelPosition, Pos.BOTTOM_RIGHT);
		StackPane.setMargin(labelPosition, new Insets(0, 15, 25, 0));
		
		root.getChildren().addAll(labelTitre, labelArtiste, labelProgres, labelDuree, labelPosition);
		
		MontrerInformationLecture(false);
	}
	
	public static void MontrerInformationLecture(boolean Show) {
		labelTitre.setVisible(Show);
		labelArtiste.setVisible(Show);
		labelProgres.setVisible(Show);
		labelDuree.setVisible(Show);
		labelPosition.setVisible(Show);
		progresLecture.setVisible(Show);
	}
	
	public void CreerStripMenu(Stage primaryStage, StackPane root) {
		MenuBar menuBar = new MenuBar();
		StackPane.setAlignment(menuBar, Pos.TOP_CENTER);
		
		Menu menuFichier = new Menu("Fichier");
		
		MenuItem ouvrirFichier = new MenuItem("Ouvrir un fichier...");
		
		ouvrirFichier.setOnAction(e -> {
			FileChooser selectionFichier = new FileChooser();
			selectionFichier.setTitle("Selection de fichier(s)");
			selectionFichier.getExtensionFilters().add(new ExtensionFilter("Fichiers MP3", "*.mp3"));
			List<File> fichiers = selectionFichier.showOpenMultipleDialog(primaryStage);
			
			if(fichiers != null) {
				OuvrirFichiers(fichiers);
			}
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TableView CreeTableView(Stage primaryStage) {
		TableColumn<Piste, Integer> colonnePosition = new TableColumn<>("Position");
		colonnePosition.setMinWidth(60);
		colonnePosition.setCellValueFactory(piste -> {
			return new SimpleIntegerProperty(piste.getValue().getPosition()).asObject();
		});
		
		TableColumn<Piste, String> colonneArtiste = new TableColumn<>("Artiste");
		colonneArtiste.setMinWidth(60);
		colonneArtiste.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(piste.getValue().getArtist());
		});
		
		TableColumn<Piste, String> colonneAlbum = new TableColumn<>("Album");
		colonneAlbum.setMinWidth(60);
		colonneAlbum.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(piste.getValue().getAlbum());
		});
		
		TableColumn<Piste, String> colonneTitre = new TableColumn<>("Titre");
		colonneTitre.setMinWidth(60);
		colonneTitre.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(piste.getValue().getTitle());
		});
		
		TableColumn<Piste, String> colonneDuree = new TableColumn<>("Durée");
		colonneDuree.setMinWidth(60);
		colonneDuree.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(Piste.formatDuration(piste.getValue().getDuration()));
		});
		
		TableView newTableView = new TableView<>();
		newTableView.setItems(Playlist);
		newTableView.getColumns().addAll(colonnePosition, colonneArtiste, colonneAlbum, colonneTitre, colonneDuree);
		newTableView.setPlaceholder(new Label("Aucune musique dans la liste de lecture!"));
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem supprimer = new MenuItem("Supprimer");
		
		supprimer.setOnAction(event -> {
			Piste pisteSelectionne = (Piste) newTableView.getSelectionModel().getSelectedItem();
			
			Playlist.removeIf(piste -> piste.getPosition() == pisteSelectionne.getPosition());
			
			for(Piste piste : Playlist) {
				if(piste.getPosition() > pisteSelectionne.getPosition()) {
					piste.setPosition(piste.getPosition() - 1);
				}
			}
			
			PlaylistSize = Playlist.size();
			
			playlistTable.getSelectionModel().select(null);
			
			playlistTable.refresh();
		});
		
		contextMenu.getItems().add(supprimer);
		
		newTableView.setRowFactory(tableView -> {
			TableRow<Piste> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
		            contextMenu.show(primaryStage, event.getScreenX(), event.getScreenY());
		        }
		    });
		    return row ;
		});
		
		colonnePosition.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.11));
		colonneArtiste.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		colonneAlbum.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		colonneTitre.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		colonneDuree.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		
		return newTableView;
	}
	
	public void OuvrirFichiers(List<File> fichiers) {
		for(File fichier : fichiers) {
			Playlist.add(new Piste(fichier));
			PlaylistSize = Playlist.size();
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
