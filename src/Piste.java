import java.io.File;

import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Piste {
	private String titre;
	private String album;
	private String artiste;
	private double duree;
	private Media audio;
	private int positionPlaylist;
	
	public Piste(File fichier) {
		audio = new Media(fichier.toURI().toString());
		
		positionPlaylist = TP2.PlaylistSize + 1;
		
		MediaPlayer mediaPlayer = new MediaPlayer(audio);
		
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				duree = audio.getDuration().toSeconds();		
				TP2.playlistTable.refresh();
			}
		});
		
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
			
			TP2.playlistTable.refresh();
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
	
	public double getDuration() {
		return duree;
	}
	
	public Media getAudio() {
		return audio;
	}
	
	public int getPosition() {
		return positionPlaylist;
	}
	
	public void setPosition(int position) {
		positionPlaylist = position;
	}
	
	public static String formatDoubleZero(int number) {
		if(number < 10) {
			return "0" + number;
		} else if(number == 0) {
			return "00";
		} else {
			return Integer.toString(number);
		}
	}
	
	public static String formatDuration(double duration) {
		int flooredDuration = (int) Math.floor(duration);
		
		int minutes = (int) Math.floor(flooredDuration/60);
		int seconds = flooredDuration - (minutes*60);
		
		return minutes + ":" + formatDoubleZero(seconds);
	}
}
