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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

@Service
public class CrawlPazar3Service {

    private final String pazar3Url = "https://www.pazar3.mk";
    private final UsersJpaRepository usersJpaRepository;

    public CrawlPazar3Service(UsersJpaRepository usersJpaRepository) {
        this.usersJpaRepository = usersJpaRepository;
    }

    public List<PostingBasic> getPostingsForP3Url(String url, boolean isConditional) throws IOException {
        ArrayList<PostingBasic> postings = new ArrayList<>();

        String crawlUrl = url.startsWith("http") ? url : this.pazar3Url + url;
        String[] segments = crawlUrl.split("mk/");
        if(!isConditional)
            segments[1] = URLEncoder.encode(segments[1], UTF_8.toString());

        URL pazar3Url = new URL(segments[0]+ "mk/" + segments[1]);
        URLConnection pazar3UrlConnection = pazar3Url.openConnection();
        pazar3UrlConnection.addRequestProperty("User-Agent", "Chrome");
        BufferedReader in = new BufferedReader(new InputStreamReader(pazar3UrlConnection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.contains("<div class=\"row row-listing \"")) {
                StringBuilder container = new StringBuilder();
                line = in.readLine();

                // aggregate content
                while (line != null && !line.contains("<div class=\"row row-listing \"")) {
                    container.append(line);
                    line = in.readLine();
                }

                // details url
                int index = container.indexOf("<a class=\"span2-ad-img-list align-center\"");
                if (index == -1)
                    index = container.indexOf("<a class=\"span2-ad-img-list\"");
                String tmp = container.substring(index);
                index = tmp.indexOf(">");
                line = tmp.substring(0, index);
                tmp = line.split("href=\"")[1];
                index = tmp.indexOf("\"");
                String detailsUrl = tmp.substring(0, index);

                // title
                tmp = line.split("title=\"")[1];
                index = tmp.indexOf("\"");
                String postingTitle = tmp.substring(0, index);

                // image
                String posterImageUrl = null;
                index = container.indexOf("<img class=\"img-polaroid span2 lazyload\"");
                if (index != -1) {
                    line = container.substring(index);
                    index = line.indexOf("/>");
                    line = line.substring(0, index);
                    line = line.split("data-src=\"")[1];
                    index = line.indexOf("\"");
                    posterImageUrl = line.substring(0, index);
                }

                index = container.indexOf("<span class=\"pull-right text-right\">");
                line = container.substring(index);
                index = line.indexOf(">");
                int index1 = line.indexOf(":");
                String dateTime = line.substring(index + 1, index1 + 3);
                dateTime = unescapeHtml4(dateTime);

                // location
                index = container.indexOf("<a itemprop=\"url\" class=\"link-html5 nobold\"");
                line = container.substring(index + "<a itemprop=\"url\" class=\"link-html5 nobold\"".length());
                index1 = line.indexOf("&");
                int index2 = line.indexOf("</a>");
                StringBuilder locations = new StringBuilder();

                while (index != -1) {
                    line = line.substring(index2);
                    index = line.indexOf("<a itemprop=\"url\" class=\"link-html5 nobold\"");

                    if (index == -1) {
                        break;
                    }

                    line = line.substring(index + "<a itemprop=\"url\" class=\"link-html5 nobold\"".length());
                    index1 = line.indexOf("&");
                    index2 = line.indexOf("</a>");
                    locations.append(line, index1, index2);
                    locations.append(", ");
                }
                StringBuilder locationSB = new StringBuilder(locations.toString());
                if (locationSB.toString().contains(">")) {
                    line = locationSB.substring(locationSB.indexOf(">") + 1);
                    locationSB = new StringBuilder(line.substring(0, line.indexOf(", ")));
                    while (line.contains(">")) {
                        line = line.substring(line.indexOf(">") + 1);
                        locationSB.append(", ").append(line.substring(0, line.indexOf(", ")));
                    }
                }
                String location = unescapeHtml4(locationSB.toString());

                // price
                String price = "По договор";
                index = container.indexOf("list-price");
                line = container.substring(index);
                index1 = line.indexOf("</p>");
                line = line.substring(0, index1);
                if (line.contains("&") && !line.contains("По Договор")) {
                    price = line.split(">")[1].split("&")[0];
                }

                // vehicle year
                index = container.indexOf("&#1043;&#1086;&#1076;&#1080;&#1085;&#1072;:");
                int year = -1;
                if (index != -1) {
                    line = container.substring(index);
                    index = line.indexOf("</b>");
                    line = line.substring(0, index);
                    line = line.split("<b>")[1];
                    if (line.contains("&#1090;&#1080;&#1090;&#1077;"))
                        year = 1980;
                    else
                        year = Integer.parseInt(line);
                }

                // mileage
                index = container.indexOf("&#1050;&#1080;&#1083;&#1086;&#1084;&#1077;&#1090;&#1088;&#1072;&#1078;&#1072;:");
                String mileage = "Не е дефинирано.";
                if (index != -1) {
                    line = container.substring(index);
                    index1 = line.indexOf("</b>");
                    line = line.substring(0, index1);
                    mileage = line.split("<b>")[1];
                }


                postings.add(new PostingBasic(dateTime, posterImageUrl, location.toString(), year, price, mileage, postingTitle, detailsUrl));
            }
        }

        return postings;
    }

