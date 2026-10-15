package it.unipd.dei.webapp.resource.test;

import java.time.LocalDateTime;
/**
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */


public class DBHealthCheck {
    /**
     * The time of the check
     */
    private LocalDateTime datetime;

    /**
     * Creates a new Check
     */
    public DBHealthCheck() {
        this.datetime = LocalDateTime.now();
    }

    /**
     * Returns the datetime of the Check.
     *
     * @return the datetime of the Check
     */
    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }


}
