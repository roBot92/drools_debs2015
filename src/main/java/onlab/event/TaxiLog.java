package onlab.event;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Logger;

import onlab.positioning.Cell;
import onlab.positioning.Coordinate;

public class TaxiLog {

	private String medallion;
	private String hack_license;
	private Date pickup_datetime;
	private Date dropoff_datetime;
	private long trip_time_in_secs;
	private double trip_distance;
	private Coordinate pickup_coordinate;
	private Coordinate dropoff_coordinate;
	private String payment_type;
	private BigDecimal fare_amount;
	private BigDecimal surcharge;
	private BigDecimal mta_tax;
	private BigDecimal tip_amount;
	private BigDecimal tolls_amount;
	private BigDecimal total_amount;
	private Cell pickup_cell;
	private Cell dropoff_cell;
	private boolean isProcessed = false;
	private boolean olderThanAQuarter = false;
	private long inserted;

	private static final Logger LOGGER = Logger.getLogger(TaxiLog.class.getName());

	public String getMedallion() {
		return medallion;
	}

	public void setMedallion(String medallion) {
		this.medallion = medallion;
	}

	public String getHack_license() {
		return hack_license;
	}

	public void setHack_license(String hack_license) {
		this.hack_license = hack_license;
	}

	public Date getPickup_datetime() {
		return pickup_datetime;
	}

	public void setPickup_datetime(Date pickup_datetime) {
		this.pickup_datetime = pickup_datetime;
	}

	public Date getDropoff_datetime() {
		return dropoff_datetime;
	}

	public void setDropoff_datetime(Date dropoff_datetime) {
		this.dropoff_datetime = dropoff_datetime;
	}

	public long getTrip_time_in_secs() {
		return trip_time_in_secs;
	}

	public void setTrip_time_in_secs(long trip_time_in_secs) {
		this.trip_time_in_secs = trip_time_in_secs;
	}

	public double getTrip_distance() {
		return trip_distance;
	}

	public void setTrip_distance(double trip_distance) {
		this.trip_distance = trip_distance;
	}

	public Coordinate getPickup_coordinate() {
		return pickup_coordinate;
	}

	public void setPickup_coordinate(Coordinate pickup_coordinate) {
		this.pickup_coordinate = pickup_coordinate;
	}

	public Coordinate getDropoff_coordinate() {
		return dropoff_coordinate;
	}

	public void setDropoff_coordinate(Coordinate dropoff_coordinate) {
		this.dropoff_coordinate = dropoff_coordinate;
	}

	public BigDecimal getFare_amount() {
		return fare_amount;
	}

	public void setFare_amount(BigDecimal fare_amount) {
		this.fare_amount = fare_amount;
	}

	public BigDecimal getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(BigDecimal surcharge) {
		this.surcharge = surcharge;
	}

	public BigDecimal getMta_tax() {
		return mta_tax;
	}

	public void setMta_tax(BigDecimal mta_tax) {
		this.mta_tax = mta_tax;
	}

	public BigDecimal getTip_amount() {
		return tip_amount;
	}

	public void setTip_amount(BigDecimal tip_amount) {
		this.tip_amount = tip_amount;
	}

	public BigDecimal getTolls_amount() {
		return tolls_amount;
	}

	public void setTolls_amount(BigDecimal tolls_amount) {
		this.tolls_amount = tolls_amount;
	}

	public BigDecimal getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(BigDecimal total_amount) {
		this.total_amount = total_amount;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public Cell getPickup_cell() {
		return pickup_cell;
	}

	public void setPickup_cell(Cell pickup_cell) {
		this.pickup_cell = pickup_cell;
	}

	public Cell getDropoff_cell() {
		return dropoff_cell;
	}

	public void setDropoff_cell(Cell dropoff_cell) {
		this.dropoff_cell = dropoff_cell;
	}

	public void setPayment_type(String payment_type) {
		if (!"UNK".equals(payment_type) && !"CRD".equals(payment_type) && !"CSH".equals(payment_type)) {
			LOGGER.warning("INVALID PAYMENT TYPE! payment type:" + payment_type);
			this.payment_type = null;
		} else {
			this.payment_type = payment_type;
		}

	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public boolean isOlderThanAQuarter() {
		return olderThanAQuarter;
	}

	public void setOlderThanAQuarter(boolean olderThanAQuarter) {
		this.olderThanAQuarter = olderThanAQuarter;
	}

	public long getInserted() {
		return inserted;
	}

	public TaxiLog setInserted(long inserted) {
		this.inserted = inserted;
		return this;
	}
	
	

}