    public PostingDetails getDetailedPostingForP3Url(String url, User user) throws IOException {
        String crawlUrl = url.startsWith("http") ? url : this.pazar3Url + url;
        crawlUrl = URLDecoder.decode(crawlUrl);
        String[] segments = crawlUrl.split("mk/");
        segments[1] = URLEncoder.encode(segments[1], UTF_8.toString());

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
        String mileageFromTo = null;
        Integer vehicleManufacturedYear = null;
        String authorContactNumber = null;
        String authorName = null;
        String detailedDescription = null;
        String postedAtDateTime = null; // dd mmm. hh:mm
        String locatedInCity = null;
        String price = "По договор.";
        String title = null;

        URL pazar3Url = new URL(segments[0]+ "mk/" + segments[1]);
        URLConnection pazar3UrlConnection = pazar3Url.openConnection();
        pazar3UrlConnection.addRequestProperty("User-Agent", "Chrome");
        BufferedReader in = new BufferedReader(new InputStreamReader(pazar3UrlConnection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.contains("<title>")) {
                String[] components = line.split(" //| ");
                StringBuilder titleSB = new StringBuilder(components[0].substring(7));
                titleSB.append(" ");
                int i = 1;
                while (!components[i].equals("|")) {
                    titleSB.append(components[i]);
                    titleSB.append("    ");
                    ++i;
                }
                title = titleSB.toString();
                continue;
            }

            if (line.contains("<strong>")) {
                authorName = line.split("</strong>")[0].substring(8);
                continue;
            }

            if (line.contains("<mark>")) {
                postedAtDateTime = line.split("</mark>")[0].substring(6);
                continue;
            }

            if (line.contains("<div class=\"carousel-item custom-photo-gallery text-center")) {
                line = in.readLine();
                postingImageUrls.add(line.split("href=\"")[1].split("\"")[0]);

                in.readLine();
                in.readLine();
                in.readLine();

                continue;
            }

            if (authorContactNumber == null && line.contains("<a href=\"tel:")) {
                authorContactNumber = line.split("tel:")[1].split("\"")[0];

                if (authorContactNumber.startsWith("//"))
                    authorContactNumber = authorContactNumber.substring(2);

                if (authorContactNumber.endsWith(" \\"))
                    authorContactNumber = authorContactNumber.substring(0, authorContactNumber.length() - 2);

                continue;
            }

            if (line.contains("ЕУР")) {
                price = line.replace("ЕУР", "€");
                continue;
            }

            if (line.contains("Година")) {
                in.readLine();
                in.readLine();
                in.readLine();
                in.readLine();
                String tmpYear = in.readLine().replaceAll(" ", "");
                vehicleManufacturedYear = Integer.parseInt(tmpYear);
                continue;
            }

            if (line.contains("Километража")) {
                in.readLine();
                in.readLine();
                in.readLine();
                in.readLine();
                mileageFromTo = in.readLine();
                continue;
            }

            if (line.contains("Регистрација")) {
                in.readLine();
                in.readLine();
                in.readLine();
                in.readLine();
                licensePlate = in.readLine();
                continue;
            }

            if (line.contains("Локација")) {
                in.readLine();
                in.readLine();
                in.readLine();

                locatedInCity = in.readLine().split("</div>")[0];
                if(locatedInCity.length() >= 5)
                    locatedInCity = locatedInCity.substring(5);
                else
                    locatedInCity = null;
                continue;
            }

            if (line.contains("Марка")) {
                in.readLine();
                in.readLine();
                in.readLine();
                vehicleBrand = in.readLine().split("</div>")[0];
                if(vehicleBrand.length() >= 5)
                    vehicleBrand = vehicleBrand.substring(5);
                else
                    vehicleBrand = null;
                continue;
            }

            if (line.contains("Менувач")) {
                in.readLine();
                in.readLine();
                in.readLine();
                in.readLine();
                transmissionType = in.readLine();
                continue;
            }

            if (line.contains("Гориво")) {
                in.readLine();
                in.readLine();
                in.readLine();
                in.readLine();
                fuelType = in.readLine();
                continue;
            }

            if (line.contains("Боја")) {
                in.readLine();
                in.readLine();
                in.readLine();
                vehicleColor = in.readLine().split("</div>")[0];
                if(vehicleColor.length() >= 5)
                    vehicleColor = vehicleColor.substring(5);
                else
                    vehicleColor = null;
                continue;
            }

            if (line.contains("Модел")) {
                in.readLine();
                in.readLine();
                in.readLine();
                vehicleModel = in.readLine().split("</div>")[0];
                if(vehicleModel.length() >= 5)
                    vehicleModel = vehicleModel.substring(5);
                else
                    vehicleModel = null;
                continue;
            }

            if (line.contains("Стил на каросерија")) {
                in.readLine();
                in.readLine();
                vehicleBodyType = in.readLine().split("</div>")[0];
                if(vehicleBodyType.length() >= 5)
                    vehicleBodyType = vehicleBodyType.substring(5);
                else
                    vehicleBodyType = null;
                continue;
            }

            if (line.contains("<div class=\"row\">")) {
                line = in.readLine();
                if (line.contains("<div class=\"col-12\">")) {
                    line = in.readLine();
                    if (line.contains("<div>")) {
                        detailedDescription = in.readLine().replaceAll("<br />", "\n");
                    }
                }
            }

        }

        return new PostingDetails(postingImageUrls, title, authorName, vehicleBrand, vehicleModel, licensePlate,
                vehicleColor, transmissionType, fuelType, vehicleBodyType, mileageFromTo, vehicleManufacturedYear, authorContactNumber,
                detailedDescription, postedAtDateTime, locatedInCity, price, isAlreadySaved);
    }

