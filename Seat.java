import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Seat
{
	private final BooleanProperty available = new SimpleBooleanProperty(true);
	private final StringProperty seatId;
	
	/**
	 * Construct a new seat object with the default availability of true.
	 *
	 * @param seatId The {@link String} name for this specific seat.
	 */
	public Seat (final String seatId)
	{
		this.seatId = new SimpleStringProperty(seatId);
	}
	
	public final class SeatPane extends StackPane
	{
		private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GREEN);
		
		/**
		 * Construct the outline and text of this seat. Color code, set events, and stack them.
		 */
		public SeatPane ()
		{
			super();
			
			final SeatText seatText = new SeatText(Seat.this.seatId.get());
			final SeatOutline seatOutline = new SeatOutline();
			
			seatOutline.strokeProperty().bind(this.color);
			seatText.strokeProperty().bind(this.color);
			
			this.setOnMouseEntered(event -> handleOnMouseEntered());
			this.setOnMouseExited(event -> handleOnMouseExited());
			this.setOnMouseClicked(event -> handleOnMouseClicked());
			
			this.getChildren().addAll(seatOutline, seatText);
		}

		private final class SeatText extends Text
		{
			/**
			 * Construct the text of the seat.
			 *
			 * @param text The {@link String} text to identify this seat.
			 */
			private SeatText (final String text)
			{
				super(text);
				this.setFont(new Font(10));
			}
		}
		
		private final class SeatOutline extends Rectangle
		{
			/**
			 * Construct the pretty attributes of the seat.
			 */
			private SeatOutline ()
			{
				super();
				
				this.setWidth(40);
				this.setHeight(40);
				this.setArcWidth(15.0);
				this.setArcHeight(10.0);
				this.setStrokeWidth(4);
				this.setFill(Color.WHITE); // Background color.
			}
		}
		
		/**
		 * When the user highlights a seat, change the color of seat and cursor type.
		 */
		private void handleOnMouseEntered ()
		{
			this.getScene().setCursor(Cursor.HAND);
			this.color.set(Seat.this.isAvailable() ? Color.CHARTREUSE : Color.DARKRED);
		}
		
		/**
		 * When the user stops highlighting a seat, revert the color of seat and cursor type.
		 */
		private void handleOnMouseExited ()
		{
			this.getScene().setCursor(Cursor.DEFAULT);
			this.color.set(Seat.this.isAvailable() ? Color.GREEN : Color.RED);
		}
		
		/**
		 * When the user clicks a seat, check if available. If so, mark as unavailable and color-code it; else, alert.
		 */
		private void handleOnMouseClicked ()
		{
			if (Seat.this.isAvailable())
			{
				Seat.this.setAvailable(false);
				this.color.set(Color.DARKRED);
			}
			else
			{
				final Alert alert = new Alert(Alert.AlertType.NONE, "This seat is already booked. Please select a different seat.", ButtonType.OK);
				alert.setTitle("Occupied");
				final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("train.png"));
				alert.show();
			}
		}
	}
	
	public boolean isAvailable ()
	{
		return available.get();
	}
	
	public BooleanProperty availableProperty ()
	{
		return available;
	}
	
	public void setAvailable (final boolean available)
	{
		this.available.set(available);
	}
}
