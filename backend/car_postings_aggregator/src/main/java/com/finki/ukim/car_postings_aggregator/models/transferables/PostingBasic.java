package com.finki.ukim.car_postings_aggregator.models.transferables;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PostingBasic {

    private String postedAtDateTime; // dd mmm. hh:mm

    private String promoImageUrl;

    private String locatedInCity;

    private LocalDateTime datePosted;

    // r5 postings lack this, show it as -See in details- or ping the url for the year
    private Integer vehicleManufacturedYear;

    private String price;

    // r5 postings lack this, show it as -See in details- or ping the url for the mileage
    private String mileageFromTo;

    private String postingDescription;

    private String detailsUrl;

    public PostingBasic() {
    }

    public PostingBasic(String postedAtDateTime,
                        String promoImageUrl,
                        String locatedInCity,
                        Integer vehicleManufacturedYear,
                        String price,
                        String mileageFromTo,
                        String postingDescription,
                        String detailsUrl) {
        this.postedAtDateTime = postedAtDateTime;
        this.promoImageUrl = promoImageUrl;
        if (this.promoImageUrl != null && this.promoImageUrl.contains("reklama5"))
            this.promoImageUrl = "http://" + this.promoImageUrl;

        this.locatedInCity = locatedInCity;
        if (this.locatedInCity != null) {
            if (this.locatedInCity.endsWith(","))
                this.locatedInCity = this.locatedInCity.substring(0, this.locatedInCity.length() - 1);
            else if (this.locatedInCity.endsWith(", "))
                this.locatedInCity = this.locatedInCity.substring(0, this.locatedInCity.length() - 2);

            if (this.locatedInCity.contains("Автомо"))
                this.locatedInCity = this.locatedInCity.substring(0, this.locatedInCity.indexOf("Автомобили") - 2);
        }

        this.vehicleManufacturedYear = vehicleManufacturedYear;
        this.price = price;
        this.mileageFromTo = mileageFromTo;
        this.postingDescription = postingDescription;
        this.detailsUrl = detailsUrl;
        this.mapPostedDate(this.postedAtDateTime);
    }

    private void mapPostedDate(String postedAtDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (postedAtDateTime.contains("Денес")) {
            String tmpDate = LocalDateTime.now().toString();
            tmpDate = tmpDate.substring(0, tmpDate.indexOf("T"));
            String tmpTime = this.postedAtDateTime.substring(this.postedAtDateTime.length() - 5);
            this.datePosted = LocalDateTime.parse(tmpDate + " " + tmpTime, formatter);
            this.postedAtDateTime = this.postedAtDateTime.replaceAll(",", " ");
        } else if (postedAtDateTime.contains("Вчера")) {
            String tmpDate = LocalDateTime.now().minusDays(1).toString();
            tmpDate = tmpDate.substring(0, tmpDate.indexOf("T"));
            String tmpTime = this.postedAtDateTime.substring(this.postedAtDateTime.length() - 5);
            this.datePosted = LocalDateTime.parse(tmpDate + " " + tmpTime, formatter);

            this.postedAtDateTime = this.postedAtDateTime.replaceAll(",", " ");
        } else {
            String[] components = postedAtDateTime.split(" ");

            String numericMonth = "01";

            if (components[1].contains("фе"))
                numericMonth = "02";
            else if (components[1].contains("мар"))
                numericMonth = "03";
            else if (components[1].contains("апр"))
                numericMonth = "04";
            else if (components[1].contains("мај"))
                numericMonth = "05";
            else if (components[1].contains("јун"))
                numericMonth = "06";
            else if (components[1].contains("јул"))
                numericMonth = "07";
            else if (components[1].contains("авг"))
                numericMonth = "08";
            else if (components[1].contains("сеп"))
                numericMonth = "09";
            else if (components[1].contains("окт"))
                numericMonth = "10";
            else if (components[1].contains("ное"))
                numericMonth = "11";
            else if (components[1].contains("дек"))
                numericMonth = "12";

            String stringDate = LocalDate.now().getYear() + "-" + numericMonth + "-" + components[0];

            if (stringDate.length() == 10)
                this.datePosted = LocalDateTime.parse(stringDate + " " + this.postedAtDateTime.substring(this.postedAtDateTime.length() - 5), formatter);
            else
                this.datePosted = LocalDateTime.now();
        }
    }

    public String getPostedAtDateTime() {
        return postedAtDateTime;
    }

    public void setPostedAtDateTime(String postedAtDateTime) {
        this.postedAtDateTime = postedAtDateTime;
    }

    public String getPromoImageUrl() {
        return promoImageUrl;
    }

    public void setPromoImageUrl(String promoImageUrl) {
        this.promoImageUrl = promoImageUrl;
    }

    public String getLocatedInCity() {
        return locatedInCity;
    }

    public void setLocatedInCity(String locatedInCity) {
        this.locatedInCity = locatedInCity;
    }

    public Integer getVehicleManufacturedYear() {
        return vehicleManufacturedYear;
    }

    public void setVehicleManufacturedYear(Integer vehicleManufacturedYear) {
        this.vehicleManufacturedYear = vehicleManufacturedYear;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMileageFromTo() {
        return mileageFromTo;
    }

    public void setMileageFromTo(String mileageFromTo) {
        this.mileageFromTo = mileageFromTo;
    }

    public String getPostingDescription() {
        return postingDescription;
    }

    public void setPostingDescription(String postingDescription) {
        this.postingDescription = postingDescription;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostingBasic that = (PostingBasic) o;
        return locatedInCity.equals(that.locatedInCity) &&
                Objects.equals(vehicleManufacturedYear, that.vehicleManufacturedYear) &&
                price.equals(that.price) &&
                Objects.equals(mileageFromTo, that.mileageFromTo) &&
                postingDescription.equals(that.postingDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locatedInCity, vehicleManufacturedYear, price, mileageFromTo, postingDescription);
    }
}