    public List<PostingBasic> getNextPagePostingsP3Url(String url, int pageNumber) throws IOException {
        String crawlUrlSection;

        if (url.contains("?Sort=")) {
            String[] components;
            String extra;
            if (url.contains("DateDesc")) {
                components = url.split("Sort=DateDesc");
                extra = "Sort=DateDesc";
            } else {
                components = url.split("Sort=PriceAsc");
                extra = "Sort=PriceAsc";
            }
            crawlUrlSection = components[0] + extra + "&Page=" + pageNumber + components[1];
        }
        String[] components = url.split("\\?");
        if (components.length > 1)
            crawlUrlSection = components[0] + "?Page=" + pageNumber + "&" + components[1];
        else
            crawlUrlSection = components[0] + "?Page=" + pageNumber + "&";


        return this.getPostingsForP3Url(crawlUrlSection, true);
    }

    public List<PostingBasic> searchPostingsP3(String searchPhrase) throws IOException {
        searchPhrase = searchPhrase.replaceAll(" ", "-");
        String crawlUrlFraction = "/oglasi/vozila/avtomobili/se-prodava/q-" + searchPhrase;

        return this.getPostingsForP3Url(crawlUrlFraction, true);
    }

    public String generateConditionalUrlP3(PostingFilterParameters postingFilterParameters) {
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

        String urlParametersBeforeProp = this.generateUrlParametersOutOfPropPazar3(newestCheapest, personCompany, buySell,
                city, priceFrom, priceTo, brand);

        String urlPropParameters = this.generateUrlParametersForPropPazar3(yearFrom, yearTo, mileageFrom, mileageTo,
                transmission, fuelType, licencePlate);

        return urlParametersBeforeProp + urlPropParameters;
    }

