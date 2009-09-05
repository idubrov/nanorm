package com.google.code.nanorm.annotations;

/**
 * Result set concurrency.
 * 
 * @author Ivan Dubrov
 */
public enum ResultSetConcurrency {

    /**
     * The constant indicating the concurrency mode for a
     * <code>ResultSet</code> object that may NOT be updated.
     * @see java.sql.ResultSet#CONCUR_READ_ONLY
     */
    CONCUR_READ_ONLY,

    /**
     * The constant indicating the concurrency mode for a
     * <code>ResultSet</code> object that may be updated.
     * @see java.sql.ResultSet#CONCUR_UPDATABLE
     */
    CONCUR_UPDATABLE;
}
