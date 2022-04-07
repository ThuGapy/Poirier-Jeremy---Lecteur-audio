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

/**
 * Classe englobant le côté javafx du lecteur audio
 */
public class TP2 extends Application {
	/**
	 * Variable correspondant à la largeur de base de la fenêtre.
	 * Correspond également à la largeur minimale de la fenêtre.
	 */
	public static int WindowWidth = 800;
	
	/**
	 * Variable correspondant à la hauteur de base de la fenêtre.
	 * Correspond également à la haute minimale de la fenêtre.
	 */
	public static int WindowHeight = 600;
	
	/**
	 * Variable qui correspond à si le lecteur audio est en train de jouer une musique.
	 */
	public static boolean isPlaying = false;
	
	/**
	 * Variable qui correspond à si le lecteur audio est présentement en pause.
	 */
	public static boolean isPaused = false;
	
	/**
	 * Variable qui correspond à la position de la musique qui est présentement joué.
	 */
	public static int playingPosition = 0;
	
	/**
	 * Variable qui correspond au nom de l'artiste de la musique qui est en train de jouer.
	 */
	public static String playingArtist;
	
	/**
	 * Variable qui correspond au titre de la musique qui est en train de jouer.
	 */
	public static String playingTitle;
	
	/**
	 * Variable qui correspond à la durée de la musique qui est en train de jouer.
	 */
	public static int playingDuration;
	
	/**
	 * Liste qui correspond aux extensions acceptés des musiques lors de l'ouverture par dossier.
	 */
	public static List<String> extensionsDossier = Arrays.asList("mp3");
	
	/**
	 * ObservableList qui correspond à la Playlist.
	 * Celle-ci est utilisé pour l'affichage dans le tableau ainsi que la lecture.
	 */
	public ObservableList<Piste> Playlist = FXCollections.observableArrayList();
	
	/**
	 * Variable correspondant à la grosseur de la playlist.
	 * Sert à déterminer la position d'une musique qui vient d'être ajouté.
	 * Utilisé pour le fait qu'il est impossible d'accéder à playlist à partir d'une autre classe.
	 */
	public static int PlaylistSize = 0;
	
	/**
	 * TableView, tableau servant à l'affichage de la playlist.
	 */
	@SuppressWarnings("rawtypes")
	public static TableView playlistTable;
	
	/**
	 * Label servant à afficher le nom de l'artiste.
	 */
	public static Label labelArtiste;
	
	/**
	 * Label servant à afficher le titre de la musique
	 */
	public static Label labelTitre;
	
	/**
	 * Label servant à afficher la durée déjà joué de la musique
	 */
	public static Label labelProgres;
	
	/**
	 * Label servant à afficher la durée d'une musique
	 */
	public static Label labelDuree;
	
	/**
	 * Label servant à afficher la position de la musique dans la playlist
	 */
	public static Label labelPosition;
	
	/**
	 * Progressbar servant à afficher la progression de la lecture
	 */
	public static ProgressBar progresLecture;
	
	/**
	 * Bouton qui permet de jouer la musique
	 */
	public static Button boutonJouer;
	
	/**
	 * Bouton qui permet de mettre sur pause la musique
	 */
	public static Button boutonPause;
	
	/**
	 * MediaPlayer qui permet de jouer l'audio de la musique
	 */
	public static MediaPlayer mediaPlayer;
	
