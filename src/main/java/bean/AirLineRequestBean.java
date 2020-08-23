package bean;
import java.util.Date;
/**
 * by default: single trip, and one adult
 */
public class AirLineRequestBean {
    String departCity;  // 城市三字码
    String destinationCity;
    Date departTime;

    public void setDepartCity(String a) {
        this.departCity = a;
    }

    public void setDestinationCity(String b) {
        this.destinationCity = b;
    }

    public void setDepartTime(Date c) {
        this.departTime = c;
    }

    public String getDepartCity() {
        return departCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public Date getDepartTime() {
        return departTime;
    }
}




