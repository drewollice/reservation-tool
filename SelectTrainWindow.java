import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.LocalTime;
import java.util.Collection;

public final class SelectTrainWindow extends BorderPane
{
	/**
	 * Construct a window for selecting a train.
	 *
	 * @param trains The {@link java.util.Collection} of {@link Train} objects from which to select.
	 */
	public SelectTrainWindow (final Collection<? extends Train> trains)
	{
		super();
		
		final IntegerProperty seatsRemaining = new SimpleIntegerProperty(trains.stream().mapToInt(Train :: getSeatsRemaining).sum());
		trains.forEach(train -> train.seatsRemainingProperty().addListener((observable, oldValue, newValue) -> seatsRemaining.set(trains.stream().mapToInt(Train :: getSeatsRemaining).sum())));
		
		final ScrollPane scrollPane = new ScrollPane(new TrainSelectionPane(trains));
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		
		this.setMaxSize(600, 600);
		
		this.setTop(new Top());
		this.setLeft(new Rectangle(50, 460, Color.LIGHTYELLOW));
		this.setCenter(scrollPane);
		this.setRight(new Rectangle(50, 460, Color.LIGHTYELLOW));
		this.setBottom(new Bottom(seatsRemaining));
	}
	
	private static final class Top extends StackPane
	{
		/**
		 * Construct a Rectangle beneath a label instructing to select a train.
		 */
		private Top ()
		{
			super();
			
			final Rectangle rectangle = new Rectangle(600, 70);
			rectangle.setFill(Color.GAINSBORO);
			
			final Label text = new Label("Select a train route.");
			text.setFont(new Font(50));
			text.setAlignment(Pos.CENTER);
			text.setTextFill(Color.BLACK);
			
			this.getChildren().addAll(rectangle, text);
		}
	}
	
	private static final class TrainSelectionPane extends TableView<Train>
	{
		/**
		 * Construct the actual Train selection table. Displays rows of trains in the following order:
		 * [departure_time, destination, seats_available]
		 *
		 * @param trains The {@link java.util.Collection} of {@link Train} objects from which to select.
		 */
		private TrainSelectionPane (final Collection<? extends Train> trains)
		{
			super(FXCollections.observableArrayList(trains));
			
			this.setPrefSize(500, 457);
			
			final TableColumn<Train, LocalTime> departureTimeCol = new TableColumn<>("Departure Time");
			final TableColumn<Train, String> destinationCol = new TableColumn<>("Destination");
			final TableColumn<Train, Integer> seatsRemainingCol = new TableColumn<>("Seats Remaining");
			
			departureTimeCol.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
			destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
			seatsRemainingCol.setCellValueFactory(new PropertyValueFactory<>("seatsRemaining"));
			
			this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
			
			this.getColumns().add(departureTimeCol);
			this.getColumns().add(destinationCol);
			this.getColumns().add(seatsRemainingCol);
			
			this.setRowFactory(callback ->
							   {
								   final TableRow<Train> row = new TableRow<>();
								   row.setOnMouseEntered(event ->
														 {
															 if (! row.isEmpty())
															 {
																 handleOnMouseEntered();
															 }
														 });
								   row.setOnMouseExited(event ->
														{
															if (! row.isEmpty())
															{
																handleOnMouseExited();
															}
														});
								   row.setOnMouseClicked(event ->
														 {
															 if (! row.isEmpty() && event.getButton() == MouseButton.PRIMARY)
															 {
																 handleOnMouseClicked(row.getItem());
															 }
														 });
								   return row;
							   });
		}
		
		/**
		 * When the user highlights a row, change the cursor.
		 */
		private void handleOnMouseEntered ()
		{
			this.getScene().setCursor(Cursor.HAND);
		}
		
		/**
		 * When the user stops highlighting a row, revert the cursor.
		 */
		private void handleOnMouseExited ()
		{
			this.getScene().setCursor(Cursor.DEFAULT);
		}
		
		/**
		 * When the user clicks a row, open the seat selection window only if the train is not full or busy already.
		 *
		 * @param train The {@link Train} to check and then open.
		 */
		private static void handleOnMouseClicked (final Train train)
		{
			if (train.isBooking())
			{
				final Alert alert = new Alert(Alert.AlertType.NONE, "This train is already booking. Please wait.", ButtonType.OK);
				alert.setTitle("Busy");
				final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("train.png"));
				alert.show();
			}
			else if (0 == train.getSeatsRemaining())
			{
				final Alert alert = new Alert(Alert.AlertType.NONE, "This train is already booked. Please select a different train.", ButtonType.OK);
				alert.setTitle("Full");
				final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("train.png"));
				alert.show();
			}
			else
			{
				train.setBooking(true);
				final Stage stage = new Stage();
				stage.setTitle("Booking Train: " + train.getDepartureTime().toString() + " - " + train.getDestination());
				stage.setScene(new Scene(new SelectSeatWindow(train)));
				stage.setResizable(false);
				stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> train.setBooking(false));
				stage.getIcons().add(new Image("train.png"));
				stage.show();
			}
		}
	}
	
	private static final class Bottom extends StackPane
	{
		/**
		 * Construct a Rectangle beneath a label dictating the sum all all train seats remaining.
		 *
		 * @param seatsRemaining The {@link javafx.beans.property.IntegerProperty} listing seat remaining count.
		 */
		private Bottom (final IntegerProperty seatsRemaining)
		{
			super();
			
			final Rectangle rectangle = new Rectangle(600, 70);
			rectangle.setFill(Color.GAINSBORO);
			
			final Text text = new Text("Seats Remaining: ");
			text.setFont(new Font(50));
			
			final Text count = new Text(Integer.toString(seatsRemaining.intValue()));
			count.setFont(new Font(50));
			count.textProperty().bind(seatsRemaining.asString());
			
			final HBox hBox = new HBox(text, count);
			hBox.setAlignment(Pos.CENTER);
			
			this.getChildren().addAll(rectangle, hBox);
		}
	}
}