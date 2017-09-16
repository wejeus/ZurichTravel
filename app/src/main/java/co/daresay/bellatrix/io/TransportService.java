package co.daresay.bellatrix.io;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface TransportService {

    @GET("v1/locations")
    Observable<LocationsResponse> findLocations(@Query("query") String query);

    @GET("v1/connections")
    Observable<ConnectionsResponse> findConnections(@Query("from") String from, @Query("to") String to);
}
