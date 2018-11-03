package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.CarClass;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/*")
public class SkyScannerRedirectController {

    public static final String SCYCSANNER_HOST = "http://partners.api.skyscanner.net";
    public static final String GET_CARS = "/apiservices/carhire/liveprices/v2/UK/GBP/en-GB/EDI/EDI/2019-07-01T10:00/2019-07-07T17:00/35";
    public static final String APIKEY = "apikey=ah016404381349804974592181457198";
    public static final String USERIP = "userip=10.90.23.72";

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/getCars")
    public CarClass getCars(
            @RequestParam(value = "startlon", defaultValue = "55") String startlon,
            @RequestParam(value = "startlat", defaultValue = "38") String startlat,
            @RequestParam(value = "finlon", defaultValue = "56") String finlon,
            @RequestParam(value = "finlat", defaultValue = "40") String finlat) throws IOException, URISyntaxException {

        return requestToSkyScanner(startlon, startlat, finlon, finlat);
        /*return new CarClass(counter.incrementAndGet(),
                String.format(template, name));*/
    }

    private CarClass requestToSkyScanner(String... params) throws URISyntaxException, IOException {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(params[0]);
        String uri = SCYCSANNER_HOST + GET_CARS + "?" + APIKEY +
                "$" + USERIP;
        request.setURI(new URI(uri));
        request.addHeader("content-type",
                "application/x-www-form-urlencoded");
        request.addHeader("accept", "application/json");
        HttpResponse response;
        String result = null;
        InputStream instream = null;

        try {
            response = client.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // A Simple JSON Response Read
                instream = entity.getContent();
                //String json = IOUtils.toString(response.getEntity().getContent());
                System.out.println("RESPONSE: " + result);
                Reader reader = new InputStreamReader(instream);
                Gson gson = new GsonBuilder().create();
                CarClass cars = gson.fromJson(reader, CarClass.class);
                System.out.println(cars);
                return cars;
                /*if (response.getStatusLine().getStatusCode() == 200) {
                    netState.setLogginDone(true);
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (instream != null) {
                instream.close();
            }
        }
        return null;
    }

}