	/**
	 * Méthode qui permet de lancer l'application JavaFX
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Met le titre de l'application ainsi que sa largeur et hauteur maximale.
		primaryStage.setTitle("TP2 - Lecteur audio");
		primaryStage.setMinHeight(WindowHeight);
		primaryStage.setMinWidth(WindowWidth);
		
		// Crée un StackPane qui sera la root de notre application
		StackPane root = new StackPane();
		
		// Crée le strip menu (la barre de menu en haut de l'application
		CreerStripMenu(primaryStage, root);
		
		// Crée le tableau de la playlist
		playlistTable = CreeTableView(primaryStage);
		
		// Crée les boutons de lecture
		CreerBouttonLecture(root);
		
		// Crée les informations de lecture
		CreerInformationLecture(primaryStage, root);
		
		// Ajuste la position du tableau de playlist
		StackPane.setMargin(playlistTable, new Insets(30, 0, 70, 0));
		
		// Ajoute le tableau au root de l'application
		root.getChildren().add(playlistTable);
		
		// Création d'une timeline s'executant 10 fois par secondes pour mettre
		// les informations de la musique à jour.
		Timeline uiUpdate = new Timeline(new KeyFrame(Duration.millis(100), e -> {
			// Si la musique joue ou est sur pause, on met l'interface à jour.
			if(isPaused || isPlaying) {
				// On montre les informations de lecture
				MontrerInformationLecture(true);
				
				// On met à jour les différents label
				labelArtiste.setText(playingArtist);
				labelTitre.setText(playingTitle);
				
				labelProgres.setText(Piste.formatDuration(mediaPlayer.getCurrentTime().toSeconds()));
				labelDuree.setText(Piste.formatDuration(playingDuration));
				
				labelPosition.setText("Position: #" + (playingPosition + 1));
				
				// On calcule la progression de lecture et on met à jour la progressbar
				double progressionLecture = mediaPlayer.getCurrentTime().toSeconds() / playingDuration;
				progresLecture.setProgress(progressionLecture);
				
				// Si on atteint la fin de la musique, on passe à la prochaine
				if(progressionLecture >= 1) {
					ProchaineMusique();
				}
			} else {
				// Sinon on cache les informations de lecture
				MontrerInformationLecture(false);
			}
		}));
		
		// On dit à la timeline de s'executer indéfiniement et de commencer
		uiUpdate.setCycleCount(Animation.INDEFINITE);
		uiUpdate.play();
		
		// On crée notre scene et on la montre
		primaryStage.setScene(new Scene(root, WindowWidth, WindowHeight));
		primaryStage.show();
	}
	
	/**
	 * Méthode qui permet la création des boutons de lecture
	 * @param root Notre StackPane correspondant au root de notre application
	 */
	public void CreerBouttonLecture(StackPane root) {
		// On crée un bouton et on lui donne une grosseur et un texte
		boutonJouer = new Button();
		boutonJouer.setMaxSize(80, 35);
		boutonJouer.setMinSize(80, 35);
		boutonJouer.setText("Jouer");
		
		// Lorsqu'on clique sur le bouton jouer.
		boutonJouer.setOnAction(e -> {	
			// Si la playlist n'a aucune musique, on fait rien.
			if(Playlist.size() <= 0) {
				return;
			}
			
			// Si la musique n'est pas sur pause.
			if(!isPaused) {
				// Si aucune musique n'est sélectionné dans le tableau au joue la première musique.
				if(playlistTable.getSelectionModel().getSelectedItem() == null) {
					JouerMusique(0);
				} else {
					// Si une musique est sélectionné, on commence la lecture avec celle-ci.
					Piste pisteSelectionne = (Piste) playlistTable.getSelectionModel().getSelectedItem();
					JouerMusique(pisteSelectionne.getPosition() - 1);
				}
			} else {
				// Si la musique est sur pause on la reprend.
				JouerMusique(playingPosition);
			}
			
			// On cache le bouton jouer et on montre le bouton pause.
			boutonJouer.setVisible(false);
			boutonPause.setVisible(true);
		});
		
		// On aligne le bouton jouer
		StackPane.setAlignment(boutonJouer, Pos.BOTTOM_CENTER);
		StackPane.setMargin(boutonJouer, new Insets(0, 0, 30, 0));
		
		// On crée le bouton pause, on lui donne sa grosseur et son texte
		boutonPause = new Button();
		boutonPause.setMaxSize(80, 35);
		boutonPause.setMinSize(80, 35);
		boutonPause.setText("Pause");
		
		// Lorsqu'on appuie sur le bouton pause
		boutonPause.setOnAction(e -> {
			// On dit que la lecture est sur pause
			isPaused = true;
			
			// On pause le mediaPlayer
			mediaPlayer.pause();
			
			// On montre le bouton jouer et on cache le bouton pause
			boutonPause.setVisible(false);
			boutonJouer.setVisible(true);
			
			// On donne le focus au bouton jouer
			boutonJouer.requestFocus();
		});
		
		// On cache le bouton pause à la création
		boutonPause.setVisible(false);
		
		// On aligne le bouton pause
		StackPane.setAlignment(boutonPause, Pos.BOTTOM_CENTER);
		StackPane.setMargin(boutonPause, new Insets(0, 0, 30, 0));
		
		// On crée un bouton prochaine piste
		Button nextSong = new Button();
		nextSong.setMaxSize(120, 35);
		nextSong.setMinSize(120, 35);
		nextSong.setText("Prochaine piste");
		
		// Lorsqu'on clique sur le bouton prochaine piste, on passe à la prochaine musique
		nextSong.setOnAction(e -> {
			ProchaineMusique();
		});
		
		// On aligne le bouton prochaine piste
		StackPane.setAlignment(nextSong, Pos.BOTTOM_CENTER);
		StackPane.setMargin(nextSong, new Insets(0, 0, 30, 220));
		
		// On crée le bouton musique précédente
		Button previousSong = new Button();
		previousSong.setMaxSize(120, 35);
		previousSong.setMinSize(120, 35);
		previousSong.setText("Piste précédente");
		
		// Lorsqu'on clique sur le bouton musique précédente, on passe à la musique précédente
		previousSong.setOnAction(e -> {
			MusiquePrecedente();
		});
		
		// On aligne le bouton musique précédente
		StackPane.setAlignment(previousSong, Pos.BOTTOM_CENTER);
		StackPane.setMargin(previousSong, new Insets(0, 220, 30, 0));
		
		// On crée une progressbar avec une largeur de 400px;
		progresLecture = new ProgressBar();
		progresLecture.setMinWidth(400);
		progresLecture.setMaxWidth(400);
		
		// On aligne la progressbar
		StackPane.setAlignment(progresLecture, Pos.BOTTOM_CENTER);
		StackPane.setMargin(progresLecture, new Insets(0, 0, 5, 0));
		
		// On ajoute nos nouveaux éléments graphique au root de notre application.
		root.getChildren().addAll(boutonJouer, boutonPause, nextSong, previousSong, progresLecture);
	}
	