    public List<PostingBasic> getConditionalPostingsP3(PostingFilterParameters postingFilterParameters) throws IOException {
        return this.getPostingsForP3Url(this.generateConditionalUrlP3(postingFilterParameters), true);
    }

    private String generateUrlParametersOutOfPropPazar3(String newestCheapest, String personCompany, String buySell,
                                                        String city, String priceFrom, String priceTo, String brand) {
        /*  Out of Prop parameters: (they come in the same order as mentioned, / separated)
            Cannot navigate directly to URL with chosen color and body type, the site doesn't allow it.
            Must navigate manually via the site's options.
            BODY_TYPE, COLOR, NEWEST_CHEAPEST (?Sort=PriceDesc OR DateDesc), PERSON_COMPANY (&Private=True OR False for company)
            example: https://www.pazar3.mk/oglasi/vozila/avtomobili/se-prodava/mal-avtomobil/siva?Sort=DateDesc&Private=True
           */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/oglasi/vozila/avtomobili");

        if (!brand.equals("none")) {
            stringBuilder.append("/");
            stringBuilder.append(p3MapCarBrand(brand));
        }

        String cityURL = !city.equals("none") ? "/" + p3MapCity(city) : null;

        String buySellURL = buySell.equals("buys") ? "/se-kupuva" : "/se-prodava";

        String newestCheapestURL = newestCheapest.equals("cheapest") ? "?Sort=PriceAsc" : "?Sort=DateDesc";

        String priceFromURL = !priceFrom.equals("none") ? "&PriceFrom=" + priceFrom : null;

        String personCompanyURL = "";
        if (!personCompany.equals("none"))
            personCompanyURL = personCompany.equals("person") ? "&Private=True" : "&Private=False";

        String priceToURL = !priceTo.equals("none") ? "&PriceTo=" + priceTo : null;

        stringBuilder.append(buySellURL);

        if (cityURL != null)
            stringBuilder.append(cityURL);

        stringBuilder.append(newestCheapestURL);

        if (!buySell.equals("buys") && priceFromURL != null)
            stringBuilder.append(priceFromURL);

        stringBuilder.append(personCompanyURL);

        if (!buySell.equals("buys") && priceToURL != null)
            stringBuilder.append(priceToURL);

        return stringBuilder.toString();
    }

