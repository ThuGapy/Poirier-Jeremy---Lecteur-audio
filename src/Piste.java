import java.io.File;

import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.util.Duration;

public class Piste {
	private String titre;
	private String album;
	private String artiste;
	private Duration duree;
	private Media audio;
	private int positionPlaylist;
	
	public Piste(File fichier) {
		audio = new Media(fichier.toURI().toString());
		
		duree = audio.getDuration();
		
		positionPlaylist = TP2.Playlist.size() + 1;
		
		audio.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
			if (change.wasAdded()) {
				switch((String)change.getKey()) {
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
		        /*System.out.println("Champ ajouté : " + change.getKey().toString());
		        System.out.println("Valeur du champ : " + change.getValueAdded().toString());*/
		    }
		    else {
		        System.out.println("Champ supprimé : " + change.getKey().toString());
		    }
		});
	}
	
	public String getTitle() {
		return titre;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public String getArtist() {
		return artiste;
	}
	
	public Duration getDuration() {
		return duree;
	}
	
	public Media getAudio() {
		return audio;
	}
	
	public int getPosition() {
		return positionPlaylist;
	}
}
