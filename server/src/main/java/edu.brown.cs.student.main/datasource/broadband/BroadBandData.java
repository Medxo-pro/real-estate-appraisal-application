package edu.brown.cs.student.main.datasource.broadband;

/**
 * A record representing a broadband-report datum.
 *
 * @param name the state and county the broadband is from
 * @param broadBand the broadband in the state and county location
 * @param stateCode the code of the state
 * @param countyCode the code of the county
 * @param date the date the data was retrieved
 */
public record BroadBandData(
    String name, String broadBand, String stateCode, String countyCode, String date) {}
