package net.semppi.semppis_mythical_legends_mod.season;

import java.time.LocalDate;
import java.time.Month;

public final class SeasonManager {

    private SeasonManager() {
    }

    public static SeasonPhase getCurrentSeasonPhase() {

        LocalDate currentDate =
                LocalDate.now();

        return getSeasonPhase(currentDate);
    }

    public static SeasonPhase getSeasonPhase(LocalDate date) {

        Month month =
                date.getMonth();

        return switch (month) {

            case MARCH ->
                    SeasonPhase.EARLY_SPRING;

            case APRIL ->
                    SeasonPhase.SPRING;

            case MAY ->
                    SeasonPhase.LATE_SPRING;


            case JUNE ->
                    SeasonPhase.EARLY_SUMMER;

            case JULY ->
                    SeasonPhase.SUMMER;

            case AUGUST ->
                    SeasonPhase.LATE_SUMMER;


            case SEPTEMBER ->
                    SeasonPhase.EARLY_AUTUMN;

            case OCTOBER ->
                    SeasonPhase.AUTUMN;

            case NOVEMBER ->
                    SeasonPhase.LATE_AUTUMN;


            case DECEMBER ->
                    SeasonPhase.EARLY_WINTER;

            case JANUARY ->
                    SeasonPhase.WINTER;

            case FEBRUARY ->
                    SeasonPhase.LATE_WINTER;
        };
    }
}