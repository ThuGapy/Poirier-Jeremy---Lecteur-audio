import java.io.File;

import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



/**
 * Classe qui correspond � une piste dans la playlist
 */
public class Piste {
	// D�claration des variables priv�s de la classe Piste
	private String titre;
	private String album;
	private String artiste;
	private double duree;
	private Media audio;
	private int positionPlaylist;
	
	/**
	 * Constructeur de la piste, cr�e l'objet et obtient la dur�e de la piste et ses m�tadatas
	 * @param fichier Le fichier du .mp3 de la musique
	 */
	public Piste(File fichier) {
		// Cr�ation de l'audio avec l'URL du fichier
		audio = new Media(fichier.toURI().toString());
		
		// On met la position de la piste comme �tant la grosseur de la playlist + 1
		// La piste est donc la derni�re de la playlist
		positionPlaylist = TP2.PlaylistSize + 1;
		
		// On cr�e un mediaPlayer pour obtenir la dur�e de la musique
		MediaPlayer mediaPlayer = new MediaPlayer(audio);
		
		// Lorsque le mediaPlayer est pr�t nous pouvons obtenir la duree.
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				duree = audio.getDuration().toSeconds();	
				
				// Lorsqu'on obtient la dur�e, on rafraichit le tableau
				TP2.playlistTable.refresh();
			}
		});
		
		// Lorsque les metadata sont pr�te, on les stocke dans des variables
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
			
			// Apr�s l'ajout des m�tadata, on rafraichit le tableau
			TP2.playlistTable.refresh();
		});
	}
	
	/**
	 * M�thode qui permet d'obtenir le titre de la musique
	 * @return La string du titre de la musique
	 */
	public String getTitle() {
		return titre;
	}
	
	/**
	 * M�thode qui permet d'obtenir l'album de la musique
	 * @return La string de l'album de la musique
	 */
	public String getAlbum() {
		return album;
	}
	
	/**
	 * M�thode qui permet d'obtenir le nom de l'artiste
	 * @return Une string du nom de l'artiste
	 */
	public String getArtist() {
		return artiste;
	}
	
	/**
	 * M�thode qui permet d'obtenir la dur�e de la musique
	 * @return La dur�e de la piste audio
	 */
	public double getDuration() {
		return duree;
	}
	
	/**
	 * M�thode qui permet d'obtenir le Media d'une piste audio. le Media correspond au fichier audio
	 * @return Le media de la musique
	 */
	public Media getAudio() {
		return audio;
	}
	
	/**
	 * M�thode qui permet d'obtenir la position d'une musique dans la playlist
	 * @return La position de la musique dans la playlist
	 */
	public int getPosition() {
		return positionPlaylist;
	}
	
	/**
	 * M�thode qui permet de changer la position d'une musique dans la playlist
	 * Utilis� lorsqu'un musique est supprim� de la playlist pour actualiser la position
	 * @param position Nouvelle position de la piste dans la playlist
	 */
	public void setPosition(int position) {
		positionPlaylist = position;
	}
	
	/**
	 * M�thode qui formate un nombre pour avoir deux zeros si celui-ci est inf�rieur � 10
	 * Par exemple 10 reste 10, 1 devient 01
	 * @param number Nombre � formatter
	 * @return La string du nombre formatt�
	 */
	public static String formatDoubleZero(int number) {
		// Si le nombre est inf�rieur � 10, on ajoute un 0 en avant.
		if(number < 10) {
			return "0" + number;
		} else {
			// Sinon on retourne une string du nombre
			return Integer.toString(number);
		}
	}
	
	/**
	 * M�thode qui retourne le temps format�
	 * @param duration Dur�e � formatter
	 * @return La string de la dur�e formatt�
	 */
	public static String formatDuration(double duration) {
		// On met la dur�e en entier (sans nombre � virgule)
		int flooredDuration = (int) Math.floor(duration);
		
		// On calcule les minutes
		int minutes = (int) Math.floor(flooredDuration/60);
		
		// On calcule les secondes
		int seconds = flooredDuration - (minutes*60);
		
		// On retourne la dur�e formatt�
		return minutes + ":" + formatDoubleZero(seconds);
	}
}
