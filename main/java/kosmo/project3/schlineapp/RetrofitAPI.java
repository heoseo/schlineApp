package kosmo.project3.schlineapp;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface RetrofitAPI {


    static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StaticInfo.my_ip+"/schline/android/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    @GET("atupdate.do?")
    Call<String> dbupdate (@Query("idx") String vid_idx,@Query("user_id") String user_id,
                           @Query("play")String play, @Query("current") String current, @Query("attend") String attend);

    @GET("time.do?")
    Call<Post> doGetUserList(@Query("subject_idx") String subject_idx,@Query("user_id") String user_id);


}
