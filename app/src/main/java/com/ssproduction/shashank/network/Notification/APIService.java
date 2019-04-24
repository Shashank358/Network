package com.ssproduction.shashank.network.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
        {
            "Content-Type:application/json",
            "Authorization:key=AAAA8wT3nTM:APA91bFukwtuzqh1ZS2UodIbXqHlLVx7RYTdkEcuQtUC_Fe2ZY-MSihBLUaZUMgeeJ0WFPjMGLRkdM-ckzxIegTh5cyqgmonFqmsWxqoRZw8AzlcHV5f3UQxAz2NmBSZWxCM1Rat063q"
        }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
