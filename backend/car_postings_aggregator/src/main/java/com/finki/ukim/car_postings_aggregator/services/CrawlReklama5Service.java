package com.finki.ukim.car_postings_aggregator.services;

import com.finki.ukim.car_postings_aggregator.models.SavedPosting;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingBasic;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingDetails;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingFilterParameters;
import com.finki.ukim.car_postings_aggregator.repositories.UsersJpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CrawlReklama5Service {

    private final String reklama5Url = "https://www.reklama5.mk";
    private final UsersJpaRepository usersJpaRepository;

    public CrawlReklama5Service(UsersJpaRepository usersJpaRepository) {
        this.usersJpaRepository = usersJpaRepository;
    }

    // url must start with forward slash
    public List<PostingBasic> getPostingsForR5Url(String url) throws IOException {
        String crawlUrl = url.startsWith("http") ? url : this.reklama5Url + url;

        ArrayList<PostingBasic> postings = new ArrayList<>();

        URL reklama5Url = new URL(crawlUrl);
        URLConnection reklama5UrlConnection = reklama5Url.openConnection();
        reklama5UrlConnection.addRequestProperty("User-Agent", "Chrome");
        BufferedReader in = new BufferedReader(new InputStreamReader(reklama5UrlConnection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.contains("<div class=\"OglasResults\"")) {
                boolean promotedFlag = false;

                // date and time
                String postingDateTime = in.readLine();

                // if is differently formatted compared to the majority
                if (!postingDateTime.contains("<br />")) {
                    String dayAndMonth = in.readLine();
                    in.readLine(); // ignore <br />
                    String time = in.readLine();

                    postingDateTime = dayAndMonth + "," + time;
                    promotedFlag = true;
                } else {
                    String[] components = postingDateTime.split("<br />");
                    String dayAndMonth = components[0].substring(56);
                    String time = components[1].substring(0, 5);

                    postingDateTime = dayAndMonth + "," + time;
                }

                String detailsUrl;
                String imageUrl;
                String postingsDescription;

                if (promotedFlag) {
                    in.readLine();

                    // image & posting's description (if promotedFlag == true, then it contains the details link as well)
                    String imageDescriptionDetailsUrlLine = in.readLine();

                    detailsUrl = imageDescriptionDetailsUrlLine.split("><img")[0].split("=\"")[1];
                    detailsUrl = detailsUrl.substring(0, detailsUrl.length() - 2);
                    detailsUrl = this.reklama5Url + detailsUrl;

                    imageUrl = imageDescriptionDetailsUrlLine.split("class")[0].split("//")[1];
                    imageUrl = imageUrl.substring(0, imageUrl.length() - 2);

                    postingsDescription = imageDescriptionDetailsUrlLine.split("SearchAdTitle\">")[1].split("</a>")[0];
                } else {
                    detailsUrl = in.readLine().split("href=\"")[1];
                    detailsUrl = detailsUrl.substring(0, detailsUrl.length() - 2);

                    // image & posting's description
                    String imageDescriptionDetailsUrlLine = in.readLine();
                    if (imageDescriptionDetailsUrlLine.contains("noImage"))
                        imageUrl = null;
                    else {
                        imageUrl = imageDescriptionDetailsUrlLine.split("class")[0].split("//")[1];
                        imageUrl = imageUrl.substring(0, imageUrl.length() - 2);
                    }

                    postingsDescription = imageDescriptionDetailsUrlLine.split("SearchAdTitle\">")[1].split("</a>")[0];
                }
                in.readLine();

                // price
                String priceString = in.readLine();
                if (!priceString.startsWith("По"))
                    priceString = priceString.split("</div>")[0].split(" ")[0];

                if (priceString.contains("."))
                    priceString = priceString.replace(".", "");

                in.readLine();
                in.readLine();

                // location
                String location = in.readLine().split("nowrap\">")[1].split("</")[0];
                if (location.contains("&gt;"))
                    location = location.replace(" &gt;", ",");

                postings.add(new PostingBasic(postingDateTime, imageUrl, location, -1, priceString, "", postingsDescription, detailsUrl));
            }
        }

        return postings;
    }

    public PostingDetails getDetailedPostingForR5Url(String url, User user) throws IOException {
        String crawlUrl = url.startsWith("http") ? url : this.reklama5Url + url;

        boolean isAlreadySaved = false;
        if (user != null) {
            Optional<com.finki.ukim.car_postings_aggregator.models.User> user1 = this.usersJpaRepository
                    .findByUsernameOrEmail(user.getUsername(), user.getUsername());
            if (user1.isPresent()) {
                for (SavedPosting savedPosting : user1.get().getSavedPostings()) {
                    if (savedPosting.getPostingUrl().contains(url)) {
                        isAlreadySaved = true;
                        break;
                    }
                }
            }
        }

        String vehicleBrand = null;
        String vehicleModel = null;
        String licensePlate = null;
        String vehicleColor = null;
        List<String> postingImageUrls = new ArrayList<>();
        String transmissionType = null;
        String fuelType = null;
        String vehicleBodyType = null;
        // r5 postings lack this, show it as -See in details- or ping the url for the mileage
        String mileageFromTo = null;
        // r5 postings lack this, show it as -See in details- or ping the url for the year
        Integer vehicleManufacturedYear = null;
        String authorContactNumber = null;
        String authorName = null;
        String detailedDescription = null;
        String postedAtDateTime = null; // dd mmm. hh:mm
        String locatedInCity = null;
        String price = "По договор.";
        String title = null;

        URL reklama5Url = new URL(crawlUrl);
        URLConnection reklama5UrlConnection = reklama5Url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(reklama5UrlConnection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.contains("<title>")) {
                title = line.split("</title>")[0].substring(11);
                continue;
            }

            if (line.contains("<meta name=\"description\"")) {
                line = line.split("content=\"")[1];
                if (line.endsWith("/>")) {
                    detailedDescription = line.substring(0, line.length() - 4);
                    continue;
                }

                StringBuilder sb = new StringBuilder();
                sb.append(line).append("\n");
                line = in.readLine();
                while (!line.contains("/>")) {
                    sb.append(line).append("\n");
                    line = in.readLine();
                }
                line = line.substring(0, line.length() - 4);
                sb.append(line);

                detailedDescription = sb.toString();
                continue;
            }

            if (line.contains("oglasTitle\" style=\"float:left")) {
                line = line.split("Објавен на: ")[1];
                postedAtDateTime = line.substring(0, line.length() - 6);
                continue;
            }

            if (line.contains("background-image: url") || line.contains("background-image:url")) {
                line = line.split("//")[1];
                int index = line.indexOf("'");
                postingImageUrls.add(line.substring(0, index));
                continue;
            }

            if (line.contains("Марка: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                vehicleBrand = line.substring(0, index);
                continue;
            }

            if (line.contains("Модел: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                vehicleModel = line.substring(0, index);
                continue;
            }

            if (line.contains("Година: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                vehicleManufacturedYear = Integer.parseInt(line.substring(0, index));
                continue;
            }

            if (line.contains("Гориво: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                fuelType = line.substring(0, index);
                continue;
            }

            if (line.contains("Километри: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                mileageFromTo = line.substring(0, index);
                continue;
            }

            if (line.contains("Менувач: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                transmissionType = line.substring(0, index);
                continue;
            }

            if (line.contains("Каросерија: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                vehicleBodyType = line.substring(0, index);
                continue;
            }

            if (line.contains("Боја: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                vehicleColor = line.substring(0, index);
                continue;
            }

            if (line.contains("Регистрација: ")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                licensePlate = line.substring(0, index);
                continue;
            }

            if (line.contains("€") && line.length() > 2) {
                price = line.split("</span>")[0];
                continue;
            }

            if (line.contains("telefon.png\" /><label>")) {
                line = line.split("<label>")[1].substring(0, 9);
                authorContactNumber = line;
                continue;
            }

            if (line.contains("user.png")) {
                line = line.split("adValue\">")[1];
                int index = line.indexOf("</b>");
                authorName = line.substring(0, index);
            }

            if (line.contains("/Content/images/location.png")) {
                locatedInCity = line.substring(line.indexOf("adValue\">") + 9, line.indexOf("</b>"));
            }

        }

        return new PostingDetails(postingImageUrls, title, authorName, vehicleBrand, vehicleModel, licensePlate,
                vehicleColor, transmissionType, fuelType, vehicleBodyType, mileageFromTo, vehicleManufacturedYear, authorContactNumber,
                detailedDescription, postedAtDateTime, locatedInCity, price, isAlreadySaved);
    }

    public List<PostingBasic> getNextPagePostingsR5Url(String url, int pageNumber) throws IOException {
        url = url.replaceAll("page=\\d+", "page=" + pageNumber);

        return this.getPostingsForR5Url(url);
    }

    public List<PostingBasic> searchPostingsR5(String searchPhrase) throws IOException {
        searchPhrase = searchPhrase.replaceAll("\\+", "%2B");
        searchPhrase = searchPhrase.replaceAll(" ", "+");
        String crawlUrlSection = "/Search?q=" + searchPhrase + "&city=&sell=0&sell=1&buy=0&buy=1&trade=0&trade=1&includeOld=0&includeOld=1&includeNew=0&includeNew=1&f31=&priceFrom=&priceTo=&f33_from=&f33_to=&f36_from=&f36_to=&f35=&f37=&f138=&f10016_from=&f10016_to=&f10042=&private=0&company=0&page=1&SortByPrice=0&zz=1&cat=24";

        return this.getPostingsForR5Url(crawlUrlSection);
    }

    public String generateConditionalUrlR5(PostingFilterParameters postingFilterParameters) {
        String newestCheapest = postingFilterParameters.getNewestCheapest();
        String personCompany = postingFilterParameters.getPersonCompany();
        String buySell = postingFilterParameters.getBuySell();
        String city = postingFilterParameters.getCity();
        String priceFrom = postingFilterParameters.getPriceFrom();
        String priceTo = postingFilterParameters.getPriceTo();
        String brand = postingFilterParameters.getBrand();
        String yearFrom = postingFilterParameters.getYearFrom();
        String yearTo = postingFilterParameters.getYearTo();
        String mileageFrom = postingFilterParameters.getMileageFrom();
        String mileageTo = postingFilterParameters.getMileageTo();
        String transmission = postingFilterParameters.getTransmission();
        String fuelType = postingFilterParameters.getFuelType();
        String licencePlate = postingFilterParameters.getLicencePlate();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/Search?q=");

        String cityURL = !city.equals("none") ? "&city=" + r5MapCity(city) : "&city=";

        String buySellURL = buySell.equals("buys") ? "&sell=0&buy=0&buy=1&trade=0&trade=1" : "&sell=0&sell=1&buy=0&trade=0&trade=1";

        stringBuilder.append(cityURL);
        stringBuilder.append(buySellURL);
        stringBuilder.append("includeOld=0&includeOld=1&includeNew=0&includeNew=1");

        String carBrandUrl = !brand.equals("none") ? "&f31=" + r5MapCarBrand(brand) : "&f31=";
        stringBuilder.append(carBrandUrl);

        String priceFromUrl = !priceFrom.equals("none") ? "&priceFrom=" + priceFrom : "&priceFrom=";
        String priceToUrl = !priceTo.equals("none") ? "&priceTo=" + priceTo : "&priceTo=";
        stringBuilder.append(priceFromUrl);
        stringBuilder.append(priceToUrl);

        String yearFromUrl = !yearFrom.equals("none") ? "&f33_from=" + yearFrom : "&f33_from=";
        String yearToUrl = !yearTo.equals("none") ? "&f33_to=" + yearTo : "&f33_to=";
        stringBuilder.append(yearFromUrl);
        stringBuilder.append(yearToUrl);

        String mileageFromUrl = !mileageFrom.equals("none") ? "&f36_from=" + mileageFrom : "&f36_from=";
        String mileageToUrl = !mileageTo.equals("none") ? "&f36_to=" + mileageTo : "&f36_to=";
        stringBuilder.append(mileageFromUrl);
        stringBuilder.append(mileageToUrl);

        String fuelTypeUrl = !fuelType.equals("none") ? "&f35=" + r5MapFuelType(fuelType) : "&f35=";
        stringBuilder.append(fuelTypeUrl);

        String transmissionTypeUrl = !transmission.equals("none") ? "&f37=" + r5MapTransmissionType(transmission) : "&f37=";
        stringBuilder.append(transmissionTypeUrl);

        String licencePlateUrl = !licencePlate.equals("none") ? "&f138=" + r5MapLicencePlate(licencePlate) : "&f138=";
        stringBuilder.append(licencePlateUrl);

        // kilowatts are not part from pazar3
        stringBuilder.append("&f10016_from=&f10016_to=&f10042=");

        String privateCompanyUrl = !personCompany.equals("none") ? personCompany.equals("person")
                ? "&private=1&company=0" : "&private=0&company=1"
                : "&private=0&company=0";
        stringBuilder.append(privateCompanyUrl);
        stringBuilder.append("&page=1");

        String sortPriceDateUrl = newestCheapest.equals("newest") ? "&SortByPrice=0" : "&SortByPrice=1";
        stringBuilder.append(sortPriceDateUrl);

        stringBuilder.append("zz=1&cat=24");

        return stringBuilder.toString();
    }

    public List<PostingBasic> getConditionalPostingsR5(PostingFilterParameters postingFilterParameters) throws IOException {
        return this.getPostingsForR5Url(this.generateConditionalUrlR5(postingFilterParameters));
    }

    private String r5MapColor(String inputColor) {
        switch (inputColor) {
            case "red":
                return "10006";
            case "white":
                return "10002";
            case "grey":
                return "10003";
            case "yellow":
                return "10007";
            case "green":
                return "10008";
            case "blue":
                return "10009";
            default:
                // black
                return "10005";
        }
    }

    private String r5MapBodyType(String inputBodyType) {
        switch (inputBodyType) {
            case "urban":
                return "303";
            case "hatchback":
                return "315";
            case "sedan":
                return "304";
            case "karavan":
                return "306";
            case "suv":
                return "311";
            case "cabriolet":
                return "307";
            default: // coupe
                return "308";
        }
    }

    private String r5MapLicencePlate(String inputLicencePlate) {
        return inputLicencePlate.equals("macedonian") ? "2494" : "2495";
    }

    private String r5MapTransmissionType(String inputTransmissionType) {
        if (inputTransmissionType.equals("manual"))
            return "300";
        else if (inputTransmissionType.equals("automatic"))
            return "301";
        else // steptronic
            return "302";
    }

    private String r5MapFuelType(String inputFuelType) {
        switch (inputFuelType) {
            case "petrol":
                return "257";
            case "diesel":
                return "258";
            case "lpg":
                return "259";
            case "hybrid":
                return "643";
            default: // electric
                return "10693";
        }
    }

    private String r5MapCity(String inputCity) {
        switch (inputCity) {
            case "Цела Македонија":
                return "";
            case "Скопје":
                return "1";
            case "Битола":
                return "2";
            case "Куманово":
                return "3";
            case "Прилеп":
                return "4";
            case "Тетово":
                return "5";
            case "Велес":
                return "6";
            case "Штип":
                return "7";
            case "Охрид":
                return "8";
            case "Гостивар":
                return "9";
            case "Струмица":
                return "10";
            case "Кавадарци":
                return "11";
            case "Кочани":
                return "12";
            case "Кичево":
                return "13";
            case "Струга":
                return "14";
            case "Радовиш":
                return "15";
            case "Гевгелија":
                return "16";
            case "Дебар":
                return "17";
            case "Крива Паланка":
                return "18";
            case "Свети Николе":
                return "19";
            case "Неготино":
                return "20";
            case "Делчево":
                return "21";
            case "Виница":
                return "22";
            case "Ресен":
                return "23";
            case "Пробиштип":
                return "24";
            case "Берово":
                return "25";
            case "Кратово":
                return "26";
            case "Крушево":
                return "28";
            case "Македонски Брод":
                return "29";
            case "Валандово":
                return "30";
            case "Демир Хисар":
                return "34";
        }
        return "";
    }

    private String r5MapCarBrand(String carBrand) {
        switch (carBrand) {
            case "Audi":
                return "81";
            case "Acura":
                return "77";
            case "Aixam":
                return "78";
            case "Alfa Romeo":
                return "79";
            case "Alpina":
                return "";
            case "Bently":
                return "82";
            case "BMW":
                return "84";
            case "Buick":
                return "83";
            case "Cadillac":
                return "85";
            case "Chevrolet":
                return "86";
            case "Chrysler":
                return "87";
            case "Citroen":
                return "88";
            case "Dacia":
                return "89";
            case "Daewoo":
                return "90";
            case "Daihatsu":
                return "91";
            case "Dodge":
                return "92";
            case "Ferrari":
                return "93";
            case "Fiat":
                return "94";
            case "Ford":
                return "95";
            case "Honda":
                return "96";
            case "Hummer":
                return "97";
            case "Hyundai":
                return "98";
            case "Infiniti":
                return "10083";
            case "Isuzu":
                return "99";
            case "Jaguar":
                return "100";
            case "Jeep":
                return "101";
            case "Kia":
                return "102";
            case "Lada":
                return "103";
            case "Lamborghini":
                return "104";
            case "Lancia":
                return "105";
            case "Land Rover":
                return "106";
            case "Lexus":
                return "107";
            case "Maserati":
                return "110";
            case "Mazda":
                return "112";
            case "Mercedes-Benz":
                return "113";
            case "Mini":
                return "114";
            case "Mitsubishi":
                return "116";
            case "Nissan":
                return "117";
            case "Opel":
                return "118";
            case "Peugeot":
                return "119";
            case "Pontiac":
                return "120";
            case "Porsche":
                return "121";
            case "Proton":
                return "122";
            case "Renault":
                return "123";
            case "Rover":
                return "125";
            case "Saab":
                return "126";
            case "Seat":
                return "127";
            case "Skoda":
                return "128";
            case "Smart":
                return "129";
            case "Ssangyong":
                return "130";
            case "Subaru":
                return "131";
            case "Suzuki":
                return "132";
            case "Toyota":
                return "133";
            case "VW Volkswagen":
                return "134";
            case "Volvo":
                return "135";
            case "Wartburg":
                return "136";
            case "Yugo":
                return "137";
            default: // Zastava
                return "138";
        }
    }
}