	/**
	 * Méthode qui fait passer le lecteur audio à la prochaine musique
	 */
	public void ProchaineMusique() {
		// Si le lecteur est en train de jouer ou est sur pause
		if(isPlaying || isPaused) {
			// On dit qu'il n'est pas sur pause et qu'il ne joue pas
			isPaused = false;
			isPlaying = false;
			
			// On arrête de mediaplayer
			mediaPlayer.stop();
			
			// Si on est déjà en train de jouer la dernière musique, on revient au début de la playlist
			if(playingPosition + 1 >= Playlist.size()) {
				playingPosition = 0;
			} else {
				// Sinon on incrémente la position de 1
				playingPosition++;
			}
			
			// On cache le bouton jouer et on montre le bouton pause
			boutonJouer.setVisible(false);
			boutonPause.setVisible(true);
			
			// On joue la nouvelle musique
			JouerMusique(playingPosition);
		}
	}
	
	/**
	 * Méthode qui permet de jouer la musique précédente
	 */
	public void MusiquePrecedente() {
		// Si le lecteur est en train de jouer ou est sur pause
		if(isPlaying || isPaused) {
			// On dit qu'il n'est pas sur pause et qu'il ne joue pas
			isPaused = false;
			isPlaying = false;
			
			// On arrête de mediaplayer
			mediaPlayer.stop();
			
			// Si nous sommes déjà en train de jouer la première musique, on joue la dernière musique de la playlist
			if(playingPosition - 1 < 0) {
				playingPosition = Playlist.size() - 1;
			} else {
				// Sinon on joue la musique précédente
				playingPosition--;
			}
			
			// On cache le bouton jouer et on montre le bouton pause
			boutonJouer.setVisible(false);
			boutonPause.setVisible(true);
			
			// On joue la nouvelle musique
			JouerMusique(playingPosition);
		}
	}
	
	/**
	 * Méthode qui joue une musique en fonction de sa position dans la playlist
	 * @param PlaylistPosition Position de la musique dans la playlist
	 */
	public void JouerMusique(int PlaylistPosition) {
		// Si le lecteur n'est pas sur pause, on crée un nouveau MediaPlayer avec la nouvelle musique
		if(!isPaused) {
			mediaPlayer = new MediaPlayer(Playlist.get(PlaylistPosition).getAudio());
		}
		
		// On joue la musique
		mediaPlayer.play();
		
		// On met à jour l'information de lecture
		isPlaying = true;
		isPaused = false;
		playingPosition = PlaylistPosition;
		
		// On montre la musique en train de jouer dans le tableau
		SelectPlayingMusic();
		
		// On met à jour l'information de lecture pour la mise à jour de l'interface
		playingArtist = Playlist.get(PlaylistPosition).getArtist();
		playingTitle = Playlist.get(PlaylistPosition).getTitle();
		playingDuration = (int) Playlist.get(PlaylistPosition).getDuration();
	}
	
