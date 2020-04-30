package com.finki.ukim.car_postings_aggregator.models.transferables;

public class PostingFilterParameters {
    private String newestCheapest;
    private String personCompany;
    private String buySell;
    private String city;
    private String priceFrom;
    private String priceTo;
    private String brand;
    private String yearFrom;
    private String yearTo;
    private String mileageFrom;
    private String mileageTo;
    private String transmission;
    private String fuelType;
    private String licencePlate;

    public PostingFilterParameters() {
    }

    public PostingFilterParameters(String newestCheapest,
                                   String personCompany,
                                   String buySell,
                                   String city,
                                   String priceFrom,
                                   String priceTo,
                                   String brand,
                                   String yearFrom,
                                   String yearTo,
                                   String mileageFrom,
                                   String mileageTo,
                                   String transmission,
                                   String fuelType,
                                   String licencePlate) {
        this.newestCheapest = newestCheapest;
        this.personCompany = personCompany;
        this.buySell = buySell;
        this.city = city;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.brand = brand;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.mileageFrom = mileageFrom;
        this.mileageTo = mileageTo;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.licencePlate = licencePlate;
    }

    public String getNewestCheapest() {
        return newestCheapest;
    }

    public void setNewestCheapest(String newestCheapest) {
        this.newestCheapest = newestCheapest;
    }

    public String getPersonCompany() {
        return personCompany;
    }

    public void setPersonCompany(String personCompany) {
        this.personCompany = personCompany;
    }

    public String getBuySell() {
        return buySell;
    }

    public void setBuySell(String buySell) {
        this.buySell = buySell;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(String priceFrom) {
        this.priceFrom = priceFrom;
    }

    public String getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(String priceTo) {
        this.priceTo = priceTo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(String yearFrom) {
        this.yearFrom = yearFrom;
    }

    public String getYearTo() {
        return yearTo;
    }

    public void setYearTo(String yearTo) {
        this.yearTo = yearTo;
    }

    public String getMileageFrom() {
        return mileageFrom;
    }

    public void setMileageFrom(String mileageFrom) {
        this.mileageFrom = mileageFrom;
    }

    public String getMileageTo() {
        return mileageTo;
    }

    public void setMileageTo(String mileageTo) {
        this.mileageTo = mileageTo;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }
}
