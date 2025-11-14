package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"checkstyle:WriteTag", "checkstyle:SuppressWarnings"})
public class StatementPrinter {
    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    @SuppressWarnings(
            {"checkstyle:FinalLocalVariable",
                    "checkstyle:SuppressWarnings",
                    "checkstyle:MissingJavadocMethod",
                    "checkstyle:Indentation"})
    public String statement() {

        StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        // ---- Loop 1: build individual performance lines ----
        for (Performance p : invoice.getPerformances()) {
            Play play = plays.get(p.getPlayID());
            result.append(String.format("  %s: %s (%s seats)%n",
                    play.getName(),
                    usd(getAmount(p)),
                    p.getAudience()));
        }

        // ---- Loop 2: calculate total volume credits ----
        int volumeCredits = getTotalVolumeCredits();

        // ---- Loop 3: calculate total amount ----
        int totalAmount = getTotalAmount();

        // ---- Final Summary ----
        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));

        return result.toString();
    }

    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings"})
    private int getTotalVolumeCredits() {
        int result = 0;
        for (Performance p : invoice.getPerformances()) {
            Play play = plays.get(p.getPlayID());
            result += getVolumeCredits(p, play);
        }
        return result;
    }

    private int getTotalAmount() {
        int result = 0;
        for (Performance p : invoice.getPerformances()) {
            result += getAmount(p);
        }
        return result;
    }

    private static int getVolumeCredits(Performance performance, Play play) {
        int result = 0;
        result += Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(play.getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings", "checkstyle:MagicNumber"})
    private int getAmount(Performance performance) {
        Play play = plays.get(performance.getPlayID());
        int result = 0;
        switch (play.getType()) {
            case "tragedy":
                result = 40000;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;

            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD);
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;

            default:
                throw new RuntimeException(String.format("unknown type: %s", play.getType()));
        }
        return result;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private String usd(int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount / 100.0);
    }
    @SuppressWarnings({"checksstyle:MissingJavadocMethod",
                       "checkstyle:EmptyLineSeparator",
                       "checkstyle:MissingJavadocMethod"}
    )
    public Play getPlay(Performance performance) {
        return plays.get(performance.getPlayID());
    }
}