	/**
	 * Méthode qui met à jour la musique sélectionné dans le tableau
	 */
	public void SelectPlayingMusic() {
		// Déclaration de l'indice
		int i = 0;
		
		// On boucle à travers les lignes du tableau
		for(Object row : playlistTable.getItems()) {
			// On cast la piste via la ligne du tableau
			Piste piste = (Piste) row;
			
			// Si la position de la piste du tableau correspond à la musique qui est en train de jouer
			if(piste.getPosition() == playingPosition + 1) {
				// On sélectionne la ligne et on donne le focus au tableau
				playlistTable.requestFocus();
				playlistTable.getSelectionModel().select(i);
				playlistTable.getFocusModel().focus(i);
				return;
			}
			
			// On incrémente l'indice
			i++;
		}
	}
	
	/**
	 * Méthode qui permet de créer les informations de lecture comme les différents Label
	 * @param primaryStage Stage correspondant à notre application JavaFX
	 * @param root StackPane correspondant au root de notre application
	 */
	public void CreerInformationLecture(Stage primaryStage, StackPane root) {
		// On crée le label du titre de la musique
		labelTitre = new Label();
		labelTitre.setText("");
		
		// On aligne le label du titre
		StackPane.setAlignment(labelTitre, Pos.BOTTOM_LEFT);
		StackPane.setMargin(labelTitre, new Insets(0, 0, 35, 15));
		
		// On crée le label du nom de l'artiste
		labelArtiste = new Label();
		labelArtiste.setText("");
		
		// On aligne le label du nom de l'artiste
		StackPane.setAlignment(labelArtiste, Pos.BOTTOM_LEFT);
		StackPane.setMargin(labelArtiste, new Insets(0, 0, 20, 15));
		
		// On crée le label de la progression de la lecture
		labelProgres = new Label();
		labelProgres.setText(Piste.formatDuration(0));
		labelProgres.setTextAlignment(TextAlignment.RIGHT);
		
		// On aligne le label de progression
		StackPane.setAlignment(labelProgres, Pos.BOTTOM_CENTER);
		StackPane.setMargin(labelProgres, new Insets(0, 440, 7, 0));
		
		// On crée le label de la durée de la musique
		labelDuree = new Label();
		labelDuree.setText(Piste.formatDuration(0));
		labelDuree.setTextAlignment(TextAlignment.LEFT);
		
		// On aligne le label de la durée
		StackPane.setAlignment(labelDuree, Pos.BOTTOM_CENTER);
		StackPane.setMargin(labelDuree, new Insets(0, 0, 7, 440));
		
		// On crée le label de position de la playlist
		labelPosition = new Label();
		labelPosition.setText("");
		labelPosition.setTextAlignment(TextAlignment.RIGHT);
		
		// On aligne le label de position
		StackPane.setAlignment(labelPosition, Pos.BOTTOM_RIGHT);
		StackPane.setMargin(labelPosition, new Insets(0, 15, 25, 0));
		
		// On ajoute les différents éléments graphique à notre StackPane
		root.getChildren().addAll(labelTitre, labelArtiste, labelProgres, labelDuree, labelPosition);
		
		// On cache les informations de lecture à la création
		MontrerInformationLecture(false);
	}
	
	/**
	 * Méthode qui permet de montrer/cacher les informations de lecture
	 * @param Show Booléen qui est utilisé pour montrer/cacher les informations de lecture
	 */
	public static void MontrerInformationLecture(boolean Show) {
		labelTitre.setVisible(Show);
		labelArtiste.setVisible(Show);
		labelProgres.setVisible(Show);
		labelDuree.setVisible(Show);
		labelPosition.setVisible(Show);
		progresLecture.setVisible(Show);
	}
	
