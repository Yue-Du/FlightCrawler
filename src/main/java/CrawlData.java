import bean.AirLineRequestBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

class CrawlData implements IAirlineCrawler {
    String url = "https://new.hnair.com/hainanair/ibe/air/processNearByFlightSearch.do";

    public void crawler(AirLineRequestBean reques) {
        try {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                // 创建一个提交数据的容
                List<BasicNameValuePair> parames = new ArrayList<BasicNameValuePair>();
                SimpleDateFormat formatting = new SimpleDateFormat( "yyyy-MM-dd");
                String date = formatting.format(reques.getDepartTime());
                parames.add(new BasicNameValuePair("Search/DateInformation/departDate", date));
                parames.add(new BasicNameValuePair("Search/DateInformation/returnDate", "2020-08-10"));
                parames.add(new BasicNameValuePair("Search/calendarSearch", "false"));
                parames.add(new BasicNameValuePair("Search/calendarSearched", "false"));
                parames.add(new BasicNameValuePair("Search/flightType", "oneway"));
                parames.add(new BasicNameValuePair("Search/Passengers/adults", "1"));
                parames.add(new BasicNameValuePair("Search/Passengers/children", "0"));
                parames.add(new BasicNameValuePair("Search/Passengers/infants", "0"));
                parames.add(new BasicNameValuePair("Search/Passengers/PoliceDisabled", "0"));
                parames.add(new BasicNameValuePair("Search/Passengers/MilitaryDisabled", "0"));
                parames.add(new BasicNameValuePair("Search/searchType", "F"));
                parames.add(new BasicNameValuePair("searchTypeValidator", "F"));
                parames.add(new BasicNameValuePair("Search/OriginDestinationInformation/Origin/location", reques.getDepartCity()));
                parames.add(new BasicNameValuePair("Search/OriginDestinationInformation/Destination/location", reques.getDestinationCity()));
                parames.add(new BasicNameValuePair("Search/calendarCacheSearchDays", "60"));
                parames.add(new BasicNameValuePair("Search/seatClass", "A"));
                parames.add(new BasicNameValuePair("Search/searchNearByAllFlights", "true"));

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(parames, "UTF-8"));

                httpPost.addHeader("Accept", "*/*");
                httpPost.addHeader("Accept-Encoding", "gzip, deflate, br");
                httpPost.addHeader("Connection", "keep-alive");
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                httpPost.addHeader("Cookie", "JSESSIONID=AF8A03D2582876B58BEF5089A31F7C2B.HUIBEServer23; Path=/hainanair/ibe");
                httpPost.addHeader("Sec-Fetch-Dest", "empty");
                httpPost.addHeader("host", "new.hnair.com");
                httpPost.addHeader("Sec-Fetch-Mode", "cors");
                httpPost.addHeader("Sec-Fetch-Site", "same-origin");
                httpPost.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.129 Safari/537.36");

                client = HttpClients.createDefault();
                response = client.execute(httpPost);
                Header[] cookies = response.getHeaders("Set-Cookie");
                boolean isCookieSet = false;
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getValue().contains("JSESSIONID")) {
                        httpPost.setHeader("Cookie", cookies[i].getValue());
                        isCookieSet = true;
                        break;
                    }
                }
                if (!isCookieSet) {
                    throw new Exception("can not get cookie");
                }
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jsonResult = JSON.parseObject(result);
                getFlightInfo(jsonResult);

            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFlightInfo(JSONObject result){
//        AirLineInfomation airlineInfo = new AirLineInfomation();
//        airlineInfo.getTripList()；
        JSONObject flightSearchResults = result.getJSONObject("FlightSearchResults");
        JSONArray flights = flightSearchResults.getJSONArray("Flights");
        JSONObject flightsIn = flights.getJSONObject(0);
        JSONArray flight = flightsIn.getJSONArray("Flight");
        for(int i = 0;i<flight.size();i++) {
            JSONObject flightting = flight.getJSONObject(i);

            // get departure and arrival information
            JSONArray flightDetails = flightting.getJSONArray("FlightDetails");
            JSONObject flightDetail = flightDetails.getJSONObject(0);
            JSONArray flightleg = flightDetail.getJSONArray("FlightLeg");
            JSONObject prices = flightting.getJSONObject("Price");
            JSONArray farebreakdowns = prices.getJSONArray("FareBreakdowns");
            JSONObject economic = farebreakdowns.getJSONObject(0);
            JSONArray farebreakdown = economic.getJSONArray("FareBreakdown");
            JSONObject finalPriceInfo = farebreakdown.getJSONObject(0);
            JSONObject passengeFare = finalPriceInfo.getJSONObject("PassengerFare");
            JSONObject baseFare = passengeFare.getJSONObject("BaseFare");
            String currency = baseFare.getString("Currency");
            String finalPrice = baseFare.getString("Amount");

            JSONArray fareInfos = prices.getJSONArray("FareInfos");
            JSONObject economicCabin = fareInfos.getJSONObject(0);
            JSONArray fareinfo = economicCabin.getJSONArray("FareInfo");


            for (int j =0; j < flightleg.size(); j ++){
                JSONObject summary = flightleg.getJSONObject(j);
                String flightNumber = summary.getString("FlightNumber");
                JSONObject departure = summary.getJSONObject("Departure");
                JSONObject arrival = summary.getJSONObject("Arrival");
                String departCity = departure.getString("TS_CityCode");
                String departTime = departure.getString("DateTime");
                String arrivalCity = arrival.getString("TS_CityCode");
                String arrivalTime = arrival.getString("DateTime");

                JSONObject flightSegment = fareinfo.getJSONObject(j * 2);
                JSONObject fareReference = flightSegment.getJSONObject("FareReference");
                String cabin = fareReference.getString("ResBookDesigCode");
                JSONArray curFareInfo = flightSegment.getJSONArray("FareInfo");
                JSONObject curFareInfos = curFareInfo.getJSONObject(0);
                JSONObject fare = curFareInfos.getJSONObject("Fare");
                String baseAmount = fare.getString("BaseAmount");
            }

            // create AirLineInfomation object
//            AirLineInfomation.TripInfomation tripInfomation = airlineInfo.new TripInfomation(flightNumber,departAirport,departTime,arrivalAirport,arrivalTime,duration,cabin,currency,amount);
//            tripInfomation.addList(tripInfomation);
        }
//        airlineInfo.getTripList(tripList);

    }

        public static void main(String args[]) throws ParseException {
        AirLineRequestBean requestInfo = new AirLineRequestBean();
        CrawlData crawl = new CrawlData();
        requestInfo.setDepartCity("CITY_BJS_CN");
        requestInfo.setDestinationCity("CITY_XMN_CN");

        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd");
        Date departDate =  formatter.parse("2020-08-25");
        requestInfo.setDepartTime(departDate);
        crawl.crawler(requestInfo);
    }
}
