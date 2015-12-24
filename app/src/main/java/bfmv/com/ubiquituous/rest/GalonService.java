package bfmv.com.ubiquituous.rest;

import bfmv.com.ubiquituous.model.Galon;
import bfmv.com.ubiquituous.model.ResponseStatus;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by MartinOenang on 12/14/2015.
 */
public interface GalonService {
    @GET("galon/status/get/{id}")
    public Call<Galon> getGalonStatus(@Path("id") String id);

    @POST("galon/update")
    public Call<ResponseStatus> updateGalon(@Body Galon galon);

    @GET("galon/get/{id}")
    public Call<Galon> getGalonInit(@Path("id") String id);
}