    private String generateUrlParametersForPropPazar3(String yearFrom, String yearTo, String mileageFrom,
                                                      String mileageTo, String transmission,
                                                      String fuelType, String licencePlate) {
        // Prop is always last query parameter
        ArrayList<String> urlParameters = new ArrayList<>();
        for (int i = 0; i < 7; ++i)
            urlParameters.add("");

        if (!yearFrom.equals("none"))
            urlParameters.set(0, p3MapYearFrom(yearFrom));
        else
            urlParameters.set(0, "");

        if (!yearTo.equals("none"))
            urlParameters.set(1, p3MapYearTo(yearTo));
        else
            urlParameters.set(1, "");

        if (!mileageFrom.equals("none"))
            urlParameters.set(2, p3MapMileageFrom(mileageFrom));
        else
            urlParameters.set(2, "");

        if (!mileageTo.equals("none"))
            urlParameters.set(3, p3MapMileageTo(mileageTo));
        else
            urlParameters.set(3, "");

        if (!transmission.equals("none"))
            urlParameters.set(4, p3MapTransmissionType(transmission));
        else
            urlParameters.set(4, "");

        if (!fuelType.equals("none"))
            urlParameters.set(5, p3MapFuelType(fuelType));
        else
            urlParameters.set(5, "");

        if (!licencePlate.equals("none"))
            urlParameters.set(6, p3MapLicencePlate(licencePlate));
        else
            urlParameters.set(6, "");

        return "&Prop=" + String.join(",", urlParameters);
    }

    private String p3MapLicencePlate(String inputLicencePlate) {
        // no commas after this one, 6 in front of it
        return inputLicencePlate.equals("macedonian") ? "73-574-Регистрација-Македонска" : "73-575-Регистрација-Странска";
    }

    private String p3MapTransmissionType(String inputTransmissionType) {
        switch (inputTransmissionType) {
            case "manual":
                return "37-566-Менувач-Рачен";
            case "automatic":
                return "37-567-Менувач-Автоматски";
            case "steptronic":
                return "37-682-Менувач-Steptronic---Tiptronic";
            default:
                return "";
        }
    }

    private String p3MapYearSpecialDigits(String yearString) {
        int year = Integer.parseInt(yearString);

        if (year >= 2000)
            return Integer.toString(year - 2000 + 29);
        else
            return Integer.toString(year - 1980 + 9);
    }

    private String p3MapFuelType(String inputFuelType) {
        switch (inputFuelType) {
            case "petrol":
                return "39-570-Гориво-Бензин";
            case "diesel":
                return "39-571-Гориво-Нафта";
            case "lpg":
                return "39-572-Гориво-Плин---Бензин";
            case "hybrid":
                return "39-681-Гориво-Хибрид";
            case "electric":
                return "39-573-Гориво-Друго";
            default:
                return "";
        }
    }

    private String p3MapYearFrom(String yearFrom) {
//         9 to 49
        return "36-" + p3MapYearSpecialDigits(yearFrom) + "--from-Година-" + yearFrom;
    }

    private String p3MapYearTo(String yearTo) {
        return "36-" + p3MapYearSpecialDigits(yearTo) + "--to-Година-" + yearTo;
    }

    private String p3MapMileageSpecialDigits(String mileage, boolean isFrom) {
        // 5000 77
        // 10k 80
        // from 100k +3 to 134
        // from 190k +3 to 161
        // 200k 165
        // +50k +3 to 500k 183
        // the same -1 for isFrom==false
        int code = -1;

        switch (mileage) {
            case "5000":
                code = 77;
                break;
            case "10000":
                code = 80;
                break;
            case "15000":
                code = 83;
                break;
            case "20000":
                code = 86;
                break;
            case "25000":
                code = 89;
                break;
            case "30000":
                code = 92;
                break;
            case "35000":
                code = 95;
                break;
            case "40000":
                code = 98;
                break;
            case "45000":
                code = 101;
                break;
            case "50000":
                code = 104;
                break;
            case "55000":
                code = 107;
                break;
            case "60000":
                code = 110;
                break;
            case "65000":
                code = 113;
                break;
            case "70000":
                code = 116;
                break;
            case "75000":
                code = 119;
                break;
            case "80000":
                code = 122;
                break;
            case "85000":
                code = 125;
                break;
            case "90000":
                code = 128;
                break;
            case "95000":
                code = 131;
                break;
            case "100000":
                code = 134;
                break;
            case "110000":
                code = 137;
                break;
            case "120000":
                code = 140;
                break;
            case "130000":
                code = 143;
                break;
            case "140000":
                code = 146;
                break;
            case "150000":
                code = 149;
                break;
            case "160000":
                code = 152;
                break;
            case "170000":
                code = 155;
                break;
            case "180000":
                code = 158;
                break;
            case "190000":
                code = 161;
                break;
            case "200000":
                code = 165;
                break;
            case "250000":
                code = 168;
                break;
            case "300000":
                code = 171;
                break;
            case "350000":
                code = 174;
                break;
            case "400000":
                code = 177;
                break;
            case "450000":
                code = 180;
                break;
            case "500000":
                code = 183;
                break;
        }
        if (isFrom)
            return Integer.toString(code);
        else
            return Integer.toString(code - 1);
    }

