import java.io.File;

import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



/**
 * Classe qui correspond à une piste dans la playlist
 */
public class Piste {
	// Déclaration des variables privés de la classe Piste
	private String titre;
	private String album;
	private String artiste;
	private double duree;
	private Media audio;
	private int positionPlaylist;
	
	/**
	 * Constructeur de la piste, crée l'objet et obtient la durée de la piste et ses métadatas
	 * @param fichier Le fichier du .mp3 de la musique
	 */
	public Piste(File fichier) {
		// Création de l'audio avec l'URL du fichier
		audio = new Media(fichier.toURI().toString());
		
		// On met la position de la piste comme étant la grosseur de la playlist + 1
		// La piste est donc la dernière de la playlist
		positionPlaylist = TP2.PlaylistSize + 1;
		
		// On crée un mediaPlayer pour obtenir la durée de la musique
		MediaPlayer mediaPlayer = new MediaPlayer(audio);
		
		// Lorsque le mediaPlayer est prêt nous pouvons obtenir la duree.
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				duree = audio.getDuration().toSeconds();	
				
				// Lorsqu'on obtient la durée, on rafraichit le tableau
				TP2.playlistTable.refresh();
			}
		});
		
		// Lorsque les metadata sont prête, on les stocke dans des variables
		audio.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
			if (change.wasAdded()) {
				switch((String) change.getKey()) {
					case "artist":
						artiste = change.getValueAdded().toString();
						break;
					case "album":
						album = change.getValueAdded().toString();
						break;
					case "title":
						titre = change.getValueAdded().toString();
						break;
				}
		    }
			
			// Après l'ajout des métadata, on rafraichit le tableau
			TP2.playlistTable.refresh();
		});
	}
	
	/**
	 * Méthode qui permet d'obtenir le titre de la musique
	 * @return La string du titre de la musique
	 */
	public String getTitle() {
		return titre;
	}
	
	/**
	 * Méthode qui permet d'obtenir l'album de la musique
	 * @return La string de l'album de la musique
	 */
	public String getAlbum() {
		return album;
	}
	
	/**
	 * Méthode qui permet d'obtenir le nom de l'artiste
	 * @return Une string du nom de l'artiste
	 */
	public String getArtist() {
		return artiste;
	}
	
	/**
	 * Méthode qui permet d'obtenir la durée de la musique
	 * @return La durée de la piste audio
	 */
	public double getDuration() {
		return duree;
	}
	
	/**
	 * Méthode qui permet d'obtenir le Media d'une piste audio. le Media correspond au fichier audio
	 * @return Le media de la musique
	 */
	public Media getAudio() {
		return audio;
	}
	
	/**
	 * Méthode qui permet d'obtenir la position d'une musique dans la playlist
	 * @return La position de la musique dans la playlist
	 */
	public int getPosition() {
		return positionPlaylist;
	}
	
	/**
	 * Méthode qui permet de changer la position d'une musique dans la playlist
	 * Utilisé lorsqu'un musique est supprimé de la playlist pour actualiser la position
	 * @param position Nouvelle position de la piste dans la playlist
	 */
	public void setPosition(int position) {
		positionPlaylist = position;
	}
	
	/**
	 * Méthode qui formate un nombre pour avoir deux zeros si celui-ci est inférieur à 10
	 * Par exemple 10 reste 10, 1 devient 01
	 * @param number Nombre à formatter
	 * @return La string du nombre formatté
	 */
	public static String formatDoubleZero(int number) {
		// Si le nombre est inférieur à 10, on ajoute un 0 en avant.
		if(number < 10) {
			return "0" + number;
		} else {
			// Sinon on retourne une string du nombre
			return Integer.toString(number);
		}
	}
	
	/**
	 * Méthode qui retourne le temps formaté
	 * @param duration Durée à formatter
	 * @return La string de la durée formatté
	 */
	public static String formatDuration(double duration) {
		// On met la durée en entier (sans nombre à virgule)
		int flooredDuration = (int) Math.floor(duration);
		
		// On calcule les minutes
		int minutes = (int) Math.floor(flooredDuration/60);
		
		// On calcule les secondes
		int seconds = flooredDuration - (minutes*60);
		
		// On retourne la durée formatté
		return minutes + ":" + formatDoubleZero(seconds);
	}
}