	/**
	 * Méthode qui crée le strip menu (Menu en haut de l'application).
	 * @param primaryStage Stage qui correspond à notre application JavaFX
	 * @param root StackPane qui correspond au root de notre application
	 */
	public void CreerStripMenu(Stage primaryStage, StackPane root) {
		// Crée la menuBar et l'aligne en haut, au milieu de l'application
		MenuBar menuBar = new MenuBar();
		StackPane.setAlignment(menuBar, Pos.TOP_CENTER);
		
		// Crée un onglet menu
		Menu menuFichier = new Menu("Fichier");
		
		// Crée une option d'ouverture par fichier
		MenuItem ouvrirFichier = new MenuItem("Ouvrir un fichier...");
		
		// Lorsqu'on clique sur l'option d'ouverture par fichier
		ouvrirFichier.setOnAction(e -> {
			// On ouvre un sélecteur de fichier et on lui donne un titre
			FileChooser selectionFichier = new FileChooser();
			selectionFichier.setTitle("Selection de fichier(s)");
			
			// On fait en sorte que seuls les fichiers .mp3 sont affichés.
			selectionFichier.getExtensionFilters().add(new ExtensionFilter("Fichiers MP3", "*.mp3"));
			
			// On obtient les fichiers obtenu via la sélection
			List<File> fichiers = selectionFichier.showOpenMultipleDialog(primaryStage);
			
			// Si il y a des fichiers de sélectionnés, on les ouvre
			if(fichiers != null) {
				OuvrirFichiers(fichiers);
			}
		});
		
		// Crée une option d'ouverture par dossier
		MenuItem ouvrirDossier = new MenuItem("Ouvrir un dossier...");
		
		// Lorsqu'on clique sur l'option d'ouverture par dossier
		ouvrirDossier.setOnAction(e -> {
			// On ouvre un sélecteur de dossier en lui donnant un titre
			DirectoryChooser selectionDossier = new DirectoryChooser();
			selectionDossier.setTitle("Selection de dossier");
			File dossier = selectionDossier.showDialog(primaryStage);
			
			// Si un dossier a été sélectionné
			if(dossier != null) {
				// On crée une ArrayList
				List<File> fichiersMusique = new ArrayList<File>();
				
				// On boucle dans les fichiers contenu dans le dossier
				for(File fichier : dossier.listFiles()) {
					// Si l'extension du fichier est accepté (.mp3)
					if(extensionsDossier.contains(ObtenirExtension(fichier))) {
						// On l'ajoute à la ArrayList
						fichiersMusique.add(fichier);
					}
				}
				
				// On ouvre les fichiers de musiques
				OuvrirFichiers(fichiersMusique);
			}
		});
		
		
		// Crée une option pour quitter le programme
		MenuItem quitterProgramme = new MenuItem("Quitter");
		
		// Lorsqu'on appuie sur le bouton crée, on ferme l'application
		quitterProgramme.setOnAction(e -> {
			System.exit(0);
		});
		
		// Crée un séparateur
		SeparatorMenuItem separateur = new SeparatorMenuItem();
		
		// On ajoute nos différentes option à l'onglet fichier
		menuFichier.getItems().addAll(ouvrirFichier, ouvrirDossier, separateur, quitterProgramme);
		
		// On ajoute l'onglet fichier au menuBar
		menuBar.getMenus().add(menuFichier);
		
		// On ajoute notre menuBar au root de notre application
		root.getChildren().add(menuBar);
	}
	
