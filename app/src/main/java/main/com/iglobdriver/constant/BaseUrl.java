package main.com.iglobdriver.constant;

/**
 * Created by Amit on 5/6/17.
 */

public class BaseUrl {
    public static String baseurl = "https://iglobapp.com/admin/webservice/";
    public static String image_baseurl = "https://iglobapp.com/admin/uploads/images/";
    public static String stripe_publish = "pk_test_3oQpHM18Yv2mFAK6vSE5I1oz";
    public static String privacy = "https://iglobapp.com/admin/privacy.php";
    public static String termsconditions = "https://iglobapp.com/admin/terms.php";

    public static BaseUrl get() {
        return new BaseUrl();
    }

    public String AddVehicle() {
        return baseurl.concat("add_vehicle");
    }

    public String getCar() {
        return baseurl.concat("car_list");
    }

    public String getVehicle() {
        return baseurl.concat("vehicle_list");
    }

    public String addExpanse() {
        return baseurl.concat("add_expance");
    }

    public String getExpanse() {
        return baseurl.concat("get_expance");
    }

    public String BookNow() {
        return baseurl.concat("booking_request_driver");
    }

    public String getVerify() {
        return baseurl.concat("get_number_profile");
    }

    public String sendWalletAmount() {
        return baseurl.concat("send_wallet_amount");
    }

    public String getStatement() {
        return baseurl.concat("get_driver_statement_old");
    }

    public String filterEarning() {
        return baseurl.concat("filter_earning");
    }

    public String DeleteCar() {
        return baseurl.concat("delete_vehical");
    }

    public String getCity() {
        return baseurl.concat("get_city");
    }

    public String getState() {
        return baseurl.concat("get_state");
    }

    public String getCountry() {
        return baseurl.concat("countrys");
    }
}