    private String p3MapMileageFrom(String mileageFrom) {
        return "38-" + p3MapMileageSpecialDigits(mileageFrom, true) + "--from-Километража-" + p3TransformMileage(Integer.parseInt(mileageFrom), false);
    }

    private String p3MapMileageTo(String mileageTo) {
        return "38-" + p3MapMileageSpecialDigits(mileageTo, false) + "--to-Километража-" + p3TransformMileage(Integer.parseInt(mileageTo), true);
    }

    private String p3TransformMileage(Integer mileage, boolean isUpperLimit) {
        if (isUpperLimit)
            mileage -= 1;

        if (mileage == 5000)
            return "5-000";

        if (mileage == 4999)
            return "4-999";

        if (mileage == 9999)
            return "9-999";

        String tmp = Integer.toString(mileage);
        if (mileage >= 10000 && mileage <= 99999) {
            return tmp.substring(0, 2) + "-" + tmp.substring(2);
        } else
            return tmp.substring(0, 3) + "-" + tmp.substring(3);
    }

    private String p3MapCity(String inputCity) {
        switch (inputCity) {
            case "Цела Македонија":
                return "";
            case "Скопје":
                return "skopje";
            case "Битола":
                return "bitola";
            case "Куманово":
                return "kumanovo";
            case "Прилеп":
                return "prilep";
            case "Тетово":
                return "tetovo";
            case "Велес":
                return "veles";
            case "Штип":
                return "stip";
            case "Охрид":
                return "ohrid";
            case "Гостивар":
                return "gostivar";
            case "Струмица":
                return "sttrumica";
            case "Кавадарци":
                return "kavadarci";
            case "Кочани":
                return "kocani";
            case "Кичево":
                return "kicevo";
            case "Струга":
                return "struga";
            case "Радовиш":
                return "radovis";
            case "Гевгелија":
                return "gevgelija";
            case "Дебар":
                return "debar";
            case "Крива Паланка":
                return "kriva-palanka";
            case "Свети Николе":
                return "sveti-nikole";
            case "Неготино":
                return "negotino";
            case "Делчево":
                return "delcevo";
            case "Виница":
                return "vinica";
            case "Ресен":
                return "resen";
            case "Пробиштип":
                return "probistip";
            case "Берово":
                return "berovo";
            case "Кратово":
                return "kratovo";
            case "Крушево":
                return "krusevo";
            case "Македонски Брод":
                return "makedonski-brod";
            case "Валандово":
                return "valandovo";
            case "Демир Хисар":
                return "demir-hisar";
        }
        return "";
    }

    private String p3MapCarBrand(String carBrand) {
        if (carBrand.equals("Citroen"))
            carBrand = carBrand.replace("e", "ë");
        else if (carBrand.equals("Skoda"))
            carBrand = carBrand.replace("S", "Š");

        return carBrand.toLowerCase().replaceAll(" ", "-");
    }
}
