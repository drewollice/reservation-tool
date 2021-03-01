import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Candidate Programming Test
 * ________________________________________
 * <p>
 * This test forms part of the hiring process for Glory Software engineering candidates.
 * <p>
 * The text file Train Data.txt (attached) contains the details of trains departing from a train station.
 * <p>
 * Write a program in Java that loads Train Data.txt and provides the following features:
 * <p>
 * 1) Lists all trains displaying destination and departure time
 * 2) Enables a train to be selected and a window or aisle seat to be booked
 * 3) In the event a desired seat (window/aisle) is not available, offers the option of booking another available seat
 * 4) If no seats are available, offers the option of picking a different train to a different destination
 * <p>
 * <p>
 * Assumptions:
 * <p>
 * 1) You can use any user interface technology e.g. Console Application, SWT, Swing, …
 * <p>
 * 2) The fields in the text file are: Departure Time, Destination, total number of seats
 * <p>
 * 3) The total number of seats can be assumed to be an equal split between window and aisle.
 * For example, 12 total means 6 window and 6 aisle seats.
 * <p>
 * 4) The solution should be provided in a .zip file.
 * The .zip file should contain all files necessary to build the solution.
 * This includes source code but also all third-party Java libraries.
 * It is not necessary to provide libraries that are part of Java 11.
 * <p>
 * 5) The solution must contain build files to build it either in the Eclipse IDE, Ant or Maven.
 */
public class Driver extends Application
{
	static Path INPUT_FILE = Path.of("Train Data.txt");//.resolve("Train Data.txt");
	
	/**
	 * This is effectively the main method of a JavaFx application; this is where the fun begins.
	 *
	 * @param stage The window that the user will see when the application opens for the first time.
	 */
	@Override
	public void start (final Stage stage)
	{
		// Populate the stage with a Train Selection Window of the trains passed in via CSV file.
		stage.setScene(new Scene(new SelectTrainWindow(ingestData(INPUT_FILE))));
		
		// Make the window pretty.
		stage.setTitle("Glory Global Candidate Programming Test");
		stage.getIcons().add(new Image("train.png"));
		stage.setResizable(false);
		stage.getScene().getStylesheets().add("style.css");
		
		// Present the screen to the user.
		stage.show();
	}
	
	/**
	 * Collect the data from a CSV formatted file and turn each record into a Train object.
	 *
	 * @param file The file path + file name + file extension of the file to be ingested.
	 *
	 * @return The List of Trains derived from each row in the CSV file.
	 */
	public static List<Train> ingestData (final Path file)
	{
		// Create a reader object to be used reading the file.
		try (BufferedReader data = Files.newBufferedReader(file))
		{
			// Return a List of new trains per each line of the file.
			return data.lines().map(Train :: new).collect(Collectors.toList());
		}
		catch (final IOException e)
		{
			// Return an empty list if anything went ary.
			return Collections.emptyList();
		}
	}
	
	/**
	 * The main method called when the file is run. All this does is launch the JavaFX app.
	 *
	 * @param args There should be no command line argument.
	 */
	public static void main (final String[] args)
	{
		// Launch the JavaFX app.
		launch(args);
	}
}