	/**
	 * Méthode qui permet de créer le tableau de playlist
	 * @param primaryStage Stage correspondant à notre application JavaFX
	 * @return Le tableau créé
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TableView CreeTableView(Stage primaryStage) {
		// On crée la colonne de la positon dans la playlist
		TableColumn<Piste, Integer> colonnePosition = new TableColumn<>("Position");
		colonnePosition.setMinWidth(60);
		colonnePosition.setCellValueFactory(piste -> {
			return new SimpleIntegerProperty(piste.getValue().getPosition()).asObject();
		});
		
		// On crée la colonne du nom de l'artiste
		TableColumn<Piste, String> colonneArtiste = new TableColumn<>("Artiste");
		colonneArtiste.setMinWidth(60);
		colonneArtiste.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(piste.getValue().getArtist());
		});
		
		// On crée la colonne du nom de l'album
		TableColumn<Piste, String> colonneAlbum = new TableColumn<>("Album");
		colonneAlbum.setMinWidth(60);
		colonneAlbum.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(piste.getValue().getAlbum());
		});
		
		// On crée la colonne du titre de la musique
		TableColumn<Piste, String> colonneTitre = new TableColumn<>("Titre");
		colonneTitre.setMinWidth(60);
		colonneTitre.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(piste.getValue().getTitle());
		});
		
		// On crée la colonne de la durée de la musique
		TableColumn<Piste, String> colonneDuree = new TableColumn<>("Durée");
		colonneDuree.setMinWidth(60);
		colonneDuree.setCellValueFactory(piste -> {
			return new ReadOnlyStringWrapper(Piste.formatDuration(piste.getValue().getDuration()));
		});
		
		// On crée notre tableau
		TableView newTableView = new TableView<>();
		// On lui donne la playlist comme étant les items à afficher
		newTableView.setItems(Playlist);
		// On ajoute nos colonnes au tableau
		newTableView.getColumns().addAll(colonnePosition, colonneArtiste, colonneAlbum, colonneTitre, colonneDuree);
		// On lui met un place holder au cas qu'il n'y ait aucune musique dans la playlist
		newTableView.setPlaceholder(new Label("Aucune musique dans la liste de lecture!"));
		
		// On crée notre contextMenu pour supprimer des pistes
		ContextMenu contextMenu = new ContextMenu();
		// On crée notre bouton supprimer
		MenuItem supprimer = new MenuItem("Supprimer");
		
		// Lorsqu'on appuie sur le bouton supprimer
		supprimer.setOnAction(event -> {
			// On trouve la ligne du tableau sélectionné
			Piste pisteSelectionne = (Piste) newTableView.getSelectionModel().getSelectedItem();
			
			// On enleve la piste de la playlist
			Playlist.removeIf(piste -> piste.getPosition() == pisteSelectionne.getPosition());
			
			// On boucle dans la playlist pour mettre à jour la position des musiques qui viennent après
			// celle qui a été supprimé
			for(Piste piste : Playlist) {
				if(piste.getPosition() > pisteSelectionne.getPosition()) {
					piste.setPosition(piste.getPosition() - 1);
				}
			}
			
			// On met à jour la grosseur de la playlist
			PlaylistSize = Playlist.size();
			
			// On déselectionne la ligne dans le tableau
			playlistTable.getSelectionModel().select(null);
			
			// On rafraichit le contenu du tableau
			playlistTable.refresh();
		});
		
		// On ajoute le bouton supprimer au menu contexte
		contextMenu.getItems().add(supprimer);
		
		// Lorsqu'on fait un click droit sur une ligne, on ouvre le contextmenu à la position de notre souris.
		// On donne la "propriété" de ce contexte à notre application pour que celui-ci se ferme automatiquement
		// Lorsqu'on clique ailleurs
		newTableView.setRowFactory(tableView -> {
			TableRow<Piste> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
		            contextMenu.show(primaryStage, event.getScreenX(), event.getScreenY());
		        }
		    });
		    return row ;
		});
		
		// On désigne la largeur des différentes colonne par rapport à la largeur du tableau
		colonnePosition.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.11));
		colonneArtiste.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		colonneAlbum.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		colonneTitre.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		colonneDuree.prefWidthProperty().bind(newTableView.widthProperty().multiply(0.22));
		
		// On retourne le tableau créé
		return newTableView;
	}
	
	/**
	 * Méthode qui permet d'ouvrir les fichiers séléctionner avec le FileChooser ou DirectoryChooser
	 * @param fichiers Fichiers qu'on veut ajouter à la playlist
	 */
	public void OuvrirFichiers(List<File> fichiers) {
		// On boucle dans les fichiers
		for(File fichier : fichiers) {
			// On les ajoute à la playlist et on met à jour la grosseur de la playlist
			Playlist.add(new Piste(fichier));
			PlaylistSize = Playlist.size();
		}
	}
	
	/**
	 * Méthode qui permet d'obtenir l'extension d'un fichier
	 * @param fichier Fichier qu'on veut obtenir l'extension
	 * @return La string de l'extension du fichier sans le point.
	 */
	public static String ObtenirExtension(File fichier) {
		String nomFichier = fichier.getName();
		return nomFichier.substring(nomFichier.lastIndexOf('.') + 1);
	}
	
	/**
	 * Méthode qui lance l'application JavaFX
	 * @param args Arguments de lancement
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Constructeur de base de la classe
	 */
	public TP2() {}
}
