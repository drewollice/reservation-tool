import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public final class Train
{
	private final LocalTime departureTime;
	private final StringProperty destination;
	private final IntegerProperty totalSeats;
	private final IntegerProperty seatsRemaining;
	private final BooleanProperty booking = new SimpleBooleanProperty(false);
	private final ObservableList<Seat> seats = FXCollections.observableArrayList();
	
	/**
	 * Construct a train object with the provided parameters.
	 *
	 * @param departureTime The {@link String} time the train departs.
	 * @param destination   The {@link String} location the train will stop at.
	 * @param totalSeats    The {@link String} total seats (both available or otherwise) on the train.
	 */
	private Train (final String departureTime, final String destination, final String totalSeats)
	{
		this.departureTime = LocalTime.parse(departureTime.trim(), DateTimeFormatter.ofPattern("HH.mm"));
		this.destination = new SimpleStringProperty(destination.trim());
		this.totalSeats = new SimpleIntegerProperty(Integer.parseInt(totalSeats.trim()));
		this.seatsRemaining = new SimpleIntegerProperty(this.totalSeats.get());
		
		// Load into the seats object a new seat as needed and identified per their position+window/aisle.
		for (int rowIndex = 0; rowIndex < this.totalSeats.get() / 2; rowIndex++)
		{
			this.seats.add(new Seat(rowIndex + "W"));
			this.seats.add(new Seat(rowIndex + "A"));
		}
		// Add 1 more seat if total seats is odd.
		if (this.totalSeats.get() > this.seats.size())
		{
			this.seats.add(new Seat(this.getSeats().size() + "W"));
		}
		
		this.seats.forEach(seat -> seat.availableProperty().addListener(observable -> this.seatsRemaining.set(Math.toIntExact(this.seats.stream().filter(Seat :: isAvailable).count()))));
	}
	
	/**
	 * Construct a train object by passing in a single string in CSV format.
	 *
	 * @param line The {@link String} CSV formatting line to be broken up an used to construct a train object.
	 */
	public Train (final String line)
	{
		this(line.split(",")[0], line.split(",")[1], line.split(",")[2]);
	}
	
	public final class SeatSelectionPane extends GridPane
	{
		/**
		 * Construct the GUI of the seats to select. Creates a row in the following order:
		 * [window, window_seat, aisle_seat, aisle, window]
		 */
		public SeatSelectionPane ()
		{
			super();
			
			// Generate 1 row at a time based on half the number of seats since 2 seats per row.
			int seatIndex = 0;
			for (int rowIndex = 0; rowIndex < Train.this.getTotalSeats() / 2; rowIndex++)
			{
				this.add(new Window(), 0, rowIndex);
				this.add(seats.get(seatIndex++).new SeatPane(), 1, rowIndex);
				this.add(seats.get(seatIndex++).new SeatPane(), 2, rowIndex);
				this.add(new Aisle(), 3, rowIndex);
				this.add(new Window(), 4, rowIndex);
			}
			// Add 1 more if odd number of total seats.
			if (seatIndex < Train.this.getSeats().size())
			{
				this.add(new Window(), 0, Train.this.getSeats().size());
				this.add(seats.get(seatIndex).new SeatPane(), 1, Train.this.getSeats().size());
				this.add(new Aisle(), 3, Train.this.getSeats().size());
				this.add(new Window(), 4, Train.this.getSeats().size());
			}
			
			this.setHgap(5);
			this.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
		}
		
		private final class Window extends StackPane
		{
			/**
			 * Construct a stack of a circle window atop a rectangle window-mounting.
			 */
			private Window ()
			{
				super(new Rectangle(10, 50, Color.DARKGREY), new Circle(5, Color.LIGHTBLUE));
			}
		}
		
		private final class Aisle extends HBox
		{
			/**
			 * Construct an HBox of 5 color stripes to make a pretty carpet in the aisle.
			 */
			private Aisle ()
			{
				super(new Rectangle(7.5, 50, Color.NAVAJOWHITE), new Rectangle(5, 50, Color.LIGHTSKYBLUE), new Rectangle(25, 50, Color.LIGHTSTEELBLUE), new Rectangle(5, 50, Color.LIGHTSKYBLUE), new Rectangle(7.5, 50, Color.NAVAJOWHITE));
				
				this.setWidth(50);
			}
		}
	}
	
	public LocalTime getDepartureTime ()
	{
		return departureTime;
	}
	
	public String getDestination ()
	{
		return destination.get();
	}
	
	public int getTotalSeats ()
	{
		return totalSeats.get();
	}
	
	public int getSeatsRemaining ()
	{
		return seatsRemaining.get();
	}
	
	public IntegerProperty seatsRemainingProperty ()
	{
		return seatsRemaining;
	}
	
	public boolean isBooking ()
	{
		return booking.get();
	}
	
	public void setBooking (final boolean booking)
	{
		this.booking.set(booking);
	}
	
	public List<Seat> getSeats ()
	{
		return Collections.unmodifiableList(seats);
	}
}