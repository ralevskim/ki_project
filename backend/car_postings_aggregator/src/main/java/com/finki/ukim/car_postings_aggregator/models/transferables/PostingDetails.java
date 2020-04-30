package com.finki.ukim.car_postings_aggregator.models.transferables;

import java.util.List;
import java.util.Objects;

public class PostingDetails {
    private List<String> postingImageUrls;

    private String title;

    private String authorName;

    private String vehicleBrand;

    private String vehicleModel;

    private String licensePlate;

    private String vehicleColor;

    private String transmissionType;

    private String fuelType;

    private String vehicleBodyType;

    // r5 postings lack this, show it as -See in details- or ping the url for the mileage
    private String mileageFromTo;

    // r5 postings lack this, show it as -See in details- or ping the url for the year
    private Integer vehicleManufacturedYear;

    private String authorContactNumber;

    private String detailedDescription;

    private String postedAtDateTime; // dd mmm. hh:mm

    private String locatedInCity;

    private String price;

    private boolean isAlreadySaved;

    public PostingDetails() {
    }

    public PostingDetails(List<String> postingImageUrls,
                          String title, String authorName,
                          String vehicleBrand,
                          String vehicleModel,
                          String licensePlate,
                          String vehicleColor,
                          String transmissionType,
                          String fuelType,
                          String vehicleBodyType,
                          String mileageFromTo,
                          Integer vehicleManufacturedYear,
                          String authorContactNumber,
                          String detailedDescription,
                          String postedAtDateTime,
                          String locatedInCity,
                          String price,
                          boolean isAlreadySaved) {
        this.postingImageUrls = postingImageUrls;
        this.title = title;
        this.authorName = authorName;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.licensePlate = licensePlate;
        this.vehicleColor = vehicleColor;
        this.transmissionType = transmissionType;
        this.fuelType = fuelType;
        this.vehicleBodyType = vehicleBodyType;
        this.mileageFromTo = mileageFromTo;
        this.vehicleManufacturedYear = vehicleManufacturedYear;
        this.authorContactNumber = authorContactNumber;
        this.detailedDescription = detailedDescription;
        this.postedAtDateTime = postedAtDateTime;
        this.locatedInCity = locatedInCity;
        this.price = price;
        this.isAlreadySaved = isAlreadySaved;
    }

    public boolean isAlreadySaved() {
        return isAlreadySaved;
    }

    public void setAlreadySaved(boolean alreadySaved) {
        isAlreadySaved = alreadySaved;
    }

    public List<String> getPostingImageUrls() {
        return postingImageUrls;
    }

    public void setPostingImageUrls(List<String> postingImageUrls) {
        this.postingImageUrls = postingImageUrls;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getVehicleBodyType() {
        return vehicleBodyType;
    }

    public void setVehicleBodyType(String vehicleBodyType) {
        this.vehicleBodyType = vehicleBodyType;
    }

    public String getMileageFromTo() {
        return mileageFromTo;
    }

    public void setMileageFromTo(String mileageFromTo) {
        this.mileageFromTo = mileageFromTo;
    }

    public Integer getVehicleManufacturedYear() {
        return vehicleManufacturedYear;
    }

    public void setVehicleManufacturedYear(Integer vehicleManufacturedYear) {
        this.vehicleManufacturedYear = vehicleManufacturedYear;
    }

    public String getAuthorContactNumber() {
        return authorContactNumber;
    }

    public void setAuthorContactNumber(String authorContactNumber) {
        this.authorContactNumber = authorContactNumber;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getPostedAtDateTime() {
        return postedAtDateTime;
    }

    public void setPostedAtDateTime(String postedAtDateTime) {
        this.postedAtDateTime = postedAtDateTime;
    }

    public String getLocatedInCity() {
        return locatedInCity;
    }

    public void setLocatedInCity(String locatedInCity) {
        this.locatedInCity = locatedInCity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostingDetails that = (PostingDetails) o;
        return postingImageUrls.equals(that.postingImageUrls) &&
                authorName.equals(that.authorName) &&
                vehicleBrand.equals(that.vehicleBrand) &&
                vehicleModel.equals(that.vehicleModel) &&
                licensePlate.equals(that.licensePlate) &&
                vehicleColor.equals(that.vehicleColor) &&
                transmissionType.equals(that.transmissionType) &&
                fuelType.equals(that.fuelType) &&
                vehicleBodyType.equals(that.vehicleBodyType) &&
                Objects.equals(mileageFromTo, that.mileageFromTo) &&
                Objects.equals(vehicleManufacturedYear, that.vehicleManufacturedYear) &&
                authorContactNumber.equals(that.authorContactNumber) &&
                detailedDescription.equals(that.detailedDescription) &&
                postedAtDateTime.equals(that.postedAtDateTime) &&
                locatedInCity.equals(that.locatedInCity) &&
                price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postingImageUrls, authorName, vehicleBrand, vehicleModel, licensePlate, vehicleColor, transmissionType, fuelType, vehicleBodyType, mileageFromTo, vehicleManufacturedYear, authorContactNumber, detailedDescription, postedAtDateTime, locatedInCity, price);
    }
}
