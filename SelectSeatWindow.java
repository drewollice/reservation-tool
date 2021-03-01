import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class SelectSeatWindow extends BorderPane
{
	/**
	 * Construct a window for selecting seats on the selected train. This window is a border pane with directions on
	 * top, seat selections in the center, and total remaining seats on the bottom.
	 *
	 * @param train The {@link Train} selected within which to choose seats.
	 */
	public SelectSeatWindow (final Train train)
	{
		super();
		
		final ScrollPane scrollPane = new ScrollPane(train.new SeatSelectionPane());
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		
		this.setTop(new Top());
		this.setLeft(new Rectangle(202.5, 460, Color.LIGHTYELLOW));
		this.setCenter(scrollPane);
		this.setRight(new Rectangle(202.5, 460, Color.LIGHTYELLOW));
		this.setBottom(new Bottom(train.seatsRemainingProperty()));
		
		this.setMaxSize(600, 600);
	}
	
	private static final class Top extends StackPane
	{
		/**
		 * Construct a Rectangle beneath a label instructing to select seat(s).
		 */
		private Top ()
		{
			super();
			
			final Rectangle rectangle = new Rectangle(600, 70);
			rectangle.setFill(Color.GAINSBORO);
			
			final Label text = new Label("Now select seat(s).");
			text.setFont(new Font(50));
			text.setAlignment(Pos.CENTER);
			text.setTextFill(Color.BLACK);
			
			this.getChildren().addAll(rectangle, text);
		}
	}
	
	private static final class Bottom extends StackPane
	{
		/**
		 * Construct a Rectangle beneath the text dictating the amount of available seats remaining.
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