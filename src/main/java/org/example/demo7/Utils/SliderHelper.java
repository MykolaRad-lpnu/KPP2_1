package org.example.demo7.Utils;

import org.controlsfx.control.RangeSlider;
import org.example.demo7.Models.FlightDisplay;

import java.util.List;

public class SliderHelper {

    public static void updateSlider(RangeSlider slider, double lowValue, double highValue) {
        if (lowValue > highValue) {
            double temp = lowValue;
            lowValue = highValue;
            highValue = temp;
        }

        if (lowValue < slider.getMin()) {
            slider.setLowValue(slider.getMin());
        } else slider.setLowValue(Math.min(lowValue, slider.getMax()));

        if (highValue > slider.getMax()) {
            slider.setHighValue(slider.getMax());
        } else slider.setHighValue(Math.max(highValue, slider.getMin()));
    }

    public static void updatePriceSlider(RangeSlider priceSlider, List<Double> prices) {
        double minPrice = prices.stream().mapToDouble(Double::doubleValue).min().orElse(1000);
        double maxPrice = prices.stream().mapToDouble(Double::doubleValue).max().orElse(10000) + 10000;

        priceSlider.setMin(1000);
        priceSlider.setMax(maxPrice);
        priceSlider.setLowValue(minPrice);
        priceSlider.setHighValue(maxPrice);
    }

    public static double parseDoubleOrDefault(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseIntOrDefault(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

