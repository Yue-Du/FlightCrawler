package bean;

import java.util.Date;
import java.util.List;

public class AirLineInfomation {

    List<TripInfomation> tripList;
    String finalPrice;

    public class TripInfomation {
        String departCity;   // 城市三字码
        String destinationCity;
        Date departTime;
        Date arriveTime;
        double price;   //人民币
        String flightNumber;
        char cabin; // 经济舱 一个字母

        double originlPrice;   //
        String originalCurrency; // CNY

        public TripInfomation(String flightNumber, String departCity, String destinationCity) {
            this.flightNumber = flightNumber;
            this.departCity = departCity;
            this.destinationCity = destinationCity;
        }

        public void setDepartTime(Date departTime) {
            this.departTime = departTime;
        }

        public void setArriveTime(Date arriveTime) {
            this.arriveTime = arriveTime;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setCabin(char cabin) {
            this.cabin = cabin;
        }

        public void setOriginlPrice(double originlPrice) {
            this.originlPrice = originlPrice;
        }

        public void setOriginalCurrency(String originalCurrency) {
            this.originalCurrency = originalCurrency;
        }
    }

    public void addList(TripInfomation tripInfomation) {
        tripList.add(tripInfomation);
    }

    public List<TripInfomation> getTripList(List<TripInfomation> tripList) {
        return tripList;
    }
}
