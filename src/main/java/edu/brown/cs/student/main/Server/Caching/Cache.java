package edu.brown.cs.student.main.Server.Caching;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.Server.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;

public class Cache {

    //private final LoadingCache<edu.brown.cs.student.main.csv.Records.LocationData, edu.brown.cs.student.main.csv.Records.ACSData> cache;

    CacheConfigurator config;

    public Cache(CacheConfigurator config, BroadbandHandler broadbandHandler) {

        this.config = config;
    }

//        this.cache = this.createCache();
//    }
//
////    public LoadingCache<edu.brown.cs.student.main.csv.Records.LocationData, edu.brown.cs.student.main.csv.Records.ACSData> createCache(){
////        return CacheBuilder.newBuilder()
////                .maximumSize(this.config.getMaximumSize())
////                .expireAfterWrite(this.config.getExpireAfterWriteMinutes(), TimeUnit.MINUTES)
////                .recordStats()
////                .build(
////                        new CacheLoader<>() {
////                            @Override
////                            public edu.brown.cs.student.main.csv.Records.ACSData load(edu.brown.cs.student.main.csv.Records.LocationData locationData) throws DatasourceException {
////                                return
////                            }
//                        });
//    }

//    public edu.brown.cs.student.main.csv.Records.ACSData getDataFromProxy(edu.brown.cs.student.main.csv.Records.LocationData locationData) throws DatasourceException {
//        URL percentURL = "https://api.weather.gov/points/" + locationData.state() + "," + locationData.county();
//       List<List<String>> coordinates = this.broadbandDataSource.getDataFromURL(percentURL);
//        String forecastURL = gridResponse.properties().forecastURL();
//        ForecastResponse forecastResponse = this.proxy.callAPI(forecastURL, ForecastResponse.class);
//        return createData(forecastResponse);
//    }
//
//    public edu.brown.cs.student.main.csv.Records.ACSData createData(ForecastResponse response) {
//        ForecastProperties forecastProperties = response.properties();
//        ForecastTemp currTemp = forecastProperties.periods().get(0);
//        String time = forecastProperties.time();
//        return new Forecast(currTemp.temp(), currTemp.unit(), time);
//    }



    public void clearCache() {
        this.cache.invalidateAll();
    }
}
